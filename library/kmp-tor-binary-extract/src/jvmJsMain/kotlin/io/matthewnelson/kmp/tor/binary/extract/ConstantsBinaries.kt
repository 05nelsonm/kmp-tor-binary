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
    private const val _ZIP_SHA256_GEOIP = "2dce16ea6a5ae3e82c90a1b82bb5ff2eb2a91be98ec2ed539ad722c246752705"
    private const val _FILE_NAME_GEOIPS_ZIP = "geoips.zip"
    private const val _FILE_NAME_GEOIPS_ZIP_SHA256 = "$_FILE_NAME_GEOIPS_ZIP.sha256sum"

    actual val ZIP_SHA256_GEOIP: String get() = _ZIP_SHA256_GEOIP
    actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf("geoip", "geoip6")

    actual val FILE_NAME_GEOIPS_ZIP: String get() = _FILE_NAME_GEOIPS_ZIP
    actual val FILE_NAME_GEOIPS_ZIP_SHA256: String get() = _FILE_NAME_GEOIPS_ZIP_SHA256

    const val ZIP_SHA256_LINUX_X64 = "5003d1866d9718d6d2a3c523baf3d91d0a66abc3c75e4ad872ff6d8a879bc6e0"
    const val ZIP_SHA256_LINUX_X86 = "18e1ec895bbb19a59b7ccb73266cafed8d852e19dfc8de0164ccac2642a410fa"
    const val ZIP_SHA256_MACOS_X64 = "23b24fe40c4294b5dfadadca1d13a2950acc08feddbd97bf06cbe4603f64558d"
    const val ZIP_SHA256_MINGW_X64 = "46e195b4e9bceafa5e0ffa4eace400368439b03af6e0718ac3cf265b5b2ac871"
    const val ZIP_SHA256_MINGW_X86 = "dfa4f7c8aac4e50a5646606ac81b3e33b55380b2acb3d9db3dab2530d80249cd"

    const val FILE_NAME_KMPTOR_ZIP = "kmptor.zip"
    const val FILE_NAME_KMPTOR_ZIP_SHA256 = "$FILE_NAME_KMPTOR_ZIP.sha256sum"

    val ZIP_MANIFEST_LINUX_X64 get() = listOf("libcrypto.so.1.1", "libevent-2.1.so.7", "libssl.so.1.1", "libstdc++/libstdc++.so.6", "tor")
    val ZIP_MANIFEST_LINUX_X86 get() = listOf("libcrypto.so.1.1", "libevent-2.1.so.7", "libssl.so.1.1", "libstdc++/libstdc++.so.6", "tor")
    val ZIP_MANIFEST_MACOS_X64 get() = listOf("libevent-2.1.7.dylib", "tor")
    val ZIP_MANIFEST_MINGW_X64 get() = listOf("tor.exe")
    val ZIP_MANIFEST_MINGW_X86 get() = listOf("tor.exe")
}
