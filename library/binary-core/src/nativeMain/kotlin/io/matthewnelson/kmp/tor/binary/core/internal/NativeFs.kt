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
@file:Suppress("FunctionName")

package io.matthewnelson.kmp.tor.binary.core.internal

import io.matthewnelson.kmp.tor.binary.core.IOException
import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import kotlinx.cinterop.*
import platform.posix.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@InternalKmpTorBinaryApi
public actual fun fs_exists(path: String): Boolean {
    val result = access(path, 0)
    return if (result != 0 && errno == ENOENT) {
        false
    } else {
        result == 0
    }
}

@InternalKmpTorBinaryApi
@Throws(IOException::class)
public actual fun fs_readFileUtf8(path: String): String = fs_readFileBytes(path).decodeToString()

@Throws(IOException::class)
@OptIn(ExperimentalContracts::class, ExperimentalForeignApi::class)
internal inline fun <T: Any?> fs_withFile(
    path: String,
    flags: String,
    block: (CPointer<FILE>) -> T
): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    @OptIn(InternalKmpTorBinaryApi::class)
    val file = fopen(path, flags) ?: throw errnoToIOException(errno)

    val result = try {
        block(file)
    } finally {
        try {
            fclose(file)
        } catch (_: Throwable) {}
    }

    return result
}
