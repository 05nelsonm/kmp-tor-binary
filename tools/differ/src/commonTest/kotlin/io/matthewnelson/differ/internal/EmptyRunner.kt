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
package io.matthewnelson.differ.internal

import io.matthewnelson.differ.internal.apply.Apply
import io.matthewnelson.differ.internal.apply.DirApply
import io.matthewnelson.differ.internal.create.Create
import io.matthewnelson.differ.internal.create.DirCreate
import okio.FileSystem
import okio.Path

internal class EmptyRunner(
    val t: Throwable? = null
) : Apply.Runner,
    DirApply.Runner,
    Create.Runner,
    DirCreate.Runner
{
    override fun run(settings: Subcommand.Settings, fs: FileSystem, file: Path, diffFile: Path) {
        t?.let { throw it }
    }

//    override fun run(fs: FileSystem) {
//        t?.let { throw it }
//    }

    override fun run(settings: Subcommand.Settings, fs: FileSystem, file1: Path, file2: Path, diffFile: Path, hrFile: Path?) {
        t?.let { throw it }
    }

    override fun run(settings: Subcommand.Settings, fs: FileSystem) {
        t?.let { throw it }
    }
}
