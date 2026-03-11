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

package io.fletchly.comparator.manager

import io.fletchly.comparator.model.actor.Actor
import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.ConversationKey
import io.fletchly.comparator.model.tool.Tool
import io.fletchly.comparator.model.web.WebPanelMessage
import io.fletchly.comparator.port.`in`.WebPanelData
import io.fletchly.comparator.port.`in`.WebPanelLifecycle
import io.fletchly.comparator.port.out.ContextPort
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.port.out.NotificationPort
import io.fletchly.comparator.port.out.VersionPort
import io.fletchly.comparator.port.out.WebPanelPort
import io.fletchly.comparator.tool.ToolRegistry

class WebPanelManager(
    private val webPanel: WebPanelPort,
    private val context: ContextPort,
    private val version: VersionPort,
    private val toolRegistry: ToolRegistry,
    private val notification: NotificationPort,
    private val log: LogPort
) : WebPanelLifecycle, WebPanelData {
    override suspend fun start(requestor: Actor) {
        val message = webPanel.start()
        displayPanelMessage(requestor, message, true)
    }

    override suspend fun stop(requestor: Actor) {
        val message = webPanel.stop(GRACE_MS, TIMEOUT_MS)
        displayPanelMessage(requestor, message, true)
    }

    override suspend fun status(requestor: Actor) {
        val message = webPanel.status()
        displayPanelMessage(requestor, message)
    }

    override suspend fun restart(requestor: Actor) {
        webPanel.restart(onStop = { msg ->
            displayPanelMessage(requestor, msg, true)
        }, onStart = { msg ->
            displayPanelMessage(requestor, msg, true)
        })
    }

    override suspend fun forceStop() {
        webPanel.forceStop()
    }

    private suspend fun displayPanelMessage(requestor: Actor, panelMessage: WebPanelMessage, printLog: Boolean = false) {
        when (panelMessage) {
            is WebPanelMessage.Ok -> {
                notification.info(requestor, panelMessage.message )
                if (printLog) log.info(panelMessage.message, this::class.simpleName)
            }
            is WebPanelMessage.Error -> {
                notification.error(requestor, panelMessage.message)
                if (printLog) log.info(panelMessage.message, this::class.simpleName)
            }
        }
    }

    override suspend fun getAllConversations(): Map<ConversationKey, Conversation> = context.getAll()

    override suspend fun getConversation(key: ConversationKey): Conversation = context.get(key)

    override suspend fun clearAllConversations() {
        context.clearAll()
        log.info("Cleared all conversations", this::class.simpleName)
    }

    override suspend fun clearConversation(key: ConversationKey) {
        context.clear(key)
        log.info("Cleared conversation ${key.uniqueId}", this::class.simpleName)
    }

    override suspend fun getAllTools(): List<Tool> = toolRegistry.getTools()

    override suspend fun getTool(name: String): Tool? = toolRegistry.getTool(name)
    override suspend fun getVersion(): String = version.getVersion()

    private companion object {
        const val GRACE_MS = 1000L
        const val TIMEOUT_MS = 1000L
    }
}