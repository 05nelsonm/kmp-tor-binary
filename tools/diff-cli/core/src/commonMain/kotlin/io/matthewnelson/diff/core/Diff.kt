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

import io.matthewnelson.component.value.clazz.NoValue
import io.matthewnelson.component.value.clazz.ValueClazz
import io.matthewnelson.diff.core.Header.Companion.readDiffFileHeader
import io.matthewnelson.diff.core.internal.apply.Apply
import io.matthewnelson.diff.core.internal.create.Create
import io.matthewnelson.diff.core.internal.InternalDiffApi
import io.matthewnelson.diff.core.internal.system
import okio.*
import okio.Path.Companion.toPath
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

public class Diff private constructor() {

    public data class Options(
        val diffFileExtensionName: String,
        val schema: Schema
    ) {

        public constructor(): this(DEFAULT_EXT_NAME)
        public constructor(diffFileExtensionName: String): this(diffFileExtensionName, Schema.latest())
        public constructor(schema: Schema): this(DEFAULT_EXT_NAME, schema)

        @Throws(IllegalArgumentException::class)
        public fun validateExtensionName() {
            with(diffFileExtensionName) {
                if (this == DEFAULT_EXT_NAME) return
                require(!contains(' ')) { "diff file extension name cannot contain white space" }
                require(lines().size == 1) { "diff file extension name cannot contain line breaks" }
                require(startsWith('.')) { "diff file extension name must start with a '.'" }
                require(length > 1) { "diff file extension name length must be greater than 1" }
            }
        }

        public companion object {
            public const val DEFAULT_EXT_NAME: String = ".diff"
        }
    }

    @Suppress("ClassName")
    public sealed class Schema private constructor(): ValueClazz(NoValue()) {

        public object v1: Schema()

        public companion object {

            @JvmStatic
            @Throws(IllegalStateException::class)
            public fun from(string: String): Schema {
                return when (string.trim()) {
                    v1.toString() -> v1
                    else -> throw IllegalStateException("Unknown DiffFile.Schema[$string]")
                }
            }

            @JvmStatic
            public fun latest(): Schema = v1
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
        public fun readHeader(fs: FileSystem, diffFile: Path): Header = fs.read(diffFile) { readDiffFileHeader() }
    }
}
