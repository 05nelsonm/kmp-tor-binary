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

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

actual sealed interface ZipArchiveExtractor {

    @Throws(ZipExtractionException::class)
    actual fun extract()

    companion object {

        /**
         * See [ZipArchiveExtractorAllJvm]
         * */
        @JvmStatic
        fun all(
            destinationDir: File,
            postExtraction: (List<File>.() -> Unit)?,
            zipFileStreamProvider: () -> InputStream,
        ): ZipArchiveExtractor =
            ZipArchiveExtractorAllJvm(destinationDir, postExtraction, zipFileStreamProvider)

        /**
         * See [ZipArchiveExtractorSelectiveJvm]
         * */
        @JvmStatic
        fun selective(
            zipFileStreamProvider: () -> InputStream,
            postExtraction: (File.() -> Unit)?,
            extractToFile: ReadOnlyZipEntry.() -> File?,
        ): ZipArchiveExtractor =
            ZipArchiveExtractorSelectiveJvm(zipFileStreamProvider, postExtraction, extractToFile)
    }
}

/**
 * Extracts entire contents of the Zip Archive to a specified directory. Any failure
 * results in the entire [destinationDir]'s deletion.
 * */
private class ZipArchiveExtractorAllJvm(
    private val destinationDir: File,
    private val postExtraction: (List<File>.() -> Unit)?,
    private val zipFileStreamProvider: () -> InputStream,
) : ZipEntryExtractor(), ZipArchiveExtractor {

    @Throws(ZipExtractionException::class)
    override fun extract() {

        val extractedFiles = mutableListOf<File>()

        try {
            if (destinationDir.exists()) {
                if (!destinationDir.isDirectory && !destinationDir.delete()) {
                    throw ZipExtractionException(
                        "Directory specified ($destinationDir) exists, " +
                        "is not a directory, and failed to delete prior to " +
                        "extracting zip archive contents"
                    )
                }
            }

            if (!destinationDir.exists() && !destinationDir.mkdir()) {
                throw ZipExtractionException(
                    "Failed to create destination dir ($destinationDir) to extract " +
                    "zip archive contents to"
                )
            }

            ZipInputStream(zipFileStreamProvider.invoke()).use { zis ->
                var entry: ZipEntry? = zis.nextEntry

                while (entry != null) {
                    val file = File(destinationDir, entry.name)

                    if (entry.isDirectory) {
                        if (!file.exists() && !file.mkdirs()) {
                            throw ZipExtractionException("Failed to create directory $file")
                        }
                        entry = zis.nextEntry
                        continue
                    }

                    zis.writeTo(file)
                    extractedFiles.add(file)
                    entry = zis.nextEntry
                }
            }

            postExtraction?.invoke(extractedFiles)
        } catch (e: ZipExtractionException) {
            for (file in extractedFiles) {
                file.delete()
            }
            throw e
        } catch (ee: Exception) {
            for (file in extractedFiles) {
                file.delete()
            }
            throw ZipExtractionException("Failed to extract Zip archive contents to $destinationDir", ee)
        }
    }
}

/**
 * Iterates through all entries of the Zip Archive. If the entry is _not_ a
 * directory, [extractToFile] will be invoked where by returning:
 *  - [File] will result in that [ReadOnlyZipEntry] being extracted to it
 *  - `null` will result in skipping to the next [ReadOnlyZipEntry], if any.
 *
 * In the event of a write error when extracting an entry to the specified [File],
 * that [File] is deleted. Files successfully extracted prior will remain.
 *
 * Upon successful extraction of an entry to a specified [File], [postExtraction]
 * (if not `null`) will be invoked in order to perform other operations, if needed.
 * */
private class ZipArchiveExtractorSelectiveJvm(
    private val zipFileStreamProvider: () -> InputStream,
    private val postExtraction: (File.() -> Unit)?,
    private val extractToFile: ReadOnlyZipEntry.() -> File?,
) : ZipEntryExtractor(), ZipArchiveExtractor {

    @Throws(ZipExtractionException::class)
    override fun extract() {

        var fileToDeleteOnBadWrite: File? = null

        try {
            ZipInputStream(zipFileStreamProvider.invoke()).use { zis ->
                var entry: ZipEntry? = zis.nextEntry

                while (entry != null) {
                    if (entry.isDirectory) {
                        entry = zis.nextEntry
                        continue
                    }

                    extractToFile.invoke(ReadOnlyZipEntry.new(entry))?.let { file ->
                        fileToDeleteOnBadWrite = file

                        zis.writeTo(file)

                        postExtraction?.invoke(file)

                        fileToDeleteOnBadWrite = null
                    }

                    entry = zis.nextEntry
                }
            }
        } catch (e: ZipExtractionException) {
            fileToDeleteOnBadWrite?.delete()
            throw e
        } catch (ee: Exception) {
            fileToDeleteOnBadWrite?.delete()
            throw ZipExtractionException("Failed to extract Zip archive", ee)
        }
    }
}

private abstract class ZipEntryExtractor {

    @Throws(ZipExtractionException::class, Exception::class)
    protected fun FilterInputStream.writeTo(file: File) {
        file.parentFile?.let { pf ->
            if (!pf.exists() && !pf.mkdirs()) {
                throw ZipExtractionException("Failed to create directory $pf")
            }
        }

        if (file.exists() && !file.delete()) {
            throw ZipExtractionException(
                "Failed to delete file $file in preparation for overwriting it"
            )
        }

        if (!file.createNewFile()) {
            throw ZipExtractionException("Failed to create file $file")
        }

        FileOutputStream(file).use { fos ->
            val buffer = ByteArray(4096)
            while (true) {
                val read = this.read(buffer)
                if (read == -1) break
                fos.write(buffer, 0, read)
            }
        }
    }
}
