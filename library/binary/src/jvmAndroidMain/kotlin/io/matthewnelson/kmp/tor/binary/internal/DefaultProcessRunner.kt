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

import java.io.IOException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal object DefaultProcessRunner: ProcessRunner {

    @JvmSynthetic
    @Throws(IOException::class, InterruptedException::class)
    override fun runAndWait(command: String): String = runAndWait(command, 5.seconds)

    @JvmSynthetic
    @Throws(IOException::class, InterruptedException::class)
    override fun runAndWait(command: String, timeout: Duration): String {
        TODO("Not yet implemented")
    }
}
