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

package io.matthewnelson.kmp.tor.binary.util

import io.matthewnelson.kmp.tor.binary.util.ImmutableSet.Companion.toImmutableSet
import io.matthewnelson.kmp.tor.binary.util.internal.commonEquals
import io.matthewnelson.kmp.tor.binary.util.internal.commonHashCode
import io.matthewnelson.kmp.tor.binary.util.internal.commonToString

@InternalKmpTorBinaryApi
public actual class Resource private constructor(
    public actual val alias: String,
    public actual val isExecutable: Boolean,
    public val moduleName: String,
    public val resourcePath: String,
) {

    @InternalKmpTorBinaryApi
    public actual class Config private actual constructor(
        public actual val errors: Set<String>,
        public actual val resources: Set<Resource>,
    ) {

        @Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
        public actual fun extractTo(destinationDir: String): Map<String, String> {
            // TODO
            return emptyMap()
        }

        @InternalKmpTorBinaryApi
        public actual companion object {

            @Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
            public actual fun create(block: Builder.() -> Unit): Config = Builder().apply(block).build()
        }

        @KmpTorBinaryDsl
        @InternalKmpTorBinaryApi
        public actual class Builder internal actual constructor() {

            private val errors = mutableSetOf<String>()
            private val resources = mutableSetOf<Resource>()

            @KmpTorBinaryDsl
            public actual fun error(
                message: String
            ): Builder {
                if (message.isNotBlank()) {
                    errors.add(message)
                }
                return this
            }

            @KmpTorBinaryDsl
            public actual fun resource(
                alias: String,
                block: Resource.Builder.() -> Unit
            ): Builder {
                if (alias.isBlank()) return this

                val res = Builder(alias).apply(block).build()
                if (res != null) {
                    resources.add(res)
                }
                return this
            }

            internal fun build(): Config = Config(
                errors.toImmutableSet(),
                resources.toImmutableSet(),
            )
        }

        public actual override fun equals(other: Any?): Boolean = commonEquals(other)
        public actual override fun hashCode(): Int = commonHashCode()
        public actual override fun toString(): String = commonToString()
    }

    @KmpTorBinaryDsl
    @InternalKmpTorBinaryApi
    public actual class Builder internal actual constructor(
        public actual val alias: String
    ) {

        public actual var isExecutable: Boolean = false

        public var moduleName: String = ""

        public var resourcePath: String = ""

        internal fun build(): Resource? {
            val module = moduleName.ifBlank { return null }
            val path = resourcePath.ifBlank { return null }

            return Resource(
                alias,
                isExecutable,
                module,
                path,
            )
        }
    }

    public actual override fun equals(other: Any?): Boolean = commonEquals(other)
    public actual override fun hashCode(): Int = commonHashCode()
    public actual override fun toString(): String = commonToString(buildMap(2) {
        put("moduleName", moduleName)
        put("resourcePath", resourcePath)
    })
}
