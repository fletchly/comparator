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

import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.MessageResult
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.model.user.User
import io.fletchly.comparator.port.`in`.MessageSender
import io.fletchly.comparator.port.out.*

/**
 * Manages conversations between users and an AI-based assistant.
 *
 * The class orchestrates the flow of messages and interactions, handling user inputs,
 * generating assistant responses, and managing tool executions within the conversation context.
 *
 * @property context Provides methods for retrieving and appending conversation data.
 * @property system Supplies the system prompt required for generating assistant responses.
 * @property ai Generates AI-driven assistant responses based on the current context and system prompt.
 * @property tool Executes specific tool calls associated with an assistant response.
 * @property chat Manages communication with users and the assistant during conversations.
 * @property notification Handles notifications for errors or informational messages during conversation processing.
 */
class ConversationManager(
    private val context: ContextPort,
    private val system: SystemInfoPort,
    private val ai: AIPort,
    private val tool: ToolManager,
    private val chat: ChatPort,
    private val notification: NotificationPort
) : MessageSender {

    override suspend fun fromUser(
        message: Message.User
    ) {
        startConversation(message)
        AssistantLoop(message.sender).run()
    }

    private suspend fun startConversation(message: Message.User) {
        context.append(message.sender, message)
        chat.user(message.sender, message)
    }

    private inner class AssistantLoop(private val target: User) {
        tailrec suspend fun run() {
            val conversation = context.get(target)

            when (val result = ai.generateResponse(system.getPrompt(), conversation)) {
                is MessageResult.Failure -> {
                    notification.error(target, result.error)
                }

                is MessageResult.Success<Message.Assistant> -> {
                    val response = result.message
                    chat.assistant(target, response)
                    context.append(target, response)

                    val toolCalls = response.toolCalls
                    if (toolCalls.isNullOrEmpty()) return
                    handleToolCalls(toolCalls)

                    run()
                }
            }
        }

        private suspend fun handleToolCalls(toolCalls: List<ToolCall>) {
            toolCalls
                .map { tool.execute(it) }
                .forEach { context.append(target, it) }
        }
    }
}