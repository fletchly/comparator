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

package io.fletchly.comparator.core.manager

import io.fletchly.comparator.manager.ConversationManager
import io.fletchly.comparator.manager.ToolManager
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.MessageResult
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.model.user.User
import io.fletchly.comparator.port.out.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ConversationManagerTest {
    private val context = mockk<ContextPort>(relaxed = true)
    private val system = mockk<SystemInfoPort>(relaxed = true)
    private val ai = mockk<AIPort>(relaxed = true)
    private val tool = mockk<ToolManager>(relaxed = true)
    private val chat = mockk<ChatPort>(relaxed = true)
    private val notification = mockk<NotificationPort>(relaxed = true)

    private val manager = ConversationManager(context, system, ai, tool, chat, notification)

    private val sender = mockk<User>()
    private val message = mockk<Message.User>(relaxed = true).also {
        every { it.sender } returns sender
    }

    @Test
    fun `fromUser appends message to context`() = runTest {
        stubAiSuccess(toolCalls = null)
        manager.fromUser(message)
        coVerify { context.append(sender, message) }
    }

    @Test
    fun `fromUser sends user message to chat`() = runTest {
        stubAiSuccess(toolCalls = null)
        manager.fromUser(message)
        coVerify { chat.message(sender, message) }
    }

    @Test
    fun `sends error notification on AI failure`() = runTest {
        val error = "something went wrong"
        coEvery { ai.generateResponse(any(), any()) } returns MessageResult.Failure(error)

        manager.fromUser(message)

        coVerify { notification.error(sender, error) }
    }

    @Test
    fun `does not append to context on AI failure`() = runTest {
        coEvery { ai.generateResponse(any(), any()) } returns MessageResult.Failure("err")

        manager.fromUser(message)

        coVerify(exactly = 0) { context.append(sender, any<Message.Assistant>()) }
    }

    @Test
    fun `appends assistant response to context`() = runTest {
        val response = stubAiSuccess(toolCalls = null)
        manager.fromUser(message)
        coVerify { context.append(sender, response) }
    }

    @Test
    fun `sends assistant response to chat`() = runTest {
        val response = stubAiSuccess(toolCalls = null)
        manager.fromUser(message)
        coVerify { chat.message(sender, response) }
    }

    @Test
    fun `does not execute tools when there are no tool calls`() = runTest {
        stubAiSuccess(toolCalls = null)
        manager.fromUser(message)
        coVerify { tool wasNot called }
    }

    @Test
    fun `executes each tool call`() = runTest {
        val toolCall1 = mockk<ToolCall>()
        val toolCall2 = mockk<ToolCall>()
        stubAiSuccess(toolCalls = listOf(toolCall1, toolCall2), thenNoTools = true)

        manager.fromUser(message)

        coVerify { tool.execute(toolCall1) }
        coVerify { tool.execute(toolCall2) }
    }

    @Test
    fun `appends tool results to context`() = runTest {
        val toolCall = mockk<ToolCall>()
        val toolResult = mockk<Message.Tool>()
        coEvery { tool.execute(toolCall) } returns toolResult
        stubAiSuccess(toolCalls = listOf(toolCall), thenNoTools = true)

        manager.fromUser(message)

        coVerify { context.append(sender, toolResult) }
    }

    @Test
    fun `loops and calls AI again after handling tool calls`() = runTest {
        val toolCall = mockk<ToolCall>()
        stubAiSuccess(toolCalls = listOf(toolCall), thenNoTools = true)

        manager.fromUser(message)

        coVerify(exactly = 2) { ai.generateResponse(any(), any()) }
    }

    private fun stubAiSuccess(
        toolCalls: List<ToolCall>?,
        thenNoTools: Boolean = false
    ): Message.Assistant {
        val response = mockk<Message.Assistant>(relaxed = true) {
            every { this@mockk.toolCalls } returns toolCalls
        }
        val successResult = MessageResult.Success(response)

        if (thenNoTools) {
            val secondResponse = mockk<Message.Assistant>(relaxed = true) {
                every { this@mockk.toolCalls } returns null
            }
            coEvery { ai.generateResponse(any(), any()) } returnsMany listOf(
                successResult,
                MessageResult.Success(secondResponse)
            )
        } else {
            coEvery { ai.generateResponse(any(), any()) } returns successResult
        }

        return response
    }
}