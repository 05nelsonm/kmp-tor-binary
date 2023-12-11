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
package io.matthewnelson.kmp.tor.binary.core.internal

import io.matthewnelson.kmp.file.*
import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.core.Resource
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.errno

@OptIn(DelicateFileApi::class, ExperimentalForeignApi::class, InternalKmpTorBinaryApi::class)
internal actual fun Resource.extractTo(destinationDir: File): File {
    val name = platform.nativeResource.name
    val destFinal = if (name.endsWith(".gz")) {
        destinationDir.resolve(name.substringBeforeLast(".gz"))
    } else {
        null
    }

    // Could be gzipped, could not. Need to extract it to FS first
    val dest = destinationDir.resolve(name)

    if (dest.exists() && !dest.delete()) {
        throw IOException("Failed to delete $dest")
    }

    if (destFinal != null && destFinal.exists() && !destFinal.delete()) {
        throw IOException("Failed to delete $destFinal")
    }

    try {
        dest.open(flags = "wb") { file ->
            platform.nativeResource.read { buffer, len ->
                val result = file.fWrite(buffer, 0, len)
                if (result < 0) {
                    throw errnoToIOException(errno)
                }
            }
        }
    } catch (t: Throwable) {
        dest.delete()
        throw t.wrapIOException()
    }

    if (destFinal == null) return dest

    try {
        // TODO: Implement gunzip instead of just copying
        dest.open(flags = "rb") { file1 ->
            destFinal.open(flags = "wb") { file2 ->
                val buf = ByteArray(4096)

                while (true) {
                    val read = file1.fRead(buf)
                    if (read < 0) throw errnoToIOException(errno)
                    if (read == 0) break

                    if (file2.fWrite(buf, len = read) < 0) {

                        throw errnoToIOException(errno)
                    }
                }
            }
        }
    } catch (t: Throwable) {
        destFinal.delete()
        throw t.wrapIOException()
    } finally {
        dest.delete()
    }

    return destFinal
}
