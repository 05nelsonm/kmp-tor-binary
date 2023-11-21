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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.matthewnelson.kmp.tor.binary.core

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@InternalKmpTorBinaryApi
public expect class Resource {

    @JvmField
    public val alias: String
    @JvmField
    public val isExecutable: Boolean

    @InternalKmpTorBinaryApi
    public class Config private constructor(
        errors: ImmutableSet<String>,
        resources: ImmutableSet<Resource>,
    ) {

        @JvmField
        public val errors: ImmutableSet<String>
        @JvmField
        public val resources: ImmutableSet<Resource>

        @Throws(Exception::class)
        public fun extractTo(destinationDir: String): ImmutableMap<String, String>

        @InternalKmpTorBinaryApi
        public companion object {

            @JvmStatic
            @KmpTorBinaryDsl
            public fun create(block: Builder.() -> Unit): Config
        }

        @KmpTorBinaryDsl
        @InternalKmpTorBinaryApi
        public class Builder internal constructor() {

            @KmpTorBinaryDsl
            public fun error(message: String): Builder

            @KmpTorBinaryDsl
            public fun resource(
                alias: String,
                require: Boolean = true,
                block: Resource.Builder.() -> Unit
            ): Builder
        }

        public override fun equals(other: Any?): Boolean
        public override fun hashCode(): Int
        public override fun toString(): String
    }

    @KmpTorBinaryDsl
    @InternalKmpTorBinaryApi
    public class Builder internal constructor(alias: String) {

        @JvmField
        public val alias: String

        @JvmField
        public var isExecutable: Boolean
    }

    public override fun equals(other: Any?): Boolean
    public override fun hashCode(): Int
    public override fun toString(): String
}
