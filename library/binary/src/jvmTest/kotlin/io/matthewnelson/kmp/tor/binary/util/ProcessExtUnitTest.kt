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
package io.matthewnelson.kmp.tor.binary.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class ProcessExtUnitTest {

    @Test
    fun givenProcess_whenWaitFor_thenBlocksUntilCompletion() {
        when (OSInfo.INSTANCE.osHost) {
            is OSHost.Linux,
            is OSHost.MacOS -> { /* run */ }
            else -> return
        }

        val runTime = measureTime {
            val p = Runtime.getRuntime().exec("sleep 0.25")
            assertFalse(p.waitFor(100.milliseconds))
            assertTrue(p.waitFor(1.seconds))
        }

        assertTrue(runTime < 500.milliseconds)
    }
}
