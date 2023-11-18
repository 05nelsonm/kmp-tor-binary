/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.kmp.tor.binary.util.internal

import io.matthewnelson.kmp.tor.binary.util.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.util.Resource

@Suppress("NOTHING_TO_INLINE")
@OptIn(InternalKmpTorBinaryApi::class)
internal inline fun Resource.commonEquals(
    other: Any?
): Boolean = other is Resource && other.alias == alias

@Suppress("NOTHING_TO_INLINE")
@OptIn(InternalKmpTorBinaryApi::class)
internal inline fun Resource.commonHashCode(): Int = 17 * 31 + alias.hashCode()

@OptIn(InternalKmpTorBinaryApi::class)
internal fun Resource.commonToString(
    platformFields: Map<String, Any>,
): String = buildString {
    appendLine("Resource: [")
    appendIndent("alias: ")
    append(alias)
    appendLine()
    appendIndent("isExecutable: ")
    append(isExecutable)
    appendLine()

    platformFields.forEach { entry ->
        appendIndent(entry.key)
        append(": ")
        append(entry.value)
        appendLine()
    }

    append(']')
}

@Suppress("NOTHING_TO_INLINE")
@OptIn(InternalKmpTorBinaryApi::class)
internal inline fun Resource.Config.commonEquals(
    other: Any?
): Boolean = other is Resource.Config && other.resources == resources

@Suppress("NOTHING_TO_INLINE")
@OptIn(InternalKmpTorBinaryApi::class)
internal inline fun Resource.Config.commonHashCode(): Int = 17 * 31 + resources.hashCode()

@OptIn(InternalKmpTorBinaryApi::class)
internal fun Resource.Config.commonToString(): String = buildString {
    appendLine("Resource.Config: [")
    appendIndent("errors: [")

    if (errors.isEmpty()) {
        appendLine(']')
    } else {
        appendLine()

        errors.forEach { error ->
            error.lines().forEach { line ->
                appendIndent(line, "        ")
                appendLine()
            }
        }
        appendIndent(']')
        appendLine()
    }

    appendIndent("resources: [")

    if (resources.isEmpty()) {
        appendLine(']')
    } else {
        appendLine()

        var count = 0
        resources.forEach { resource ->
            resource.toString().lines().let { lines ->
                for (i in 1..<lines.lastIndex) {
                    appendIndent(lines[i])
                    appendLine()
                }
            }

            if (++count < resources.size) {
                appendLine()
            }
        }

        appendIndent(']')
        appendLine()
    }

    append(']')
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun StringBuilder.appendIndent(
    value: Any,
    indent: String = "    "
): StringBuilder = append(indent).append(value)
