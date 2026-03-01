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

import io.fletchly.comparator.manager.ToolManager
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.model.tool.Tool
import io.fletchly.comparator.model.tool.ToolList
import io.fletchly.comparator.model.tool.ToolResult
import io.fletchly.comparator.port.out.LogPort
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ToolManagerTest {
    private val log: LogPort = mockk {
        every { info(any(), any()) } just Runs
    }

    private fun toolWithName(name: String, result: ToolResult = ToolResult.Success(name, JsonPrimitive("ok"))): Tool =
        mockk<Tool> {
            every { this@mockk.name } returns name
            coEvery { execute(any()) } returns result
        }

    private fun toolCall(name: String, args: Map<String, Any> = emptyMap()): ToolCall =
        ToolCall(name, args)

    private fun managerWith(vararg tools: Tool): ToolManager =
        ToolManager(log).also { it.register(ToolList(tools.toList())) }

    // --- register() ---

    @Test
    fun `register throws when duplicate tool names are provided`() {
        val manager = ToolManager(log)
        assertFailsWith<IllegalArgumentException> {
            manager.register(ToolList(listOf(toolWithName("duplicate"), toolWithName("duplicate"))))
        }
    }

    @Test
    fun `register throws with duplicate names listed in message`() {
        val manager = ToolManager(log)
        val ex = assertFailsWith<IllegalArgumentException> {
            manager.register(ToolList(listOf(toolWithName("dup"), toolWithName("dup"))))
        }
        assertContains(ex.message ?: "", "dup")
    }

    @Test
    fun `register accepts tools with unique names`() {
        assertDoesNotThrow {
            managerWith(toolWithName("a"), toolWithName("b"))
        }
    }

    // --- tools property ---

    @Test
    fun `tools returns all registered tools`() {
        val a = toolWithName("a")
        val b = toolWithName("b")
        val manager = managerWith(a, b)

        val tools = manager.tools
        assertContains(tools, a)
        assertContains(tools, b)
        assertEquals(2, tools.size)
    }

    @Test
    fun `tools returns empty list when nothing is registered`() {
        val manager = managerWith()
        assertEquals(emptyList(), manager.tools)
    }

    // --- execute() ---

    @Test
    fun `returns tool not found message when tool does not exist`() = runTest {
        val manager = managerWith()
        val result = manager.execute(toolCall("unknown"))
        assertEquals("tool not found: unknown", result.content)
    }

    @Test
    fun `delegates execution to the correct tool`() = runTest {
        val tool = toolWithName("my-tool")
        val manager = managerWith(tool)
        val call = toolCall("my-tool", mapOf("x" to "hello"))

        manager.execute(call)

        coVerify { tool.execute(mapOf("x" to "hello")) }
    }

    @Test
    fun `returns tool result as message on success`() = runTest {
        val tool = toolWithName("my-tool", ToolResult.Success("my-tool", JsonPrimitive("result")))
        val manager = managerWith(tool)

        val message = manager.execute(toolCall("my-tool"))

        assertEquals(ToolResult.Success("my-tool", JsonPrimitive("result")).toString(), message.content)
    }

    @Test
    fun `returns tool result as message on failure`() = runTest {
        val tool = toolWithName("my-tool", ToolResult.Failure("my-tool", "something went wrong"))
        val manager = managerWith(tool)

        val message = manager.execute(toolCall("my-tool"))

        assertEquals(ToolResult.Failure("my-tool", "something went wrong").toString(), message.content)
    }

    @Test
    fun `does not execute other tools when one is called`() = runTest {
        val target = toolWithName("target")
        val other = toolWithName("other")
        val manager = managerWith(target, other)

        manager.execute(toolCall("target"))

        coVerify(exactly = 0) { other.execute(any()) }
    }

    // --- logging ---

    @Test
    fun `logs info after successful tool execution`() = runTest {
        val tool = toolWithName("my-tool", ToolResult.Success("my-tool", JsonPrimitive("result")))
        val manager = managerWith(tool)

        manager.execute(toolCall("my-tool", mapOf("x" to "hello")))

        verify { log.info(any(), "my-tool") }
    }

    @Test
    fun `logs info after failed tool execution`() = runTest {
        val tool = toolWithName("my-tool", ToolResult.Failure("my-tool", "oops"))
        val manager = managerWith(tool)

        manager.execute(toolCall("my-tool"))

        verify { log.info(any(), "my-tool") }
    }

    @Test
    fun `does not log when tool is not found`() = runTest {
        val manager = managerWith()

        manager.execute(toolCall("ghost"))

        verify(exactly = 0) { log.info(any(), any()) }
    }
}