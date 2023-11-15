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
@file:JvmName("ProcessExt")

package io.matthewnelson.kmp.tor.binary.util

import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.time.Duration

/**
 * Android the [Process.waitFor] function that has a timeout is
 * only available for API 26+. This is simply a re-implementation
 * of the blocking call.
 *
 * Will block current thread for specified [timeout], or until
 * the [Process] completes.
 *
 * @param [timeout] The duration in which to block for
 * @param [destroyOnTimeout] Will call [Process.destroyForcibly] in the
 *   event that [timeout] is exceeded before the process finishes.
 * @return t/f for if the process has completed
 * */
public fun Process.waitFor(timeout: Duration, destroyOnTimeout: Boolean = false): Boolean {
    val startTime = System.nanoTime()
    var remaining = timeout.inWholeNanoseconds

    do {
        try {
            exitValue()
            return true
        } catch (ex: IllegalThreadStateException) {
            if (remaining > 0) {
                Thread.sleep(
                    min(
                        (TimeUnit.NANOSECONDS.toMillis(remaining) + 1).toDouble(),
                        100.0
                    ).toLong()
                )
            }
        }

        remaining = timeout.inWholeNanoseconds - (System.nanoTime() - startTime)
    } while (remaining > 0)

    if (destroyOnTimeout) {
        destroyForcibly()
    }

    return false
}
