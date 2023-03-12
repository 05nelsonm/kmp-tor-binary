rootProject.name = "kmp-tor-binary"

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

includeBuild("build-logic")

@Suppress("PrivatePropertyName")
private val CHECK_PUBLICATION: String? by settings

if (CHECK_PUBLICATION != null) {
    include(":tools:check-publication")
} else {
    listOf(
        "kmp-tor-binary-android",
        "kmp-tor-binary-extract",
        "kmp-tor-binary-geoip",
        "kmp-tor-binary-linuxx64",
        "kmp-tor-binary-linuxx86",
        "kmp-tor-binary-macosx64",
        "kmp-tor-binary-macosarm64",
        "kmp-tor-binary-mingwx64",
        "kmp-tor-binary-mingwx86",
    ).forEach { name ->
        include(":library:$name")
    }
}
