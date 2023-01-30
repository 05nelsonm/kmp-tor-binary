/*
 * Copyright (c) 2022 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.kmp.tor.binary.extract

import kotlin.test.Test

abstract class BaseExtractorJvmJsUnitTest: BaseExtractorUnitTest() {

    @Test
    fun givenExtractor_whenExtractLinuxX64Resource_thenIsSuccessful() {
        assertBinaryResourceExtractionIsSuccessful(TorResourceLinuxX64)
    }

    @Test
    fun givenExtractor_whenExtractLinuxX86Resource_thenIsSuccessful() {
        assertBinaryResourceExtractionIsSuccessful(TorResourceLinuxX86)
    }

    @Test
    fun givenExtractor_whenExtractMacosX64Resource_thenIsSuccessful() {
        assertBinaryResourceExtractionIsSuccessful(TorResourceMacosX64)
    }

    @Test
    fun givenExtractor_whenExtractMacosArm64Resource_thenIsSuccessful() {
        assertBinaryResourceExtractionIsSuccessful(TorResourceMacosArm64)
    }

    @Test
    fun givenExtractor_whenExtractMingwX64Resource_thenIsSuccessful() {
        assertBinaryResourceExtractionIsSuccessful(TorResourceMingwX64)
    }

    @Test
    fun givenExtractor_whenExtractMingwX86Resource_thenIsSuccessful() {
        assertBinaryResourceExtractionIsSuccessful(TorResourceMingwX86)
    }
}
