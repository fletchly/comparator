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

import io.mockk.every
import io.mockk.mockk
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
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
        bootstrapper.rootModule.verify(
            extraTypes = listOf(JavaPlugin::class)
        )
    }

    @Test
    fun `module graph instantiates all bindings without errors`() {
        koinApplication {
            modules(bootstrapper.rootModule)
            checkModules {
                withInstance(mockPlugin)
            }
        }
    }
}