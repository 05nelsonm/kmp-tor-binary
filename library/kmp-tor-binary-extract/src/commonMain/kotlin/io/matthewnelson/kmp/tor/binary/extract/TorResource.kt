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
@file:Suppress("SpellCheckingInspection")

package io.matthewnelson.kmp.tor.binary.extract

/**
 * Base sealed class for modeling resources provided by
 * the kmp-tor-binary library.
 *
 * @see [Extractor]
 * */
public expect sealed class TorResource private constructor() {

    public abstract val sha256sum: String

    /**
     * Resource model for geoip and geoip6 files.
     *
     * @see [TorResourceGeoip]
     * @see [TorResourceGeoip6]
     * */
    public sealed class Geoips(): TorResource {
        public abstract val resourcePath: String
    }

    /**
     * Resource model for Tor binaries.
     * */
    public sealed class Binaries: TorResource {
        public abstract val resourceDirPath: String
        public abstract val resourceManifest: List<String>
    }
}

public object TorResourceGeoip: TorResource.Geoips() {
    override val resourcePath: String get() = "kmptor/geoip.gz"
    /* GEOIP */ override val sha256sum: String get() = "24abd2396bed565d5ef1ecc6d37c0a7fc97c987e3250965f0252f9c9ed72d280"
}

public object TorResourceGeoip6: TorResource.Geoips() {
    override val resourcePath: String get() = "kmptor/geoip6.gz"
    /* GEOIP6 */ override val sha256sum: String get() = "03eafac45dd0702f8f9349e8d77be8e4d40ce7aa8ff82e85387748b165b5c435"
}
