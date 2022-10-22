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
@file:JsModule("fs")
@file:JsNonModule
package io.matthewnelson.kmp.tor.binary.extract.internal

internal external fun mkdirSync(path: String): String?

// Can potentially return a Buffer if ReadFileOptions.encoding is null...
internal external fun readFileSync(path: String, options: ReadFileOptions): Any

// Returns a Buffer
internal external fun readFileSync(path: String): Any

// data can be a String or a Buffer
internal external fun writeFileSync(path: String, data: Any)

internal external fun realpathSync(path: String): String

internal external fun rmdirSync(path: String, options: RmDirOptions)

internal external fun rmSync(path: String, options: RmOptions)

internal external fun lstatSync(path: String): Stats

internal external fun existsSync(path: String): Boolean

internal open external class ReadFileOptions {
    open var encoding: String?
}

internal open external class RmOptions {
    open var force: Boolean
}

internal open external class RmDirOptions {
    open var recursive: Boolean
}

internal open external class Stats {
    fun isFile(): Boolean
    fun isDirectory(): Boolean
}
