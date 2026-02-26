package io.fletchly.comparator.core.model.actor

import java.util.UUID

/**
 * Represents an entity that can interact with the assistant
 */
interface Actor {
    val displayName: String
    val uniqueId: UUID
    val isOp: Boolean
}