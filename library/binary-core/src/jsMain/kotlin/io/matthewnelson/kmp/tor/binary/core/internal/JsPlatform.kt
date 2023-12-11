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

@OptIn(DelicateFileApi::class, InternalKmpTorBinaryApi::class)
internal actual fun Resource.extractTo(destinationDir: File): File {
    var fileName = platform.resourcePath.substringAfterLast('/')
    val isGzipped = if (fileName.endsWith(".gz")) {
        fileName = fileName.substringBeforeLast(".gz")
        true
    } else {
        false
    }

    val destination = destinationDir.resolve(fileName)
    val moduleResource = resolveResource(platform.moduleName + platform.resourcePath).toFile()

    if (destination.exists() && !destination.delete()) {
        throw IOException("Failed to delete $destination")
    }

    var buffer = moduleResource.read()

    if (isGzipped) {
        buffer = Buffer.wrap(zlib_gunzipSync(buffer.unwrap()))
    }

    try {
        destination.write(buffer)
    } catch (t: IOException) {
        destination.delete()
        throw t
    }

    return destination
}

@Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")
private inline fun resolveResource(path: String): String = try {
    js("require.resolve(path)") as String
} catch (t: Throwable) {
    @OptIn(DelicateFileApi::class)
    throw t.toIOException()
}
