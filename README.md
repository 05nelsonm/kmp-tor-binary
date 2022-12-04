# kmp-tor-binary
[![badge-license]][url-license]
[![badge-latest-release]][url-latest-release]

[![badge-kotlin]][url-kotlin]

![badge-platform-android]
![badge-platform-jvm]

<!-- TODO: Add Node.js badge
![badge-platform-nodejs]
-->

Tor binary resource distribution for the [kmp-tor][url-kmp-tor] project  

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
       val vTor = "4.7.11-0"
       val vKmpTor = "1.3.1" // <-- see kmp-tor repo for latest version
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
      val vTor = "4.7.11-0"
      val vKmpTor = "1.3.1" // <-- see kmp-tor repo for latest version
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


### That's it, you should be good to go for your `Node.js` project!

</details>
-->

## Git

This project utilizes git submodules. You will need to initialize them when 
cloning the repository via:

```
git checkout master
git pull
git submodule update --init
```

## Where do the binaries come from?

Binaries are reproducibly built via Tor Project's [tor-browser-build][url-tor-browser-build] project.

## Building Binaries Yourself

 - Clone the repo and initialize submodules:
   ```
   git clone https://github.com/05nelsonm/kmp-tor-binary.git
   cd kmp-tor-binary
   git submodule update --init
   ```

 - Install dependencies:
     - See `tor-browser-build`'s `README` for needed dependencies and install them:
       ```
       cd library/binary-build/tor-browser-build
       nano README
       ```

 - Initialize `tor-browser-build`'s `rbm` submodule:
   ```
   make submodule-update
   ```

 - Run the build script:
     - Change directories back to `binary-build`:
       ```
       cd ../
       ```
     - Running the script will automatically print `help` to see what targets are available:
       ```
       ./scripts/build_binaries.sh
       ```
         - Example:
           ```
           ./scripts/build_binaries.sh desktop-all
           ```


Building a target will automatically extract, package, and move binaries/geoip files 
to the appropriate `kmp-tor-binary-*` module and update sha256sum/manifest constants.  

As binaries are reproducibly built, running `git diff` should show no changes.

<!-- TAG_VERSION -->
[badge-latest-release]: https://img.shields.io/badge/latest--release-4.7.11--0-5d2f68.svg?logo=torproject&style=flat&logoColor=5d2f68
[badge-license]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat

<!-- TAG_DEPENDENCIES -->
[badge-kotlin]: https://img.shields.io/badge/kotlin-1.6.21-blue.svg?logo=kotlin

[badge-platform-android]: https://camo.githubusercontent.com/b1d9ad56ab51c4ad1417e9a5ad2a8fe63bcc4755e584ec7defef83755c23f923/687474703a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d616e64726f69642d3645444238442e7376673f7374796c653d666c6174
[badge-platform-apple-silicon]: https://camo.githubusercontent.com/a92c841ffd377756a144d5723ff04ecec886953d40ac03baa738590514714921/687474703a2f2f696d672e736869656c64732e696f2f62616467652f737570706f72742d2535424170706c6553696c69636f6e2535442d3433424246462e7376673f7374796c653d666c6174
[badge-platform-ios]: https://camo.githubusercontent.com/1fec6f0d044c5e1d73656bfceed9a78fd4121b17e82a2705d2a47f6fd1f0e3e5/687474703a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d696f732d4344434443442e7376673f7374796c653d666c6174
[badge-platform-jvm]: https://camo.githubusercontent.com/700f5dcd442fd835875568c038ae5cd53518c80ae5a0cf12c7c5cf4743b5225b/687474703a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d6a766d2d4442343133442e7376673f7374796c653d666c6174
[badge-platform-js]: https://camo.githubusercontent.com/3e0a143e39915184b54b60a2ecedec75e801f396d34b5b366c94ec3604f7e6bd/687474703a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d6a732d4638444235442e7376673f7374796c653d666c6174
[badge-platform-nodejs]: https://camo.githubusercontent.com/d08fda729ceebcae0f23c83499ca8f06105350f037661ac9a4cc7f58edfdbca9/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d6e6f64656a732d3638613036332e7376673f7374796c653d666c6174
[badge-platform-linux]: https://camo.githubusercontent.com/a2c518ecf30b2c88dd6af8bbc5281b6014686b916368e6197ef2a5e1dda7adb4/687474703a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d6c696e75782d3244334636432e7376673f7374796c653d666c6174
[badge-platform-macos]: https://camo.githubusercontent.com/1b8313498db244646b38a4480186ae2b25464e5e8d71a1920c52b2be5212b909/687474703a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d6d61636f732d3131313131312e7376673f7374796c653d666c6174
[badge-platform-tvos]: https://camo.githubusercontent.com/4ac08d7fb1bcb8ef26388cd2bf53b49626e1ab7cbda581162a946dd43e6a2726/687474703a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d74766f732d3830383038302e7376673f7374796c653d666c6174
[badge-platform-watchos]: https://camo.githubusercontent.com/135dbadae40f9cabe7a3a040f9380fb485cff36c90909f3c1ae36b81c304426b/687474703a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d77617463686f732d4330433043302e7376673f7374796c653d666c6174
[badge-platform-windows]: https://camo.githubusercontent.com/01bd13daf3ea3068952f50840e3f36a305803cc248af08f084cb9e37df78123d/687474703a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d77696e646f77732d3444373643442e7376673f7374796c653d666c6174

[url-bundletool]: https://github.com/google/bundletool
[url-latest-release]: https://github.com/05nelsonm/kmp-tor-binary/releases/latest
[url-license]: https://www.apache.org/licenses/LICENSE-2.0
[url-kotlin]: https://kotlinlang.org
[url-kmp-tor]: https://github.com/05nelsonm/kmp-tor
[url-tor-browser-build]: https://gitlab.torproject.org/tpo/applications/tor-browser-build/
