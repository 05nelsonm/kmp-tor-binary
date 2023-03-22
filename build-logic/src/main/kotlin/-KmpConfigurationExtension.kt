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
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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

fun KmpConfigurationExtension.configureTool(
    project: Project,
    mainKtPath: String,
    enableNative: Boolean = true,
    action: Action<KmpConfigurationContainerDsl>
) {
    val (entryJvm, entryNative) = if (mainKtPath.isEmpty()) {
        Pair("MainKt", "main")
    } else {
        Pair("$mainKtPath.MainKt", "$mainKtPath.main")
    }

    configure {
        jvm {
            pluginIds("application")

            target { withJava() }

            kotlinJvmTarget = JavaVersion.VERSION_1_8
            compileSourceCompatibility = JavaVersion.VERSION_1_8
            compileTargetCompatibility = JavaVersion.VERSION_1_8
        }

        if (enableNative) {
            fun KotlinNativeTarget.setup() { binaries { executable { entryPoint = entryNative } } }

            val osName = System.getProperty("os.name")
            when {
                osName.startsWith("Windows", true) -> {
                    mingwX64(project.name) { target { setup() } }
                }

                osName == "Mac OS X" -> {
                    macosX64(project.name) { target { setup() } }
                }

                osName.contains("Mac", true) -> {
                    macosArm64(project.name) { target { setup() } }
                }

                osName == "Linux" -> {
                    linuxX64(project.name) { target { setup() } }
                }
            }
        }

        common {
            sourceSetMain {
                val libs = project.the<LibrariesForLibs>()

                dependencies {
                    implementation(libs.cliKt)
                }
            }

            sourceSetTest {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
        }

        kotlin {
            explicitApi()

            with(sourceSets) {
                findByName("jvmMain")?.run {
                    project.extensions.configure<JavaApplication>("application") {
                        mainClass.set(entryJvm)
                    }
                }
            }
        }

        action.execute(this)
    }
}
