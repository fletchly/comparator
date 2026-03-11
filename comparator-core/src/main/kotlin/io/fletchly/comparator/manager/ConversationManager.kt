/*
 * This file is part of comparator, licensed under the Apache License 2.0
 *
 * Copyright (c) 2026 fletchly <https://github.com/fletchly>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fletchly.comparator.manager

import io.fletchly.comparator.model.actor.Actor
import io.fletchly.comparator.model.message.ConversationKey
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.MessageResult
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.model.tool.ToolContext
import io.fletchly.comparator.model.event.ConversationEvent
import io.fletchly.comparator.port.`in`.MessageSender
import io.fletchly.comparator.port.out.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages user conversations and ensures efficient response generation while handling concurrent requests.
 *
 * The `ConversationManager` coordinates interactions between various components involved in a conversational system.
 * It manages user-specific contexts, generates AI responses, and invokes tools as necessary to fulfill user queries.
 *
 * @constructor Instantiates a new `ConversationManager` with the specified dependencies.
 *
 * @param context The system for managing user-specific conversational contexts.
 * @param system The configuration providing system-level prompts and behavior.
 * @param ai The AI response generation engine.
 * @param tool The manager responsible for executing tool invocations.
 * @param chat The interface for sending messages to users.
 * @param notification The component for sending notifications to users.
 */
class ConversationManager(
    private val context: ContextPort,
    private val system: SystemConfigPort,
    private val ai: AIPort,
    private val tool: ToolManager,
    private val chat: ChatPort,
    private val notification: NotificationPort,
    private val event: EventPort,
    private val coroutineScope: CoroutineScopePort
) : MessageSender {
    private val scopeChannels = ConcurrentHashMap<ConversationKey, Channel<Message.User>>()

    override suspend fun sendUser(message: Message.User, toolContext: ToolContext) {
        val channel = scopeChannels.computeIfAbsent(message.actor.conversationKey) { key ->
            createChannel(key, toolContext)
        }

        if (channel.trySend(message).isFailure) {
            chat.message(message.actor, Message.Assistant("Too many messages queued, please wait."))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun createChannel(key: ConversationKey, toolContext: ToolContext): Channel<Message.User> {
        val channel = Channel<Message.User>(capacity = MAX_QUEUED_MESSAGES)

        coroutineScope.launch {
            for (message in channel) {
                startConversation(message)
                AssistantLoop(message.actor, toolContext).run()
                if (channel.isEmpty) channel.close()
            }
            scopeChannels.remove(key)
        }

        return channel
    }

    private suspend fun startConversation(message: Message.User) {
        context.append(message.actor.conversationKey, message)
        event.emit(ConversationEvent.MessageAdded(message.actor.conversationKey))
        chat.message(message.actor, message)
    }

    private inner class AssistantLoop(private val target: Actor, private val toolContext: ToolContext) {
        tailrec suspend fun run() {
            val conversation = context.get(target.conversationKey)

            when (val result = ai.generateResponse(system.getPrompt(), conversation)) {
                is MessageResult.Failure -> notification.error(target, result.error)

                is MessageResult.Success<Message.Assistant> -> {
                    val response = result.message
                    if (response.content.isNotBlank()) chat.message(target, response)
                    context.append(target.conversationKey, response)
                    event.emit(ConversationEvent.MessageAdded(target.conversationKey))

                    val toolCalls = response.toolCalls
                    if (toolCalls.isNullOrEmpty()) return
                    handleToolCalls(toolCalls)

                    run()
                }
            }
        }

        private suspend fun handleToolCalls(toolCalls: List<ToolCall>) {
            toolCalls
                .map { toolCall -> tool.execute(toolCall, toolContext) }
                .forEach {
                    context.append(target.conversationKey, it)
                    event.emit(ConversationEvent.MessageAdded(target.conversationKey))
                }
        }
    }

    companion object {
        const val MAX_QUEUED_MESSAGES = 16
    }
}