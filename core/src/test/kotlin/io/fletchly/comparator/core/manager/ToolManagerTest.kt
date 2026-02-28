package io.fletchly.comparator.core.manager

import io.fletchly.comparator.manager.ToolManager
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.model.tool.Tool
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
            ToolManager(listOf(toolWithName("duplicate"), toolWithName("duplicate")))
        }
    }

    @Test
    fun `throws with duplicate names listed in message`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            ToolManager(listOf(toolWithName("dup"), toolWithName("dup")))
        }
        assertContains(ex.message ?: "", "dup")
    }

    @Test
    fun `accepts tools with unique names`() {
        assertDoesNotThrow {
            ToolManager(listOf(toolWithName("a"), toolWithName("b")))
        }
    }

    @Test
    fun `returns tool not found message when tool does not exist`() = runTest {
        val manager = ToolManager(emptyList())
        val result = manager.execute(toolCall("unknown"))
        assertEquals("tool not found: unknown", result.content)
    }

    @Test
    fun `delegates execution to the correct tool`() = runTest {
        val tool = toolWithName("my-tool")
        val manager = ToolManager(listOf(tool))
        val call = toolCall("my-tool", mapOf("x" to "hello"))

        manager.execute(call)

        coVerify { tool.execute(mapOf("x" to "hello")) }
    }

    @Test
    fun `returns tool result as message on success`() = runTest {
        val tool = toolWithName("my-tool", ToolResult.Success("my-tool", JsonPrimitive("result")))
        val manager = ToolManager(listOf(tool))

        val message = manager.execute(toolCall("my-tool"))

        assertEquals(ToolResult.Success("my-tool", JsonPrimitive("result")).toString(), message.content)
    }

    @Test
    fun `returns tool result as message on failure`() = runTest {
        val tool = toolWithName("my-tool", ToolResult.Failure("my-tool", "something went wrong"))
        val manager = ToolManager(listOf(tool))

        val message = manager.execute(toolCall("my-tool"))

        assertEquals(ToolResult.Failure("my-tool", "something went wrong").toString(), message.content)
    }

    @Test
    fun `does not execute other tools when one is called`() = runTest {
        val target = toolWithName("target")
        val other = toolWithName("other")
        val manager = ToolManager(listOf(target, other))

        manager.execute(toolCall("target"))

        coVerify(exactly = 0) { other.execute(any()) }
    }
}