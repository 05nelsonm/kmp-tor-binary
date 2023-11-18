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

import io.matthewnelson.kmp.tor.binary.util.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.util.Resource
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

public const val ALIAS_TOR: String = "tor"
public const val ALIAS_GEOIP: String = "geoip"
public const val ALIAS_GEOIP6: String = "geoip6"

public expect class KmpTorBinary(installationDir: String) {

    @JvmField
    public val installationDir: String

    @Throws(Exception::class)
    public fun install(): Map<String, String>

    internal companion object {

        @get:JvmSynthetic
        @OptIn(InternalKmpTorBinaryApi::class)
        internal val Config: Resource.Config
    }
}
