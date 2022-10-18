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

@Suppress("SpellCheckingInspection")
internal expect object PlatformConstants {
    internal val ARCHIVE_SHA256_VALUE_GEOIP: String
    internal val ARCHIVE_FILE_NAME_GEOIP: String

    internal val ARCHIVE_SHA256_LINUX_X64: String
    internal val ARCHIVE_SHA256_LINUX_X86: String
    internal val ARCHIVE_SHA256_MACOS_X64: String
    internal val ARCHIVE_SHA256_MINGW_X64: String
    internal val ARCHIVE_SHA256_MINGW_X86: String
    internal val ARCHIVE_FILE_NAME_KMPTOR: String
}
