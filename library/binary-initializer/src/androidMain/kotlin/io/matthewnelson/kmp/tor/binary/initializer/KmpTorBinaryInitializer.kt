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
package io.matthewnelson.kmp.tor.binary.initializer

import android.content.Context
import java.io.File

public class KmpTorBinaryInitializer private constructor() {

    @get:JvmName("cacheDir")
    public var cacheDir: File? = null
        private set
    @get:JvmName("nativeLibraryDir")
    public var nativeLibraryDir: File? = null
        private set
    @get:JvmName("noBackupFilesDir")
    public var noBackupFilesDir: File? = null
        private set

    public fun findLib(name: String): File? {
        nativeLibraryDir?.walkTopDown()
            ?.iterator()
            ?.forEach { file ->
                if (file.isFile && file.name == name) {
                    return file
                }
            }

        return null
    }

    @Throws(IllegalStateException::class)
    public fun requireLib(name: String): File = findLib(name)
        ?: throw IllegalStateException("Failed to find lib[$name]")

    public companion object {

        @JvmField
        public val INSTANCE: KmpTorBinaryInitializer = KmpTorBinaryInitializer()

        @JvmStatic
        @JvmSynthetic
        internal fun init(context: Context) {
            INSTANCE.initialize(context)
        }
    }

    private fun initialize(context: Context) {
        cacheDir = context.cacheDir
        nativeLibraryDir = File(context.applicationInfo.nativeLibraryDir)
        noBackupFilesDir = context.noBackupFilesDir
    }
}
