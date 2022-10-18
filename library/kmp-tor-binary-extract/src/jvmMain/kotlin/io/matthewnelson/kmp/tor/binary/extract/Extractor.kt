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

import io.matthewnelson.kmp.tor.binary.extract.internal.mapManifestToDestination
import java.io.File
import java.util.zip.GZIPInputStream

/**
 * Extracts [TorResource]es to their desired
 * locations.
 * */
actual class Extractor: ExtractorJvm() {

    /**
     * Extracts geoip files.
     *
     * @param [destination] The file to write to
     * @param [cleanExtraction] Perform a clean extraction of the [resource]
     * @throws [ExtractionException]
     * */
    @Throws(ExtractionException::class)
    actual fun extract(
        resource: TorResource.Geoips,
        destination: String,
        cleanExtraction: Boolean
    ) {
        extract(resource, destination, cleanExtraction) {
            javaClass.getResourceAsStream("/" + resource.resourcePath)!!
        }
    }

    /**
     * Extract binaries to desired [destinationDir], and
     * returns the [TorFilePath] to the extracted Tor file
     * to start the daemon.
     *
     * @param [destinationDir] The directory to write files to
     * @param [cleanExtraction] Performs a clean extraction of all files for the [resource]
     * @throws [ExtractionException]
     * */
    @Throws(ExtractionException::class)
    actual fun extract(
        resource: TorResource.Binaries,
        destinationDir: String,
        cleanExtraction: Boolean,
    ): TorFilePath {
        val manifest = resource.resourceManifest
        val sha256SumFile = File(destinationDir, FILE_NAME_SHA256_TOR)
        val filesWritten = ArrayList<File>(manifest.size + 1).apply { add(sha256SumFile) }
        val extractionDir = File(destinationDir)

        try {
            if (extractionDir.exists()) {
                if (!extractionDir.isDirectory && !extractionDir.delete()) {
                    throw ExtractionException(
                        "Directory specified ($destinationDir) exists, " +
                                "is not a directory, and failed to delete prior to " +
                                "extracting resources."
                    )
                }
            } else {
                if (!extractionDir.mkdirs()) {
                    throw ExtractionException(
                        "Failed to create destinationDir ($destinationDir) to extract $resource to."
                    )
                }
            }

            val sha256Sum = resource.sha256sum
            val isSha256SumValid = validateSha256SumFile(sha256SumFile, sha256Sum)

            val resourceDir = resource.resourceDirPath
            val extractResourceTo = ArrayList<Pair<String, File>>(manifest.size)
            var shouldExtract = !isSha256SumValid || cleanExtraction

            var torFile: File? = null

            manifest.mapManifestToDestination(destinationDir) { manifestItem, destination ->
                val writeTo = File(destination)
                extractResourceTo.add(Pair("/$resourceDir/$manifestItem", writeTo))

                if (writeTo.nameWithoutExtension.lowercase() == "tor") {
                    torFile = writeTo
                }

                if (!writeTo.exists()) {
                    shouldExtract = true
                }
            }

            if (shouldExtract) {
                extractResourceTo.forEach { item ->
                    filesWritten.add(item.second)
                    val stream = GZIPInputStream(javaClass.getResourceAsStream(item.first))
                    item.second.write(stream)
                }
            }

            if (!isSha256SumValid) {
                sha256SumFile.writeText(sha256Sum)
            }

            return torFile?.canonicalPath ?: throw NullPointerException("Tor binary file was not found after extraction.")
        } catch (e: ExtractionException) {
            for (file in filesWritten) {
                try {
                    file.delete()
                } catch (_: Exception) {}
            }

            throw e
        } catch (e: Exception) {
            for (file in filesWritten) {
                try {
                    file.delete()
                } catch (_: Exception) {}
            }

            throw ExtractionException("Failed to extract $resource to $destinationDir", e)
        }
    }
}
