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

import io.matthewnelson.kmp.tor.binary.internal.*
import io.matthewnelson.kmp.tor.binary.internal.ARCH_MAP
import io.matthewnelson.kmp.tor.binary.internal.DefaultProcessRunner
import io.matthewnelson.kmp.tor.binary.internal.PATH_MAP_FILES
import io.matthewnelson.kmp.tor.binary.internal.PATH_OS_RELEASE
import io.matthewnelson.kmp.tor.binary.internal.ProcessRunner

public actual class OSInfo private actual constructor(
    private val process: ProcessRunner,
    private val pathMapFiles: String,
    private val pathOSRelease: String
) {

    public actual companion object {

        public actual val INSTANCE: OSInfo = get()

        internal fun get(
            process: ProcessRunner = DefaultProcessRunner,
            pathMapFiles: String = PATH_MAP_FILES,
            pathOSRelease: String = PATH_OS_RELEASE,
        ): OSInfo = OSInfo(process, pathMapFiles, pathOSRelease)
    }

    public actual val osHost: OSHost by lazy {
        osHost(platform()?.ifBlank { null } ?: "unknown")
    }

    public actual val osArch: OSArch by lazy {
        osArch(arch()?.ifBlank { null } ?: "unknown")
    }

    // https://nodejs.org/api/os.html#osplatform
    internal fun osHost(name: String): OSHost {
        return when (val lName = name.lowercase()) {
            "win32" -> OSHost.Windows
            "darwin" -> OSHost.MacOS
            "freebsd" -> OSHost.FreeBSD
            "android" -> OSHost.Linux.Android
            "linux" -> {
                if (isLinuxMusl()) {
                    OSHost.Linux.Musl
                } else {
                    OSHost.Linux.Libc
                }
            }
            else -> OSHost.Unknown(lName)
        }
    }

    // https://nodejs.org/api/os.html#osarch
    internal fun osArch(name: String): OSArch {
        val lArch = name.lowercase()

        val mapped = ARCH_MAP[lArch]

        return when {
            mapped != null -> mapped
            lArch.startsWith("arm") -> resolveArmArchType()
            else -> null
        } ?: OSArch.Unsupported(lArch)
    }

    private fun isLinuxMusl(): Boolean {
        if (!existsSync(pathMapFiles)) return false

        try {
            readdirSync(pathMapFiles, OptionsReadDir()).forEach { entry ->
                var path = normalize(resolve(pathMapFiles, entry))
                if (lstatSync(path).isSymbolicLink()) {
                    path = readlinkSync(path)
                }

//                println(path)

                if (path.lowercase().contains("musl")) {
                    return true
                }
            }
        } catch (_: Throwable) {}

        return false
    }

    private fun resolveArmArchType(): OSArch? {
        // TODO
        return null
    }
}
