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

import io.matthewnelson.kmp.tor.binary.extract.internal.ExtractorDelegateJvmAndroid

/**
 * Extracts [TorResource]es to their desired
 * locations.
 * */
public actual class Extractor {

    private val delegate = ExtractorDelegateJvmAndroid()

    /**
     * Extracts geoip files.
     *
     * @param [destination] The file to write to
     * @param [cleanExtraction] Perform a clean extraction of the [resource]
     *   by deleting the old file, and re-extracting the file.
     * @throws [ExtractionException]
     * */
    @Throws(ExtractionException::class)
    public actual fun extract(
        resource: TorResource.Geoips,
        destination: String,
        cleanExtraction: Boolean
    ) {
        delegate.extract(resource, destination, cleanExtraction) { resourcePath ->
            try {
                javaClass.getResourceAsStream("/$resourcePath")!!
            } catch (t: Throwable) {
                throw delegate.resourceNotFound(resourcePath, t)
            }
        }
    }

    /**
     * Extract binaries to desired [destinationDir], and
     * returns the [TorFilePath] to the extracted Tor file
     * to start the daemon.
     *
     * @param [destinationDir] The directory to write files to
     * @param [cleanExtraction] Performs a clean extraction of all files for the [resource]
     *   by deleting the [destinationDir], and re-extracting all files.
     * @throws [ExtractionException]
     * */
    @Throws(ExtractionException::class)
    public actual fun extract(
        resource: TorResource.Binaries,
        destinationDir: String,
        cleanExtraction: Boolean,
    ): TorFilePath {
        return delegate.extract(resource, destinationDir, cleanExtraction) { resourcePath ->
            try {
                javaClass.getResourceAsStream("/$resourcePath")!!
            } catch (t: Throwable) {
                throw delegate.resourceNotFound(resourcePath, t)
            }
        }
    }
}
