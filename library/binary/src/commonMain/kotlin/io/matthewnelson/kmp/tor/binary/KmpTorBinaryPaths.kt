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

import kotlin.jvm.JvmField

/**
 * The absolute file paths of installed resources
 * via [KmpTorBinary.install].
 * */
public class KmpTorBinaryPaths(
    @JvmField
    public val geoip: String,
    @JvmField
    public val geoip6: String,
    @JvmField
    public val tor: String,
) {

    override fun equals(other: Any?): Boolean {
        return  other is KmpTorBinaryPaths
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
            appendLine("KmpTorBinaryPaths: [")
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
