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
    private const val _ZIP_SHA256_GEOIP = "6c7065ee430edecb3c2ac37da3c567e4a4c2b63085bc19b739d8976686cae92a"
    private const val _FILE_NAME_GEOIPS_ZIP = "geoips.zip"
    private const val _FILE_NAME_GEOIPS_ZIP_SHA256 = "$_FILE_NAME_GEOIPS_ZIP.sha256sum"

    actual val ZIP_SHA256_GEOIP: String get() = _ZIP_SHA256_GEOIP
    actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf("geoip", "geoip6")

    actual val FILE_NAME_GEOIPS_ZIP: String get() = _FILE_NAME_GEOIPS_ZIP
    actual val FILE_NAME_GEOIPS_ZIP_SHA256: String get() = _FILE_NAME_GEOIPS_ZIP_SHA256

    const val ZIP_SHA256_LINUX_X64 = "16a364ff7c6a154c2296a7c8c8106ef00a95bb8d794d485236ed95c0797b5cab"
    const val ZIP_SHA256_LINUX_X86 = "42221d0c3188ae838513bdc6f16489086c329ba0d4dddf458afa00a296c29cfd"
    const val ZIP_SHA256_MACOS_X64 = "ded77048302600429f9ba5bd6241589b5574397ecdde242ffe043c6d96fbfe88"
    const val ZIP_SHA256_MINGW_X64 = "c5b8f06c3b6be3ffb572c7cea25ae6b96aabd8e322e04d5fcd9b08208e89f03a"
    const val ZIP_SHA256_MINGW_X86 = "6f42f18e15278dba90132ae3234c2a7de1d646101ce63c6029d8ffafdcff5eb7"

    const val FILE_NAME_KMPTOR_ZIP = "kmptor.zip"
    const val FILE_NAME_KMPTOR_ZIP_SHA256 = "$FILE_NAME_KMPTOR_ZIP.sha256sum"

    val ZIP_MANIFEST_LINUX_X64 get() = listOf("libcrypto.so.1.1", "libevent-2.1.so.7", "libssl.so.1.1", "libstdc++/libstdc++.so.6", "tor")
    val ZIP_MANIFEST_LINUX_X86 get() = listOf("libcrypto.so.1.1", "libevent-2.1.so.7", "libssl.so.1.1", "libstdc++/libstdc++.so.6", "tor")
    val ZIP_MANIFEST_MACOS_X64 get() = listOf("libevent-2.1.7.dylib", "tor")
    val ZIP_MANIFEST_MINGW_X64 get() = listOf("libcrypto-1_1-x64.dll", "libevent-2-1-7.dll", "libevent_core-2-1-7.dll", "libevent_extra-2-1-7.dll", "libgcc_s_seh-1.dll", "libssl-1_1-x64.dll", "libssp-0.dll", "libwinpthread-1.dll", "tor.exe", "zlib1.dll")
    val ZIP_MANIFEST_MINGW_X86 get() = listOf("libcrypto-1_1.dll", "libevent-2-1-7.dll", "libevent_core-2-1-7.dll", "libevent_extra-2-1-7.dll", "libgcc_s_dw2-1.dll", "libssl-1_1.dll", "libssp-0.dll", "libwinpthread-1.dll", "tor.exe", "zlib1.dll")
}
