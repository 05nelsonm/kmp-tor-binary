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

/**
 * Base sealed class for modeling resources provided by
 * the kmp-tor-binary library.
 *
 * @see [Extractor]
 * */
actual sealed class TorResource private actual constructor() {

    actual abstract val sha256sum: String

    /**
     * Resource model for geoip and geoip6 files.
     *
     * @see [TorResourceGeoip]
     * @see [TorResourceGeoip6]
     * */
    actual sealed class Geoips: TorResource() {
        actual abstract val resourcePath: String
    }

    /**
     * Resource model for Tor binaries.
     *
     * @see [TorResourceLinuxX64]
     * @see [TorResourceLinuxX86]
     * @see [TorResourceMacosX64]
     * @see [TorResourceMingwX64]
     * @see [TorResourceMingwX86]
     * */
    actual sealed class Binaries: TorResource() {
        actual abstract val resourceDirPath: String
        actual abstract val resourceManifest: List<String>
    }
}

object TorResourceLinuxX64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/linux/x64"
    /* LINUX_X64 */ override val resourceManifest: List<String> get() = listOf("libcrypto.so.1.1.gz", "libevent-2.1.so.7.gz", "libssl.so.1.1.gz", "libstdc++.so.6.gz", "tor.gz")
    /* LINUX_X64 */ override val sha256sum: String get() = "7ea1e0a19f63d2542b34e1cfe8f8135b278a0eea5a7fd8d25e78e12972834ae2"
}

object TorResourceLinuxX86: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/linux/x86"
    /* LINUX_X86 */ override val resourceManifest: List<String> get() = listOf("libcrypto.so.1.1.gz", "libevent-2.1.so.7.gz", "libssl.so.1.1.gz", "libstdc++.so.6.gz", "tor.gz")
    /* LINUX_X86 */ override val sha256sum: String get() = "187d67cd9307c4993563b6b1a960126a1501b710f4af82d58ea442be11bd965d"
}

object TorResourceMacosX64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/macos/x64"
    /* MACOS_X64 */ override val resourceManifest: List<String> get() = listOf("libevent-2.1.7.dylib.gz", "tor.gz")
    /* MACOS_X64 */ override val sha256sum: String get() = "7c5687a3916483ee6c402ed77a99c6025c19b1bfdf54cb6e2e00601642451c82"
}

object TorResourceMacosArm64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/macos/arm64"
    /* MACOS_ARM64 */ override val resourceManifest: List<String> get() = listOf("libevent-2.1.7.dylib.gz", "tor.gz")
    /* MACOS_ARM64 */ override val sha256sum: String get() = "0d9217a47af322d72e9213c1afdd53f4f571ff0483d8053726e56efeec850ff1"
}

object TorResourceMingwX64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/mingw/x64"
    /* MINGW_X64 */ override val resourceManifest: List<String> get() = listOf("tor.exe.gz", "tor-gencert.exe.gz")
    /* MINGW_X64 */ override val sha256sum: String get() = "f228252a094f3fed8d9d08d6b98f1925644cfbbf59fbd08c566bf184027068e4"
}

object TorResourceMingwX86: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/mingw/x86"
    /* MINGW_X86 */ override val resourceManifest: List<String> get() = listOf("tor.exe.gz", "tor-gencert.exe.gz")
    /* MINGW_X86 */ override val sha256sum: String get() = "6c5bb29ef588c6685283b68d616586781ae84bd0ae20ee7eb3aba66351c3a051"
}
