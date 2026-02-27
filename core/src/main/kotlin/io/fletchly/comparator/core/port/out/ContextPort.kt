package io.fletchly.comparator.core.port.out

import io.fletchly.comparator.core.model.actor.Actor
import io.fletchly.comparator.core.model.message.Conversation

interface ContextPort {
    suspend fun get(actor: Actor): Conversation
    suspend fun clear(actor: Actor)
}