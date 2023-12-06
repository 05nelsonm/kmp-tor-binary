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
package io.matthewnelson.kmp.tor.binary

import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.core.SynchronizedObject
import io.matthewnelson.kmp.tor.binary.core.api.IOException
import io.matthewnelson.kmp.tor.binary.core.api.Installer
import io.matthewnelson.kmp.tor.binary.core.synchronized
import io.matthewnelson.kmp.tor.binary.internal.*
import io.matthewnelson.kmp.tor.binary.internal.ALIAS_GEOIP
import io.matthewnelson.kmp.tor.binary.internal.ALIAS_GEOIP6
import io.matthewnelson.kmp.tor.binary.internal.RESOURCE_CONFIG
import io.matthewnelson.kmp.tor.binary.internal.findLibTor
import kotlin.concurrent.Volatile

public class KmpTorBinary(
    installationDir: String
): Installer<Installer.Paths.Tor>(
    installationDir
) {

    private val installer = RealInstaller()

    @Throws(IllegalStateException::class, IOException::class)
    public override fun install(): Paths.Tor = installer.install()

    @OptIn(InternalKmpTorBinaryApi::class)
    private inner class RealInstaller: SynchronizedObject() {

        @Volatile
        private var paths: Paths.Tor? = null

        fun install(): Paths.Tor {
            return paths ?: synchronized(this) {
                paths ?: RESOURCE_CONFIG.extractTo(installationDir)
                    .findLibTor()
                    .let { map ->

                        // If an exception has not been encountered at
                        // this point, the map will contain all 3 paths.
                        Paths.Tor(
                            geoip = map[ALIAS_GEOIP]!!,
                            geoip6 = map[ALIAS_GEOIP6]!!,
                            tor = map[ALIAS_TOR]!!,
                        )
                    }.also { paths = it }
            }
        }
    }
}
