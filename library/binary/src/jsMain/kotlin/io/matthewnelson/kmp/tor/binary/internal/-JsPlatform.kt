/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.kmp.tor.binary.internal

import io.matthewnelson.kmp.tor.binary.ALIAS_GEOIP
import io.matthewnelson.kmp.tor.binary.ALIAS_GEOIP6
import io.matthewnelson.kmp.tor.binary.ALIAS_TOR
import io.matthewnelson.kmp.tor.binary.util.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.util.OSHost
import io.matthewnelson.kmp.tor.binary.util.OSInfo
import io.matthewnelson.kmp.tor.binary.util.Resource

@OptIn(InternalKmpTorBinaryApi::class)
@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
internal actual fun Resource.Config.Builder.configure() {
    val module = "kmp-tor-binary-resources"

    resource(ALIAS_GEOIP) {
        isExecutable = false
        moduleName = module
        resourcePath = PATH_RESOURCE_GEOIP
    }

    resource(ALIAS_GEOIP6) {
        isExecutable = false
        moduleName = module
        resourcePath = PATH_RESOURCE_GEOIP6
    }

    val host = OSInfo.INSTANCE.osHost

    if (host is OSHost.Unknown) {
        error("Unknown host[$host]")
        return
    }

    val arch = OSInfo.INSTANCE.osArch

    val torResourcePath = host.toTorResourcePath(arch)

    if (torResourcePath == null) {
        error("Unsupported architecutre[$arch] for host[$host]")
        return
    }

    resource(ALIAS_TOR) {
        isExecutable = true
        moduleName = module
        resourcePath = torResourcePath
    }
}
