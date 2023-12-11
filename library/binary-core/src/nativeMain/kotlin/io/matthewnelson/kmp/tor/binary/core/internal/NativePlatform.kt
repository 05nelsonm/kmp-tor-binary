/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.kmp.tor.binary.core.internal

import io.matthewnelson.kmp.file.File
import io.matthewnelson.kmp.file.IOException
import io.matthewnelson.kmp.file.resolve
import io.matthewnelson.kmp.tor.binary.core.InternalKmpTorBinaryApi
import io.matthewnelson.kmp.tor.binary.core.Resource

@OptIn(InternalKmpTorBinaryApi::class)
internal actual fun Resource.extractTo(destinationDir: File): File {
    var fileName = platform.nativeResource.name
    val isGzipped = if (fileName.endsWith(".gz")) {
        fileName = fileName.substringBeforeLast(".gz")
        true
    } else {
        false
    }

    val destination = destinationDir.resolve(fileName)

    if (destination.exists() && !destination.delete()) {
        throw IOException("Failed to delete $destination")
    }

    TODO("""
        outputStream(destination).use { oStream ->
          val out = if (isGzipped) {
              gunzipStream { buf, len ->
                  oStream.write(buf, 0, len)
              }
          } else {
              oStream
          }
          //
          nativeResource.read { buffer, len ->
              out.write(buffer, 0, len)
          }
        }
    """)


    return destination
}
