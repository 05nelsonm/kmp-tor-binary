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

import io.matthewnelson.kmp.tor.binary.extract.annotation.InternalTorBinaryApi

@InternalTorBinaryApi
// TODO: Move to platform specific locations
@Suppress("ObjectPropertyName", "SpellCheckingInspection")
actual object ConstantsBinaries {
    private const val _ARCHIVE_SHA256_VALUE_GEOIP = "d25e55d538b0628a17795ff45b94ff772f8e1bec2808f3dbc162c383a0cd0a41"
    private const val _ARCHIVE_FILE_NAME_GEOIP = "geoips.tar.gz"

    actual val ARCHIVE_SHA256_VALUE_GEOIP: String get() = _ARCHIVE_SHA256_VALUE_GEOIP
    actual val ARCHIVE_MANIFEST_GEOIP: List<String> get() = listOf("geoip", "geoip6")

    actual val ARCHIVE_FILE_NAME_GEOIP: String get() = _ARCHIVE_FILE_NAME_GEOIP
    actual val ARCHIVE_SHA256_FILE_NAME_GEOIP: String get() = "$ARCHIVE_FILE_NAME_GEOIP.sha256sum"

    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_SHA256_VALUE_GEOIP"))
    actual val ZIP_SHA256_GEOIP: String get() = ARCHIVE_SHA256_VALUE_GEOIP
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_MANIFEST_GEOIP"))
    actual val ZIP_MANIFEST_GEOIP: List<String> get() = ARCHIVE_MANIFEST_GEOIP
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_FILE_NAME_GEOIP"))
    actual val FILE_NAME_GEOIPS_ZIP: String get() = ARCHIVE_FILE_NAME_GEOIP
    @Deprecated("Variable name changed", ReplaceWith("ARCHIVE_SHA256_FILE_NAME_GEOIP"))
    actual val FILE_NAME_GEOIPS_ZIP_SHA256: String get() = ARCHIVE_SHA256_FILE_NAME_GEOIP
}
