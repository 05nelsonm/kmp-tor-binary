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
    private const val _ARCHIVE_SHA256_VALUE_GEOIP = "f3c31f3208a239d030e5367ece4772983c964887f9ba5a34667f3e6e24d94ec2"
    private const val _ARCHIVE_FILE_NAME_GEOIP = "geoips.zip"

    @JvmStatic
    internal actual val ARCHIVE_SHA256_VALUE_GEOIP: String get() = _ARCHIVE_SHA256_VALUE_GEOIP
    @JvmStatic
    internal actual val ARCHIVE_FILE_NAME_GEOIP: String get() = _ARCHIVE_FILE_NAME_GEOIP

    private const val _ARCHIVE_SHA256_VALUE_LINUX_X64 = "7c23de9cdb4647770d45a613f94743fd59fc3eeebbff239ea098fe2a2a2904f6"
    private const val _ARCHIVE_SHA256_VALUE_LINUX_X86 = "b0af5ee8d488d69badc31c91c88ceb7ec1ca90d77f6e4689e46fafb7885e5ff2"
    private const val _ARCHIVE_SHA256_VALUE_MACOS_X64 = "63b97fc87f94b56e9f1411ad585fe2a66871b046d5e9d7f3af4672112d851ea2"
    private const val _ARCHIVE_SHA256_VALUE_MINGW_X64 = "791d22886255261f26a6036a6b011cfcaaeaca9c407f610ac299b389a2049b20"
    private const val _ARCHIVE_SHA256_VALUE_MINGW_X86 = "6be52087bdedf6f4e0a86fbf23c13a19ee41e6a090e9bc2d097e11a6a41a1367"

    @JvmStatic
    internal actual val ARCHIVE_SHA256_VALUE_LINUX_X64: String get() = _ARCHIVE_SHA256_VALUE_LINUX_X64
    @JvmStatic
    internal actual val ARCHIVE_SHA256_VALUE_LINUX_X86: String get() = _ARCHIVE_SHA256_VALUE_LINUX_X86
    @JvmStatic
    internal actual val ARCHIVE_SHA256_VALUE_MACOS_X64: String get() = _ARCHIVE_SHA256_VALUE_MACOS_X64
    @JvmStatic
    internal actual val ARCHIVE_SHA256_VALUE_MINGW_X64: String get() = _ARCHIVE_SHA256_VALUE_MINGW_X64
    @JvmStatic
    internal actual val ARCHIVE_SHA256_VALUE_MINGW_X86: String get() = _ARCHIVE_SHA256_VALUE_MINGW_X86

    private const val _ARCHIVE_FILE_NAME_KMPTOR = "kmptor.zip"

    @JvmStatic
    internal actual val ARCHIVE_FILE_NAME_KMPTOR: String get() = _ARCHIVE_FILE_NAME_KMPTOR
}
