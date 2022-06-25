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
    private const val _ZIP_SHA256_GEOIP = "bcd9aa2d70565d48ae5e1defd483237149fe7ccba8d66f71fd9a4e20fa136905"
    private const val _FILE_NAME_GEOIPS_ZIP = "geoips.zip"
    private const val _FILE_NAME_GEOIPS_ZIP_SHA256 = "$_FILE_NAME_GEOIPS_ZIP.sha256sum"

    actual val ZIP_SHA256_GEOIP: String get() = _ZIP_SHA256_GEOIP
    actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf("geoip", "geoip6")

    actual val FILE_NAME_GEOIPS_ZIP: String get() = _FILE_NAME_GEOIPS_ZIP
    actual val FILE_NAME_GEOIPS_ZIP_SHA256: String get() = _FILE_NAME_GEOIPS_ZIP_SHA256

    const val ZIP_SHA256_LINUX_X64 = "fb683004fc1e728620b9f9bf021a8075ffe5e7f8011db783fc8eb5540cf327e5"
    const val ZIP_SHA256_LINUX_X86 = "77550f5c40b4c1d58d7a4ca5b15bdbf3716ab4f4a03920f75be719f205254ea0"
    const val ZIP_SHA256_MACOS_X64 = "7de9535cf6d527becc6fcbbc0515c8a8a4603898742cb6fd99d90691b71b86d8"
    const val ZIP_SHA256_MINGW_X64 = "860595649e4855b97d833430f3a53d06392b39e26ebb43a55b9115fb43526b10"
    const val ZIP_SHA256_MINGW_X86 = "cf4ec2b27b30ebab970b5d1ce80af5d9d8d5fcbea66f48d756afdd4ee2e25353"

    const val FILE_NAME_KMPTOR_ZIP = "kmptor.zip"
    const val FILE_NAME_KMPTOR_ZIP_SHA256 = "$FILE_NAME_KMPTOR_ZIP.sha256sum"

    val ZIP_MANIFEST_LINUX_X64 get() = listOf("libcrypto.so.1.1", "libevent-2.1.so.7", "libssl.so.1.1", "libstdc++/libstdc++.so.6", "tor")
    val ZIP_MANIFEST_LINUX_X86 get() = listOf("libcrypto.so.1.1", "libevent-2.1.so.7", "libssl.so.1.1", "libstdc++/libstdc++.so.6", "tor")
    val ZIP_MANIFEST_MACOS_X64 get() = listOf("libevent-2.1.7.dylib", "tor")
    val ZIP_MANIFEST_MINGW_X64 get() = listOf("libcrypto-1_1-x64.dll", "libevent-2-1-7.dll", "libevent_core-2-1-7.dll", "libevent_extra-2-1-7.dll", "libgcc_s_seh-1.dll", "libssl-1_1-x64.dll", "libssp-0.dll", "libwinpthread-1.dll", "tor.exe", "zlib1.dll")
    val ZIP_MANIFEST_MINGW_X86 get() = listOf("libcrypto-1_1.dll", "libevent-2-1-7.dll", "libevent_core-2-1-7.dll", "libevent_extra-2-1-7.dll", "libgcc_s_dw2-1.dll", "libssl-1_1.dll", "libssp-0.dll", "libwinpthread-1.dll", "tor.exe", "zlib1.dll")
}
