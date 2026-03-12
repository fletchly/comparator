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

package io.fletchly.comparator.di

import io.fletchly.comparator.manager.ContextLifecycleManager
import io.fletchly.comparator.manager.ConversationManager
import io.fletchly.comparator.manager.ToolManager
import io.fletchly.comparator.manager.PanelManager
import io.fletchly.comparator.port.`in`.ContextLifecycle
import io.fletchly.comparator.port.`in`.MessageSender
import io.fletchly.comparator.port.`in`.ToolExecutor
import io.fletchly.comparator.port.`in`.ToolRegistryLifecycle
import io.fletchly.comparator.port.`in`.PanelData
import io.fletchly.comparator.port.`in`.PanelLifecycle
import io.fletchly.comparator.tool.ToolRegistry
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

val coreModule = module {
    singleOf(::ToolManager) binds arrayOf(ToolExecutor::class, ToolRegistryLifecycle::class, ToolRegistry::class)
    singleOf(::ContextLifecycleManager) bind ContextLifecycle::class
    singleOf(::ConversationManager) bind MessageSender::class
    singleOf(::PanelManager) binds arrayOf(PanelLifecycle::class, PanelData::class)
}