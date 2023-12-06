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
package io.matthewnelson.kmp.tor.binary.core

import io.matthewnelson.encoding.base16.Base16
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString
import org.kotlincrypto.hash.sha2.SHA256
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(InternalKmpTorBinaryApi::class)
class NativeResourceUnitTest {

    @Test
    fun givenNativeResource_whenRead_thenPassesValidation() {
        var invocations = 0L

        // read will throw if size and sha256 are invalid
        resource_lorem_ipsum.read { _, _ -> invocations++ }

        // Ensure that there was something going on
        assertEquals(resource_lorem_ipsum.chunks, invocations)
    }

    @Test
    fun givenNativeResource_whenSha256_thenIsSameAsFile() {
        val loremIpsumFile = TEST_SUPPORT_DIR.resolve(resource_lorem_ipsum.name)

        val sha256 = filesystem().read(loremIpsumFile) {
            SHA256().digest(readByteArray())
        }.encodeToString(Base16 { encodeToLowercase = true })

        assertEquals(resource_lorem_ipsum.sha256, sha256)
    }
}
