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

import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.core.ANDROID_SDK_INT
import kotlin.test.Test
import kotlin.test.assertNotNull

@OptIn(InternalKmpTorBinaryApi::class)
class AndroidSdkIntAndroidTest {

    @Test
    fun givenAndroidSdkIntJava_whenAndroidRuntime_thenIsNotNull() {
        assertNotNull(ANDROID_SDK_INT)
    }
}
