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

import kotlin.jvm.JvmField

/**
 * All supported Operating Systems for jvm/node-js
 *
 * [path] is directly correlated to the resource
 * path in which tor will be located.
 *
 * [arches] are the supported architectures for the
 * given [OSHost].
 * */
@InternalKmpTorBinaryApi
public sealed class OSHost private constructor(
    @JvmField
    public val path: String,
    @JvmField
    public val arches: Set<OSArch>,
) {

    @InternalKmpTorBinaryApi
    public object FreeBSD: OSHost("freebsd", emptySet())

    @InternalKmpTorBinaryApi
    public sealed class Linux private constructor(
        subtype: String,
        arches: Set<OSArch>,
    ): OSHost("linux-$subtype", arches) {

        @InternalKmpTorBinaryApi
        public object Android: Linux("android", ImmutableSet.of(
            OSArch.Aarch64,
            OSArch.Armv7,
            OSArch.X86,
            OSArch.X86_64,
        ))
        @InternalKmpTorBinaryApi
        public object Libc: Linux("libc", ImmutableSet.of(
            OSArch.Aarch64,
            OSArch.Armv7,
            OSArch.X86,
            OSArch.X86_64,
        ))
        @InternalKmpTorBinaryApi
        public object Musl: Linux("musl", emptySet())

    }

    @InternalKmpTorBinaryApi
    public object MacOS: OSHost("macos", ImmutableSet.of(
        OSArch.Aarch64,
        OSArch.X86_64,
    ))
    @InternalKmpTorBinaryApi
    public object Windows: OSHost("mingw", ImmutableSet.of(
        OSArch.X86,
        OSArch.X86_64,
    ))

    @InternalKmpTorBinaryApi
    public class Unknown(
        @JvmField
        public val name: String
    ): OSHost("", emptySet()) {
        override fun equals(other: Any?): Boolean = other is Unknown && other.name == name
        override fun hashCode(): Int = 16 * 31 + name.hashCode()
        override fun toString(): String = name
    }

    override fun equals(other: Any?): Boolean = other is OSHost && other.path == path
    override fun hashCode(): Int = 17 * 31 + path.hashCode()
    override fun toString(): String = path
}
