package io.fletchly.comparator.core.model.tool

import kotlinx.serialization.json.JsonElement

/**
 * Represents the result of a tool's execution.
 *
 * The `ToolResult` interface defines the contract for all possible outcomes of a tool run,
 * including both successful and failed executions. It includes a property for the name of
 * the tool that produced the result and provides two distinct implementations:
 * [ToolResult.Success] and [ToolResult.Failure].
 */
sealed interface ToolResult {
    val toolName: String

    /**
     * Represents the successful result of a tool's execution.
     *
     * This data class is an implementation of the [ToolResult] interface
     * and is used to encapsulate the outcome of a successful execution of a tool.
     * It provides the name of the tool that produced the result and the resulting
     * value produced by the tool in the form of a [JsonElement].
     *
     * @property toolName The name of the tool that generated this result.
     * @property value The result produced by the tool, represented as a [JsonElement].
     */
    data class Success(override val toolName: String, val value: JsonElement) : ToolResult

    /**
     * Represents a failed result of a tool's execution.
     *
     * This data class is an implementation of the [ToolResult] interface and is used to encapsulate
     * information about a failure that occurred during the execution of a tool.
     *
     * @property toolName The name of the tool that generated this failure result.
     * @property message A detailed message describing the reason for the failure.
     */
    data class Failure(override val toolName: String, val message: String) : ToolResult
}