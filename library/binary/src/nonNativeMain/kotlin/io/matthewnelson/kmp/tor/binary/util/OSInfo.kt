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

package io.matthewnelson.kmp.tor.binary.util

import io.matthewnelson.kmp.tor.binary.internal.ProcessRunner
import kotlin.jvm.JvmField
import kotlin.jvm.JvmName

internal const val PATH_MAP_FILES = "/proc/self/map_files"
internal const val PATH_OS_RELEASE = "/etc/os-release"

public expect class OSInfo private constructor(
    process: ProcessRunner,
    pathMapFiles: String,
    pathOSRelease: String,
) {

    @get:JvmName("osHost")
    public val osHost: OSHost
    @get:JvmName("osArch")
    public val osArch: OSArch

    public companion object {

        @JvmField
        public val INSTANCE: OSInfo
    }
}
