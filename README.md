# kmp-tor-binary
[![Kotlin](https://img.shields.io/badge/kotlin-1.6.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)  

![android](https://camo.githubusercontent.com/b1d9ad56ab51c4ad1417e9a5ad2a8fe63bcc4755e584ec7defef83755c23f923/687474703a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d616e64726f69642d3645444238442e7376673f7374796c653d666c6174)
![jvm](https://camo.githubusercontent.com/700f5dcd442fd835875568c038ae5cd53518c80ae5a0cf12c7c5cf4743b5225b/687474703a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d6a766d2d4442343133442e7376673f7374796c653d666c6174)  

Tor binary distribution for the [kmp-tor](https://github.com/05nelsonm/kmp-tor) project  

## Get Started

With the exception of **Jvm/NodeJs**, the [kmp-tor](https://github.com/05nelsonm/kmp-tor) 
dependency automatically imports the necessary binaries for you.  

Note that the [kmp-tor](https://github.com/05nelsonm/kmp-tor) dependency automatically 
imports the `kmp-tor-binary-geoip` dependency for **all** targets.  

<details>
    <summary>Android Configuration</summary>

Android requires some configuration so binaries will be appropriately extracted to your 
app's `nativeLibraryDir` upon application installation.  

Ensure JavaVersion is greater than or equal to 11
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

Enable legacy packaging for JniLibs
```kotlin
// build.gradle.kts
android {
    packagingOptions {
        jniLibs.useLegacyPackaging = true
    }
}
```

Add to your `AndroidManifest.xml`, within the `application` tag:
```
android:extractNativeLibs="true"
```

If you are publishing your application to Google Play using app bundling, add the following
to your project's `gradle.properties` file:
```groovy
android.bundle.enableUncompressedNativeLibs=false
```

You can also see, prior to pushing your release to Google Play, if the bundled apk extracts
binaries appropriately by using the [bundletool](https://github.com/google/bundletool) to
verify.

Configure splits for each architecture by adding the following to your application module's
`android` block:
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

            // By default all ABIs are included, so use reset() and include to specify
            // that we only want APKs for x86 and x86_64, armeabi-v7a, and arm64-v8a.

            // Resets the list of ABIs that Gradle should create APKs for to none.
            reset()

            // Specifies a list of ABIs that Gradle should create APKs for.
            include("x86", "armeabi-v7a", "arm64-v8a", "x86_64")

            // Specify whether or not you wish to also generate a universal APK that
            // includes _all_ ABIs.
            isUniversalApk = true
        }
    }
}
```

</details>

<details>
    <summary>Java/NodeJS Configuration</summary>

Binaries for Java/NodeJS are **not** automatically imported with the 
[kmp-tor](https://github.com/05nelsonm/kmp-tor) dependency. You will need 
to explicitly add the platform specific dependencies you wish to support, for example:

<!-- TODO: Add
    // MacOS aarch64
    implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-macosarm64:$vTor")
-->

```kotlin
// build.gradle.kts
dependencies {
    val vTor = "0.4.7.10"
    val vKmpTor = "1.3.1" // <-- see kmp-tor repo for latest versions
    implementation("io.matthewnelson.kotlin-components:kmp-tor:$vTor+$vKmpTor")

    // Linux x86_64
    implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-linuxx64:$vTor")
    // Linux i686
    implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-linuxx86:$vTor")
    // MacOS x86_64
    implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-macosx64:$vTor")
    // Windows x86_64
    implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-mingwx64:$vTor")
    // Windows i686
    implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-mingwx86:$vTor")
}
```

</details>

## Git

This project utilizes git submodules. You will need to initialize them when 
cloning the repository via:

```bash
$ git checkout master
$ git pull
$ git submodule update --init
```

## Where do the binaries come from?

Binaries for Jvm and Android are reproducibly built via Tor Project's <a href="https://gitweb.torproject.org/builders/tor-browser-build.git/" target="_blank">tor-browser-build</a>.

## Building Yourself

Clone the repo and initialize submodules:
```
git clone https://github.com/05nelsonm/kmp-tor-binary.git
cd kmp-tor-binary
git submodule update --init
```

Install dependencies
```
# See tor-browser-build's README for needed dependencies and install them
cd library/binary-build/binary-build-executables/tor-browser-build
nano README
```

Initialize tor-browser-build's rbm submodule
```
make submodule-update
```

Run the build script
```
# Change directories back to binary-build-executables directory
cd ../

# Running the script will automatically print HELP to see what targets are available
./scripts/build_binaries.sh

# EX:
./scripts/build_binaries.sh android-armv7
```

Building a target will automatically extract, package, and move binaries/geoip files 
to the appropriate `kmp-tor-binary-*` module and update sha256sum/manifest constants. 
As binaries are reproducibly built, running `git diff` should show no changes.
