package io.fletchly.comparator.core.port.`in`

import io.fletchly.comparator.core.model.actor.Actor

interface ShowInfo {
    suspend fun forActor(actor: Actor)
}