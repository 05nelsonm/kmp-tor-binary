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
package io.matthewnelson.kmp.tor.binary.extract

import io.matthewnelson.kmp.tor.binary.extract.annotation.InternalTorBinaryApi
import io.matthewnelson.kmp.tor.binary.extract.internal.PlatformConstants
import kotlin.jvm.JvmStatic

@InternalTorBinaryApi
@Suppress("SpellCheckingInspection")
actual object ConstantsBinaries {
    @JvmStatic
    actual val ARCHIVE_SHA256_VALUE_GEOIP: String get() = PlatformConstants.ARCHIVE_SHA256_VALUE_GEOIP
    @JvmStatic
    actual val ARCHIVE_MANIFEST_GEOIP: List<String> get() = listOf("geoip", "geoip6")

    @JvmStatic
    actual val ARCHIVE_FILE_NAME_GEOIP: String get() = PlatformConstants.ARCHIVE_FILE_NAME_GEOIP
    @JvmStatic
    actual val ARCHIVE_SHA256_FILE_NAME_GEOIP: String get() = "$ARCHIVE_FILE_NAME_GEOIP.sha256sum"

    @JvmStatic
    val ARCHIVE_SHA256_VALUE_LINUX_X64 get() = PlatformConstants.ARCHIVE_SHA256_LINUX_X64
    @JvmStatic
    val ARCHIVE_SHA256_VALUE_LINUX_X86 get() = PlatformConstants.ARCHIVE_SHA256_LINUX_X86
    @JvmStatic
    val ARCHIVE_SHA256_VALUE_MACOS_X64 get() = PlatformConstants.ARCHIVE_SHA256_MACOS_X64
    @JvmStatic
    val ARCHIVE_SHA256_VALUE_MINGW_X64 get() = PlatformConstants.ARCHIVE_SHA256_MINGW_X64
    @JvmStatic
    val ARCHIVE_SHA256_VALUE_MINGW_X86 get() = PlatformConstants.ARCHIVE_SHA256_MINGW_X86

    @JvmStatic
    val ARCHIVE_FILE_NAME_KMPTOR get() = PlatformConstants.ARCHIVE_FILE_NAME_KMPTOR
    @JvmStatic
    val ARCHIVE_SHA256_FILE_NAME_KMPTOR get() = "$ARCHIVE_FILE_NAME_KMPTOR.sha256sum"

    @JvmStatic
    val ARCHIVE_MANIFEST_LINUX_X64 get() = listOf("libcrypto.so.1.1", "libevent-2.1.so.7", "libssl.so.1.1", "libstdc++/libstdc++.so.6", "tor")
    @JvmStatic
    val ARCHIVE_MANIFEST_LINUX_X86 get() = listOf("libcrypto.so.1.1", "libevent-2.1.so.7", "libssl.so.1.1", "libstdc++/libstdc++.so.6", "tor")
    @JvmStatic
    val ARCHIVE_MANIFEST_MACOS_X64 get() = listOf("libevent-2.1.7.dylib", "tor")
    @JvmStatic
    val ARCHIVE_MANIFEST_MINGW_X64 get() = listOf("tor.exe")
    @JvmStatic
    val ARCHIVE_MANIFEST_MINGW_X86 get() = listOf("tor.exe")

    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_SHA256_VALUE_GEOIP"))
    actual val ZIP_SHA256_GEOIP: String get() = ARCHIVE_SHA256_VALUE_GEOIP
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_MANIFEST_GEOIP"))
    actual val ZIP_MANIFEST_GEOIP: List<String> get() = ARCHIVE_MANIFEST_GEOIP
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_FILE_NAME_GEOIP"))
    actual val FILE_NAME_GEOIPS_ZIP: String get() = ARCHIVE_FILE_NAME_GEOIP
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_SHA256_FILE_NAME_GEOIP"))
    actual val FILE_NAME_GEOIPS_ZIP_SHA256: String get() = ARCHIVE_SHA256_FILE_NAME_GEOIP
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_SHA256_VALUE_LINUX_X64"))
    val ZIP_SHA256_LINUX_X64 get() = ARCHIVE_SHA256_VALUE_LINUX_X64
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_SHA256_VALUE_LINUX_X86"))
    val ZIP_SHA256_LINUX_X86 get() = ARCHIVE_SHA256_VALUE_LINUX_X86
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_SHA256_VALUE_MACOS_X64"))
    val ZIP_SHA256_MACOS_X64 get() = ARCHIVE_SHA256_VALUE_MACOS_X64
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_SHA256_VALUE_MINGW_X64"))
    val ZIP_SHA256_MINGW_X64 get() = ARCHIVE_SHA256_VALUE_MINGW_X64
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_SHA256_VALUE_MINGW_X86"))
    val ZIP_SHA256_MINGW_X86 get() = ARCHIVE_SHA256_VALUE_MINGW_X86
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_FILE_NAME_KMPTOR"))
    val FILE_NAME_KMPTOR_ZIP get() = ARCHIVE_FILE_NAME_KMPTOR
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_SHA256_FILE_NAME_KMPTOR"))
    val FILE_NAME_KMPTOR_ZIP_SHA256 get() = ARCHIVE_SHA256_FILE_NAME_KMPTOR
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_MANIFEST_LINUX_X64"))
    val ZIP_MANIFEST_LINUX_X64 get() = ARCHIVE_MANIFEST_LINUX_X64
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_MANIFEST_LINUX_X86"))
    val ZIP_MANIFEST_LINUX_X86 get() = ARCHIVE_MANIFEST_LINUX_X86
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_MANIFEST_MACOS_X64"))
    val ZIP_MANIFEST_MACOS_X64 get() = ARCHIVE_MANIFEST_MACOS_X64
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_MANIFEST_MINGW_X64"))
    val ZIP_MANIFEST_MINGW_X64 get() = ARCHIVE_MANIFEST_MINGW_X64
    @JvmStatic
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_MANIFEST_MINGW_X86"))
    val ZIP_MANIFEST_MINGW_X86 get() = ARCHIVE_MANIFEST_MINGW_X86
}
