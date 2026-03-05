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

package io.fletchly.comparator

import io.fletchly.comparator.annotation.AllowedValues
import io.fletchly.comparator.annotation.ToolFunction
import io.fletchly.comparator.annotation.ToolParameter
import io.fletchly.comparator.exception.ToolException
import io.fletchly.comparator.model.Parameter
import io.fletchly.comparator.model.ToolResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Suppress("RedundantSuspendModifier")
class ToolBuilderTest {
    // Fixture data classes

    @Serializable
    data class SimpleResult(val value: String)

    @Serializable
    data class NumberResult(val value: Double)

    // Valid handler fixtures

    @ToolFunction(name = "echo", description = "Echoes a string back")
    suspend fun echoHandler(
        @ToolParameter("The string to echo") input: String
    ): SimpleResult = SimpleResult(input)

    @ToolFunction(description = "Uses function name as tool name")
    suspend fun noNameHandler(
        @ToolParameter("A number") value: Int
    ): NumberResult = NumberResult(value.toDouble())

    @ToolFunction(name = "greet", description = "Greets someone")
    suspend fun optionalParamHandler(
        @ToolParameter("The name") name: String,
        @ToolParameter("The greeting", required = false) greeting: String?
    ): SimpleResult = SimpleResult("${greeting ?: "Hello"}, $name")

    @ToolFunction(name = "allowed", description = "Uses allowed values")
    suspend fun allowedValuesHandler(
        @ToolParameter("The unit") @AllowedValues("celsius", "fahrenheit") unit: String
    ): SimpleResult = SimpleResult(unit)

    @ToolFunction(name = "list_tool", description = "Accepts a list")
    suspend fun listParamHandler(
        @ToolParameter("List of tags") tags: List<String>
    ): SimpleResult = SimpleResult(tags.joinToString())

    @ToolFunction(name = "throwing_tool", description = "Throws a ToolException")
    suspend fun throwingHandler(
        @ToolParameter("Input") input: String
    ): SimpleResult = throw ToolException("something went wrong", null)

    @ToolFunction(name = "all_primitives", description = "Tests all primitive types")
    suspend fun allPrimitivesHandler(
        @ToolParameter("String param") s: String,
        @ToolParameter("Int param") i: Int,
        @ToolParameter("Long param") l: Long,
        @ToolParameter("Float param") f: Float,
        @ToolParameter("Double param") d: Double,
        @ToolParameter("Boolean param") b: Boolean
    ): SimpleResult = SimpleResult("$s $i $l $f $d $b")

    // Invalid handler fixtures

    suspend fun missingToolFunctionAnnotation(
        @ToolParameter("Input") input: String
    ): SimpleResult = SimpleResult(input)

    @ToolFunction(name = "missing_param", description = "Missing @ToolParameter annotation")
    suspend fun missingParamAnnotation(input: String): SimpleResult = SimpleResult(input)

    @ToolFunction(name = "non_serializable", description = "Returns non-serializable type")
    suspend fun nonSerializableReturnType(
        @ToolParameter("Input") input: String
    ): Any = input

    @ToolFunction(name = "nullable_return", description = "Returns nullable type")
    suspend fun nullableReturnType(
        @ToolParameter("Input") input: String
    ): SimpleResult? = SimpleResult(input)

    @ToolFunction(name = "unsupported_param", description = "Has unsupported parameter type")
    suspend fun unsupportedParamType(
        @ToolParameter("Input") input: Map<String, String>
    ): SimpleResult = SimpleResult("x")

    @ToolFunction(name = "bad_allowed_values", description = "AllowedValues on non-string")
    suspend fun allowedValuesOnNonString(
        @ToolParameter("Input") @AllowedValues("1", "2") input: Int
    ): SimpleResult = SimpleResult("x")

    @ToolFunction(name = "empty_allowed_values", description = "Empty AllowedValues")
    suspend fun emptyAllowedValues(
        @ToolParameter("Input") @AllowedValues input: String
    ): SimpleResult = SimpleResult("x")

    fun nonSuspendHandler(
        @ToolParameter("Input") input: String
    ): SimpleResult = SimpleResult(input)

    @ToolFunction(name = "blank_description", description = "")
    suspend fun blankDescriptionHandler(
        @ToolParameter("Input") input: String
    ): SimpleResult = SimpleResult(input)

    @ToolFunction(name = "blank_param_description", description = "Valid description")
    suspend fun blankParamDescriptionHandler(
        @ToolParameter("") input: String
    ): SimpleResult = SimpleResult(input)

    @Test
    fun `builds tool with correct name from annotation`() {
        val tool = tool(::echoHandler)
        assertEquals("echo", tool.name)
    }

    @Test
    fun `falls back to function name when tool name is empty`() {
        val tool = tool(::noNameHandler)
        assertEquals("noNameHandler", tool.name)
    }

    @Test
    fun `builds tool with correct description`() {
        val tool = tool(::echoHandler)
        assertEquals("Echoes a string back", tool.description)
    }

    @Test
    fun `builds correct number of parameters`() {
        val tool = tool(::echoHandler)
        assertEquals(1, tool.parameters.size)
    }

    @Test
    fun `parameter has correct name`() {
        val tool = tool(::echoHandler)
        assertEquals("input", tool.parameters.first().name)
    }

