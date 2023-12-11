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

@InternalKmpTorBinaryApi
public actual class PlatformResource private constructor(
    @JvmField
    public val resourceClass: Class<*>,
    @JvmField
    public val resourcePath: String,
) {

    @KmpTorBinaryCoreDsl
    @InternalKmpTorBinaryApi
    public actual class Builder internal actual constructor() {

        @JvmField
        public var resourceClass: Class<*>? = null

        @JvmField
        public var resourcePath: String? = null

        @JvmSynthetic
        internal actual fun build(): PlatformResource? {
            val clazz = resourceClass ?: return null
            var path = resourcePath ?: return null

            if (!path.startsWith('/')) {
                path = "/$path"
            }

            return PlatformResource(clazz, path)
        }
    }

    actual override fun equals(other: Any?): Boolean {
        return  other is PlatformResource
                && other.resourceClass == resourceClass
                && other.resourcePath == resourcePath
    }

    actual override fun hashCode(): Int {
        var result = 17
        result = result * 31 + resourceClass.hashCode()
        result = result * 31 + resourcePath.hashCode()
        return result
    }

    actual override fun toString(): String = buildString {
        appendLine("PlatformResource: [")
        append("    resourceClass: ")
        appendLine(resourceClass)
        append("    resourcePath: ")
        appendLine(resourcePath)
        append(']')
    }
}
