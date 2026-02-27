package io.fletchly.comparator.core.port.`in`

import io.fletchly.comparator.core.model.actor.Actor

interface ClearContext {
    suspend fun forActor(actor: Actor)
    suspend fun forAll()
}