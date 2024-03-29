# kmp-tor-binary
[![badge-license]][url-license]
[![badge-latest-release]][url-latest-release]

[![badge-kotlin]][url-kotlin]

![badge-platform-android]
![badge-platform-jvm]

<!-- TODO: Add Node.js badge
![badge-platform-js-node]
![badge-support-js-ir]
-->

Tor binary resource distribution for the [kmp-tor][url-kmp-tor] project  

**NOTE:** This branch is for [kmp-tor][url-kmp-tor] `1.x.x` support and is feature frozen. 
See [master](https://github.com/05nelsonm/kmp-tor-binary/tree/master) branch for the latest and greatest.

## Getting Started (Configuration)

<details>
    <summary>Android</summary>

Tor binaries for `Android` **are automatically imported** with the [kmp-tor][url-kmp-tor] 
dependency, so you do **not** need to add the `kmp-tor-binary-android` dependency 
**if** you are using [kmp-tor][url-kmp-tor]. **CONFIGURATION BELOW IS STILL NEEDED THOUGH.**  

`Android` requires some configuration so binaries will be appropriately extracted to your 
app's `nativeLibraryDir` upon application installation.  

 - Ensure `JavaVersion` is greater than or equal to 11:
   ```kotlin
   // build.gradle.kts

   android {
       // ...

       compileOptions {
           sourceCompatibility = JavaVersion.VERSION_11
           targetCompatibility = JavaVersion.VERSION_11
       }

       kotlinOptions {
           jvmTarget = JavaVersion.VERSION_11.toString()
       }
   }
   ```

 - Enable legacy packaging for `jniLibs` directory:
   ```kotlin
   // build.gradle.kts

   android {
       // ...

       packagingOptions {
           jniLibs.useLegacyPackaging = true
       }
   }
   ```

 - Add to your `AndroidManifest.xml`, within the `application` tag:
   ```xml
   <application
       android:extractNativeLibs="true">
   
   </application>
   ```

 - Configure splits for each architecture by adding the following to your 
   application module's `android` block:
   ```kotlin
   // build.gradle.kts

   android {
       // ...

       splits {

           // Configures multiple APKs based on ABI. This helps keep the size
           // down, since PT binaries can be large.
           abi {

               // Enables building multiple APKs per ABI.
               isEnable = true

               // By default, all ABIs are included, so use reset() and include to specify
               // that we only want APKs for x86 and x86_64, armeabi-v7a, and arm64-v8a.

               // Resets the list of ABIs that Gradle should create APKs for to none.
               reset()

               // Specifies a list of ABIs that Gradle should create APKs for.
               include("x86", "armeabi-v7a", "arm64-v8a", "x86_64")

               // Specify whether you wish to also generate a universal APK that
               // includes _all_ ABIs.
               isUniversalApk = true
           }
       }
   }
   ```

 - If you are publishing your application to Google Play using app bundling,
   add the following to your project's `gradle.properties` file:
   ```groovy
   android.bundle.enableUncompressedNativeLibs=false
   ```

     - You can also verify (prior to pushing your release to Google Play)
       if the bundled apk extracts binaries on install correctly by using
       the [bundletool][url-bundletool].  

### That's it, you should be good to go for your `Android` project!

</details>

<details>
    <summary>Java</summary>

Tor binaries for `Java` are **not** automatically imported with the [kmp-tor][url-kmp-tor] 
dependency. You need to add the dependencies for the platform(s) you wish to support.

<!-- TAG_VERSION -->

 - Add dependencies:
   ```kotlin
   // build.gradle.kts

   dependencies {
       val vTor = "4.8.10-0"
       val vKmpTor = "1.4.4" // <-- see kmp-tor repo for latest version
       implementation("io.matthewnelson.kotlin-components:kmp-tor:$vTor-$vKmpTor")

       // Linux x86_64
       implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-linuxx64:$vTor")
       // Linux i686
       implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-linuxx86:$vTor")
       // macOS aarch64
       implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-macosarm64:$vTor")
       // macOS x86_64
       implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-macosx64:$vTor")
       // Windows x86_64
       implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-mingwx64:$vTor")
       // Windows i686
       implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-mingwx86:$vTor")
   }
   ```
 - If a specific platform or architecture is not currently supported by `kmp-tor-binary`, you can package 
   your own and provide them to [kmp-tor][url-kmp-tor] at runtime for extraction and execution.
   ```kotlin
   // Add the additional 'extract' dependency
   dependencies {
       implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-extract:$vTor")
   }
   ```
     - See [TorBinaryResource][url-tor-binary-resource] documentation for packaging requirements and details.
     - Load them via `kmp-tor`'s [PlatformInstaller][url-kmp-tor-platform-installer] (available since v`4.7.13-1-1.4.0`)
       ```kotlin
       val installer = PlatformInstaller.custom(
           option = InstallOption.CleanInstallIfMissing,
           resource = TorBinaryResource.from(
               os = TorBinaryResource.OS.Linux,
               arch = "arm64",
               loadPathPrefix = "com.example",
               sha256sum = "abcdefg123...",
               resourceManifest = listOf(
                   "directory/file1.gz",
                   "directory/file2.gz",
                   "file3.gz",
                   "tor.gz",
               )
           )
       )
       ```
     - Then in the module that contains your custom binary resources
       ```kotlin
       // pacakge ${loadPathPrefix}.<lowercase os name>.<arch>
       package com.example.linux.arm64
       
       // Must be named "Loader"
       private class Loader
       ```
         - In the event finding your custom resources fails using the standard method, extraction will 
           fall back to attempting to retrieve the `com.example.linux.arm64.Loader` via reflection and
           use its `ClassLoader` to retrieve the resources located in that module/jar. This is often the 
           case for projects that are utilizing the Java 9 `Module` system (such as JavaFX).

### That's it, you should be good to go for your `Java` project!

</details>

<!-- TODO: Uncomment + comment out TAG_VERSION declarations (when kmp-tor is ready for nodejs)
<details>
    <summary>Node.js</summary>

Tor binaries for `Node.js` are **not** automatically imported with the [kmp-tor][url-kmp-tor]
dependency. You need to add the dependencies for the platform(s) you wish to support, which 
are distributed via `npmjs` as `node` modules (available since `4.7.10-1`).

TODO: Comment out
TAG_VERSION

- Add dependencies:
  ```kotlin
  // build.gradle.kts

  dependencies {
      val vTor = "4.8.10-0"
      val vKmpTor = "1.4.4" // <-- see kmp-tor repo for latest version
      implementation("io.matthewnelson.kotlin-components:kmp-tor:$vTor-$vKmpTor")

      // Linux x86_64
      implementation(npm("kmp-tor-binary-linuxx64", vTor))
      // Linux i686
      implementation(npm("kmp-tor-binary-linuxx86", vTor))
      // macOS aarch64
      implementation(npm("kmp-tor-binary-macosarm64", vTor))
      // macOS x86_64
      implementation(npm("kmp-tor-binary-macosx64", vTor))
      // Windows x86_64
      implementation(npm("kmp-tor-binary-mingwx64", vTor))
      // Windows i686
      implementation(npm("kmp-tor-binary-mingwx86", vTor))
  }
  ```
 - If a specific platform or architecture is not currently supported by `kmp-tor-binary`, you can package 
   your own and provide them to [kmp-tor][url-kmp-tor] at runtime for extraction and execution.
   ```kotlin
   // Add the additional `extract` dependency
   dependencies {
       implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-extract:$vTor")
   }
   ```
     - See [TorBinaryResource][url-tor-binary-resource] documentation for packaging requirements and details.
     - Load them via `kmp-tor`'s [PlatformInstaller][url-kmp-tor-platform-installer] (available since v`4.7.13-1-1.4.0`)
       ```kotlin
       val installer = PlatformInstaller.custom(
           option = InstallOption.CleanInstallIfMissing,
           resource = TorBinaryResource.from(
               os = TorBinaryResource.OS.Linux,
               arch = "arm64",

               // This will look for your binary assets listed below
               // in module "com-example-linuxarm64".
               loadPathPrefix = "com-example",

               sha256sum = "abcdefg123...",
               resourceManifest = listOf(
                   "directory/file1.gz",
                   "directory/file2.gz",
                   "file3.gz",
                   "tor.gz",
               )
           )
       )
       ```

### That's it, you should be good to go for your `Node.js` project!

</details>
-->

## Where do the binaries come from?

Binaries are reproducibly built via Tor Project's [tor-browser-build][url-tor-browser-build]

You can verify the reproducability of published binaries by following the [BUILD.md](BUILD.md) guide.

<!-- TAG_VERSION -->
[badge-latest-release]: https://img.shields.io/badge/latest--release-4.8.10--0-5d2f68.svg?logo=torproject&style=flat&logoColor=5d2f68
[badge-license]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat

<!-- TAG_DEPENDENCIES -->
[badge-kotlin]: https://img.shields.io/badge/kotlin-1.9.10-blue.svg?logo=kotlin

<!-- TAG_PLATFORMS -->
[badge-platform-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat
[badge-platform-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat
[badge-platform-js]: http://img.shields.io/badge/-js-F8DB5D.svg?style=flat
[badge-platform-js-node]: https://img.shields.io/badge/-nodejs-68a063.svg?style=flat
[badge-platform-linux]: http://img.shields.io/badge/-linux-2D3F6C.svg?style=flat
[badge-platform-macos]: http://img.shields.io/badge/-macos-111111.svg?style=flat
[badge-platform-ios]: http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat
[badge-platform-tvos]: http://img.shields.io/badge/-tvos-808080.svg?style=flat
[badge-platform-watchos]: http://img.shields.io/badge/-watchos-C0C0C0.svg?style=flat
[badge-platform-wasm]: https://img.shields.io/badge/-wasm-624FE8.svg?style=flat
[badge-platform-windows]: http://img.shields.io/badge/-windows-4D76CD.svg?style=flat
[badge-support-android-native]: http://img.shields.io/badge/support-[AndroidNative]-6EDB8D.svg?style=flat
[badge-support-apple-silicon]: http://img.shields.io/badge/support-[AppleSilicon]-43BBFF.svg?style=flat
[badge-support-js-ir]: https://img.shields.io/badge/support-[js--IR]-AAC4E0.svg?style=flat

[url-bundletool]: https://github.com/google/bundletool
[url-latest-release]: https://github.com/05nelsonm/kmp-tor-binary/releases/latest
[url-license]: https://www.apache.org/licenses/LICENSE-2.0
[url-kotlin]: https://kotlinlang.org
[url-kmp-tor]: https://github.com/05nelsonm/kmp-tor
[url-kmp-tor-platform-installer]: https://github.com/05nelsonm/kmp-tor/blob/master/library/kmp-tor/src/jvmMain/kotlin/io/matthewnelson/kmp/tor/PlatformInstaller.kt
[url-tor-browser-build]: https://gitlab.torproject.org/tpo/applications/tor-browser-build/
[url-tor-binary-resource]: https://github.com/05nelsonm/kmp-tor-binary/blob/master/library/kmp-tor-binary-extract/src/jvmJsMain/kotlin/io/matthewnelson/kmp/tor/binary/extract/TorBinaryResource.kt
