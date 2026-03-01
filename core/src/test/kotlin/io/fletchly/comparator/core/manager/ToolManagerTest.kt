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
import io.fletchly.comparator.util.ToolList
import io.fletchly.comparator.model.tool.ToolResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ToolManagerTest {
    private fun toolWithName(name: String, result: ToolResult = ToolResult.Success(name, JsonPrimitive("ok"))): Tool =
        mockk<Tool> {
            every { this@mockk.name } returns name
            coEvery { execute(any()) } returns result
        }

    private fun toolCall(name: String, args: Map<String, Any> = emptyMap()): ToolCall =
        ToolCall(name, args)

    @Test
    fun `throws when duplicate tool names are provided`() {
        assertFailsWith<IllegalArgumentException> {
            ToolManager(ToolList(listOf(toolWithName("duplicate"), toolWithName("duplicate"))))
        }
    }

    @Test
    fun `throws with duplicate names listed in message`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            ToolManager(ToolList(listOf(toolWithName("dup"), toolWithName("dup"))))
        }
        assertContains(ex.message ?: "", "dup")
    }

    @Test
    fun `accepts tools with unique names`() {
        assertDoesNotThrow {
            ToolManager(ToolList(listOf(toolWithName("a"), toolWithName("b"))))
        }
    }

    @Test
    fun `returns tool not found message when tool does not exist`() = runTest {
        val manager = ToolManager(ToolList(emptyList()))
        val result = manager.execute(toolCall("unknown"))
        assertEquals("tool not found: unknown", result.content)
    }

    @Test
    fun `delegates execution to the correct tool`() = runTest {
        val tool = toolWithName("my-tool")
        val manager = ToolManager(ToolList(listOf(tool)))
        val call = toolCall("my-tool", mapOf("x" to "hello"))

        manager.execute(call)

        coVerify { tool.execute(mapOf("x" to "hello")) }
    }

    @Test
    fun `returns tool result as message on success`() = runTest {
        val tool = toolWithName("my-tool", ToolResult.Success("my-tool", JsonPrimitive("result")))
        val manager = ToolManager(ToolList(listOf(tool)))

        val message = manager.execute(toolCall("my-tool"))

        assertEquals(ToolResult.Success("my-tool", JsonPrimitive("result")).toString(), message.content)
    }

    @Test
    fun `returns tool result as message on failure`() = runTest {
        val tool = toolWithName("my-tool", ToolResult.Failure("my-tool", "something went wrong"))
        val manager = ToolManager(ToolList(listOf(tool)))

        val message = manager.execute(toolCall("my-tool"))

        assertEquals(ToolResult.Failure("my-tool", "something went wrong").toString(), message.content)
    }

    @Test
    fun `does not execute other tools when one is called`() = runTest {
        val target = toolWithName("target")
        val other = toolWithName("other")
        val manager = ToolManager(ToolList(listOf(target, other)))

        manager.execute(toolCall("target"))

        coVerify(exactly = 0) { other.execute(any()) }
    }
}