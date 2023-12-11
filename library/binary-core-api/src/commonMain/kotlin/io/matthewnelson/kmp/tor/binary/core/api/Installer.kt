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
package io.matthewnelson.kmp.tor.binary.core.api

import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.IOException
import kotlin.jvm.JvmField

/**
 * Abstraction for installing tor resources
 *
 * @sample [io.matthewnelson.kmp.tor.binary.KmpTorBinary]
 * */
public abstract class Installer<P: Installer.Paths>(
    @JvmField
    public val installationDir: File,
) {

    /**
     * Installs resources to the specified [installationDir].
     *
     * @throws [IllegalStateException] if there was an error configuring
     *   resources for extraction. Only applicable to Android/Jvm/Js
     * @throws [IOException] if writing resources to the specified
     *   [installationDir] failed.
     * */
    @Throws(IllegalStateException::class, IOException::class)
    public abstract fun install(): P

    public abstract class Paths private constructor() {

        public class Tor(
            @JvmField
            public val geoip: File,
            @JvmField
            public val geoip6: File,
            @JvmField
            public val tor: File,
        ): Paths() {

            override fun equals(other: Any?): Boolean {
                return  other is Tor
                        && other.geoip == geoip
                        && other.geoip6 == geoip6
                        && other.tor == tor
            }

            override fun hashCode(): Int {
                var result = 17
                result = result * 31 + geoip.hashCode()
                result = result * 31 + geoip6.hashCode()
                result = result * 31 + tor.hashCode()
                return result
            }

            override fun toString(): String {
                return buildString {
                    appendLine("Paths.Tor: [")
                    append("    geoip: ")
                    appendLine(geoip)
                    append("    geoip6: ")
                    appendLine(geoip6)
                    append("    tor: ")
                    appendLine(tor)
                    append(']')
                }
            }
        }
    }
}
