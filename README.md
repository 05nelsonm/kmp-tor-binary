# kmp-tor-binary
[![badge-license]][url-license]
<!-- [![badge-latest-release]][url-latest-release] -->

[![badge-kotlin]][url-kotlin]

![badge-platform-android]
![badge-platform-jvm]
![badge-platform-js-node]
![badge-support-js-ir]

This project is focused on the compilation, packaging, distribution and installation of `tor`
resources for Kotlin Multiplatform, primarily to be consumed as a dependency by the 
[kmp-tor][url-kmp-tor] project.

**NOTE:** Support for [kmp-tor][url-kmp-tor] version `1.x.x` is 
maintained [HERE](https://github.com/05nelsonm/kmp-tor-binary/tree/1.x.x)

### Compilation

`tor` is compiled via the `external/task.sh` script using `Docker` (for Android/Jvm/Node.js) in order 
to maintain reproducability. The maintainer then creates detached code signatures for Apple/Windows targets 
which are checked into `git`; this is so others wishing to verify reproducability of the `tor` binaries 
they are running (or providing to their users) can do so. More on that later.

You can view the `help` output of `task.sh` by running `./external/task.sh` from the project's root directory.

```
# clone repository
$ cd kmp-tor-binary
$ ./external/task.sh
```

### Packaging

The compiled output is "packaged" for the given platforms (currently Android/Jvm/Node.js) and moved to 
their designated gradle module resource directories (e.g. `library/binary/src/jvmMain/resources`).

Running `./external/task.sh package` after a build will do the following.

**Android/Jvm/Node.js:**
 - Android `tor` binaries (`libtor.so` files) are moved to the `src/androidMain/jniLibs/{ABI}` directory
 - `geoip` & `geoip6` files are `gzipped` and moved to the `src/jvmAndroidMain/resources` directory
 - Detached code signatures for macOS and Windows are applied to the compilied `tor` binaries
 - `tor` is `gzipped` and moved to the `src/jvmMain/resources` directory for their respective host and 
   architecture.
 - `geoip`, `geoip6`, and `tor` files for each host/architecture are then published to `Npmjs`
   via the `library/npmjs` module (See https://www.npmjs.com/package/kmp-tor-binary-resources). The 
   `library/binary` module then consumes that `npm` dependency in order to access the resources from 
   Kotlin Multiplatform.

**iOS/macOS/tvOS/watchOS:**
 - Supporting darwin targets for Kotlin Multiplatform is a work in progress. See Issue 
   [[#120]](https://github.com/05nelsonm/kmp-tor-binary/issues/120)

### Distribution

New releases will be published to Maven Central and can be consumed as a Kotlin Multiplatform 
dependency.

### Installation

The [kmp-tor][url-kmp-tor] project will handle all of this behind the scenes. 
If you are not using that, simply call:

```kotlin
val paths = KmpTorBinary(destinationDir = "/path/to/my/tor/dir").install()
```

It will either throw an exception or extract the resources to the specified directory
 - Note that for Android it will depend on a few things
     - Android Runtime (Emulators or devices) it will search for `libtor.so` within the 
       application's `nativeLibraryDir`
         - See the `library/binary-initializer` module for more details on how it does that
     - Unit Tests you can add the `binary-android-unit-test` dependency and things will just 
       magically work 
         - Use `testImplementation` when adding the dependency!!! Do **NOT** ship your app with 
           that.

```kotlin
println(paths)

// KmpTorBinary.Paths: [
//     geoip: /path/to/my/tor/dir/geoip
//     geoip6: /path/to/my/tor/dir/geoip6
//     tor: /path/to/my/tor/dir/tor
// ]
```

See [HERE](https://github.com/05nelsonm/kmp-tor-binary/issues/85#issuecomment-1819747564) for 
more details.

<!--

TODO: gradle configuration for android

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

-->

<!-- TAG_VERSION -->
[badge-latest-release]: https://img.shields.io/badge/latest--release-4.8.6--0-5d2f68.svg?logo=torproject&style=flat&logoColor=5d2f68
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
