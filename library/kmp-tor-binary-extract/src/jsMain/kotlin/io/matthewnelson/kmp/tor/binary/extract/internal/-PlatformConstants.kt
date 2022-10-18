/*
 * Copyright (c) 2022 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.kmp.tor.binary.extract.internal

@Suppress("ObjectPropertyName", "SpellCheckingInspection")
internal actual object PlatformConstants {
    private const val _ARCHIVE_SHA256_VALUE_GEOIP = "d25e55d538b0628a17795ff45b94ff772f8e1bec2808f3dbc162c383a0cd0a41"
    private const val _ARCHIVE_FILE_NAME_GEOIP = "geoips.tar.gz"

    internal actual val ARCHIVE_SHA256_VALUE_GEOIP: String get() = _ARCHIVE_SHA256_VALUE_GEOIP
    internal actual val ARCHIVE_FILE_NAME_GEOIP: String get() = _ARCHIVE_FILE_NAME_GEOIP

    private const val _ARCHIVE_SHA256_VALUE_LINUX_X64 = "787ec03cdb96a328df894c89dddb6de8f616b7d8803b45e293dde0f9fe870bb1"
    private const val _ARCHIVE_SHA256_VALUE_LINUX_X86 = "7ed8d8887600407a2285619a63a3edd4b9c2136d2c1de0dbc69cd876df348918"
    private const val _ARCHIVE_SHA256_VALUE_MACOS_X64 = "5b9091852ccf55d5a13e61da74f882b2e4951a5317a0fa9677f2db74b536f24d"
    private const val _ARCHIVE_SHA256_VALUE_MINGW_X64 = "462e9042a7ad1f9c0f1a7d113b0a8ca3049e54a7df34647a4e96456b1ff77006"
    private const val _ARCHIVE_SHA256_VALUE_MINGW_X86 = "7ac128a2a92b691bfa924007d220017bb63de5d6ce2dae843351db4613815543"

    internal actual val ARCHIVE_SHA256_VALUE_LINUX_X64: String get() = _ARCHIVE_SHA256_VALUE_LINUX_X64
    internal actual val ARCHIVE_SHA256_VALUE_LINUX_X86: String get() = _ARCHIVE_SHA256_VALUE_LINUX_X86
    internal actual val ARCHIVE_SHA256_VALUE_MACOS_X64: String get() = _ARCHIVE_SHA256_VALUE_MACOS_X64
    internal actual val ARCHIVE_SHA256_VALUE_MINGW_X64: String get() = _ARCHIVE_SHA256_VALUE_MINGW_X64
    internal actual val ARCHIVE_SHA256_VALUE_MINGW_X86: String get() = _ARCHIVE_SHA256_VALUE_MINGW_X86

    private const val _ARCHIVE_FILE_NAME_KMPTOR = "kmptor.tar.gz"

    internal actual val ARCHIVE_FILE_NAME_KMPTOR: String get() = _ARCHIVE_FILE_NAME_KMPTOR
}
