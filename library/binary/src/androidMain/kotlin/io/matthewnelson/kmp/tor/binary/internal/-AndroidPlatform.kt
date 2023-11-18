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

import io.matthewnelson.kmp.tor.binary.ALIAS_GEOIP
import io.matthewnelson.kmp.tor.binary.ALIAS_GEOIP6
import io.matthewnelson.kmp.tor.binary.ALIAS_TOR
import io.matthewnelson.kmp.tor.binary.KmpTorBinary
import io.matthewnelson.kmp.tor.binary.util.*

@JvmSynthetic
@OptIn(InternalKmpTorBinaryApi::class)
internal actual fun Resource.Config.Builder.configure() {
    val clazz = KmpTorBinary::class.java

    resource(ALIAS_GEOIP) {
        isExecutable = false
        resourceClass = clazz
        resourcePath = PATH_RESOURCE_GEOIP
    }

    resource(ALIAS_GEOIP6) {
        isExecutable = false
        resourceClass = clazz
        resourcePath = PATH_RESOURCE_GEOIP6
    }

    // Is Android Runtime.
    //
    // Binaries are extracted on application install
    // to the nativeLib directory. This is required as
    // android does not allow execution from the app dir
    // (cannot download executables and run them).
    if (ANDROID_SDK_INT != null) return

    // Android Unit Test. Check for support via binary-android-unit-test
    val host = OSInfo.INSTANCE.osHost

    if (host is OSHost.Unknown) {
        error("Unknown host[$host]")
        return
    }

    val arch = OSInfo.INSTANCE.osArch

    val torResourcePath = host.toTorResourcePath(arch)

    if (torResourcePath == null) {
        error("Unsupported architecutre[$arch] for host[$host]")
        return
    }

    val loader = "io.matthewnelson.kmp.tor.binary.android.unit.test.Loader"

    val loaderClass = try {
        Class
            .forName(loader)
            ?: throw ClassNotFoundException("Failed to find class $loader")
    } catch (t: Throwable) {
        error("""
            Failed to find class $loader
            Missing dependency for Android Unit Tests?

            Try adding the 'binary-android-unit-test' dependency
            via testImplementation
        """.trimIndent())
        return
    }

    resource(ALIAS_TOR) {
        isExecutable = true
        resourceClass = loaderClass
        resourcePath = torResourcePath
    }
}
