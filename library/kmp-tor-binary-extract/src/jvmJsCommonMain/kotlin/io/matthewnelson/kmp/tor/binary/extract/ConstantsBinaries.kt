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

@Suppress("ObjectPropertyName", "SpellCheckingInspection")
actual object ConstantsBinaries {
    private const val _ZIP_SHA256_GEOIP = "76954322ae9a9dfb371fdfb8f527e8250a38df2db614e654cafbff1487246827"
    private const val _FILE_NAME_GEOIPS_ZIP = "geoips.zip"
    private const val _FILE_NAME_GEOIPS_ZIP_SHA256 = "$_FILE_NAME_GEOIPS_ZIP.sha256sum"

    actual val ZIP_SHA256_GEOIP: String get() = _ZIP_SHA256_GEOIP
    actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf("geoip6", "geoip")

    actual val FILE_NAME_GEOIPS_ZIP: String get() = _FILE_NAME_GEOIPS_ZIP
    actual val FILE_NAME_GEOIPS_ZIP_SHA256: String get() = _FILE_NAME_GEOIPS_ZIP_SHA256

    const val ZIP_SHA256_LINUX_X64 = "6a88d1162c100ad57d61eac94d94450a487fab86b74894f019a1873f77d9f04b"
    const val ZIP_SHA256_LINUX_X86 = "43428afd4c6ca57f130207df3a94ff2d579597f5fd02651b7d7c4ecdd56da0fd"
    const val ZIP_SHA256_MACOS_X64 = "4c31390efc5876ff6c0f6a43bee6234e391e0a81c44d5daef283023ffd7cea00"
    const val ZIP_SHA256_MINGW_X64 = "8ffc1d59fd24fac482f76da30bdc721bd67d7e096e64d20ce4bb784e735d8249"
    const val ZIP_SHA256_MINGW_X86 = "8ae27072d584c4bb288859e001c32a91a4e521802dc92f25752c5fb34cb7fbea"

    const val FILE_NAME_KMPTOR_ZIP = "kmptor.zip"
    const val FILE_NAME_KMPTOR_ZIP_SHA256 = "$FILE_NAME_KMPTOR_ZIP.sha256sum"

    val ZIP_MANIFEST_LINUX_X64 get() = listOf("libssl.so.1.1", "libevent-2.1.so.7", "libcrypto.so.1.1", "tor", "libstdc++/", "libstdc++/libstdc++.so.6")
    val ZIP_MANIFEST_LINUX_X86 get() = listOf("libssl.so.1.1", "libevent-2.1.so.7", "libcrypto.so.1.1", "tor", "libstdc++/", "libstdc++/libstdc++.so.6")
    val ZIP_MANIFEST_MACOS_X64 get() = listOf("libevent-2.1.7.dylib", "tor")
    val ZIP_MANIFEST_MINGW_X64 get() = listOf("libssl-1_1-x64.dll", "tor.exe", "libcrypto-1_1-x64.dll", "libwinpthread-1.dll", "libevent-2-1-7.dll", "libevent_core-2-1-7.dll", "libevent_extra-2-1-7.dll", "zlib1.dll", "libgcc_s_seh-1.dll", "libssp-0.dll")
    val ZIP_MANIFEST_MINGW_X86 get() = listOf("libgcc_s_dw2-1.dll", "tor.exe", "libssl-1_1.dll", "libwinpthread-1.dll", "libevent-2-1-7.dll", "libevent_core-2-1-7.dll", "libevent_extra-2-1-7.dll", "libcrypto-1_1.dll", "zlib1.dll", "libssp-0.dll")
}
