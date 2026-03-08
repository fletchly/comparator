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

import io.fletchly.comparator.model.actor.Actor
import io.fletchly.comparator.model.message.ConversationKey
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.conversationOf
import io.fletchly.comparator.model.options.ContextOptions
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class CaffeineContextStoreTest {
    private val actor1 = mockk<Actor> { every { conversationKey.uniqueId } returns UUID.randomUUID() }
    private val actor2 = mockk<Actor> { every { conversationKey.uniqueId } returns UUID.randomUUID() }

    private fun storeOf(limit: Int, expireAfterAccessMinutes: Long = 60) =
        CaffeineContextStore(ContextOptions(limit, expireAfterAccessMinutes))

    @Test
    fun `returns empty conversation for unknown user`() = runTest {
        val store = storeOf(10)

        val result = store.get(actor1.conversationKey)

        assertEquals(conversationOf(), result)
    }

    @Test
    fun `returns correct conversation for user`() = runTest {
        val store = storeOf(10)
        val message = Message.User("Hello", actor1)
        store.append(actor1.conversationKey, message)

        val result = store.get(actor1.conversationKey)

        assertEquals(listOf(message), result.messages)
    }

    @Test
    fun `returns independent conversations per user`() = runTest {
        val store = storeOf(10)
        store.append(actor1.conversationKey, Message.User("Hello from scope1", actor1))
        store.append(actor2.conversationKey, Message.User("Hello from actor2", actor1))

        val result1 = store.get(actor1.conversationKey)
        val result2 = store.get(actor2.conversationKey)

        assertEquals(1, result1.size)
        assertEquals(1, result2.size)
        assertNotEquals(result1, result2)
    }

    @Test
    fun `appends multiple messages in order`() = runTest {
        val store = storeOf(10)
        val messages = listOf(
            Message.User("First", actor1),
            Message.Assistant("Second", null),
            Message.User("Third", actor1)
        )

        messages.forEach { store.append(actor1.conversationKey, it) }

        assertEquals(messages, store.get(actor1.conversationKey).messages)
    }

    @Test
    fun `removes oldest message when limit is reached`() = runTest {
        val store = storeOf(3)
        val first = Message.User("First", actor1)
        store.append(actor1.conversationKey, first)
        store.append(actor1.conversationKey, Message.User("Second", actor1))
        store.append(actor1.conversationKey, Message.User("Third", actor1))
        // limit=3, third append triggers trim (size >= 3), so "First" is removed
        assertFalse(store.get(actor1.conversationKey).messages.contains(first))
        assertEquals(2, store.get(actor1.conversationKey).size)
    }

    @Test
    fun `does not remove messages when under limit`() = runTest {
        val store = storeOf(10)
        store.append(actor1.conversationKey, Message.User("First", actor1))
        store.append(actor1.conversationKey, Message.User("Second", actor1))

        assertEquals(2, store.get(actor1.conversationKey).size)
    }

    @Test
    fun `clear removes conversation for specified user`() = runTest {
        val store = storeOf(10)
        store.append(actor1.conversationKey, Message.User("Hello", actor1))

        store.clear(actor1.conversationKey)

        assertEquals(conversationOf(), store.get(actor1.conversationKey))
    }

    @Test
    fun `clear does not affect other users`() = runTest {
        val store = storeOf(10)
        store.append(actor1.conversationKey, Message.User("Hello from scope1", actor1))
        store.append(actor2.conversationKey, Message.User("Hello from actor2", actor2))

        store.clear(actor1.conversationKey)

        assertEquals(conversationOf(), store.get(actor1.conversationKey))
        assertEquals(1, store.get(actor2.conversationKey).size)
    }

    @Test
    fun `clear on unknown user does not throw`() = runTest {
        val store = storeOf(10)

        store.clear(actor1.conversationKey) // should be a no-op
    }

    @Test
    fun `clearAll removes all conversations`() = runTest {
        val store = storeOf(10)
        store.append(actor1.conversationKey, Message.User("Hello from scope1", actor1))
        store.append(actor2.conversationKey, Message.User("Hello from actor2", actor2))

        store.clearAll()

        assertEquals(conversationOf(), store.get(actor1.conversationKey))
        assertEquals(conversationOf(), store.get(actor2.conversationKey))
    }

    @Test
    fun `handles concurrent appends without data loss`() = runTest {
        val store = storeOf(1000)
        val messageCount = 100

        (1..messageCount)
            .map { i -> launch(Dispatchers.Default) { store.append(actor1.conversationKey, Message.User("Message $i", actor1)) } }
            .joinAll()

        assertEquals(messageCount, store.get(actor1.conversationKey).size)
    }

    @Test
    fun `handles concurrent access from multiple users`() = runTest {
        val store = storeOf(1000)

        listOf(
            launch(Dispatchers.Default) { repeat(50) { store.append(actor1.conversationKey, Message.User("scope1 message", actor1)) } },
            launch(Dispatchers.Default) { repeat(50) { store.append(actor2.conversationKey, Message.User("actor2 message", actor2)) } }
        ).joinAll()

        assertEquals(50, store.get(actor1.conversationKey).size)
        assertEquals(50, store.get(actor2.conversationKey).size)
    }
}