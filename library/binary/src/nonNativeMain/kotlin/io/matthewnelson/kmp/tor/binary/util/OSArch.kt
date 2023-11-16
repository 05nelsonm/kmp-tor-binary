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
@file:Suppress("ClassName")

package io.matthewnelson.kmp.tor.binary.util

import kotlin.jvm.JvmField

/**
 * All supported Architectures for jvm/node-js
 *
 * [path] is directly correlated to the resource
 * path in which tor will be located.
 * */
public sealed class OSArch private constructor(
    @JvmField
    public val path: String
) {

    public object Aarch64: OSArch("aarch64")
    public object Armv7a: OSArch("armv7a")
    public object X86: OSArch("x86")
    public object X86_64: OSArch("x86_64")

    public class Unsupported(
        @JvmField
        public val arch: String
    ): OSArch("") {
        override fun equals(other: Any?): Boolean = other is Unsupported && other.arch == arch
        override fun hashCode(): Int = 17 * 31 + arch.hashCode()
        override fun toString(): String = arch
    }

    override fun equals(other: Any?): Boolean = other is OSArch && other.path == path
    override fun hashCode(): Int = 17 * 31 + path.hashCode()
    override fun toString(): String = path
}
