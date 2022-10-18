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
    // TODO: Update with gunzipped archive details
    private const val _ARCHIVE_SHA256_VALUE_GEOIP = ""
    private const val _ARCHIVE_FILE_NAME_GEOIP = "geoips.tar.gz"

    internal actual val ARCHIVE_SHA256_VALUE_GEOIP: String get() = _ARCHIVE_SHA256_VALUE_GEOIP
    internal actual val ARCHIVE_FILE_NAME_GEOIP: String get() = _ARCHIVE_FILE_NAME_GEOIP

    // TODO: Update with gunzipped archive sha256sum values
    private const val _ARCHIVE_SHA256_LINUX_X64 = ""
    private const val _ARCHIVE_SHA256_LINUX_X86 = ""
    private const val _ARCHIVE_SHA256_MACOS_X64 = ""
    private const val _ARCHIVE_SHA256_MINGW_X64 = ""
    private const val _ARCHIVE_SHA256_MINGW_X86 = ""

    internal actual val ARCHIVE_SHA256_LINUX_X64: String get() = _ARCHIVE_SHA256_LINUX_X64
    internal actual val ARCHIVE_SHA256_LINUX_X86: String get() = _ARCHIVE_SHA256_LINUX_X86
    internal actual val ARCHIVE_SHA256_MACOS_X64: String get() = _ARCHIVE_SHA256_MACOS_X64
    internal actual val ARCHIVE_SHA256_MINGW_X64: String get() = _ARCHIVE_SHA256_MINGW_X64
    internal actual val ARCHIVE_SHA256_MINGW_X86: String get() = _ARCHIVE_SHA256_MINGW_X86

    private const val _ARCHIVE_FILE_NAME_KMPTOR = "kmptor.tar.gz"

    internal actual val ARCHIVE_FILE_NAME_KMPTOR: String get() = _ARCHIVE_FILE_NAME_KMPTOR
}
