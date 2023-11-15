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

import io.matthewnelson.kmp.tor.binary.internal.DefaultProcessRunner
import io.matthewnelson.kmp.tor.binary.internal.ProcessRunner
import java.io.File
import java.util.*

/**
 * Implementation based off of:
 *
 * [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc/blob/master/src/main/java/org/sqlite/util/OSInfo.java)
 * */
public actual class OSInfo private actual constructor(private val process: ProcessRunner) {

    public actual companion object {

        @JvmField
        public actual val INSTANCE: OSInfo = get(DefaultProcessRunner)

        @JvmSynthetic
        @JvmStatic
        internal fun get(process: ProcessRunner): OSInfo = OSInfo(process)
    }

    private val archMap: Map<String, OSArch> by lazy {
        mutableMapOf<String, OSArch>().apply {
            put("x86", OSArch.X86)
            put("i386", OSArch.X86)
            put("i486", OSArch.X86)
            put("i586", OSArch.X86)
            put("i686", OSArch.X86)
            put("pentium", OSArch.X86)

            put("x86_64", OSArch.X86_64)
            put("amd64", OSArch.X86_64)
            put("em64t", OSArch.X86_64)
            put("universal", OSArch.X86_64) // openjdk7 Mac

            put("aarch64", OSArch.Aarch64)
            put("arm64", OSArch.Aarch64)
        }
    }

    public actual val osHost: OSHost by lazy {
        osHost(System.getProperty("os.name"))
    }

    public actual val osArch: OSArch by lazy {
        osArch(System.getProperty("os.arch"))
    }

    @JvmSynthetic
    internal fun osHost(name: String): OSHost {
        val lName = name.lowercase(Locale.US)

        return when {
            lName.contains("windows") -> OSHost.Windows
            lName.contains("mac") -> OSHost.MacOS
            lName.contains("darwin") -> OSHost.MacOS
            lName.contains("freebsd") -> null // Not yet supported
            isAndroidRuntime() -> OSHost.Linux.Android
            isAndroidTermux() -> OSHost.Linux.Android
            isLinuxMusl() -> null // Not yet supported
            lName.contains("linux") -> OSHost.Linux.Libc
            else -> null
        } ?: OSHost.Unsupported(
            name.replace("\\W", "")
                .lowercase(Locale.US)
        )
    }

    @JvmSynthetic
    internal fun osArch(name: String): OSArch {
        val lArch = name.lowercase(Locale.US)

        return when {
            // TODO
            else -> null
        } ?: OSArch.Unsupported(
            name.replace("\\W", "")
                .lowercase(Locale.US)
        )
    }

    private fun isAndroidRuntime(): Boolean {
        return System.getProperty("java.runtime.name", "")
            .contains("android", true)
    }

    private fun isAndroidTermux(): Boolean {
        return try {
            process.runAndWait("uname -o")
                .lowercase()
                .contains("android")
        } catch (_: Throwable) {
            false
        }
    }

    private fun isLinuxMusl(): Boolean {
        val mapFilesDir = File("/proc/self/map_files")

        if (mapFilesDir.exists()) {
            try {
                mapFilesDir
                    .walkTopDown()
                    .maxDepth(1)
                    .iterator()
                    .forEach { file ->
                        // map_files directory contains symbolic links that must
                        // be resolved which canonicalPath will do for us.
                        val canonicalPath = file.canonicalPath

//                        println("${file.path} >> $canonicalPath")

                        if (canonicalPath.lowercase().contains("musl")) {
                            return true
                        }
                    }
            } catch (_: Throwable) {}
        } else {
            // Fallback to checking for Alpine Linux in the event
            // it's an older kernel which may not have map_files
            // directory.
            try {
                File("/etc/os-release")
                    .inputStream()
                    .bufferedReader()
                    .use { reader ->
                        var isAlpine = false

                        while (true) {
                            val line = reader.readLine()

//                            println(line)

                            if (line.startsWith("ID")) {
                                isAlpine = line.contains("alpine")
                                break
                            }
                        }

                        return isAlpine
                    }
            } catch (_: Throwable) {
                return false
            }
        }

        return false
    }
}