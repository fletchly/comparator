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
import io.fletchly.comparator.model.message.conversationOf
import io.fletchly.comparator.model.user.ConversationScope
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
    private val scope = mockk<CoroutineScopePort>()

    private val user = mockk<ConversationScope>()

    @BeforeTest
    fun setUp() {
        every { system.prompt } returns "You are a helpful assistant."
    }

    private fun TestScope.buildManager(): ConversationManager {
        every { scope.launch(any()) } answers {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler), block = firstArg())
        }
        return ConversationManager(context, system, ai, tool, chat, notification, scope)
    }

    // --- fromUser: basic flow ---

    @Test
    fun `fromUser appends message to context and echoes it to chat`() = runTest {
        val manager = buildManager()
        val message = Message.User(content = "Hello", sender = user)

        coEvery { context.get(user) } returns conversationOf(message)
        coEvery { ai.generateResponse(any(), any()) } returns
                MessageResult.Success(Message.Assistant(content = "Hi!"))

        manager.fromUser(message)

        coVerify { context.append(user, message) }
        coVerify { chat.message(user, message) }
    }

    @Test
    fun `fromUser warns and does not queue when channel is full`() = runTest {
        val manager = buildManager()
        val message = Message.User(content = "Flood", sender = user)

        coEvery { context.get(user) } returns conversationOf(message)
        coEvery { ai.generateResponse(any(), any()) } coAnswers {
            delay(Long.MAX_VALUE)
            MessageResult.Success(Message.Assistant(content = ""))
        }

        repeat(ConversationManager.MAX_QUEUED_MESSAGES + 2) {
            manager.fromUser(message)
        }

        coVerify(atLeast = 1) {
            chat.message(user, Message.Assistant("Too many messages queued, please wait."))
        }
    }

    // --- AssistantLoop: no tool calls ---

    @Test
    fun `assistant response with content is sent to chat and appended to context`() = runTest {
        val manager = buildManager()
        val userMessage = Message.User(content = "Hey", sender = user)
        val assistantMessage = Message.Assistant(content = "Hello!")

        coEvery { context.get(user) } returns conversationOf(userMessage)
        coEvery { ai.generateResponse(any(), any()) } returns MessageResult.Success(assistantMessage)

        manager.fromUser(userMessage)

        coVerify { chat.message(user, assistantMessage) }
        coVerify { context.append(user, assistantMessage) }
        coVerify(exactly = 0) { tool.execute(any()) }
    }

    @Test
    fun `assistant response with blank content is appended to context but not sent to chat`() = runTest {
        val manager = buildManager()
        val userMessage = Message.User(content = "Hey", sender = user)
        val blankAssistant = Message.Assistant(content = "   ")

        coEvery { context.get(user) } returns conversationOf(userMessage)
        coEvery { ai.generateResponse(any(), any()) } returns MessageResult.Success(blankAssistant)

        manager.fromUser(userMessage)

        coVerify(exactly = 0) { chat.message(user, blankAssistant) }
        coVerify { context.append(user, blankAssistant) }
    }

    // --- AssistantLoop: tool calls ---

    @Test
    fun `single tool call is executed, result appended, then loop continues`() = runTest {
        val manager = buildManager()
        val userMessage = Message.User(content = "Search something", sender = user)
        val toolCall = mockk<ToolCall>()
        val toolResult = Message.Tool(content = "tool output")
        val assistantWithTool = Message.Assistant(content = "Using tool...", toolCalls = listOf(toolCall))
        val finalAssistant = Message.Assistant(content = "Done!")

        coEvery { tool.execute(toolCall) } returns toolResult
        coEvery { context.get(user) } returnsMany listOf(
            conversationOf(userMessage),
            conversationOf(userMessage, assistantWithTool, toolResult)
        )
        coEvery { ai.generateResponse(any(), any()) } returnsMany listOf(
            MessageResult.Success(assistantWithTool),
            MessageResult.Success(finalAssistant)
        )

        manager.fromUser(userMessage)

        coVerify { tool.execute(toolCall) }
        coVerify { context.append(user, toolResult) }
        coVerify { chat.message(user, finalAssistant) }
    }

    @Test
    fun `multiple tool calls are all executed and their results appended`() = runTest {
        val manager = buildManager()
        val userMessage = Message.User(content = "Do many things", sender = user)
        val toolCall1 = mockk<ToolCall>()
        val toolCall2 = mockk<ToolCall>()
        val toolResult1 = Message.Tool(content = "result 1")
        val toolResult2 = Message.Tool(content = "result 2")
        val assistantWithTools = Message.Assistant(
            content = "Calling tools",
            toolCalls = listOf(toolCall1, toolCall2)
        )
        val finalAssistant = Message.Assistant(content = "All done")

        coEvery { tool.execute(toolCall1) } returns toolResult1
        coEvery { tool.execute(toolCall2) } returns toolResult2
        coEvery { context.get(user) } returnsMany listOf(
            conversationOf(userMessage),
            conversationOf(userMessage, assistantWithTools, toolResult1, toolResult2)
        )
        coEvery { ai.generateResponse(any(), any()) } returnsMany listOf(
            MessageResult.Success(assistantWithTools),
            MessageResult.Success(finalAssistant)
        )

        manager.fromUser(userMessage)

        coVerify { tool.execute(toolCall1) }
        coVerify { tool.execute(toolCall2) }
        coVerify { context.append(user, toolResult1) }
        coVerify { context.append(user, toolResult2) }
    }

    @Test
    fun `empty tool calls list is treated as terminal - loop does not recurse`() = runTest {
        val manager = buildManager()
        val userMessage = Message.User(content = "Hey", sender = user)
        val assistantEmptyTools = Message.Assistant(content = "Done", toolCalls = emptyList())

        coEvery { context.get(user) } returns conversationOf(userMessage)
        coEvery { ai.generateResponse(any(), any()) } returns MessageResult.Success(assistantEmptyTools)

        manager.fromUser(userMessage)

        coVerify(exactly = 1) { ai.generateResponse(any(), any()) }
        coVerify(exactly = 0) { tool.execute(any()) }
    }

    // --- AssistantLoop: failure ---

    @Test
    fun `AI failure sends error notification without messaging chat`() = runTest {
        val manager = buildManager()
        val userMessage = Message.User(content = "Uh oh", sender = user)
        val errorMessage = "AI unavailable"

        coEvery { context.get(user) } returns conversationOf(userMessage)
        coEvery { ai.generateResponse(any(), any()) } returns MessageResult.Failure(errorMessage)

        manager.fromUser(userMessage)

        coVerify { notification.error(user, errorMessage) }
        coVerify(exactly = 0) { chat.message(user, any<Message.Assistant>()) }
    }

    // --- ConversationScope isolation ---

    @Test
    fun `two users receive independent conversations`() = runTest {
        val manager = buildManager()
        val scope2 = mockk<ConversationScope>()
        val msg1 = Message.User(content = "From user 1", sender = user)
        val msg2 = Message.User(content = "From user 2", sender = scope2)
        val response = Message.Assistant(content = "OK")

        coEvery { context.get(user) } returns conversationOf(msg1)
        coEvery { context.get(scope2) } returns conversationOf(msg2)
        coEvery { ai.generateResponse(any(), any()) } returns MessageResult.Success(response)

        manager.fromUser(msg1)
        manager.fromUser(msg2)

        coVerify { context.append(user, msg1) }
        coVerify { context.append(scope2, msg2) }
        coVerify(exactly = 0) { context.append(user, msg2) }
        coVerify(exactly = 0) { context.append(scope2, msg1) }
    }
}