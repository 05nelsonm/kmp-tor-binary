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
    private const val _ARCHIVE_SHA256_VALUE_GEOIP = "2dce16ea6a5ae3e82c90a1b82bb5ff2eb2a91be98ec2ed539ad722c246752705"
    private const val _ARCHIVE_FILE_NAME_GEOIP = "geoips.zip"

    @JvmStatic
    internal actual val ARCHIVE_SHA256_VALUE_GEOIP: String get() = _ARCHIVE_SHA256_VALUE_GEOIP
    @JvmStatic
    internal actual val ARCHIVE_FILE_NAME_GEOIP: String get() = _ARCHIVE_FILE_NAME_GEOIP

    private const val _ARCHIVE_SHA256_LINUX_X64 = "5003d1866d9718d6d2a3c523baf3d91d0a66abc3c75e4ad872ff6d8a879bc6e0"
    private const val _ARCHIVE_SHA256_LINUX_X86 = "18e1ec895bbb19a59b7ccb73266cafed8d852e19dfc8de0164ccac2642a410fa"
    private const val _ARCHIVE_SHA256_MACOS_X64 = "23b24fe40c4294b5dfadadca1d13a2950acc08feddbd97bf06cbe4603f64558d"
    private const val _ARCHIVE_SHA256_MINGW_X64 = "46e195b4e9bceafa5e0ffa4eace400368439b03af6e0718ac3cf265b5b2ac871"
    private const val _ARCHIVE_SHA256_MINGW_X86 = "dfa4f7c8aac4e50a5646606ac81b3e33b55380b2acb3d9db3dab2530d80249cd"

    @JvmStatic
    internal actual val ARCHIVE_SHA256_LINUX_X64: String get() = _ARCHIVE_SHA256_LINUX_X64
    @JvmStatic
    internal actual val ARCHIVE_SHA256_LINUX_X86: String get() = _ARCHIVE_SHA256_LINUX_X86
    @JvmStatic
    internal actual val ARCHIVE_SHA256_MACOS_X64: String get() = _ARCHIVE_SHA256_MACOS_X64
    @JvmStatic
    internal actual val ARCHIVE_SHA256_MINGW_X64: String get() = _ARCHIVE_SHA256_MINGW_X64
    @JvmStatic
    internal actual val ARCHIVE_SHA256_MINGW_X86: String get() = _ARCHIVE_SHA256_MINGW_X86

    private const val _ARCHIVE_FILE_NAME_KMPTOR = "kmptor.zip"

    @JvmStatic
    internal actual val ARCHIVE_FILE_NAME_KMPTOR: String get() = _ARCHIVE_FILE_NAME_KMPTOR
}
