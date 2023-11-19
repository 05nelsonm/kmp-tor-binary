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
@file:Suppress("KotlinRedundantDiagnosticSuppress")

package io.matthewnelson.kmp.tor.binary.internal

import io.matthewnelson.kmp.tor.binary.util.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.util.OSArch
import io.matthewnelson.kmp.tor.binary.util.OSHost
import io.matthewnelson.kmp.tor.binary.util.Resource
import kotlin.jvm.JvmSynthetic

@JvmSynthetic
internal const val PATH_RESOURCE_GEOIP = "/io/matthewnelson/kmp/tor/binary/geoip.gz"
@JvmSynthetic
internal const val PATH_RESOURCE_GEOIP6 = "/io/matthewnelson/kmp/tor/binary/geoip6.gz"

@JvmSynthetic
@OptIn(InternalKmpTorBinaryApi::class)
internal expect fun Resource.Config.Builder.configure()

@JvmSynthetic
@Suppress("NOTHING_TO_INLINE")
@OptIn(InternalKmpTorBinaryApi::class)
internal inline fun OSHost.toTorResourcePathOrNull(osArch: OSArch): String? {
    if (!osArch.isSupportedBy(this)) return null

    val name = when (this) {
        is OSHost.FreeBSD,
        is OSHost.Linux,
        is OSHost.MacOS -> "tor.gz"
        is OSHost.Windows -> "tor.exe.gz"
        is OSHost.Unknown -> return null
    }

    return "/io/matthewnelson/kmp/tor/binary/native/$this/$osArch/$name"
}
