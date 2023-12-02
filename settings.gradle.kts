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
        "binary",
        "binary-android-unit-test",
        "binary-core",
        "binary-initializer",
        "npmjs",
    ).forEach { module ->
        include(":library:$module")
    }

    listOf(
        "cli-core",
        "diff-cli",
        "diff-cli:core",
    ).forEach { module ->
        include(":tools:$module")
    }
}
