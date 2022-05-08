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
    private const val _ZIP_SHA256_GEOIP = "cf995a23ce327cbb65821e3a31f6bb15345b35e5c9abceb482ba5feb39ea9ffa"
    private const val _FILE_NAME_GEOIPS_ZIP = "geoips.zip"
    private const val _FILE_NAME_GEOIPS_ZIP_SHA256 = "$_FILE_NAME_GEOIPS_ZIP.sha256sum"

    actual val ZIP_SHA256_GEOIP: String get() = _ZIP_SHA256_GEOIP
    actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf("geoip", "geoip6")

    actual val FILE_NAME_GEOIPS_ZIP: String get() = _FILE_NAME_GEOIPS_ZIP
    actual val FILE_NAME_GEOIPS_ZIP_SHA256: String get() = _FILE_NAME_GEOIPS_ZIP_SHA256

    const val ZIP_SHA256_LINUX_X64 = "4bc17c3fc0bbd8854530ceb3eb1a826ac89628f6557d371ca478aa03cb46fc46"
    const val ZIP_SHA256_LINUX_X86 = "80f01ee07730e8ef938001baac59d0031ec30bd9a867d75b45f7372c18fa341b"
    const val ZIP_SHA256_MACOS_X64 = "4fcba096b89704e7309eb24664f6f5d17ad198e28a3de29ec47f1893f8823ea4"
    const val ZIP_SHA256_MINGW_X64 = "dcf3ff47127633638b38e96c788e799c5809b16b123495d347341121f846eb70"
    const val ZIP_SHA256_MINGW_X86 = "daa863ea40788f53388bc27ee19c1e3465e3ff430a5ddcdf1189b23101d0931c"

    const val FILE_NAME_KMPTOR_ZIP = "kmptor.zip"
    const val FILE_NAME_KMPTOR_ZIP_SHA256 = "$FILE_NAME_KMPTOR_ZIP.sha256sum"

    val ZIP_MANIFEST_LINUX_X64 get() = listOf("libcrypto.so.1.1", "libevent-2.1.so.7", "libssl.so.1.1", "libstdc++/libstdc++.so.6", "tor")
    val ZIP_MANIFEST_LINUX_X86 get() = listOf("libcrypto.so.1.1", "libevent-2.1.so.7", "libssl.so.1.1", "libstdc++/libstdc++.so.6", "tor")
    val ZIP_MANIFEST_MACOS_X64 get() = listOf("libevent-2.1.7.dylib", "tor")
    val ZIP_MANIFEST_MINGW_X64 get() = listOf("libcrypto-1_1-x64.dll", "libevent-2-1-7.dll", "libevent_core-2-1-7.dll", "libevent_extra-2-1-7.dll", "libgcc_s_seh-1.dll", "libssl-1_1-x64.dll", "libssp-0.dll", "libwinpthread-1.dll", "tor.exe", "zlib1.dll")
    val ZIP_MANIFEST_MINGW_X86 get() = listOf("libcrypto-1_1.dll", "libevent-2-1-7.dll", "libevent_core-2-1-7.dll", "libevent_extra-2-1-7.dll", "libgcc_s_dw2-1.dll", "libssl-1_1.dll", "libssp-0.dll", "libwinpthread-1.dll", "tor.exe", "zlib1.dll")
}
