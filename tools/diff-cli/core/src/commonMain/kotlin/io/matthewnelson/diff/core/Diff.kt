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
package io.matthewnelson.diff.core

import io.matthewnelson.diff.core.Header.Companion.readDiffHeader
import io.matthewnelson.diff.core.internal.apply.Apply
import io.matthewnelson.diff.core.internal.create.Create
import io.matthewnelson.diff.core.internal.InternalDiffApi
import io.matthewnelson.diff.core.internal.system
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import okio.*
import okio.Path.Companion.toPath
import kotlin.jvm.JvmField
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * Diff
 *
 * Create and Apply file diff's
 *
 * @see [apply]
 * @see [create]
 * @see [readHeader]
 * */
public class Diff private constructor() {

    /**
     * Diff file schema versions.
     * */
    @Suppress("EnumEntryName")
    public enum class Schema(@JvmField public val code: Int) {
        v1(code = 1);

        public companion object {
            @JvmStatic
            public fun latest(): Schema = v1
        }
    }

    /**
     * [Options] for use when creating a new [Diff] via [create].
     *
     * @see [Builder]
     * */
    public class Options {

        @JvmField
        public val diffFileExtensionName: String
        @JvmField
        public val useStaticTime: Boolean
        @JvmField
        public val schema: Schema

        public constructor(): this(Builder())

        public constructor(builder: Builder) {
            diffFileExtensionName = builder.diffFileExtensionName
            useStaticTime = builder.useStaticTime
            schema = builder.schema
        }

        public constructor(configure: Builder.() -> Unit) {
            val builder = Builder()
            configure(builder)
            diffFileExtensionName = builder.diffFileExtensionName
            useStaticTime = builder.useStaticTime
            schema = builder.schema
        }

        /**
         * Configure [Options]
         * */
        public class Builder  {
            @get:JvmName("diffFileExtensionName")
            public var diffFileExtensionName: String = DEFAULT_EXT_NAME
                private set

            /**
             * Use a different file extension name for the diff file
             *
             * Default is .diff
             *
             * @throws [IllegalArgumentException] if [value]:
             *  - Contains whitespace
             *  - Contains new lines
             *  - Does not start with '.'
             *  - Is less than 2 chars
             * */
            @Throws(IllegalArgumentException::class)
            public fun diffFileExtensionName(value: String): Builder {
                with(value) {
                    require(!contains(' ')) { "diff file extension name cannot contain white space" }
                    require(lines().size == 1) { "diff file extension name cannot contain line breaks" }
                    require(startsWith('.')) { "diff file extension name must start with a '.'" }
                    require(length > 1) { "diff file extension name length must be greater than 1" }
                }

                diffFileExtensionName = value
                return this
            }

            /**
             * Will use a static time value for [Header.createdAt],
             * instead of now().
             *
             * @see [time]
             * */
            @JvmField
            public var useStaticTime: Boolean = false

            public fun useStaticTime(value: Boolean): Builder {
                useStaticTime = value
                return this
            }

            @JvmField
            public var schema: Schema = Schema.latest()
            public fun schema(value: Schema): Builder {
                schema = value
                return this
            }
        }

        internal fun time(): Instant {
            return if (useStaticTime) {
                STATIC_TIME.toInstant()
            } else {
                Clock.System.now()
            }
        }

        override fun equals(other: Any?): Boolean {
            return  other is Options
                    && other.diffFileExtensionName == diffFileExtensionName
                    && other.useStaticTime == useStaticTime
                    && other.schema == schema
        }

        override fun hashCode(): Int {
            var result = 17
            result = result * 31 + diffFileExtensionName.hashCode()
            result = result * 31 + useStaticTime.hashCode()
            result = result * 31 + schema.hashCode()
            return result
        }

        override fun toString(): String {
            return """
                Diff.Options [
                    diffFileExtensionName: $diffFileExtensionName
                    useStaticTime: $useStaticTime
                    schema: $schema
                ]
            """.trimIndent()
        }

        public companion object {
            public const val DEFAULT_EXT_NAME: String = ".diff"
            public const val STATIC_TIME: String = "1971-08-21T00:01:00Z"
        }
    }

    public companion object {

        /**
         * Apply a diff to it's associate file.
         * */
        @JvmStatic
        @Throws(IllegalStateException::class, Exception::class)
        public fun apply(diffFilePath: String, applyToFilePath: String) {
            apply(diffFilePath.toPath(), applyToFilePath.toPath())
        }

        /**
         * Apply a diff to it's associate file.
         * */
        @JvmStatic
        @Throws(IllegalStateException::class, IOException::class)
        public fun apply(diffFile: Path, applyTo: Path) {
            @OptIn(InternalDiffApi::class)
            apply(FileSystem.system(), diffFile, applyTo)
        }

        /**
         * Apply a diff to it's associate file.
         * */
        @JvmStatic
        @InternalDiffApi
        @Throws(IllegalStateException::class, IOException::class)
        public fun apply(fs: FileSystem, diffFile: Path, applyTo: Path) {
            Apply.diff(fs, diffFile, applyTo)
        }

        /**
         * Create a diff from 2 files and write it to the associated directory
         * */
        @JvmStatic
        @JvmOverloads
        @Throws(
            NoDiffException::class,
            IllegalArgumentException::class,
            IllegalStateException::class,
            Exception::class,
        )
        public fun create(
            file1Path: String,
            file2Path: String,
            diffDirPath: String,
            options: Options = Options(),
        ): String {
            return create(file1Path.toPath(), file2Path.toPath(), diffDirPath.toPath(), options).toString()
        }

        /**
         * Create a diff from 2 files and write it to the associated directory
         * */
        @JvmStatic
        @JvmOverloads
        @Throws(
            NoDiffException::class,
            IllegalArgumentException::class,
            IllegalStateException::class,
            Exception::class,
        )
        public fun create(
            file1: Path,
            file2: Path,
            diffDir: Path,
            options: Options = Options()
        ): Path {
            @OptIn(InternalDiffApi::class)
            return create(FileSystem.system(), file1, file2, diffDir, options)
        }

        /**
         * Create a diff from 2 files and write it to the associated directory
         * */
        @JvmStatic
        @JvmOverloads
        @InternalDiffApi
        @Throws(
            NoDiffException::class,
            IllegalArgumentException::class,
            IllegalStateException::class,
            Exception::class,
        )
        public fun create(
            fs: FileSystem,
            file1: Path,
            file2: Path,
            diffDir: Path,
            options: Options = Options()
        ): Path {
            return Create.diff(fs, file1, file2, diffDir, options)
        }

        /**
         * Reads only the diff's header and returns it.
         * */
        @JvmStatic
        @Throws(IllegalStateException::class, Exception::class)
        public fun readHeader(diffFilePath: String): Header = readHeader(diffFilePath.toPath())

        /**
         * Reads only the diff's header and returns it.
         * */
        @JvmStatic
        @OptIn(InternalDiffApi::class)
        @Throws(IllegalStateException::class, IOException::class)
        public fun readHeader(diffFile: Path): Header = readHeader(FileSystem.system(), diffFile)

        /**
         * Reads only the diff's header and returns it.
         * */
        @JvmStatic
        @InternalDiffApi
        @Throws(IllegalStateException::class, IOException::class)
        public fun readHeader(fs: FileSystem, diffFile: Path): Header = fs.read(diffFile) { readDiffHeader() }
    }
}
