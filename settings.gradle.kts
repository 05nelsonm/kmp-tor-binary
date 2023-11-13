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
    include(":library:binary")
    include(":library:binary-android-unit-test")
    include(":tools:diff-cli")
    include(":tools:diff-cli:core")
}
