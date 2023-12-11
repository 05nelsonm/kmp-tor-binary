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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.matthewnelson.kmp.tor.binary.core

import io.matthewnelson.kmp.tor.binary.core.internal.appendIndent

@InternalKmpTorBinaryApi
public actual class PlatformResource private constructor(
    public val nativeResource: NativeResource,
) {

    @KmpTorBinaryCoreDsl
    @InternalKmpTorBinaryApi
    public actual class Builder internal actual constructor() {

        public var nativeResource: NativeResource? = null

        internal actual fun build(): PlatformResource? {
            val resource = nativeResource ?: return null

            return PlatformResource(resource)
        }
    }

    actual override fun equals(other: Any?): Boolean {
        return  other is PlatformResource
                && other.nativeResource == nativeResource
    }

    actual override fun hashCode(): Int {
        return 17 * 31 + nativeResource.hashCode()
    }

    actual override fun toString(): String = buildString {
        appendLine("PlatformResource: [")
        appendIndent("nativeResource: [")
        appendLine()

        nativeResource.toString().lines().let { lines ->
            for (i in 1..<lines.lastIndex) {
                appendIndent(lines[i])
                appendLine()
            }
            appendIndent(']')
            appendLine()
        }

        append(']')
    }
}
