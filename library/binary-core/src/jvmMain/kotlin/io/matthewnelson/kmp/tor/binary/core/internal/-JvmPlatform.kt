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

import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.IOException
import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.core.Resource
import java.util.zip.GZIPInputStream

@OptIn(InternalKmpTorBinaryApi::class)
internal actual fun Resource.extractTo(destinationDir: File): File {
    var fileName = platform.resourcePath.substringAfterLast('/')
    val isGzipped = if (fileName.endsWith(".gz")) {
        fileName = fileName.substringBeforeLast(".gz")
        true
    } else {
        false
    }

    val destination = destinationDir.resolve(fileName)

    var resourceStream = platform.resourceClass.getResourceAsStream(platform.resourcePath)
        ?: throw IOException("Failed to get resource input stream for ${platform.resourcePath}")

    if (destination.exists() && !destination.delete()) {
        try {
            resourceStream.close()
        } catch (_: Throwable) {}
        throw IOException("Failed to delete $destination")
    }

    if (isGzipped) {
        resourceStream = GZIPInputStream(resourceStream)
    }

    resourceStream.use { iStream ->
        destination.outputStream().use { oStream ->
            val buf = ByteArray(4096)

            while (true) {
                val read = iStream.read(buf)
                if (read == -1) break
                oStream.write(buf, 0, read)
            }
        }
    }

    return destination
}

@Throws(IOException::class)
internal actual fun File.setResourcePermissions(isExecutable: Boolean) {
    setReadable(false, false)
    setWritable(false, false)
    setReadable(true, true)
    if (isExecutable) {
        setExecutable(true, true)
    }
}
