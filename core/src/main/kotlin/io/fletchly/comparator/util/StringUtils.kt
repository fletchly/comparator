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