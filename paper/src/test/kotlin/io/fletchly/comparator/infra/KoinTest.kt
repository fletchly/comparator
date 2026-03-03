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

package io.fletchly.comparator.infra

import io.fletchly.comparator.adapter.config.PluginConfigService
import io.fletchly.comparator.infra.KoinBootstrapper
import io.fletchly.comparator.model.tool.ToolDefinition
import io.fletchly.comparator.util.HttpClients
import io.mockk.every
import io.mockk.mockk
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.verify.verify
import kotlin.test.AfterTest
import kotlin.test.Test

class KoinBootstrapperTest : KoinTest {

    private val mockPlugin = mockk<JavaPlugin>(relaxed = true) {
        every { logger } returns mockk(relaxed = true)
    }
    private val bootstrapper = KoinBootstrapper(mockPlugin)

    @AfterTest
    fun tearDown() = bootstrapper.stop()

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `start() resolves module graph without errors`() {
        bootstrapper.modules.verify(
            extraTypes = listOf(JavaPlugin::class)
        )
    }

    @Test
    fun `loadToolModules() loads both tools when both enabled`() {
        val koin = bootstrapper.start()

        val mockConfig = mockk<PluginConfigService> {
            every { config.tool.gameVersion.enabled } returns true
            every { config.tool.webSearch.enabled } returns true
            every { config.tool.webSearch.apiKey } returns "test-api-key"
        }

        loadKoinModules(module { single<PluginConfigService> { mockConfig } })

        bootstrapper.loadToolModules(koin)

        assertEquals(2, koin.getAll<ToolDefinition>().size)
    }

    @Test
    fun `loadToolModules() loads no tools when both disabled`() {
        val koin = bootstrapper.start()

        val mockConfig = mockk<PluginConfigService> {
            every { config.tool.gameVersion.enabled } returns false
            every { config.tool.webSearch.enabled } returns false
        }

        loadKoinModules(module { single<PluginConfigService> { mockConfig } })

        bootstrapper.loadToolModules(koin)

        assertTrue(koin.getAll<ToolDefinition>().isEmpty())
    }
}