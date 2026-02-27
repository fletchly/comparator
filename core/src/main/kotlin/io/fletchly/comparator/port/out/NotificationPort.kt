package io.fletchly.comparator.port.out

import io.fletchly.comparator.model.user.User

/**
 * Defines a port for sending notification messages to users.
 */
interface NotificationPort {
    /**
     * Sends an informational notification message to a specified user.
     *
     * This method is used to deliver non-critical, informational messages
     * to a user, such as updates or confirmations of certain operations.
     *
     * @param user The recipient of the notification message.
     * @param message The content of the informational message to be sent.
     */
    suspend fun info(user: User, message: String)

    /**
     * Sends an error notification message to a specified user.
     *
     * This method is used to deliver critical or error-related messages to a user,
     * usually indicating that a system operation has failed or encountered an issue.
     *
     * @param user The recipient of the error notification message.
     * @param message A description of the error or issue being reported.
     */
    suspend fun error(user: User, message: String)
}