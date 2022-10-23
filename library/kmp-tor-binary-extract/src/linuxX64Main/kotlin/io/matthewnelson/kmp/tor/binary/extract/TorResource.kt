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
