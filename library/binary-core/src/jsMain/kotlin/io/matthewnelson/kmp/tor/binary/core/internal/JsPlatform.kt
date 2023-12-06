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
@file:Suppress("FunctionName", "ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")

package io.matthewnelson.kmp.tor.binary.core.internal

import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.core.OSHost
import io.matthewnelson.kmp.tor.binary.core.OSInfo

@InternalKmpTorBinaryApi
public actual val path_separator: Char by lazy {
    try {
        path_sep.first()
    } catch (_: Throwable) {
        if (OSInfo.INSTANCE.osHost is OSHost.Windows) {
            '\\'
        } else {
            '/'
        }
    }
}

@InternalKmpTorBinaryApi
public actual fun path_parent(path: String): String? {
    val parent = path_dirname(path)
    if (parent == path) return null
    return parent
}

@InternalKmpTorBinaryApi
public actual fun fs_mkdir(path: String): Boolean {
    if (fs_exists(path)) return false

    return try {
        fs_mkdirSync(path)
        true
    } catch (_: Throwable) {
        false
    }
}

@InternalKmpTorBinaryApi
public actual fun fs_readFileBytes(path: String): ByteArray {
    val buffer = fs_readFileSync(path)
    val bytes = ByteArray(buffer.length.toInt())
    for (i in bytes.indices) {
        bytes[i] = buffer.readInt8(i) as Byte
    }
    buffer.fill()
    return bytes
}

@InternalKmpTorBinaryApi
public actual fun fs_readFileUtf8(path: String): String {
    return fs_readFileSync(path).let { buffer ->
        buffer.toString("utf8", 0, buffer.length)
            .also { buffer.fill() }
    }
}

@InternalKmpTorBinaryApi
public actual fun fs_rm(
    path: String,
    recursively: Boolean,
    force: Boolean,
): Boolean {
    if (!fs_exists(path)) return false
    fs_rmSync(path, Options.Remove(force = force, recursive = recursively))
    return fs_exists(path)
}
