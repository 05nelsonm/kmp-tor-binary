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
@file:Suppress("KotlinRedundantDiagnosticSuppress")

package io.matthewnelson.differ.internal

import io.matthewnelson.encoding.base16.Base16
import io.matthewnelson.encoding.base64.Base64
import io.matthewnelson.encoding.builders.*
import okio.FileSystem
import okio.Path

@Suppress("NOTHING_TO_INLINE")
internal expect inline fun FileSystem.Companion.get(): FileSystem

@Suppress("NOTHING_TO_INLINE")
@Throws(IllegalArgumentException::class)
internal inline fun Path.requireFileExistAndNotEmpty(fs: FileSystem, argName: String) {
    require(fs.exists(this)) { "$argName does not exist" }

    val md = fs.metadata(this)
    require(md.isRegularFile) { "$argName exists, but is not a file" }
    require(md.size != 0L) { "$argName is empty" }
}

@Suppress("NOTHING_TO_INLINE")
@Throws(IllegalArgumentException::class)
internal inline fun Path.requireFileDoesNotExist(fs: FileSystem, argName: String) {
    require(!fs.exists(this)) { "$argName exists" }
}

/**
 * Checks if the path is a directory.
 *
 * @return true when the directory needs to be created
 * */
@Suppress("NOTHING_TO_INLINE")
@Throws(IllegalArgumentException::class)
internal inline fun Path.requireDirOrNull(fs: FileSystem, argName: String): Boolean {
    val isDir = fs.metadataOrNull(this)?.isDirectory
    require(isDir != false) { "$argName is not a directory" }
    return isDir == null
}

@Suppress("NOTHING_TO_INLINE")
@Throws(IllegalArgumentException::class)
internal inline fun String.requireDiffFileExtensionNameValid(argName: String) {
    require(!contains(' ')) { "$argName cannot contain white space" }
    require(lines().size == 1) { "$argName cannot contain line breaks" }
    require(startsWith('.')) { "$argName must start with a '.'" }
    require(length > 1) { "$argName length must be greater than 1" }
}

internal val base16: Base16 = Base16 { encodeToLowercase = true }

internal val base64: Base64 = Base64 { lineBreakInterval = 64 }
