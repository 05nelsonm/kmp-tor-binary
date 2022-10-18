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

@Suppress("SpellCheckingInspection")
expect object ConstantsBinaries {
    val ARCHIVE_SHA256_VALUE_GEOIP: String
    val ARCHIVE_MANIFEST_GEOIP: List<String>

    val ARCHIVE_FILE_NAME_GEOIP: String
    val ARCHIVE_SHA256_FILE_NAME_GEOIP: String

    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_SHA256_VALUE_GEOIP"))
    actual val ZIP_SHA256_GEOIP: String
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_MANIFEST_GEOIP"))
    actual val ZIP_MANIFEST_GEOIP: List<String>
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_FILE_NAME_GEOIP"))
    actual val FILE_NAME_GEOIPS_ZIP: String
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_SHA256_FILE_NAME_GEOIP"))
    actual val FILE_NAME_GEOIPS_ZIP_SHA256: String
}
