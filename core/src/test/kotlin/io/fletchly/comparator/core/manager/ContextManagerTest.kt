package io.fletchly.comparator.core.manager

import io.fletchly.comparator.manager.ContextManager
import io.fletchly.comparator.model.user.User
import io.fletchly.comparator.port.out.ContextPort
import io.fletchly.comparator.port.out.NotificationPort
import io.mockk.called
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ContextManagerTest {
    private val context = mockk<ContextPort>(relaxed = true)
    private val notification = mockk<NotificationPort>(relaxed = true)
    private val manager = ContextManager(context, notification)

    @Test
    fun `clear calls context clear for each target`() = runTest {
        val users = arrayOf(mockk<User>(), mockk<User>())
        manager.clear(*users)
        users.forEach { coVerify { context.clear(it) } }
    }

    @Test
    fun `clear does not send notification`() = runTest {
        manager.clear(mockk<User>())
        verify { notification wasNot called }
    }

    @Test
    fun `clearWithFeedback sends a singular notification for one target`() = runTest {
        val sender = mockk<User>()
        manager.clearWithFeedback(sender, mockk<User>())
        coVerify { notification.info(sender, "Cleared context for 1 player") }
    }

    @Test
    fun `clearWithFeedback sends a plural notification for multiple targets`() = runTest {
        val sender = mockk<User>()
        manager.clearWithFeedback(sender, mockk<User>(), mockk<User>(), mockk<User>())
        coVerify { notification.info(sender, "Cleared context for 3 players") }
    }

    @Test
    fun `clearAll delegates to context`() = runTest {
        manager.clearAll()
        coVerify { context.clearAll() }
    }
}