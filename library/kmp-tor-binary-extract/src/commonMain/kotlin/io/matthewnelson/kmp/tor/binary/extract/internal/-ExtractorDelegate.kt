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
package io.matthewnelson.kmp.tor.binary.extract.internal

import io.matthewnelson.kmp.tor.binary.extract.*

internal const val FILE_NAME_SHA256_SUFFIX = "_sha256.txt"
internal const val FILE_NAME_SHA256_TOR = "tor$FILE_NAME_SHA256_SUFFIX"

/**
 * Platform-agnostic implementation for extracting [TorResource]es
 * to desired locations.
 * */
internal abstract class ExtractorDelegate <F: Any, S: Any> {

    protected abstract fun String.toFile(): F
    protected abstract fun String.normalize(): String
    protected open val fsSeparator: Char get() = '/'
    protected abstract fun isFile(file: F): Boolean
    protected abstract fun isDirectory(file: F): Boolean
    protected abstract fun nameWithoutExtension(file: F): String
    protected abstract fun canonicalPath(file: F?): String?
    protected abstract fun exists(file: F): Boolean

    protected abstract fun deleteFile(file: F): Boolean
    protected abstract fun deleteDirectory(file: F): Boolean
    protected abstract fun mkdirs(file: F): Boolean

    protected abstract fun gunzip(stream: S): S

    protected abstract fun readText(file: F): String
    protected abstract fun writeText(file: F, text: String)
    @Throws(ExtractionException::class)
    protected abstract fun F.write(stream: S)

    @Throws(ExtractionException::class)
    internal fun extract(
        resource: TorResource.Geoips,
        destination: String,
        cleanExtraction: Boolean,
        provideStream: (resourcePath: String) -> S,
    ) {
        try {
            val destinationNormalized = destination.normalize()
            val destinationFile = destinationNormalized.toFile()

            if (exists(destinationFile) && isDirectory(destinationFile)) {
                throw ExtractionException("destination for ${resource.resourcePath} extraction cannot be a directory")
            }

            val sha256SumValue = resource.sha256sum
            val sha256SumFile = "$destinationNormalized$FILE_NAME_SHA256_SUFFIX".toFile()
            val isSha256SumValid = checkSha256SumFile(sha256SumFile, sha256SumValue)

            if (!cleanExtraction && exists(destinationFile) && isSha256SumValid) {
                return
            }

            val gunzipStream = try {
                gunzip(provideStream.invoke(resource.resourcePath))
            } catch (e: ExtractionException) {
                throw e
            } catch (t: Throwable) {
                throw ExtractionException("Failed to open stream for ${resource.resourcePath}", t)
            }

            try {
                destinationFile.write(gunzipStream)
            } catch (t: Throwable) {
                deleteFile(destinationFile)
                throw t
            }

            if (isSha256SumValid) return

            try {
                writeText(sha256SumFile, sha256SumValue)
            } catch (t: Throwable) {
                deleteFile(destinationFile)
                deleteFile(sha256SumFile)
                throw ExtractionException("Failed to write sha256sum to file $sha256SumFile", t)
            }
        } catch (e: ExtractionException) {
            throw e
        } catch (t: Throwable) {
            throw ExtractionException("Failed to extract ${resource.resourcePath} to $destination", t)
        }
    }

    @Throws(ExtractionException::class)
    internal fun extract(
        resource: TorResource.Binaries,
        destinationDir: String,
        cleanExtraction: Boolean,
        provideStream: (resourcePath: String) -> S
    ): TorFilePath {
        val destinationDirNormalized = try {
            destinationDir.normalize()
        } catch (t: Throwable) {
            throw ExtractionException("Failed to normalize destinationDir: $destinationDir")
        }

        val manifest = resource.resourceManifest
        val filesWritten = ArrayList<F>(manifest.size + 1)
        val extractionToDir = destinationDirNormalized.toFile()

        try {
            if (exists(extractionToDir)) {
                if (!isDirectory(extractionToDir) && !deleteFile(extractionToDir)) {
                    throw ExtractionException(
                        "Directory specified ($destinationDir) exists, " +
                        "is not a directory, and failed to delete prior to " +
                        "extracting resources."
                    )
                }
            } else {
                if (!mkdirs(extractionToDir)) {
                    throw ExtractionException(
                        "Failed to create destinationDir ($destinationDir) to extract $resource to."
                    )
                }
            }

            val sha256SumFile = "$destinationDirNormalized$fsSeparator$FILE_NAME_SHA256_TOR".normalize().toFile()
            filesWritten.add(sha256SumFile)

            val sha256SumValue = resource.sha256sum
            val isSha256SumValid = checkSha256SumFile(sha256SumFile, sha256SumValue)

            val resourceDirPath = resource.resourceDirPath
            val extractResourceTo = ArrayList<Pair<String, F>>(manifest.size)
            var shouldExtract = !isSha256SumValid || cleanExtraction

            var torFile: F? = null

            manifest.mapManifestToDestination(destinationDirNormalized) { manifestItem, destination ->
                val writeTo = destination.toFile()
                extractResourceTo.add(Pair("$resourceDirPath/$manifestItem", writeTo))

                if (nameWithoutExtension(writeTo).lowercase() == "tor") {
                    torFile = writeTo
                }

                if (!exists(writeTo)) {
                    shouldExtract = true
                }
            }

            if (shouldExtract) {
                extractResourceTo.forEach { item ->
                    filesWritten.add(item.second)
                    val gunzipStream = gunzip(provideStream.invoke(item.first))
                    item.second.write(gunzipStream)
                }
            }

            if (!isSha256SumValid) {
                writeText(sha256SumFile, sha256SumValue)
            }

            return canonicalPath(torFile) ?: throw NullPointerException("Tor binary file was not found after extraction")
        } catch (e: ExtractionException) {
            for (file in filesWritten) {
                try {
                    deleteFile(file)
                } catch (_: Exception) {}
            }

            throw e
        } catch (e: Exception) {
            for (file in filesWritten) {
                try {
                    deleteFile(file)
                } catch (_: Exception) {}
            }

            throw ExtractionException("Failed to extract $resource to $destinationDir", e)
        }
    }

    internal open fun resourceNotFound(resource: String, t: Throwable): ExtractionException {
        return ExtractionException("Resource not found: $resource", t)
    }

    /**
     * The [TorResource.sha256sum] is persisted to the filesystem after
     * each [extract] in order to mitigate unnecessary extraction on every
     * Tor start.
     * */
    private fun checkSha256SumFile(file: F, sha256Sum: String): Boolean {
        return try {
            if (exists(file)) {
                if (isFile(file)) {
                    readText(file) == sha256Sum
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
}
