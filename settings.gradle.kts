rootProject.name = "kmp-tor-binary"

includeBuild("kotlin-components/includeBuild/dependencies")
includeBuild("kotlin-components/includeBuild/kmp")

@Suppress("PrivatePropertyName")
private val CHECK_PUBLICATION: String? by settings
if (CHECK_PUBLICATION != null) {
    include(":tools:check-publication")
} else {
    include(":library:kmp-tor-binary-android")
    include(":library:kmp-tor-binary-extract")
    include(":library:kmp-tor-binary-geoip")
    include(":library:kmp-tor-binary-linuxx64")
    include(":library:kmp-tor-binary-linuxx86")
    include(":library:kmp-tor-binary-macosx64")
    include(":library:kmp-tor-binary-macosarm64")
    include(":library:kmp-tor-binary-mingwx64")
    include(":library:kmp-tor-binary-mingwx86")
}
