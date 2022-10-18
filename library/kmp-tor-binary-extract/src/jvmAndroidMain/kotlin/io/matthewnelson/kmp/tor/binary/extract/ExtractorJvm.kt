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
package io.matthewnelson.kmp.tor.binary.extract

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.GZIPInputStream

abstract class ExtractorJvm internal constructor() {

    protected fun validateSha256SumFile(
        file: File,
        sha256Sum: String,
    ): Boolean {
        return try {
            if (file.exists()) {
                if (file.isFile) {
                    file.readText() == sha256Sum
                } else {
                    false
                }
            } else {
                false
            }
        } catch (_: Throwable) {
            false
        }
    }

    @Throws(ExtractionException::class)
    protected fun extract(
        resource: TorResource.Geoips,
        destination: String,
        cleanExtraction: Boolean,
        provideStream: () -> InputStream,
    ) {
        try {
            val destinationFile = File(destination)

            if (destinationFile.exists() && destinationFile.isDirectory) {
                throw ExtractionException("destination for ${resource.resourcePath} extraction cannot be a directory")
            }

            val sha256SumFile = File("${destination}_sha256.txt")
            val isSha256SumValid = validateSha256SumFile(sha256SumFile, resource.sha256sum)

            if (!cleanExtraction && destinationFile.exists() && isSha256SumValid) {
                return
            }

            val stream: GZIPInputStream = try {
                GZIPInputStream(provideStream())
            } catch (e: Exception) {
                throw ExtractionException("Failed to open stream for ${resource.resourcePath}", e)
            }

            try {
                destinationFile.write(stream)
            } catch (e: Exception) {
                destinationFile.delete()
                throw e
            }

            if (isSha256SumValid) {
                return
            }

            try {
                sha256SumFile.writeText(resource.sha256sum)
            } catch (e: Exception) {
                destinationFile.delete()
                sha256SumFile.delete()
                throw ExtractionException("Failed to write sha256sum to file $sha256SumFile", e)
            }
        } catch (e: ExtractionException) {
            throw e
        } catch (ee: Exception) {
            throw ExtractionException("Failed to extract ${resource.resourcePath} to $destination", ee)
        }
    }

    protected fun File.write(stream: InputStream) {
        stream.use { iStream ->
            parentFile?.let { pf ->
                if (!pf.exists() && !pf.mkdirs()) {
                    throw ExtractionException("Failed to create directory $pf")
                }
            }

            if (exists() && !delete()) {
                throw ExtractionException("Failed to delete file $this before overwriting it.")
            }

            if (!createNewFile()) {
                throw ExtractionException("Failed to create file $this")
            }

            FileOutputStream(this).use { oStream ->
                val buffer = ByteArray(4096)
                while (true) {
                    val read = iStream.read(buffer)
                    if (read == -1) break
                    oStream.write(buffer, 0, read)
                }
            }
        }
    }
}
