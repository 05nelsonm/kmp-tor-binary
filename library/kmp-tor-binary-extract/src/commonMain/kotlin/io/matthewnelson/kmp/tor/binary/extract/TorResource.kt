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
expect sealed class TorResource private constructor() {

    abstract val sha256sum: String

    /**
     * Resource model for geoip and geoip6 files.
     *
     * @see [TorResourceGeoip]
     * @see [TorResourceGeoip6]
     * */
    sealed class Geoips(): TorResource {
        abstract val resourcePath: String
    }

    /**
     * Resource model for Tor binaries.
     * */
    sealed class Binaries: TorResource {
        abstract val resourceDirPath: String
        abstract val resourceManifest: List<String>
    }
}

object TorResourceGeoip: TorResource.Geoips() {
    override val resourcePath: String get() = "kmptor/geoip.gz"
    /* GEOIP */ override val sha256sum: String get() = "7e777efc194ea9788171636085b19875d19397d3249fbb88136534037a3dc38f"
}

object TorResourceGeoip6: TorResource.Geoips() {
    override val resourcePath: String get() = "kmptor/geoip6.gz"
    /* GEOIP6 */ override val sha256sum: String get() = "f11bd1d7546cad00b6db0a1594f3ac1daf9f541004fd7efb5414e068693d6add"
}
