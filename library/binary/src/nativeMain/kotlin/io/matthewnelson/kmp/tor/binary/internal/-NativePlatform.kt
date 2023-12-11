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

import io.matthewnelson.kmp.tor.binary.core.*

// Native
@OptIn(InternalKmpTorBinaryApi::class)
internal actual val RESOURCE_CONFIG: Resource.Config by lazy {
    Resource.Config.create {
        resource(ALIAS_GEOIP) {
            isExecutable = false

            platform {
                nativeResource = resource_geoip_gz
            }
        }

        resource(ALIAS_GEOIP6) {
            isExecutable = false

            platform {
                nativeResource = resource_geoip6_gz
            }
        }

        resource(ALIAS_TOR) {
            isExecutable = true

            platform {
                nativeResource = resourceTor()
            }
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
@OptIn(InternalKmpTorBinaryApi::class)
internal expect inline fun PlatformResource.Builder.resourceTor(): NativeResource