    @Test
    fun `parameter has correct type`() {
        val tool = tool(::echoHandler)
        assertEquals(Parameter.Type.STRING, tool.parameters.first().type)
    }

    @Test
    fun `parameter has correct description`() {
        val tool = tool(::echoHandler)
        assertEquals("The string to echo", tool.parameters.first().description)
    }

    @Test
    fun `parameter is required by default`() {
        val tool = tool(::echoHandler)
        assertTrue(tool.parameters.first().required)
    }

    @Test
    fun `optional parameter is marked not required`() {
        val tool = tool(::optionalParamHandler)
        val greeting = tool.parameters.find { it.name == "greeting" }
        assertNotNull(greeting)
        assertFalse(greeting.required)
    }

    @Test
    fun `all primitive parameter types are mapped correctly`() {
        val tool = tool(::allPrimitivesHandler)
        val types = tool.parameters.associate { it.name to it.type }
        assertEquals(Parameter.Type.STRING, types["s"])
        assertEquals(Parameter.Type.INTEGER, types["i"])
        assertEquals(Parameter.Type.INTEGER, types["l"])
        assertEquals(Parameter.Type.NUMBER, types["f"])
        assertEquals(Parameter.Type.NUMBER, types["d"])
        assertEquals(Parameter.Type.BOOLEAN, types["b"])
    }

    @Test
    fun `list parameter is mapped to ARRAY type`() {
        val tool = tool(::listParamHandler)
        val param = tool.parameters.first()
        assertEquals(Parameter.Type.ARRAY, param.type)
        assertEquals(Parameter.Type.STRING, param.elementType)
    }

    @Test
    fun `allowed values are populated on parameter`() {
        val tool = tool(::allowedValuesHandler)
        assertEquals(listOf("celsius", "fahrenheit"), tool.parameters.first().enum)
    }

    @Test
    fun `fails when missing @ToolFunction annotation`() {
        assertFailsWith<IllegalStateException> {
            tool(::missingToolFunctionAnnotation)
        }
    }

    @Test
    fun `fails when handler is not suspend`() {
        assertFailsWith<IllegalArgumentException> {
            tool(::nonSuspendHandler)
        }
    }

    @Test
    fun `fails when tool description is blank`() {
        assertFailsWith<IllegalArgumentException> {
            tool(::blankDescriptionHandler)
        }
    }

    @Test
    fun `fails when parameter is missing @ToolParameter annotation`() {
        assertFailsWith<IllegalStateException> {
            tool(::missingParamAnnotation)
        }
    }

    @Test
    fun `fails when parameter description is blank`() {
        assertFailsWith<IllegalArgumentException> {
            tool(::blankParamDescriptionHandler)
        }
    }

    @Test
    fun `fails when return type is not serializable`() {
        assertFailsWith<IllegalStateException> {
            tool(::nonSerializableReturnType)
        }
    }

    @Test
    fun `fails when return type is nullable`() {
        assertFailsWith<IllegalArgumentException> {
            tool(::nullableReturnType)
        }
    }

    @Test
    fun `fails when parameter type is unsupported`() {
        assertFailsWith<IllegalStateException> {
            tool(::unsupportedParamType)
        }
    }

    @Test
    fun `fails when @AllowedValues is used on non-string parameter`() {
        assertFailsWith<IllegalArgumentException> {
            tool(::allowedValuesOnNonString)
        }
    }

    @Test
    fun `fails when @AllowedValues has no values`() {
        assertFailsWith<IllegalArgumentException> {
            tool(::emptyAllowedValues)
        }
    }

    // --- Execution tests ---

    @Test
    fun `executes handler and returns Success`() = runTest {
        val tool = tool(::echoHandler)
        val result = tool.execute(mapOf("input" to "hello"))
        assertIs<ToolResult.Success>(result)
    }

    @Test
    fun `success result contains correct tool name`() = runTest {
        val tool = tool(::echoHandler)
        val result = tool.execute(mapOf("input" to "hello")) as ToolResult.Success
        assertEquals("echo", result.toolName)
    }

    @Test
    fun `success result contains correct value`() = runTest {
        val tool = tool(::echoHandler)
        val result = tool.execute(mapOf("input" to "hello")) as ToolResult.Success
        assertEquals(SimpleResult("hello"), result.value)
    }

    @Test
    fun `handler throwing ToolException returns Failure`() = runTest {
        val tool = tool(::throwingHandler)
        val result = tool.execute(mapOf("input" to "x"))
        assertIs<ToolResult.Failure>(result)
    }

    @Test
    fun `failure result contains correct tool name`() = runTest {
        val tool = tool(::throwingHandler)
        val result = tool.execute(mapOf("input" to "x")) as ToolResult.Failure
        assertEquals("throwing_tool", result.toolName)
    }

    @Test
    fun `failure result contains correct error message`() = runTest {
        val tool = tool(::throwingHandler)
        val result = tool.execute(mapOf("input" to "x")) as ToolResult.Failure
        assertEquals("something went wrong", result.error.message)
    }

    @Test
    fun `executes handler with list parameter`() = runTest {
        val tool = tool(::listParamHandler)
        val result = tool.execute(mapOf("tags" to listOf("a", "b", "c"))) as ToolResult.Success
        assertEquals(SimpleResult("a, b, c"), result.value)
    }
}