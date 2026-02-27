package io.fletchly.comparator.manager

import io.fletchly.comparator.model.user.User
import io.fletchly.comparator.port.`in`.ClearContext
import io.fletchly.comparator.port.out.ContextPort
import io.fletchly.comparator.port.out.NotificationPort
import io.fletchly.comparator.util.pluralize

/**
 * Manages the context and notification operations for a given set of actors in a conversational system.
 *
 * This class provides functionality to clear context information for one or multiple actors
 * and to notify the sender of any successful context clear operations. It interacts with the
 * context storage and notification mechanisms through the provided `ContextPort` and `NotificationPort` interfaces.
 *
 * @constructor Creates a `ContextManager` with a specified context storage and notification mechanism.
 *
 * @param context The storage mechanism for managing conversation contexts.
 * @param notification The notification mechanism for sending updates to actors.
 */
class ContextManager(
    private val context: ContextPort,
    private val notification: NotificationPort
) : ClearContext {
    override suspend fun clear(vararg target: User) {
        target.forEach { context.clear(it) }
    }

    override suspend fun clearWithFeedback(
        sender: User,
        vararg target: User
    ) {
        clear(*target)
        notification.info(sender, "Cleared context for ${target.size} ${"player".pluralize(target.size)}")
    }

    override suspend fun clearAll() {
        context.clearAll()
    }
}