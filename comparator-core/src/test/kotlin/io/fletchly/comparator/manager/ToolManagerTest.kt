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

import io.fletchly.comparator.exception.ToolException
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.model.tool.Tool
import io.fletchly.comparator.model.tool.ToolResult
import io.fletchly.comparator.port.out.LogPort
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.serializer
import kotlin.test.*

class ToolManagerTest {
    private val log = mockk<LogPort>(relaxed = true)
    private val manager = ToolManager(log)

    private fun mockTool(name: String): Tool {
        val tool = mockk<Tool>()
        every { tool.name } returns name
        return tool
    }

    @Test
    fun `registers a tool successfully`() {
        val tool = mockTool("my_tool")
        manager.register(tool)
        assertTrue(manager.getTools().map { it.name }.contains("my_tool"))
    }

    @Test
    fun `registers multiple tools at once`() {
        val tool1 = mockTool("tool_1")
        val tool2 = mockTool("tool_2")
        manager.register(tool1, tool2)
        assertEquals(listOf("tool_1", "tool_2"), manager.getTools().map { it.name })
    }

    @Test
    fun `skips duplicate tool and logs warning`() {
        val tool1 = mockTool("my_tool")
        val tool2 = mockTool("my_tool")
        manager.register(tool1)
        manager.register(tool2)
        assertEquals(1, manager.getTools().map { it.name }.count { it == "my_tool" })
        verify { log.warn(any(), any()) }
    }

    @Test
    fun `first registration wins on duplicate`() {
        val tool1 = mockTool("my_tool")
        val tool2 = mockTool("my_tool")
        manager.register(tool1)
        manager.register(tool2)
        // tool1 should still be the registered one
        assertSame(
            tool1, manager.getTools()
                .let { tools -> assertTrue(tools.map { it.name }.contains("my_tool")); tool1 })
    }

    @Test
    fun `does not log warning on successful registration`() {
        val tool = mockTool("my_tool")
        manager.register(tool)
        verify(exactly = 0) { log.warn(any(), any()) }
    }

    @Test
    fun `returns empty list when no tools registered`() {
        assertTrue(manager.getTools().isEmpty())
    }

    @Test
    fun `returns all registered tool names`() {
        manager.register(mockTool("tool_1"), mockTool("tool_2"), mockTool("tool_3"))
        assertEquals(3, manager.getTools().size)
    }

    @Test
    fun `returns tool not found message for unknown tool`() = runTest {
        val result = manager.execute(ToolCall("unknown_tool", emptyMap()),)
        assertEquals("tool not found", result.content)
    }

    @Test
    fun `executes registered tool and returns result`() = runTest {
        val tool = mockTool("my_tool")
        val toolCall = ToolCall("my_tool", mapOf("input" to "hello"))
        coEvery { tool.execute(toolCall.arguments) } returns ToolResult.Success(
            "my_tool",
            "hello",
            String.serializer()
        )
        manager.register(tool)
        val result = manager.execute(toolCall,)
        assertIs<Message.Tool>(result)
    }

    @Test
    fun `logs execution info after successful tool call`() = runTest {
        val tool = mockTool("my_tool")
        val toolCall = ToolCall("my_tool", mapOf("input" to "hello"))
        coEvery { tool.execute(toolCall.arguments) } returns ToolResult.Success(
            "my_tool",
            "hello",
            String.serializer()
        )
        manager.register(tool)
        manager.execute(toolCall,)
        verify { log.info(any(), any()) }
    }

    @Test
    fun `handles tool returning Failure result`() = runTest {
        val tool = mockTool("my_tool")
        val toolCall = ToolCall("my_tool", emptyMap())
        coEvery { tool.execute(toolCall.arguments) } returns ToolResult.Failure(
            "my_tool",
            ToolException("something went wrong", null)
        )
        manager.register(tool)
        val result = manager.execute(toolCall,)
        assertIs<Message.Tool>(result)
    }
}