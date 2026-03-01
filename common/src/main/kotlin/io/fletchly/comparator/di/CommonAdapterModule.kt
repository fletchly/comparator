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

import io.fletchly.comparator.adapter.ollama.OllamaAIProvider
import io.fletchly.comparator.adapter.persistence.InMemoryContextStore
import io.fletchly.comparator.adapter.tool.web.WebSearchTool
import io.fletchly.comparator.model.tool.ToolDefinition
import io.fletchly.comparator.port.out.AIPort
import io.fletchly.comparator.port.out.ContextPort
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val commonAdapterModule = module {
    singleOf(::OllamaAIProvider) bind AIPort::class
    singleOf(::InMemoryContextStore) bind ContextPort::class
    singleOf(::WebSearchTool) bind ToolDefinition::class
}