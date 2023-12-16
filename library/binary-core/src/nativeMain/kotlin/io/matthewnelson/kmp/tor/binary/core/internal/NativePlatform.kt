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
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.posix.errno
import platform.zlib.gzFile
import platform.zlib.gzclose_r
import platform.zlib.gzopen
import platform.zlib.gzread
import platform.zlib.Z_OK
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(DelicateFileApi::class, ExperimentalForeignApi::class, InternalKmpTorBinaryApi::class)
internal actual fun Resource.extractTo(destinationDir: File, onlyIfDoesNotExist: Boolean): File {
    val name = platform.nativeResource.name
    val destFinal = if (name.endsWith(".gz")) {
        destinationDir.resolve(name.substringBeforeLast(".gz"))
    } else {
        null
    }

    // Could be gzipped, could not. Need to extract it to FS first
    val dest = destinationDir.resolve(name)

    if (onlyIfDoesNotExist) {
        val file = (destFinal ?: dest)
        if (file.exists()) return file
    }

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
    } catch (e: Exception) {
        dest.delete()
        throw e
    }

    val mode = if (isExecutable) "500" else "400"

    if (destFinal == null) {
        // not gzipped. dest is the final destination
        try {
            dest.chmod(mode)
        } catch (e: IOException) {
            dest.delete()
            throw e
        }
        return dest
    }

    try {
        dest.gzOpenRead { gzFile ->
            destFinal.open(flags = "wb") { file ->
                val buf = ByteArray(4096)

                while (true) {
                    val read = buf.usePinned { pinned ->
                        gzread(gzFile, pinned.addressOf(0), buf.size.toUInt())
                    }

                    if (read < 0) throw errnoToIOException(errno)
                    if (read == 0) break
                    val write = file.fWrite(buf, 0, read)
                    if (write < 0) throw errnoToIOException(errno)
                }
            }
        }

        destFinal.chmod(mode)
    } catch (e: IOException) {
        destFinal.delete()
        throw e
    } finally {
        // Always clean up and delete the gzipped file
        dest.delete()
    }

    return destFinal
}

@Throws(IOException::class)
@OptIn(DelicateFileApi::class, ExperimentalContracts::class, ExperimentalForeignApi::class)
private inline fun <T: Any?> File.gzOpenRead(
    block: (file: gzFile) -> T,
): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    val ptr: gzFile = gzopen(path, "rb") ?: throw errnoToIOException(errno)

    val result = try {
        block(ptr)
    } finally {
        if (gzclose_r(ptr) != Z_OK) {
            throw errnoToIOException(errno)
        }
    }

    return result
}
