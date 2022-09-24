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

@Suppress("ObjectPropertyName", "SpellCheckingInspection")
actual object ConstantsBinaries {
    private const val _ZIP_SHA256_GEOIP = "2dce16ea6a5ae3e82c90a1b82bb5ff2eb2a91be98ec2ed539ad722c246752705"
    private const val _FILE_NAME_GEOIPS_ZIP = "geoips.zip"
    private const val _FILE_NAME_GEOIPS_ZIP_SHA256 = "$_FILE_NAME_GEOIPS_ZIP.sha256sum"

    actual val ZIP_SHA256_GEOIP: String get() = _ZIP_SHA256_GEOIP
    actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf("geoip", "geoip6")
    actual val FILE_NAME_GEOIPS_ZIP: String get() = _FILE_NAME_GEOIPS_ZIP
    actual val FILE_NAME_GEOIPS_ZIP_SHA256: String get() = _FILE_NAME_GEOIPS_ZIP_SHA256
}
