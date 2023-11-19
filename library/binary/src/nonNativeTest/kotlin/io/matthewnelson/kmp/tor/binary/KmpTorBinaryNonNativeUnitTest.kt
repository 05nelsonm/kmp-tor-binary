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
package io.matthewnelson.kmp.tor.binary

import io.matthewnelson.kmp.tor.binary.util.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.util.OSInfo
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(InternalKmpTorBinaryApi::class)
class KmpTorBinaryNonNativeUnitTest: KmpTorBinaryBaseUnitTest() {

    @Test
    fun givenTorBinaryResources_whenConfigured_thenIsExpectedForHostMachine() {
        if (!OSInfo.INSTANCE.osArch.isSupportedBy(OSInfo.INSTANCE.osHost)) {
            // If host machine running this test is not supported, the
            // tor binaries will not be configured and an error will
            // be added instead.
            assertEquals(1, KmpTorBinary.Config.errors.size)
            assertEquals(2, KmpTorBinary.Config.resources.size)
        } else {
            // Specifically for Android, this should obtain the Loader
            // class from module :binary-android-unit-test and have
            // the appropriate resources to load for the host machine.
            assertEquals(0, KmpTorBinary.Config.errors.size)
            assertEquals(3, KmpTorBinary.Config.resources.size)
        }

        println(KmpTorBinary.Config)
    }
}
