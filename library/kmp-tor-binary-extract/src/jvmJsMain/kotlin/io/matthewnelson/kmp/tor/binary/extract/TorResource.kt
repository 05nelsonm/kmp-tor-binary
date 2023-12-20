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
    /* LINUX_X64 */ override val resourceManifest: List<String> get() = listOf("libcrypto.so.3.gz", "libevent-2.1.so.7.gz", "libssl.so.3.gz", "libstdc++.so.6.gz", "tor.gz")
    /* LINUX_X64 */ override val sha256sum: String get() = "9337f2861e1dce1be6a7029048fec0d5c4bb8d513c849f3a348e601e2cd59c60"
}

public object TorResourceLinuxX86: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/linux/x86"
    /* LINUX_X86 */ override val resourceManifest: List<String> get() = listOf("libcrypto.so.3.gz", "libevent-2.1.so.7.gz", "libssl.so.3.gz", "libstdc++.so.6.gz", "tor.gz")
    /* LINUX_X86 */ override val sha256sum: String get() = "208ecd5b1748c14988ee1c8d5135a115bf2613c5af1cba2f6a12501f925a88bc"
}

public object TorResourceMacosX64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/macos/x64"
    /* MACOS_X64 */ override val resourceManifest: List<String> get() = listOf("libevent-2.1.7.dylib.gz", "tor.gz")
    /* MACOS_X64 */ override val sha256sum: String get() = "44373f166a05b9bc381e54215841ddc75777532e5de911d89fb2287a032128d1"
}

public object TorResourceMacosArm64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/macos/arm64"
    /* MACOS_ARM64 */ override val resourceManifest: List<String> get() = listOf("libevent-2.1.7.dylib.gz", "tor.gz")
    /* MACOS_ARM64 */ override val sha256sum: String get() = "2fa2704459000db6b4539aa0d034b69fcff5c16c1c30baf598f6a04068a22dab"
}

public object TorResourceMingwX64: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/mingw/x64"
    /* MINGW_X64 */ override val resourceManifest: List<String> get() = listOf("tor-gencert.exe.gz", "tor.exe.gz")
    /* MINGW_X64 */ override val sha256sum: String get() = "a5e681100dd4d61735117f580598ddac8be149a6b552ed8d23ccdcc75af4fea4"
}

public object TorResourceMingwX86: TorResource.Binaries() {
    override val resourceDirPath: String get() = "kmptor/mingw/x86"
    /* MINGW_X86 */ override val resourceManifest: List<String> get() = listOf("tor-gencert.exe.gz", "tor.exe.gz")
    /* MINGW_X86 */ override val sha256sum: String get() = "05e71ec79f9deb9a3e04ee564d4633c806db45bc5b76a116efad25e776c45222"
}
