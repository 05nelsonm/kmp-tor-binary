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
package io.matthewnelson.kmp.tor.binary

import okio.FileSystem
import okio.NodeJsFileSystem
import okio.Path

actual fun filesystem(): FileSystem = NodeJsFileSystem

actual fun Path.canExecute(): Boolean {
    val fs = js("require('fs')")
    val xOk = fs.constants.X_OK

    return try {
        fs.accessSync(toString(), xOk)
        true
    } catch (_: Throwable) {
        false
    }
}
