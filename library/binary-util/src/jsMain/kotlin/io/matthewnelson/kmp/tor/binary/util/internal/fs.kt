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
@file:JsModule("fs")
@file:JsNonModule
package io.matthewnelson.kmp.tor.binary.util.internal

internal external fun mkdirSync(path: String): String?

internal external fun readdirSync(path: String, options: OptionsReadDir): Array<String>

internal external fun readFileSync(path: String, options: OptionsReadFileUtf8): Buffer

internal external fun readFileSync(path: String): Buffer

// data can be a String or a Buffer
internal external fun writeFileSync(path: String, data: Any)

internal external fun readlinkSync(path: String): String

internal external fun rmSync(path: String, options: OptionsRm)

internal external fun lstatSync(path: String): Stats

internal external fun existsSync(path: String): Boolean

// https://nodejs.org/api/fs.html#file-modes
internal external fun chmodSync(path: String, mode: Int)

internal open external class Stats {
    fun isFile(): Boolean
    fun isDirectory(): Boolean
    fun isSymbolicLink(): Boolean
}
