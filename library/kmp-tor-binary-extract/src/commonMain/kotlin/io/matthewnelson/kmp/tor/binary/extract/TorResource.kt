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
    /* GEOIP */ override val sha256sum: String get() = "8fa0d2bf75a29d346e856eff63b9ed0f7740df578bdcac3b33364f4a88a168e9"
}

public object TorResourceGeoip6: TorResource.Geoips() {
    override val resourcePath: String get() = "kmptor/geoip6.gz"
    /* GEOIP6 */ override val sha256sum: String get() = "c9b87ed1a97adb5040a18ffe7117276cd5a112eb27c588eb989f4abe0ec58879"
}
