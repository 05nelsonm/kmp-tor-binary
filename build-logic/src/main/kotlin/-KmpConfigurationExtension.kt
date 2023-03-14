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
import io.matthewnelson.kmp.configuration.extension.KmpConfigurationExtension
import io.matthewnelson.kmp.configuration.extension.container.target.KmpConfigurationContainerDsl
import org.gradle.api.Action
import org.gradle.api.JavaVersion

fun KmpConfigurationExtension.configureShared(
    androidNameSpace: String? = null,
    enableJvm: Boolean = true,
    publish: Boolean = false,
    npmPublish: Boolean = false,
    explicitApi: Boolean = true,
    action: Action<KmpConfigurationContainerDsl>
) {
    configure {
        if (enableJvm) {
            jvm {
                if (androidNameSpace == null) { target { withJava() } }

                kotlinJvmTarget = JavaVersion.VERSION_1_8
                compileSourceCompatibility = JavaVersion.VERSION_1_8
                compileTargetCompatibility = JavaVersion.VERSION_1_8
            }
        }

        if (androidNameSpace != null) {
            androidLibrary {
                target { publishLibraryVariants("release") }

                android {
                    buildToolsVersion = "33.0.1"
                    compileSdk = 33
                    namespace = androidNameSpace

                    defaultConfig {
                        minSdk = 16
                        targetSdk = 33

                        testInstrumentationRunnerArguments["disableAnalytics"] = "true"
                    }
                }

                kotlinJvmTarget = JavaVersion.VERSION_1_8
                compileSourceCompatibility = JavaVersion.VERSION_1_8
                compileTargetCompatibility = JavaVersion.VERSION_1_8
            }
        }

        common {
            if (publish) { pluginIds("publication") }
            if (npmPublish) { pluginIds("publication-npm") }
        }

        if (explicitApi) { kotlin { explicitApi() } }

        action.execute(this)
    }
}