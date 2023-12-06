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
@file:Suppress("FunctionName", "KotlinRedundantDiagnosticSuppress")

package io.matthewnelson.kmp.tor.binary.core.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import platform.posix.chmod
import platform.posix.fileno
import platform.posix.mkdir
import platform.posix.pread
import platform.posix.FILE

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun fs_platform_chmod(
    path: String,
    mode: UInt
): Int {
    @OptIn(ExperimentalForeignApi::class)
    return chmod(path, mode.convert()).convert()
}

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun fs_platform_mkdir(
    path: String,
    mode: UInt
): Int {
    @OptIn(ExperimentalForeignApi::class)
    return mkdir(path, mode.convert()).convert()
}

@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalForeignApi::class)
internal actual inline fun fs_platform_read(
    file: CPointer<FILE>,
    buf: CPointer<ByteVar>,
    size: Int,
    offset: Long,
): Int = pread(fileno(file), buf, size.convert(), offset).convert()