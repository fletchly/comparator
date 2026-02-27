package io.fletchly.comparator.core.model.user

import java.util.*

/**
 * Represents a user within the system with basic identifying information and operational privileges.
 *
 * This interface is primarily used to define users that interact with the system in various contexts,
 * such as sending messages, receiving notifications, and participating in conversations.
 *
 * @property displayName The name to be displayed for the user.
 * @property uniqueId A universally unique identifier (UUID) representing the user.
 * @property isOp A flag indicating whether the user has operator (admin) privileges.
 */
interface User {
    val displayName: String
    val uniqueId: UUID
    val isOp: Boolean
}