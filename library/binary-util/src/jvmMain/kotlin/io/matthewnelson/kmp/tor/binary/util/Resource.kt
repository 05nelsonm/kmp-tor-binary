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

import io.matthewnelson.kmp.tor.binary.util.ImmutableMap.Companion.toImmutableMap
import io.matthewnelson.kmp.tor.binary.util.ImmutableSet.Companion.toImmutableSet
import io.matthewnelson.kmp.tor.binary.util.internal.throwIfError
import io.matthewnelson.kmp.tor.binary.util.internal.commonEquals
import io.matthewnelson.kmp.tor.binary.util.internal.commonHashCode
import io.matthewnelson.kmp.tor.binary.util.internal.commonToString
import java.io.File
import java.io.IOException
import java.util.zip.GZIPInputStream

@InternalKmpTorBinaryApi
public actual class Resource private constructor(
    @JvmField
    public actual val alias: String,
    @JvmField
    public actual val isExecutable: Boolean,
    @JvmField
    public val resourceClass: Class<*>,
    @JvmField
    public val resourcePath: String,
) {

    @InternalKmpTorBinaryApi
    public actual class Config private actual constructor(
        @JvmField
        public actual val errors: Set<String>,
        @JvmField
        public actual val resources: Set<Resource>,
    ) {

        @Throws(Exception::class)
        public actual fun extractTo(destinationDir: String): ImmutableMap<String, String> {
            // Check if any errors have been set for this config and
            // throw them if that is the case before extracting anything.
            throwIfError()

            val dir = File(destinationDir).canonicalFile

            if (!dir.exists() && !dir.mkdirs()) {
                throw IOException("Failed to create destinationDir[$dir]")
            }

            val map = LinkedHashMap<String, String>(resources.size, 1.0f)

            try {
                resources.forEach { resource ->
                    val file = resource.extractTo(dir)
                    map[resource.alias] = file.absolutePath
                    if (resource.isExecutable) {
                        file.setExecutable(true)
                    }
                }
            } catch (e: Exception) {
                map.forEach { entry ->
                    try {
                        File(entry.value).delete()
                    } catch (_: Throwable) {}
                }
                throw e
            }

            return map.toImmutableMap()
        }

        private fun Resource.extractTo(destinationDir: File): File {
            var fileName = resourcePath.substringAfterLast('/')
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

            var resourceStream = resourceClass.getResourceAsStream(resourcePath)
                ?: throw IOException("Failed to get resource input stream for $resourcePath")

            if (isGzipped) {
                resourceStream = GZIPInputStream(resourceStream)
            }

            resourceStream.use { iStream ->
                destination.outputStream().use { oStream ->
                    val buf = ByteArray(4096)

                    while (true) {
                        val read = iStream.read(buf)
                        if (read == -1) break
                        oStream.write(buf, 0, read)
                    }
                }
            }

            return destination
        }

        @InternalKmpTorBinaryApi
        public actual companion object {

            @JvmStatic
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

                val resource = Builder(alias).apply(block).build()
                if (resource != null) {
                    resources.add(resource)
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
        @JvmField
        public actual val alias: String
    ) {

        @JvmField
        public actual var isExecutable: Boolean = false

        @JvmField
        public var resourceClass: Class<*>? = null

        @JvmField
        public var resourcePath: String = ""


        internal fun build(): Resource? {
            val clazz = resourceClass ?: return null
            var path = resourcePath.ifBlank { return null }

            if (path.first() != '/') {
                path = "/$path"
            }

            return Resource(
                alias,
                isExecutable,
                clazz,
                path,
            )
        }
    }

    public actual override fun equals(other: Any?): Boolean = commonEquals(other)
    public actual override fun hashCode(): Int = commonHashCode()
    public actual override fun toString(): String = commonToString(buildMap(2) {
        put("resourceClass", resourceClass)
        put("resourcePath", resourcePath)
    })
}
