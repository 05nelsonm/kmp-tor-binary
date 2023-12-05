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
@file:Suppress("FunctionName", "ClassName", "ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")

package io.matthewnelson.kmp.tor.binary.core.internal

import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi

/** [docs](https://nodejs.org/api/fs.html#fschmodsyncpath-mode) */
@JsName("chmodSync")
@InternalKmpTorBinaryApi
public actual external fun fs_chmod(path: String, mode: String)

/** [docs](https://nodejs.org/api/fs.html#fsexistssyncpath) */
@JsName("existsSync")
@InternalKmpTorBinaryApi
public actual external fun fs_exists(path: String): Boolean

/** [docs](https://nodejs.org/api/fs.html#fsmkdirsyncpath-options) */
@JsName("mkdirSync")
internal external fun fs_mkdirSync(path: String): String?

/** [docs](https://nodejs.org/api/fs.html#fsreaddirsyncpath-options) */
@JsName("readdirSync")
internal external fun fs_readdirSync(path: String, options: Options.ReadDir): Array<String>

/** [docs](https://nodejs.org/api/fs.html#fsreadfilesyncpath-options) */
@JsName("readFileSync")
internal external fun fs_readFileSync(path: String): buffer_Buffer

/** [docs](https://nodejs.org/api/fs.html#fsrealpathsyncpath-options) */
@JsName("realpathSync")
internal actual external fun fs_realpath(path: String): String

/** [docs](https://nodejs.org/api/fs.html#fsrmsyncpath-options) */
@JsName("rmSync")
internal external fun fs_rmSync(path: String, options: Options.Remove)

/** [docs](https://nodejs.org/api/fs.html#fswritefilesyncfile-data-options) */
@JsName("writeFileSync")
internal external fun fs_writeFileSync(path: String, data: buffer_Buffer)

/** [docs](https://nodejs.org/api/fs.html#fslstatsyncpath-options) */
@JsName("lstatSync")
@InternalKmpTorBinaryApi
public external fun fs_lstatSync(path: String): fs_Stats

/** [docs](https://nodejs.org/api/fs.html#fsstatsyncpath-options) */
@JsName("statSync")
@InternalKmpTorBinaryApi
public external fun fs_statSync(path: String): fs_Stats

/** [docs](https://nodejs.org/api/fs.html#class-fsstats) */
@JsName("Stats")
@InternalKmpTorBinaryApi
public external class fs_Stats {
    public val mode: Number
    public fun isFile(): Boolean
    public fun isDirectory(): Boolean
    public fun isSymbolicLink(): Boolean
}
