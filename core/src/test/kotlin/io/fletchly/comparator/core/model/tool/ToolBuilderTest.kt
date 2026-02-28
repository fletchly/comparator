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

package io.fletchly.comparator.core.model.tool

import io.fletchly.comparator.exception.ToolDefinitionException
import io.fletchly.comparator.exception.ToolException
import io.fletchly.comparator.model.tool.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.InputStream
import kotlin.test.*

@Serializable
data class SerializableOutput(val value: String)

@Suppress("RedundantSuspendModifier")
class ToolBuilderTest {
    private fun buildSimpleTool(): Tool = tool("simple") {
        description = "A simple tool"
        executes(::simpleHandler)
    }

    @Test
    fun `build throws when no handler is provided`() {
        assertFailsWith<IllegalArgumentException> {
            tool("broken") { description = "no handler" }
        }
    }

    @Test
    fun `build throws when handler is not a suspend function`() {
        assertFailsWith<IllegalArgumentException> {
            tool("broken") {
                executes(::nonSuspendHandler)
            }
        }
    }

    @Test
    fun `build throws when return type is not serializable`() {
        assertFailsWith<ToolDefinitionException> {
            tool("broken") { executes(::unserializableHandler) }
        }
    }

    @Test
    fun `build throws when parameter has no Description annotation`() {
        assertFailsWith<IllegalStateException> {
            tool("broken") { executes(::noDescriptionHandler) }
        }
    }

    @Test
    fun `built tool has correct name`() {
        assertEquals("simple", buildSimpleTool().name)
    }

    @Test
    fun `built tool has correct description`() {
        assertEquals("A simple tool", buildSimpleTool().description)
    }

    @Test
    fun `parameter name is inferred from function parameter`() {
        val param = buildSimpleTool().parameters.single()
        assertEquals("input", param.name)
    }

    @Test
    fun `parameter description comes from annotation`() {
        val param = buildSimpleTool().parameters.single()
        assertEquals("a string value", param.description)
    }

    @Test
    fun `string parameter maps to STRING type`() {
        val param = buildSimpleTool().parameters.single()
        assertEquals(ToolParameter.Type.STRING, param.type)
    }

    @Test
    fun `int parameter maps to INTEGER type`() {
        val param = tool("t") { executes(::intParamHandler) }.parameters.single()
        assertEquals(ToolParameter.Type.INTEGER, param.type)
    }

    @Test
    fun `boolean parameter maps to BOOLEAN type`() {
        val param = tool("t") { executes(::booleanParamHandler) }.parameters.single()
        assertEquals(ToolParameter.Type.BOOLEAN, param.type)
    }

    @Test
    fun `non-null parameter is required`() {
        val param = buildSimpleTool().parameters.single()
        assertTrue(param.required)
    }

    @Test
    fun `nullable parameter is not required`() {
        val param = tool("t") { executes(::nullableParamHandler) }.parameters.single()
        assertFalse(param.required)
    }

    @Test
    fun `multiple parameters are all mapped`() {
        val params = tool("t") { executes(::multiParamHandler) }.parameters
        assertEquals(2, params.size)
        assertEquals("a", params[0].name)
        assertEquals("b", params[1].name)
    }

    @Test
    fun `execute returns success with string result`() = runTest {
        val result = buildSimpleTool().execute(mapOf("input" to "hello"))
        assertIs<ToolResult.Success>(result)
        assertEquals(JsonPrimitive("hello"), result.value)
    }

    @Test
    fun `execute returns success with number result`() = runTest {
        val result = tool("t") { executes(::intHandler) }.execute(mapOf("n" to 21))
        assertIs<ToolResult.Success>(result)
        assertEquals(JsonPrimitive(42), result.value)
    }

    @Test
    fun `execute returns failure when ToolException is thrown`() = runTest {
        val result = tool("t") { executes(::throwingHandler) }.execute(mapOf("x" to "oops"))
        assertIs<ToolResult.Failure>(result)
        assertEquals("bad input", result.error)
    }

    @Test
    fun `execute serializes data class result`() = runTest {
        val result = tool("t") { executes(::serializableHandler) }.execute(mapOf("x" to "hi"))
        assertIs<ToolResult.Success>(result)
        assertEquals(JsonObject(mapOf("value" to JsonPrimitive("hi"))), result.value)
    }

    companion object {
        suspend fun simpleHandler(
            @Description("a string value") input: String
        ): String = input

        suspend fun multiParamHandler(
            @Description("first value") a: String,
            @Description("second value") b: String
        ): String = "$a$b"

        suspend fun nullableParamHandler(
            @Description("optional value") input: String?
        ): String = input ?: "default"

        fun nonSuspendHandler(@Description("a string value") input: String): String = input

        suspend fun unserializableHandler(@Description("x") input: String): InputStream =
            error("this should never be called")

        suspend fun noDescriptionHandler(input: String): String = input

        suspend fun intParamHandler(@Description("n") n: Int): String = "$n"

        suspend fun booleanParamHandler(@Description("flag") flag: Boolean): String = "$flag"

        suspend fun intHandler(@Description("n") n: Int): Int = n * 2

        suspend fun throwingHandler(@Description("x") x: String): String = throw ToolException("bad input")

        suspend fun serializableHandler(@Description("x") x: String): SerializableOutput = SerializableOutput(x)
    }
}