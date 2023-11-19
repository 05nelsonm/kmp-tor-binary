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

import io.matthewnelson.kmp.tor.binary.internal.*
import io.matthewnelson.kmp.tor.binary.internal.ALIAS_GEOIP
import io.matthewnelson.kmp.tor.binary.internal.ALIAS_GEOIP6
import io.matthewnelson.kmp.tor.binary.internal.ALIAS_TOR
import io.matthewnelson.kmp.tor.binary.internal.configure
import io.matthewnelson.kmp.tor.binary.internal.findLibTor
import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.core.Resource
import java.io.File
import java.nio.file.Path
import kotlin.concurrent.Volatile
import kotlin.io.path.absolutePathString

public actual class KmpTorBinary
public actual constructor(
    @JvmField
    public actual val installationDir: String
) {

    public constructor(installationDir: File): this(installationDir.absolutePath)
    public constructor(installationDir: Path): this(installationDir.absolutePathString())

    @Volatile
    private var paths: KmpTorBinaryPaths? = null

    @Throws(Exception::class)
    public actual fun install(): KmpTorBinaryPaths {
        return paths ?: synchronized(this) {
            @OptIn(InternalKmpTorBinaryApi::class)
            paths ?: Config.extractTo(installationDir).findLibTor().let { map ->
                // if extractTo did not throw exception, map will be
                // populated with all resource aliases.
                KmpTorBinaryPaths(
                    geoip = map[ALIAS_GEOIP]!!,
                    geoip6 = map[ALIAS_GEOIP6]!!,
                    tor = map[ALIAS_TOR]!!,
                )
            }.also { paths = it }
        }
    }

    internal actual companion object {

        @get:JvmSynthetic
        @OptIn(InternalKmpTorBinaryApi::class)
        internal actual val Config: Resource.Config by lazy {
            // lazily load so that OSInfo doesn't
            // hammer main thread.
            Resource.Config.create { configure() }
        }
    }
}
