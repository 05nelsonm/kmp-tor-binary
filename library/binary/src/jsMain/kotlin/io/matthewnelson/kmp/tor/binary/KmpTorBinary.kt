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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.matthewnelson.kmp.tor.binary

import io.matthewnelson.kmp.tor.binary.internal.ALIAS_GEOIP
import io.matthewnelson.kmp.tor.binary.internal.ALIAS_GEOIP6
import io.matthewnelson.kmp.tor.binary.internal.ALIAS_TOR
import io.matthewnelson.kmp.tor.binary.internal.configure
import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.core.Resource

public actual class KmpTorBinary
public actual constructor(
    public actual val installationDir: String
) {

    private var paths: KmpTorBinaryPaths? = null

    @Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
    public actual fun install(): KmpTorBinaryPaths {
        // TODO: synchronized
        @OptIn(InternalKmpTorBinaryApi::class)
        return paths ?: Config.extractTo(installationDir).let { map ->
            // if extractTo did not throw exception, map will be
            // populated with all resource aliases.
            KmpTorBinaryPaths(
                map[ALIAS_GEOIP]!!,
                map[ALIAS_GEOIP6]!!,
                map[ALIAS_TOR]!!,
            )
        }.also { paths = it }
    }

    internal actual companion object {

        @OptIn(InternalKmpTorBinaryApi::class)
        internal actual val Config: Resource.Config by lazy {
            // lazily load so that OSInfo doesn't
            // hammer main thread.
            Resource.Config.create { configure() }
        }
    }
}
