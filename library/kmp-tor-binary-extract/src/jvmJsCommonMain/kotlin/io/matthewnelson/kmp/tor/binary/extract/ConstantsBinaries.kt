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
    private const val _ZIP_SHA256_GEOIP = "45e6e697b9d738ea04e25d323902765172ac75d3d6668cd4a2c07a9f6649253e"
    private const val _FILE_NAME_GEOIPS_ZIP = "geoips.zip"
    private const val _FILE_NAME_GEOIPS_ZIP_SHA256 = "$_FILE_NAME_GEOIPS_ZIP.sha256sum"

    actual val ZIP_SHA256_GEOIP: String get() = _ZIP_SHA256_GEOIP
    actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf("geoip", "geoip6")

    actual val FILE_NAME_GEOIPS_ZIP: String get() = _FILE_NAME_GEOIPS_ZIP
    actual val FILE_NAME_GEOIPS_ZIP_SHA256: String get() = _FILE_NAME_GEOIPS_ZIP_SHA256

    const val ZIP_SHA256_LINUX_X64 = "b20d26afdf3756f1477e6439eca5c032bd9631f7ce0083113b025a3d332660a7"
    const val ZIP_SHA256_LINUX_X86 = "c7a2446a68364ef5b465909f1d6084c77c173c3c60f56046ef93c2068fddd8f6"
    const val ZIP_SHA256_MACOS_X64 = "4c31390efc5876ff6c0f6a43bee6234e391e0a81c44d5daef283023ffd7cea00"
    const val ZIP_SHA256_MINGW_X64 = "7f1c6fdd1865d78da906c920391f343d7e119cf40d55dd49b41070f9662359c1"
    const val ZIP_SHA256_MINGW_X86 = "791d6958a5db1d22647703cebc6b0b97d4db77a5db912b1e87055e8f47609cbb"

    const val FILE_NAME_KMPTOR_ZIP = "kmptor.zip"
    const val FILE_NAME_KMPTOR_ZIP_SHA256 = "$FILE_NAME_KMPTOR_ZIP.sha256sum"

    val ZIP_MANIFEST_LINUX_X64 get() = listOf("libcrypto.so.1.1", "libevent-2.1.so.7", "libssl.so.1.1", "libstdc++/libstdc++.so.6", "tor")
    val ZIP_MANIFEST_LINUX_X86 get() = listOf("libcrypto.so.1.1", "libevent-2.1.so.7", "libssl.so.1.1", "libstdc++/libstdc++.so.6", "tor")
    val ZIP_MANIFEST_MACOS_X64 get() = listOf("libevent-2.1.7.dylib", "tor")
    val ZIP_MANIFEST_MINGW_X64 get() = listOf("libcrypto-1_1-x64.dll", "libevent-2-1-7.dll", "libevent_core-2-1-7.dll", "libevent_extra-2-1-7.dll", "libgcc_s_seh-1.dll", "libssl-1_1-x64.dll", "libssp-0.dll", "libwinpthread-1.dll", "tor.exe", "zlib1.dll")
    val ZIP_MANIFEST_MINGW_X86 get() = listOf("libcrypto-1_1.dll", "libevent-2-1-7.dll", "libevent_core-2-1-7.dll", "libevent_extra-2-1-7.dll", "libgcc_s_dw2-1.dll", "libssl-1_1.dll", "libssp-0.dll", "libwinpthread-1.dll", "tor.exe", "zlib1.dll")
}
