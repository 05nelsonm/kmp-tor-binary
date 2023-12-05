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

import io.matthewnelson.kmp.tor.binary.core.ImmutableMap.Companion.toImmutableMap
import io.matthewnelson.kmp.tor.binary.core.ImmutableSet.Companion.toImmutableSet
import io.matthewnelson.kmp.tor.binary.core.internal.*
import io.matthewnelson.kmp.tor.binary.core.internal.commonEquals
import io.matthewnelson.kmp.tor.binary.core.internal.commonHashCode
import io.matthewnelson.kmp.tor.binary.core.internal.commonToString
import io.matthewnelson.kmp.tor.binary.core.internal.throwIfError

@InternalKmpTorBinaryApi
public actual class Resource private constructor(
    public actual val alias: String,
    public actual val isExecutable: Boolean,
    public val moduleName: String,
    public val resourcePath: String,
) {

    @InternalKmpTorBinaryApi
    public actual class Config private actual constructor(
        public actual val errors: ImmutableSet<String>,
        public actual val resources: ImmutableSet<Resource>,
    ) {

        @Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
        public actual fun extractTo(destinationDir: String): ImmutableMap<String, String> {
            // Check if any errors have been set for this config and
            // throw them if that is the case before extracting anything.
            throwIfError()

            val dir = fs_canonicalize(destinationDir)

            if (!fs_exists(dir) && !fs_mkdirs(dir)) {
                throw RuntimeException("Failed to create destinationDir[$dir]")
            }

            val map = LinkedHashMap<String, String>(resources.size, 1.0f)

            try {
                resources.forEach { resource ->
                    val file = resource.extractTo(dir)
                    map[resource.alias] = file
                    if (resource.isExecutable) {
                        // These are the same executable permissions
                        // that are set for jvm upon resource file
                        // extraction.
                        fs_chmod(file, "764")
                    }
                }
            } catch (e: Exception) {
                map.forEach { entry ->
                    try {
                        fs_rm(entry.value)
                    } catch (_: Throwable) {}
                }
                throw e
            }

            return map.toImmutableMap()
        }

        private fun Resource.extractTo(destinationDir: String): String {
            var fileName = resourcePath.substringAfterLast('/')
            val isGzipped = if (fileName.endsWith(".gz")) {
                fileName = fileName.substringBeforeLast(".gz")
                true
            } else {
                false
            }

            val destination = path_join(destinationDir, fileName)
            val moduleResource = resolveResource(moduleName + resourcePath)

            if (fs_exists(destination) && !fs_rm(destination)) {
                throw IllegalStateException("Failed to delete $destination")
            }

            var buffer = fs_readFileSync(moduleResource)

            if (isGzipped) {
                buffer = zlib_gunzipSync(buffer)
            }

            fs_writeFileSync(destination, buffer)

            return destination
        }

        @InternalKmpTorBinaryApi
        public companion object {

            @Suppress("UNUSED_PARAMETER")
            private fun resolveResource(path: String): String = js("require.resolve(path)") as String

            @KmpTorBinaryCoreDsl
            public fun create(block: Builder.() -> Unit): Config = Builder().apply(block).build()
        }

        @KmpTorBinaryCoreDsl
        @InternalKmpTorBinaryApi
        public actual class Builder internal actual constructor() {

            private val errors = mutableSetOf<String>()
            private val resources = mutableSetOf<Resource>()

            @KmpTorBinaryCoreDsl
            public actual fun error(
                message: String
            ): Builder {
                if (message.isNotBlank()) {
                    errors.add(message)
                }
                return this
            }

            @KmpTorBinaryCoreDsl
            public actual fun resource(
                alias: String,
                require: Boolean,
                block: Resource.Builder.() -> Unit
            ): Builder {
                if (alias.isBlank()) return this

                val resource = Builder(alias).apply(block).build()
                if (resource != null) {
                    resources.add(resource)
                } else if (require) {
                    error("Resource[$alias] was malconfigured")
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

    @KmpTorBinaryCoreDsl
    @InternalKmpTorBinaryApi
    public actual class Builder internal actual constructor(
        public actual val alias: String
    ) {

        public actual var isExecutable: Boolean = false

        public var moduleName: String = ""

        public var resourcePath: String = ""

        internal fun build(): Resource? {
            val module = moduleName.ifBlank { return null }
            var path = resourcePath.ifBlank { return null }

            if (path.first() != '/') {
                path = "/$path"
            }

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
