package io.fletchly.comparator.core.port.out

/**
 * Represents a port for retrieving system-level contextual information.
 */
interface SystemInfoPort {
    /**
     * Retrieves a system-level prompt used for initializing or guiding the behavior
     * of a conversational system or assistant.
     *
     * @return A string representing the system-level prompt.
     */
    suspend fun getPrompt(): String
}