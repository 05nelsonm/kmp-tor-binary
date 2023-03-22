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
package io.matthewnelson.diff.internal

import com.github.ajalt.clikt.parameters.arguments.ArgumentTransformContext
import okio.FileSystem
import okio.Path

internal object RequireFileExistsAndNotEmpty : (ArgumentTransformContext, Path) -> Unit {
    override fun invoke(p1: ArgumentTransformContext, p2: Path) {
        with(FileSystem.get()) {
            require(exists(p2)) { "${p1.name} does not exist" }

            val md = metadata(p2)
            require(md.isRegularFile) { "${p1.name} exists, but is not a file" }
            require(md.size != 0L) { "${p1.name} is empty" }
        }
    }
}

internal object RequireFileDoesNotExist: (ArgumentTransformContext, Path) -> Unit {
    override fun invoke(p1: ArgumentTransformContext, p2: Path) {
        with(FileSystem.get()) {
            require(!exists(p2)) { "${p1.name} exists" }
        }
    }
}

internal object RequireDirOrNull: (ArgumentTransformContext, Path) -> Unit {
    override fun invoke(p1: ArgumentTransformContext, p2: Path) {
        with(FileSystem.get()) {
            require(metadataOrNull(p2)?.isDirectory != false) { "${p1.name} is not a directory" }
        }
    }
}
