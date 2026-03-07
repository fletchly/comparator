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

package io.fletchly.comparator.util

import kotlin.math.abs

/**
 * Returns the singular or plural form of this string based on the given [count].
 *
 * Defaults to appending "s" for the plural form, but an explicit [plural] can
 * be provided for irregular words.
 *
 * @param count The number of items being described. May be negative.
 * @param plural The plural form of this string. Defaults to `"${this}s"`.
 * @return The singular form if [count] is 1 or -1, otherwise the plural form.
 *
 * @sample
 * "apple".pluralize(1)              // "apple"
 * "apple".pluralize(0)              // "apples"
 * "apple".pluralize(-5)             // "apples"
 * "child".pluralize(2, "children") // "children"
 */
fun String.pluralize(count: Int, plural: String = "${this}s"): String {
    return if (abs(count) == 1) this else plural
}