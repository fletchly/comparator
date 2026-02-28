package io.fletchly.comparator.port.`in`

import io.fletchly.comparator.model.tool.Tool

/**
 * Defines a registry for managing a collection of tools within the system.
 *
 * This interface provides access to a list of tools that can be executed within
 * the system. The tools encapsulate specific functionality and are designed to
 * handle various tasks as part of the system's operations.
 *
 * @property tools A list of tools available in the registry. Each tool contains
 *                 its definition and execution logic.
 */
interface ToolRegistry {
    val tools: List<Tool>
}