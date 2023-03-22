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
internal inline fun Path.requireFileExistAndNotEmpty(argName: String) {
    val fs = FileSystem.get()
    require(fs.exists(this)) { "$argName does not exist" }

    val md = fs.metadata(this)
    require(md.isRegularFile) { "$argName exists, but is not a file" }
    require(md.size != 0L) { "$argName is empty" }
}

@Suppress("NOTHING_TO_INLINE")
@Throws(IllegalArgumentException::class)
internal inline fun Path.requireFileDoesNotExist(argName: String) {
    val fs = FileSystem.get()
    require(!fs.exists(this)) { "$argName exists" }
}

@Suppress("NOTHING_TO_INLINE")
@Throws(IllegalArgumentException::class)
internal inline fun Path.requireDirOrNull(argName: String) {
    val fs = FileSystem.get()
    require(fs.metadataOrNull(this)?.isDirectory != false) { "$argName is not a directory" }
}

internal val base16: Base16 = Base16 { encodeToLowercase = true }

internal val base64: Base64 = Base64 { lineBreakInterval = 64 }
