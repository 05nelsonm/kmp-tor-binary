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
package io.matthewnelson.kmp.tor.binary.core.internal

import io.matthewnelson.kmp.tor.binary.core.ImmutableMap
import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.core.OSArch
import kotlin.jvm.JvmSynthetic

@JvmSynthetic
internal const val PATH_MAP_FILES = "/proc/self/map_files"
@JvmSynthetic
internal const val PATH_OS_RELEASE = "/etc/os-release"

@get:JvmSynthetic
@InternalKmpTorBinaryApi
internal val ARCH_MAP: ImmutableMap<String, OSArch> by lazy {
    ImmutableMap.of(
        Pair("x86", OSArch.X86),
        Pair("i386", OSArch.X86),
        Pair("i486", OSArch.X86),
        Pair("i586", OSArch.X86),
        Pair("i686", OSArch.X86),
        Pair("pentium", OSArch.X86),

        Pair("x64", OSArch.X86_64),
        Pair("x86_64", OSArch.X86_64),
        Pair("amd64", OSArch.X86_64),
        Pair("em64t", OSArch.X86_64),
        Pair("universal", OSArch.X86_64),

        Pair("aarch64", OSArch.Aarch64),
        Pair("arm64", OSArch.Aarch64),
    )
}
