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

import io.matthewnelson.kmp.tor.binary.extract.internal.*
import io.matthewnelson.kmp.tor.binary.extract.internal.existsSync
import io.matthewnelson.kmp.tor.binary.extract.internal.lstatSync
import io.matthewnelson.kmp.tor.binary.extract.internal.realpathSync
import io.matthewnelson.kmp.tor.binary.extract.internal.sep

/**
 * Extracts [TorResource]es to their desired
 * locations.
 *
 * @see [ExtractorCommon]
 * */
actual class Extractor: ExtractorCommon<String, Any>() {

    /**
     * Extracts geoip files.
     *
     * @param [destination] The file to write to
     * @param [cleanExtraction] Perform a clean extraction of the [resource]
     *   by deleting the old file, and re-extracting the file.
     * @throws [ExtractionException]
     * */
    actual fun extract(
        resource: TorResource.Geoips,
        destination: String,
        cleanExtraction: Boolean
    ) {
        extract(resource, destination, cleanExtraction) { resourcePath ->
            val modulePath = "kmp-tor-binary-geoip/$resourcePath"

            val resolvedPath = try {
                resolveResource(modulePath)
            } catch (t: Throwable) {
                throw resourceNotFound(modulePath, t)
            }

            readFileSync(resolvedPath)
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
    actual fun extract(
        resource: TorResource.Binaries,
        destinationDir: String,
        cleanExtraction: Boolean,
    ): TorFilePath {
        return extract(resource, destinationDir, cleanExtraction) { resourcePath ->
            val modulePath = when (resource) {
                is TorResourceLinuxX64 -> "kmp-tor-binary-linuxx64"
                is TorResourceLinuxX86 -> "kmp-tor-binary-linuxx86"
                is TorResourceMacosArm64 -> "kmp-tor-binary-macosarm64"
                is TorResourceMacosX64 -> "kmp-tor-binary-macosx64"
                is TorResourceMingwX64 -> "kmp-tor-binary-mingwx64"
                is TorResourceMingwX86 -> "kmp-tor-binary-mingwx86"
            } + "/$resourcePath"

            val resolvedPath = try {
                resolveResource(modulePath)
            } catch (t: Throwable) {
                throw resourceNotFound(modulePath, t)
            }

            readFileSync(resolvedPath)
        }
    }


    override fun String.toFile(): String = this
    override fun isFile(file: String): Boolean = lstatSync(file).isFile()
    override fun isDirectory(file: String): Boolean = lstatSync(file).isDirectory()
    override fun nameWithoutExtension(file: String): String = file.substringAfterLast(sep).substringBeforeLast('.')
    override fun canonicalPath(file: String?): String? = file?.let { realpathSync(it) }
    override fun exists(file: String): Boolean = existsSync(file)

    override fun deleteFile(file: String): Boolean {
        try {
            rmSync(file, RmOptions().apply {
                force = true
            })
        } catch (_: Throwable) {}

        return exists(file)
    }

    override fun deleteDirectory(file: String): Boolean {
        try {
            rmdirSync(file, RmDirOptions().apply {
                recursive = true
            })
        } catch (_: Throwable) {}

        return exists(file)
    }

    override fun mkdirs(file: String): Boolean {
        try {
            mkdirSync(file)
        } catch (_: Throwable) {}

        return exists(file) && isDirectory(file)
    }

    override fun gunzip(stream: Any): Any { return gunzipSync(stream) }

    override fun readText(file: String): String = readFileSync(file, ReadFileOptions().apply { encoding = "utf8" }) as String
    override fun writeText(file: String, text: String) {
        writeFileSync(file, text)
    }

    override fun String.write(stream: Any) {
        val parentDir = substringBeforeLast(sep)
        if (parentDir != this) {
            if (!exists(parentDir) && !mkdirs(this)) {
                throw ExtractionException("Failed to create directory $parentDir")
            }
        }

        if (exists(this) && !deleteFile(this)) {
            throw ExtractionException("Failed to delete file $this before overwriting it.")
        }

        try {
            writeFileSync(this, stream)
        } catch (t: Throwable) {
            throw ExtractionException("Failed to write data to $this", t)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun resolveResource(path: String): String = js("require.resolve(path)") as String
}
