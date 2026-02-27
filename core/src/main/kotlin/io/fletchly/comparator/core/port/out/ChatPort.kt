package io.fletchly.comparator.core.port.out

import io.fletchly.comparator.core.model.actor.Actor
import io.fletchly.comparator.core.model.message.Message

interface ChatPort {
    suspend fun user(target: Actor, message: Message.User)
    suspend fun assistant(target: Actor, message: Message.Assistant)
}