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
@file:Suppress("KotlinRedundantDiagnosticSuppress")

package io.matthewnelson.differ.core.internal

import io.matthewnelson.encoding.base16.Base16
import io.matthewnelson.encoding.base64.Base64
import io.matthewnelson.encoding.builders.*
import okio.FileSystem

@InternalDifferApi
@Suppress("NOTHING_TO_INLINE")
public expect inline fun FileSystem.Companion.system(): FileSystem

internal val BASE16: Base16 = Base16 { encodeToLowercase = true }
internal val BASE64: Base64 = Base64 { lineBreakInterval = 64 }

internal const val LINE_BREAK: String = "#"
