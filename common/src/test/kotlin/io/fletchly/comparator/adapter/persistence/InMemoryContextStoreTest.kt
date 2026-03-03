/*
 * This file is part of comparator, licensed under the Apache License 2.0
 *
 * Copyright (c) 2026 fletchly <https://github.com/fletchly>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fletchly.comparator.adapter.persistence

import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.conversationOf
import io.fletchly.comparator.model.user.User
import io.fletchly.comparator.model.options.ContextOptions
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class InMemoryContextStoreTest {
    private val user1 = mockk<User> { every { uniqueId } returns UUID.randomUUID() }
    private val user2 = mockk<User> { every { uniqueId } returns UUID.randomUUID() }

    private fun storeOf(limit: Int, expireAfterAccessMinutes: Long = 60) =
        InMemoryContextStore(ContextOptions(limit, expireAfterAccessMinutes))

    @Test
    fun `returns empty conversation for unknown user`() = runTest {
        val store = storeOf(10)

        val result = store.get(user1)

        assertEquals(conversationOf(), result)
    }

    @Test
    fun `returns correct conversation for user`() = runTest {
        val store = storeOf(10)
        val message = Message.User("Hello", user1)
        store.append(user1, message)

        val result = store.get(user1)

        assertEquals(listOf(message), result.messages)
    }

    @Test
    fun `returns independent conversations per user`() = runTest {
        val store = storeOf(10)
        store.append(user1, Message.User("Hello from user1", user1))
        store.append(user2, Message.User("Hello from user2", user1))

        val result1 = store.get(user1)
        val result2 = store.get(user2)

        assertEquals(1, result1.size)
        assertEquals(1, result2.size)
        assertNotEquals(result1, result2)
    }

    @Test
    fun `appends multiple messages in order`() = runTest {
        val store = storeOf(10)
        val messages = listOf(
            Message.User("First", user1),
            Message.Assistant("Second", null),
            Message.User("Third", user1)
        )

        messages.forEach { store.append(user1, it) }

        assertEquals(messages, store.get(user1).messages)
    }

    @Test
    fun `removes oldest message when limit is reached`() = runTest {
        val store = storeOf(3)
        val first = Message.User("First", user1)
        store.append(user1, first)
        store.append(user1, Message.User("Second", user1))
        store.append(user1, Message.User("Third", user1))
        // limit=3, third append triggers trim (size >= 3), so "First" is removed
        assertFalse(store.get(user1).messages.contains(first))
        assertEquals(2, store.get(user1).size)
    }

    @Test
    fun `does not remove messages when under limit`() = runTest {
        val store = storeOf(10)
        store.append(user1, Message.User("First", user1))
        store.append(user1, Message.User("Second", user1))

        assertEquals(2, store.get(user1).size)
    }

    @Test
    fun `clear removes conversation for specified user`() = runTest {
        val store = storeOf(10)
        store.append(user1, Message.User("Hello", user1))

        store.clear(user1)

        assertEquals(conversationOf(), store.get(user1))
    }

    @Test
    fun `clear does not affect other users`() = runTest {
        val store = storeOf(10)
        store.append(user1, Message.User("Hello from user1", user1))
        store.append(user2, Message.User("Hello from user2", user2))

        store.clear(user1)

        assertEquals(conversationOf(), store.get(user1))
        assertEquals(1, store.get(user2).size)
    }

    @Test
    fun `clear on unknown user does not throw`() = runTest {
        val store = storeOf(10)

        store.clear(user1) // should be a no-op
    }

    @Test
    fun `clearAll removes all conversations`() = runTest {
        val store = storeOf(10)
        store.append(user1, Message.User("Hello from user1", user1))
        store.append(user2, Message.User("Hello from user2", user2))

        store.clearAll()

        assertEquals(conversationOf(), store.get(user1))
        assertEquals(conversationOf(), store.get(user2))
    }

    @Test
    fun `handles concurrent appends without data loss`() = runTest {
        val store = storeOf(1000)
        val messageCount = 100

        (1..messageCount)
            .map { i -> launch(Dispatchers.Default) { store.append(user1, Message.User("Message $i", user1)) } }
            .joinAll()

        assertEquals(messageCount, store.get(user1).size)
    }

    @Test
    fun `handles concurrent access from multiple users`() = runTest {
        val store = storeOf(1000)

        listOf(
            launch(Dispatchers.Default) { repeat(50) { store.append(user1, Message.User("user1 message", user1)) } },
            launch(Dispatchers.Default) { repeat(50) { store.append(user2, Message.User("user2 message", user2)) } }
        ).joinAll()

        assertEquals(50, store.get(user1).size)
        assertEquals(50, store.get(user2).size)
    }
}