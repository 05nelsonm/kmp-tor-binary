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
    /* LINUX_X64 */ override val sha256sum: String get() = "b4db051903529df59db8abdd05203df71fb0e96a4c57e0527ec0de611c8b2555"
}

object TorResourceLinuxX86: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/linux/x86"
    /* LINUX_X86 */ override val resourceManifest: List<String> get() = listOf("libcrypto.so.1.1.gz", "libevent-2.1.so.7.gz", "libssl.so.1.1.gz", "libstdc++.so.6.gz", "tor.gz")
    /* LINUX_X86 */ override val sha256sum: String get() = "bfec4f6f7a93876bd4c06baa87eedc2069089ffe3206c74af98254ad9784594a"
}

object TorResourceMacosX64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/macos/x64"
    /* MACOS_X64 */ override val resourceManifest: List<String> get() = listOf("libevent-2.1.7.dylib.gz", "tor.gz")
    /* MACOS_X64 */ override val sha256sum: String get() = "5875f96de2b1de1a39709bae888ab3bb4506f45dcb8ab659078050ee6ddfb3ff"
}

object TorResourceMacosArm64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/macos/arm64"
    /* MACOS_ARM64 */ override val resourceManifest: List<String> get() = listOf("libevent-2.1.7.dylib.gz", "tor.gz")
    /* MACOS_ARM64 */ override val sha256sum: String get() = "e91de27613c830efa57de2a0f97b3670fd6f6f10513d14bbec5697fc3206bd3e"
}

object TorResourceMingwX64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/mingw/x64"
    /* MINGW_X64 */ override val resourceManifest: List<String> get() = listOf("tor.exe.gz", "tor-gencert.exe.gz")
    /* MINGW_X64 */ override val sha256sum: String get() = "09e1a10f10f39cb3fb0de5bad80b08d6c381590c9bd1dab518667dedba6459b9"
}

object TorResourceMingwX86: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/mingw/x86"
    /* MINGW_X86 */ override val resourceManifest: List<String> get() = listOf("tor.exe.gz", "tor-gencert.exe.gz")
    /* MINGW_X86 */ override val sha256sum: String get() = "54b848f1d428de1460183e5911fbff48cf4c241e2341e47c94f04081ed88a8e2"
}
