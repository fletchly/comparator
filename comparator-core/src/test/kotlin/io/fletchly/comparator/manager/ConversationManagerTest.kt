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
import io.fletchly.comparator.model.message.conversationOf
import io.fletchly.comparator.model.tool.ToolContext
import io.fletchly.comparator.port.out.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConversationManagerTest {

    private val context = mockk<ContextPort>(relaxed = true)
    private val system = mockk<SystemConfigPort>()
    private val ai = mockk<AIPort>()
    private val tool = mockk<ToolManager>(relaxed = true)
    private val chat = mockk<ChatPort>(relaxed = true)
    private val notification = mockk<NotificationPort>(relaxed = true)
    private val event = mockk<EventPort>(relaxed = true)
    private val coroutineScope = mockk<CoroutineScopePort>()
    private val toolContext = mockk<ToolContext>(relaxed = true)

    private val conversationKey = mockk<ConversationKey> {
        every { uniqueId } returns UUID.randomUUID()
    }
    private val actor = mockk<Actor> {
        every { conversationKey } returns this@ConversationManagerTest.conversationKey
    }

    @BeforeTest
    fun setUp() {
        every { system.getPrompt() } returns "You are a helpful assistant."
    }

    private fun TestScope.buildManager(): ConversationManager {
        every { coroutineScope.launch(any()) } answers {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler), block = firstArg())
        }
        return ConversationManager(context, system, ai, tool, chat, notification, event, coroutineScope)
    }

    // --- sendUser: basic flow ---

    @Test
    fun `sendUser appends message to context and echoes it to chat`() = runTest {
        val manager = buildManager()
        val message = Message.User(content = "Hello", actor = actor)

        coEvery { context.get(conversationKey) } returns conversationOf(message)
        coEvery { ai.generateResponse(any(), any()) } returns
                MessageResult.Success(Message.Assistant(content = "Hi!"))

        manager.sendUser(message, toolContext)

        coVerify { context.append(conversationKey, message) }
        coVerify { chat.message(actor, message) }
    }

    @Test
    fun `sendUser warns and does not queue when channel is full`() = runTest {
        val manager = buildManager()
        val message = Message.User(content = "Flood", actor = actor)

        coEvery { context.get(conversationKey) } returns conversationOf(message)
        coEvery { ai.generateResponse(any(), any()) } coAnswers {
            delay(Long.MAX_VALUE)
            MessageResult.Success(Message.Assistant(content = ""))
        }

        repeat(ConversationManager.MAX_QUEUED_MESSAGES + 2) {
            manager.sendUser(message, toolContext)
        }

        coVerify(atLeast = 1) {
            chat.message(actor, Message.Assistant("Too many messages queued, please wait."))
        }
    }

    // --- AssistantLoop: no tool calls ---

    @Test
    fun `assistant response with content is sent to chat and appended to context`() = runTest {
        val manager = buildManager()
        val userMessage = Message.User(content = "Hey", actor = actor)
        val assistantMessage = Message.Assistant(content = "Hello!")

        coEvery { context.get(conversationKey) } returns conversationOf(userMessage)
        coEvery { ai.generateResponse(any(), any()) } returns MessageResult.Success(assistantMessage)

        manager.sendUser(userMessage, toolContext)

        coVerify { chat.message(actor, assistantMessage) }
        coVerify { context.append(conversationKey, assistantMessage) }
        coVerify(exactly = 0) { tool.execute(any(), any()) }
    }

    @Test
    fun `assistant response with blank content is appended to context but not sent to chat`() = runTest {
        val manager = buildManager()
        val userMessage = Message.User(content = "Hey", actor = actor)
        val blankAssistant = Message.Assistant(content = "   ")

        coEvery { context.get(conversationKey) } returns conversationOf(userMessage)
        coEvery { ai.generateResponse(any(), any()) } returns MessageResult.Success(blankAssistant)

        manager.sendUser(userMessage, toolContext)

        coVerify(exactly = 0) { chat.message(actor, blankAssistant) }
        coVerify { context.append(conversationKey, blankAssistant) }
    }

    // --- AssistantLoop: tool calls ---

    @Test
    fun `single tool call is executed, result appended, then loop continues`() = runTest {
        val manager = buildManager()
        val userMessage = Message.User(content = "Search something", actor = actor)
        val toolCall = mockk<ToolCall>()
        every { toolCall.arguments } returns emptyMap()
        val toolResult = Message.Tool(content = "tool output", name = "tool_name")
        val assistantWithTool = Message.Assistant(content = "Using tool...", toolCalls = listOf(toolCall))
        val finalAssistant = Message.Assistant(content = "Done!")

        coEvery { tool.execute(toolCall, toolContext) } returns toolResult
        coEvery { context.get(conversationKey) } returnsMany listOf(
            conversationOf(userMessage),
            conversationOf(userMessage, assistantWithTool, toolResult)
        )
        coEvery { ai.generateResponse(any(), any()) } returnsMany listOf(
            MessageResult.Success(assistantWithTool),
            MessageResult.Success(finalAssistant)
        )

        manager.sendUser(userMessage, toolContext)

        coVerify { tool.execute(toolCall, toolContext) }
        coVerify { context.append(conversationKey, toolResult) }
        coVerify { chat.message(actor, finalAssistant) }
    }

    @Test
    fun `multiple tool calls are all executed and their results appended`() = runTest {
        val manager = buildManager()
        val userMessage = Message.User(content = "Do many things", actor = actor)
        val toolCall1 = mockk<ToolCall>()
        val toolCall2 = mockk<ToolCall>()
        every { toolCall1.arguments } returns emptyMap()
        every { toolCall2.arguments } returns emptyMap()
        val toolResult1 = Message.Tool(content = "result 1", name = "tool1")
        val toolResult2 = Message.Tool(content = "result 2", name = "tool2")
        val assistantWithTools = Message.Assistant(
            content = "Calling tools",
            toolCalls = listOf(toolCall1, toolCall2)
        )
        val finalAssistant = Message.Assistant(content = "All done")

        coEvery { tool.execute(toolCall1, toolContext) } returns toolResult1
        coEvery { tool.execute(toolCall2, toolContext) } returns toolResult2
        coEvery { context.get(conversationKey) } returnsMany listOf(
            conversationOf(userMessage),
            conversationOf(userMessage, assistantWithTools, toolResult1, toolResult2)
        )
        coEvery { ai.generateResponse(any(), any()) } returnsMany listOf(
            MessageResult.Success(assistantWithTools),
            MessageResult.Success(finalAssistant)
        )

        manager.sendUser(userMessage, toolContext)

        coVerify { tool.execute(toolCall1, toolContext) }
        coVerify { tool.execute(toolCall2, toolContext) }
        coVerify { context.append(conversationKey, toolResult1) }
        coVerify { context.append(conversationKey, toolResult2) }
    }

    @Test
    fun `empty tool calls list is treated as terminal - loop does not recurse`() = runTest {
        val manager = buildManager()
        val userMessage = Message.User(content = "Hey", actor = actor)
        val assistantEmptyTools = Message.Assistant(content = "Done", toolCalls = emptyList())

        coEvery { context.get(conversationKey) } returns conversationOf(userMessage)
        coEvery { ai.generateResponse(any(), any()) } returns MessageResult.Success(assistantEmptyTools)

        manager.sendUser(userMessage, toolContext)

        coVerify(exactly = 1) { ai.generateResponse(any(), any()) }
        coVerify(exactly = 0) { tool.execute(any(), any()) }
    }

    // --- AssistantLoop: failure ---

    @Test
    fun `AI failure sends error notification without messaging chat`() = runTest {
        val manager = buildManager()
        val userMessage = Message.User(content = "Uh oh", actor = actor)
        val errorMessage = "AI unavailable"

        coEvery { context.get(conversationKey) } returns conversationOf(userMessage)
        coEvery { ai.generateResponse(any(), any()) } returns MessageResult.Failure(errorMessage)

        manager.sendUser(userMessage, toolContext)

        coVerify { notification.error(actor, errorMessage) }
        coVerify(exactly = 0) { chat.message(actor, any<Message.Assistant>()) }
    }

    // --- Actor isolation ---

    @Test
    fun `two actors receive independent conversations`() = runTest {
        val manager = buildManager()
        val conversationKey2 = mockk<ConversationKey> { every { uniqueId } returns UUID.randomUUID() }
        val actor2 = mockk<Actor> { every { conversationKey } returns conversationKey2 }
        val msg1 = Message.User(content = "From actor 1", actor = actor)
        val msg2 = Message.User(content = "From actor 2", actor = actor2)
        val response = Message.Assistant(content = "OK")

        coEvery { context.get(conversationKey) } returns conversationOf(msg1)
        coEvery { context.get(conversationKey2) } returns conversationOf(msg2)
        coEvery { ai.generateResponse(any(), any()) } returns MessageResult.Success(response)

        manager.sendUser(msg1, toolContext)
        manager.sendUser(msg2, toolContext)

        coVerify { context.append(conversationKey, msg1) }
        coVerify { context.append(conversationKey2, msg2) }
        coVerify(exactly = 0) { context.append(conversationKey, msg2) }
        coVerify(exactly = 0) { context.append(conversationKey2, msg1) }
    }

    // --- Channel lifecycle ---

    @Test
    fun `channel is removed from map after processing when empty`() = runTest {
        val manager = buildManager()
        val message = Message.User(content = "Hello", actor = actor)

        coEvery { context.get(conversationKey) } returns conversationOf(message)
        coEvery { ai.generateResponse(any(), any()) } returns
                MessageResult.Success(Message.Assistant(content = "Hi!"))

        manager.sendUser(message, toolContext)

        // Channel drained and closed, next message creates a fresh channel
        // and the AI is called again rather than reusing the old channel
        coEvery { context.get(conversationKey) } returns conversationOf(message)
        manager.sendUser(message, toolContext)

        coVerify(exactly = 2) { ai.generateResponse(any(), any()) }
    }
}