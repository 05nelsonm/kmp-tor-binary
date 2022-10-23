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

import android.content.Context
import io.matthewnelson.kmp.tor.binary.extract.internal.ExtractorDelegateJvmAndroid
import java.io.InputStream

/**
 * Extracts [TorResource]es to their desired
 * locations.
 *
 * @see [ExtractorDelegateJvmAndroid]
 * */
actual class Extractor(context: Context) {

    private val appContext = context.applicationContext
    private val delegate = object : ExtractorDelegateJvmAndroid() {
        override fun resourceNotFound(resource: String, t: Throwable): ExtractionException {
            return ExtractionException("Asset not found: $resource", t)
        }
    }

    /**
     * Extracts geoip files.
     *
     * @param [destination] The file to write to
     * @param [cleanExtraction] Perform a clean extraction of the [resource]
     *   by deleting the old file, and re-extracting the file.
     * @throws [ExtractionException]
     * */
    @Throws(ExtractionException::class)
    actual fun extract(
        resource: TorResource.Geoips,
        destination: String,
        cleanExtraction: Boolean,
    ) {
        delegate.extract(resource, destination, cleanExtraction) { resourcePath ->
            openAssetFileStream(resourcePath)
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
    actual fun extract(
        resource: TorResource.Binaries,
        destinationDir: String,
        cleanExtraction: Boolean,
    ): TorFilePath {
        return delegate.extract(resource, destinationDir, cleanExtraction) { resourcePath ->
            openAssetFileStream(resourcePath)
        }
    }

    @Throws(ExtractionException::class)
    private fun openAssetFileStream(resourcePath: String): InputStream {
        // Packaging gzipped files in the assets directory results in the
        // stripping of the .gz file extension when the apk is built. So
        // we check first for path w/o the .gz extension, then with it.

        return try {
            appContext.assets.open(resourcePath.dropLast(3))
        } catch (_: Throwable) {
            try {
                appContext.assets.open(resourcePath)
            } catch (t: Throwable) {
                throw delegate.resourceNotFound(resourcePath, t)
            }
        }
    }
}
