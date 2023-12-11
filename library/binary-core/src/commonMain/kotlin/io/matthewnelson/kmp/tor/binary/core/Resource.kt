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

import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.IOException
import io.matthewnelson.kmp.file.canonicalFile
import io.matthewnelson.kmp.file.wrapIOException
import io.matthewnelson.kmp.tor.binary.core.ImmutableMap.Companion.toImmutableMap
import io.matthewnelson.kmp.tor.binary.core.ImmutableSet.Companion.toImmutableSet
import io.matthewnelson.kmp.tor.binary.core.internal.appendIndent
import io.matthewnelson.kmp.tor.binary.core.internal.extractTo
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

@InternalKmpTorBinaryApi
public class Resource private constructor(
    @JvmField
    public val alias: String,
    @JvmField
    public val isExecutable: Boolean,
    @JvmField
    public val platform: PlatformResource,
) {

    @InternalKmpTorBinaryApi
    public class Config private constructor(
        @JvmField
        public val errors: ImmutableSet<String>,
        @JvmField
        public val resources: ImmutableSet<Resource>,
    ) {

        @InternalKmpTorBinaryApi
        public companion object {

            @JvmStatic
            @KmpTorBinaryCoreDsl
            public fun create(
                block: Builder.() -> Unit
            ): Config = Builder().apply(block).build()
        }

        @Throws(IllegalStateException::class, IOException::class)
        public fun extractTo(destinationDir: File): ImmutableMap<String, File> {
            check(errors.isEmpty()) {
                buildString {
                    errors.forEach { error ->
                        appendLine(error)
                    }
                }
            }

            val dir = destinationDir.canonicalFile()

            if (!dir.exists() && !dir.mkdirs()) {
                throw IOException("Failed to create destinationDir[$dir]")
            }

            val map = LinkedHashMap<String, File>(resources.size, 1.0f)

            try {
                resources.forEach { resource ->
                    val file = resource.extractTo(dir)
                    map[resource.alias] = file
                }
            } catch (t: Throwable) {
                map.forEach { entry ->
                    try {
                        entry.value.delete()
                    } catch (_: IOException) {}
                }

                throw t.wrapIOException { "Failed to extract resources to destinationDir[$dir]" }
            }

            return map.toImmutableMap()
        }

        @KmpTorBinaryCoreDsl
        @InternalKmpTorBinaryApi
        public class Builder internal constructor() {

            private val errors = mutableSetOf<String>()
            private val resources = mutableSetOf<Resource>()

            @KmpTorBinaryCoreDsl
            public fun error(
                message: String
            ): Builder {
                if (message.isNotBlank()) {
                    errors.add(message)
                }
                return this
            }

            @KmpTorBinaryCoreDsl
            public fun resource(
                alias: String,
                require: Boolean = true,
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

            @JvmSynthetic
            internal fun build(): Config = Config(
                errors.toImmutableSet(),
                resources.toImmutableSet(),
            )
        }

        public override fun equals(other: Any?): Boolean {
            return  other is Config
                    && other.errors == errors
                    && other.resources == resources
        }

        public override fun hashCode(): Int {
            var result = 17
            result = result * 31 + errors.hashCode()
            result = result * 31 + resources.hashCode()
            return result
        }

        public override fun toString(): String = buildString {
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
    }

    @KmpTorBinaryCoreDsl
    @InternalKmpTorBinaryApi
    public class Builder internal constructor(
        @JvmField
        public val alias: String
    ) {

        @JvmField
        public var isExecutable: Boolean = false

        private var platform: PlatformResource? = null

        @KmpTorBinaryCoreDsl
        public fun platform(
            block: PlatformResource.Builder.() -> Unit
        ): Builder {
            platform = PlatformResource.Builder().apply(block).build()
            return this
        }

        @JvmSynthetic
        internal fun build(): Resource? {
            val p = platform ?: return null

            return Resource(alias, isExecutable, p)
        }
    }

    public override fun equals(other: Any?): Boolean = other is Resource && other.alias == alias
    public override fun hashCode(): Int = 17 * 31 + alias.hashCode()
    public override fun toString(): String = buildString {
        appendLine("Resource: [")
        appendIndent("alias: ")
        appendLine(alias)
        appendIndent("isExecutable: ")
        appendLine(isExecutable)
        appendIndent("platform: [")
        appendLine()

        platform.toString().lines().let { lines ->
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
