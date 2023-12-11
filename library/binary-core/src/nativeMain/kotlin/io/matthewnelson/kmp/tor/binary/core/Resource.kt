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

import io.matthewnelson.kmp.file.*
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
    public val nativeResource: NativeResource,
) {

    @InternalKmpTorBinaryApi
    public actual class Config private actual constructor(
        public actual val errors: ImmutableSet<String>,
        public actual val resources: ImmutableSet<Resource>,
    ) {

        @Throws(IllegalStateException::class, IOException::class)
        public actual fun extractTo(destinationDir: File): ImmutableMap<String, File> {
            // Check if any errors have been set for this config and
            // throw them if that is the case before extracting anything.
            throwIfError()

            val dir = destinationDir.canonicalFile()

            if (!dir.exists() && !dir.mkdirs()) {
                throw IOException("Failed to create destinationDir[$dir]")
            }

            val map = LinkedHashMap<String, File>(resources.size, 1.0f)

            try {
                resources.forEach { resource ->
                    val file = resource.extractTo(dir)
                    map[resource.alias] = file
                    @OptIn(DelicateFileApi::class)
                    file.chmod(if (resource.isExecutable) "500" else "400")
                }
            } catch (t: Throwable) {
                map.forEach { entry ->
                    try {
                        entry.value.delete()
                    } catch (_: Throwable) {}
                }

                throw t.wrapIOException { "Failed to extract resources to destinationDir[$dir]" }
            }

            return map.toImmutableMap()
        }

        private fun Resource.extractTo(destinationDir: File): File {
            var fileName = nativeResource.name
            val isGzipped = if (fileName.endsWith(".gz")) {
                fileName = fileName.substringBeforeLast(".gz")
                true
            } else {
                false
            }

            val destination = destinationDir.resolve(fileName)

            if (destination.exists() && !destination.delete()) {
                throw IOException("Failed to delete $destination")
            }

            TODO("""
                outputStream(destination).use { oStream ->
                  val out = if (isGzipped) {
                      gunzipStream { buf, len ->
                          oStream.write(buf, 0, len)
                      }
                  } else {
                      oStream
                  }
                  //
                  nativeResource.read { buffer, len ->
                      out.write(buffer, 0, len)
                  }
                }
            """.trimIndent())


            return destination
        }

        @InternalKmpTorBinaryApi
        public companion object {

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

        public var nativeResource: NativeResource? = null

        internal fun build(): Resource? {
            val resource = nativeResource ?: return null

            return Resource(
                alias,
                isExecutable,
                resource,
            )
        }
    }

    public actual override fun equals(other: Any?): Boolean = commonEquals(other)
    public actual override fun hashCode(): Int = commonHashCode()
    public actual override fun toString(): String = commonToString(buildMap(1) {
        put("nativeResource", buildString {
            appendLine('[')

            nativeResource.toString().lines().let { lines ->
                for (i in 1..<lines.lastIndex) {
                    appendIndent(lines[i])
                    appendLine()
                }

                appendIndent(']')
            }
        })
    })
}
