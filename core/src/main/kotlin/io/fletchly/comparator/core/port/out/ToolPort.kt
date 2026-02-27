package io.fletchly.comparator.core.port.out

import io.fletchly.comparator.core.model.message.Message
import io.fletchly.comparator.core.model.message.MessageResult

interface ToolPort {
    suspend fun execute(tool: String): MessageResult<Message.Tool>
}