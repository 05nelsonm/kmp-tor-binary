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
package io.matthewnelson.kmp.tor.binary.initializer

import android.content.Context
import androidx.startup.AppInitializer
import androidx.startup.Initializer

public class KmpTorBinaryInitializerImpl: Initializer<KmpTorBinaryInitializer> {
    override fun create(context: Context): KmpTorBinaryInitializer {
        val appInitializer = AppInitializer.getInstance(context)
        check(appInitializer.isEagerlyInitialized(javaClass)) {
            errorMsg()
        }
        KmpTorBinaryInitializer.init(context)
        return KmpTorBinaryInitializer.INSTANCE
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    public companion object {

        @Suppress("NOTHING_TO_INLINE")
        public inline fun errorMsg(): String = ERROR_MSG.trimIndent()

        @PublishedApi
        internal const val ERROR_MSG: String = """
            KmpTorBinaryInitializerImpl cannot be initialized lazily.
            Please ensure that you have:
            <meta-data
                android:name='io.matthewnelson.kmp.tor.binary.initializer.KmpTorBinaryInitializerImpl'
                android:value='androidx.startup' />
            under InitializationProvider in your AndroidManifest.xml
        """
    }
}
