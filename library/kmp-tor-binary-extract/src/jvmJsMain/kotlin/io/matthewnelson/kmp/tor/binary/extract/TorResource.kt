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
public actual sealed class TorResource private actual constructor() {

    public actual abstract val sha256sum: String

    /**
     * Resource model for geoip and geoip6 files.
     *
     * @see [TorResourceGeoip]
     * @see [TorResourceGeoip6]
     * */
    public actual sealed class Geoips: TorResource() {
        public actual abstract val resourcePath: String
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
    public actual sealed class Binaries: TorResource() {
        public actual abstract val resourceDirPath: String
        public actual abstract val resourceManifest: List<String>
    }
}

public object TorResourceLinuxX64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/linux/x64"
    /* LINUX_X64 */ override val resourceManifest: List<String> get() = listOf("libcrypto.so.1.1.gz", "libevent-2.1.so.7.gz", "libssl.so.1.1.gz", "libstdc++.so.6.gz", "tor.gz")
    /* LINUX_X64 */ override val sha256sum: String get() = "a766e07310b1ede3a06ef889cb46023fed5dc8044b326c20adf342242be92ec6"
}

public object TorResourceLinuxX86: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/linux/x86"
    /* LINUX_X86 */ override val resourceManifest: List<String> get() = listOf("libcrypto.so.1.1.gz", "libevent-2.1.so.7.gz", "libssl.so.1.1.gz", "libstdc++.so.6.gz", "tor.gz")
    /* LINUX_X86 */ override val sha256sum: String get() = "acf8e837ebbd63c5ca1cdf577ace02a6066a737974aae763991e4a0f28e56fb0"
}

public object TorResourceMacosX64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/macos/x64"
    /* MACOS_X64 */ override val resourceManifest: List<String> get() = listOf("libevent-2.1.7.dylib.gz", "tor.gz")
    /* MACOS_X64 */ override val sha256sum: String get() = "f7cda13f5add304dcdbe6965ebc3953b4678004c0373c5b8cecf0decf88566e7"
}

public object TorResourceMacosArm64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/macos/arm64"
    /* MACOS_ARM64 */ override val resourceManifest: List<String> get() = listOf("libevent-2.1.7.dylib.gz", "tor.gz")
    /* MACOS_ARM64 */ override val sha256sum: String get() = "3538d8316acde0f08d9746f04b91f47eeb5df24b662391596570e4716ab06797"
}

public object TorResourceMingwX64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/mingw/x64"
    /* MINGW_X64 */ override val resourceManifest: List<String> get() = listOf("tor.exe.gz", "tor-gencert.exe.gz")
    /* MINGW_X64 */ override val sha256sum: String get() = "b9647acee2af7196e809a47487f36b308be735095042f124f236c09e9d986882"
}

public object TorResourceMingwX86: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/mingw/x86"
    /* MINGW_X86 */ override val resourceManifest: List<String> get() = listOf("tor.exe.gz", "tor-gencert.exe.gz")
    /* MINGW_X86 */ override val sha256sum: String get() = "dfc1b39837957843334431bc341223f1805aec88c24666fba1b9f55a16d5a31e"
}
