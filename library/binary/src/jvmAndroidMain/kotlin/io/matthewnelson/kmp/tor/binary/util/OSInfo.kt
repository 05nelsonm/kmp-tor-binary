/*
 *  Copyright 2008 Taro L. Saito
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 **/
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
import io.matthewnelson.kmp.tor.binary.internal.PATH_MAP_FILES
import io.matthewnelson.kmp.tor.binary.internal.PATH_OS_RELEASE
import io.matthewnelson.kmp.tor.binary.internal.ProcessRunner
import java.io.File
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

/**
 * Implementation based off of:
 *
 * [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc/blob/master/src/main/java/org/sqlite/util/OSInfo.java)
 * */
public actual class OSInfo private actual constructor(
    private val process: ProcessRunner,
    private val pathMapFiles: String,
    private val pathOSRelease: String,
) {

    public actual companion object {

        @JvmField
        public actual val INSTANCE: OSInfo = get()

        @JvmStatic
        @JvmSynthetic
        internal fun get(
            process: ProcessRunner = DefaultProcessRunner,
            pathMapFile: String = PATH_MAP_FILES,
            pathOSRelease: String = PATH_OS_RELEASE,
        ): OSInfo = OSInfo(process, pathMapFile, pathOSRelease)
    }

    @get:JvmName("osHost")
    public actual val osHost: OSHost by lazy {
        osHost(System.getProperty("os.name")?.ifBlank { null } ?: "unknown")
    }

    @get:JvmName("osArch")
    public actual val osArch: OSArch by lazy {
        osArch(System.getProperty("os.arch")?.ifBlank { null } ?: "unknown")
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

    @JvmSynthetic
    internal fun osHost(name: String): OSHost {
        val lName = name.lowercase(Locale.US)

        return when {
            lName.contains("windows") -> OSHost.Windows
            lName.contains("mac") -> OSHost.MacOS
            lName.contains("darwin") -> OSHost.MacOS
            lName.contains("freebsd") -> OSHost.FreeBSD
            isAndroidRuntime() -> OSHost.Linux.Android
            isAndroidTermux() -> OSHost.Linux.Android
            isLinuxMusl() -> OSHost.Linux.Musl
            lName.contains("linux") -> OSHost.Linux.Libc
            else -> OSHost.Unknown(
                name.replace("\\W", "")
                    .lowercase(Locale.US)
            )
        }
    }

    @JvmSynthetic
    internal fun osArch(name: String): OSArch {
        val lArch = name.lowercase(Locale.US)

        val mapped = archMap[lArch]

        return when {
            mapped != null -> mapped
            lArch.startsWith("arm") -> resolveArmArchType()
            else -> null
        } ?: OSArch.Unsupported(
            name.replace("\\W", "")
                .lowercase(Locale.US)
        )
    }

    private fun isAndroidRuntime(): Boolean {
        return System.getProperty("java.runtime.name")
            ?.contains("android", true) == true
    }

    private fun isAndroidTermux(): Boolean {
        return try {
            process.runAndWait(listOf("uname", "-o"))
                .lowercase()
                .contains("android")
        } catch (_: Throwable) {
            false
        }
    }

    private fun isLinuxMusl(): Boolean {
        val mapFilesDir = File(pathMapFiles)
        var fileCount = -1

        if (mapFilesDir.exists()) {
            try {
                mapFilesDir
                    .walkTopDown()
                    .maxDepth(1)
                    .iterator()
                    .forEach { file ->

                        // first file is always "map_files"
                        fileCount++

                        // map_files directory contains symbolic links that must
                        // be resolved which canonicalPath will do for us.
                        val canonicalPath = file.canonicalPath

//                        println("${file.path} >> $canonicalPath")

                        if (canonicalPath.lowercase().contains("musl")) {
                            return true
                        }
                    }
            } catch (_: Throwable) {
                fileCount = 0
            }
        }

        if (fileCount < 1) {
            // Fallback to checking for Alpine Linux in the event
            // it's an older kernel which may not have map_files
            // directory.
            try {
                File(pathOSRelease)
                    .inputStream()
                    .bufferedReader()
                    .use { reader ->
                        while (true) {
                            val line = reader.readLine()

//                            println(line)

                            // ID and ID_LIKE arguments
                            if (line.startsWith("ID")) {
                                if (line.contains("alpine")) return true
                            }
                        }
                    }
            } catch (_: Throwable) {
                // EOF or does not exist
                return false
            }
        }

        return false
    }

    private fun resolveArmArchType(): OSArch? {
        when (osHost) {
            is OSHost.Windows,
            is OSHost.Unknown -> return null
            else -> { /* run */ }
        }

        // aarch64, armv5t, armv5te, armv5tej, armv5tejl, armv6, armv7, armv7l
        val machineHardwareName = try {
            process.runAndWait(listOf("uname", "-m"))
        } catch (_: Throwable) {
            return null
        }

        // Should never be the case because it's in archMap which
        // is always checked before calling this function.
        if (
            machineHardwareName.startsWith("aarch64")
            || machineHardwareName.startsWith("arm64")
        ) {
            return OSArch.Aarch64
        }

        if (osHost is OSHost.MacOS) {
            return null
        }

        // If android and NOT aarch64, return the only other
        // supported arm architecture.
        if (osHost is OSHost.Linux.Android) {
            return OSArch.Armv7
        }

        if (machineHardwareName.startsWith("armv7")) {
            return OSArch.Armv7
        }

        // Java 1.8 introduces a system property to determine armel or armhf
        // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8005545
        System.getProperty("sun.arch.abi")?.let { abi ->
            if (abi.startsWith("gnueabihf")) {
                return OSArch.Armv7
            }
        }

        // For java7, still need to run some shell commands to determine ABI of JVM
        val javaHome = System.getProperty("java.home")?.ifBlank { null } ?: return null

        // determine if first JVM found uses ARM hard-float ABI
        try {
            Runtime.getRuntime().exec(arrayOf("which", "readelf")).let { process ->
                // If it did not finish before timeout
                if (!process.waitFor(250.milliseconds)) return null

                if (process.exitValue() != 0) return null
            }

            val cmdArray = arrayOf(
                "/bin/sh",
                "-c",
                "find '"
                        + javaHome
                        + "' -name 'libjvm.so' | head -1 | xargs readelf -A | "
                        + "grep 'Tag_ABI_VFP_args: VFP registers'"
            )

            Runtime.getRuntime().exec(cmdArray).let { process ->
                // If it did not finish before timeout
                if (!process.waitFor(250.milliseconds)) return null

                if (process.exitValue() == 0) return OSArch.Armv7
            }
        } catch (_: Throwable) {}

        // Unsupported
        return null
    }
}
