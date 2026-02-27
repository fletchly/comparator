package io.fletchly.comparator.core.port.out

import io.fletchly.comparator.core.model.message.Conversation
import io.fletchly.comparator.core.model.message.Message
import io.fletchly.comparator.core.model.message.MessageResult

interface AIPort {
    suspend fun generateResponse(conversation: Conversation): MessageResult<Message.Assistant>
}