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
package io.matthewnelson.kmp.tor.binary.internal

import io.matthewnelson.kmp.tor.binary.core.ImmutableMap
import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.core.Resource
import kotlin.jvm.JvmSynthetic

internal const val ALIAS_TOR: String = "tor"
internal const val ALIAS_GEOIP: String = "geoip"
internal const val ALIAS_GEOIP6: String = "geoip6"

@get:JvmSynthetic
@OptIn(InternalKmpTorBinaryApi::class)
internal expect val RESOURCE_CONFIG: Resource.Config

// For Android to parse nativeLibraryDir
@JvmSynthetic
@Throws(IllegalStateException::class)
@OptIn(InternalKmpTorBinaryApi::class)
internal expect fun ImmutableMap<String, String>.findLibTor(): Map<String, String>
