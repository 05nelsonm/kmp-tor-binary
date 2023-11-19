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

package io.matthewnelson.kmp.tor.binary

import io.matthewnelson.kmp.tor.binary.internal.configure
import io.matthewnelson.kmp.tor.binary.internal.findLibTor
import io.matthewnelson.kmp.tor.binary.util.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.util.Resource
import java.io.File
import java.nio.file.Path
import kotlin.concurrent.Volatile
import kotlin.io.path.absolutePathString

public actual class KmpTorBinary
public actual constructor(
    @JvmField
    public actual val installationDir: String
) {

    public constructor(installationDir: File): this(installationDir.absolutePath)
    public constructor(installationDir: Path): this(installationDir.absolutePathString())

    @Volatile
    private var map: Map<String, String>? = null

    @Throws(Exception::class)
    public actual fun install(): Map<String, String> {
        return map ?: synchronized(this) {
            @OptIn(InternalKmpTorBinaryApi::class)
            map ?: run {
                Config.extractTo(installationDir)
                    .findLibTor()
                    .also { map = it }
            }
        }
    }

    internal actual companion object {

        @get:JvmSynthetic
        @OptIn(InternalKmpTorBinaryApi::class)
        internal actual val Config: Resource.Config by lazy {
            // lazily load so that OSInfo doesn't
            // hammer main thread.
            Resource.Config.create { configure() }
        }
    }
}
