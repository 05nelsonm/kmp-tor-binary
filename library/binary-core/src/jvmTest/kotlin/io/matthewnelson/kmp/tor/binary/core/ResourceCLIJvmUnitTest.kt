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

import kotlin.test.Test

class ResourceCLIJvmUnitTest {

    @Test
    fun givenResourceCLI_whenRun_thenWritesResourceKT() {
        // Will utilize the CLI tool to write lorem_ipsum.txt file
        // to nativeTest source set directory. This ensures that
        // 1) The CLI tool works (it's JVM only currently)
        // 2) The generated resource file is reproducable (no git diffs, unless something changed)
        io.matthewnelson.resource.cli.main(arrayOf(
            /* packageName:   */ "io.matthewnelson.kmp.tor.binary.core",
            /* pathSourceSet: */ TEST_SUPPORT_DIR
                .parent
                ?.resolve("src")
                ?.resolve("nativeTest")
                ?.toString()!!,
            /* pathFile       */ TEST_SUPPORT_DIR
                .resolve("lorem_ipsum.txt")
                .toString(),
            "--quiet"
        ))
    }
}
