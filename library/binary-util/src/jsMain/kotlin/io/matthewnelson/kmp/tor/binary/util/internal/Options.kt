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
package io.matthewnelson.kmp.tor.binary.util.internal

import io.matthewnelson.kmp.tor.binary.util.InternalKmpTorBinaryApi

@InternalKmpTorBinaryApi
public sealed class Options {

    @InternalKmpTorBinaryApi
    public class ReadDir private constructor(
        public val encoding: String?,
        public val withFileTypes: Boolean,
        public val recursive: Boolean,
    ): Options() {
        // will force readdir to return an array of strings
        public constructor(recursive: Boolean): this("utf8", false, recursive)
    }

    @InternalKmpTorBinaryApi
    public class ReadUtf8 private constructor(
        public val encoding: String?,
        public val flag: String?,
    ): Options() {
        public constructor(): this("utf8", "r")
    }

    @InternalKmpTorBinaryApi
    public class Remove(
        public val force: Boolean = true,
        public val recursive: Boolean = true,
    ): Options()
}
