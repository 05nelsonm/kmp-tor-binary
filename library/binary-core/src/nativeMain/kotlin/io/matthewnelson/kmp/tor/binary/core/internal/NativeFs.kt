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

import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi

@InternalKmpTorBinaryApi
@Throws(Exception::class)
public actual fun fs_chmod(path: String, mode: String) {
    TODO()
}

@InternalKmpTorBinaryApi
public actual fun fs_exists(path: String): Boolean {
    TODO()
}

@InternalKmpTorBinaryApi
public actual fun fs_mkdir(path: String): Boolean {
    TODO()
}

@InternalKmpTorBinaryApi
@Throws(Exception::class)
public actual fun fs_readFileBytes(path: String): ByteArray {
    TODO()
}

@InternalKmpTorBinaryApi
@Throws(Exception::class)
public actual fun fs_readFileUtf8(path: String): String {
    TODO()
}

@Throws(Exception::class)
internal actual fun fs_realpath(path: String): String {
    TODO()
}

@InternalKmpTorBinaryApi
@Throws(Exception::class)
public actual fun fs_rm(
    path: String,
    recursively: Boolean,
    force: Boolean,
): Boolean {
    TODO()
}
