package io.fletchly.comparator.core.port.`in`

import io.fletchly.comparator.core.model.actor.Actor
import io.fletchly.comparator.core.model.message.Message

interface SendMessage {
    suspend fun fromUser(sender: Actor, message: Message.User)
}