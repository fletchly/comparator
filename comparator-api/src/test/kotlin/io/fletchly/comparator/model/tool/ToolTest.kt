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

package io.fletchly.comparator.model.tool

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ToolTest {
    @Serializable
    data class SimpleResult(val value: String)

    private fun buildSimpleTool(vararg params: Parameter): Tool {
        return Tool("test_tool", "A test tool", params.toList()) { args ->
            ToolResult.Success("test_tool", SimpleResult("ok"), SimpleResult.serializer())
        }
    }

    // --- Required parameter validation ---

    @Test
    fun `returns failure when required parameter is missing`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.STRING, "An input", required = true)
        )
        val result = tool.execute(emptyMap())
        assertIs<ToolResult.Failure>(result)
    }

    @Test
    fun `failure message mentions missing parameter name`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.STRING, "An input", required = true)
        )
        val result = tool.execute(emptyMap()) as ToolResult.Failure
        assertTrue(result.error.message.contains("input"))
    }

    @Test
    fun `does not return failure when optional parameter is missing`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.STRING, "An input", required = false)
        )
        val result = tool.execute(emptyMap())
        assertIs<ToolResult.Success>(result)
    }

    // --- Type validation ---

    @Test
    fun `returns failure when string parameter receives wrong type`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.STRING, "An input")
        )
        val result = tool.execute(mapOf("input" to 42))
        assertIs<ToolResult.Failure>(result)
    }

    @Test
    fun `accepts Int for INTEGER parameter`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.INTEGER, "An input")
        )
        val result = tool.execute(mapOf("input" to 42))
        assertIs<ToolResult.Success>(result)
    }

    @Test
    fun `accepts Long for INTEGER parameter`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.INTEGER, "An input")
        )
        val result = tool.execute(mapOf("input" to 42L))
        assertIs<ToolResult.Success>(result)
    }

    @Test
    fun `accepts Float for NUMBER parameter`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.NUMBER, "An input")
        )
        val result = tool.execute(mapOf("input" to 3.14f))
        assertIs<ToolResult.Success>(result)
    }

    @Test
    fun `accepts Double for NUMBER parameter`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.NUMBER, "An input")
        )
        val result = tool.execute(mapOf("input" to 3.14))
        assertIs<ToolResult.Success>(result)
    }

    @Test
    fun `accepts Boolean for BOOLEAN parameter`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.BOOLEAN, "An input")
        )
        val result = tool.execute(mapOf("input" to true))
        assertIs<ToolResult.Success>(result)
    }

    @Test
    fun `returns failure when boolean parameter receives wrong type`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.BOOLEAN, "An input")
        )
        val result = tool.execute(mapOf("input" to "true"))
        assertIs<ToolResult.Failure>(result)
    }

    @Test
    fun `failure message mentions parameter name and expected type on type mismatch`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.INTEGER, "An input")
        )
        val result = tool.execute(mapOf("input" to "not_an_int")) as ToolResult.Failure
        assertTrue(result.error.message.contains("input"))
        assertTrue(result.error.message.contains("INTEGER"))
    }

    // --- Array validation ---

    @Test
    fun `accepts list of strings for ARRAY parameter`() = runTest {
        val tool = buildSimpleTool(
            Parameter("tags", Parameter.Type.ARRAY, "A list", elementType = Parameter.Type.STRING)
        )
        val result = tool.execute(mapOf("tags" to listOf("a", "b", "c")))
        assertIs<ToolResult.Success>(result)
    }

    @Test
    fun `returns failure when array parameter receives non-list`() = runTest {
        val tool = buildSimpleTool(
            Parameter("tags", Parameter.Type.ARRAY, "A list", elementType = Parameter.Type.STRING)
        )
        val result = tool.execute(mapOf("tags" to "not_a_list"))
        assertIs<ToolResult.Failure>(result)
    }

    @Test
    fun `returns failure when array element is wrong type`() = runTest {
        val tool = buildSimpleTool(
            Parameter("tags", Parameter.Type.ARRAY, "A list", elementType = Parameter.Type.STRING)
        )
        val result = tool.execute(mapOf("tags" to listOf("valid", 42, "also_valid")))
        assertIs<ToolResult.Failure>(result)
    }

    @Test
    fun `failure message mentions index of invalid array element`() = runTest {
        val tool = buildSimpleTool(
            Parameter("tags", Parameter.Type.ARRAY, "A list", elementType = Parameter.Type.STRING)
        )
        val result = tool.execute(mapOf("tags" to listOf("valid", 42))) as ToolResult.Failure
        assertTrue(result.error.message.contains("[1]"))
    }

    @Test
    fun `returns failure when array element is null`() = runTest {
        val tool = buildSimpleTool(
            Parameter("tags", Parameter.Type.ARRAY, "A list", elementType = Parameter.Type.STRING)
        )
        val result = tool.execute(mapOf("tags" to listOf("valid", null)))
        assertIs<ToolResult.Failure>(result)
    }

    @Test
    fun `accepts empty list for ARRAY parameter`() = runTest {
        val tool = buildSimpleTool(
            Parameter("tags", Parameter.Type.ARRAY, "A list", elementType = Parameter.Type.STRING)
        )
        val result = tool.execute(mapOf("tags" to emptyList<String>()))
        assertIs<ToolResult.Success>(result)
    }

    // --- Execution ---

    @Test
    fun `returns success when all parameters are valid`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.STRING, "An input")
        )
        val result = tool.execute(mapOf("input" to "hello"))
        assertIs<ToolResult.Success>(result)
    }

    @Test
    fun `success result contains correct tool name`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.STRING, "An input")
        )
        val result = tool.execute(mapOf("input" to "hello")) as ToolResult.Success
        assertEquals("test_tool", result.toolName)
    }

    @Test
    fun `failure result contains cause of illegal argument exception`() = runTest {
        val tool = buildSimpleTool(
            Parameter("input", Parameter.Type.STRING, "An input", required = true)
        )
        val result = tool.execute(emptyMap()) as ToolResult.Failure
        assertIs<IllegalArgumentException>(result.error.cause)
    }
}