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
    val ZIP_SHA256_GEOIP: String
    val ZIP_MANIFEST_GEOIP: List<String>

    val FILE_NAME_GEOIPS_ZIP: String
    val FILE_NAME_GEOIPS_ZIP_SHA256: String
}
