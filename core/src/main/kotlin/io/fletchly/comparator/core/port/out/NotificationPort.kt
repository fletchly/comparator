package io.fletchly.comparator.core.port.out

import io.fletchly.comparator.core.model.message.Message

interface NotificationPort {
    suspend fun info(string: Message)
    suspend fun error(string: Message)
}