package io.matthewnelson.kmp.tor.binary.extract

import kotlin.test.Test
import kotlin.test.fail

class TorBinaryResourceUnitTest {

    @Test
    fun givenAssertThrew_whenExpectedDoesNotThrow_thenThrowsAssertionError() {
        try {
            assertThrew<IllegalArgumentException> { /* don't throw */ }
        } catch (_: AssertionError) {
            // pass
        }
    }

    @Test
    fun givenTorBinaryResource_whenAllValid_thenDoesNotThrowException() {
        create()
    }

    @Test
    fun givenTorBinaryResource_whenArchBlank_thenThrowsException() {
        assertThrew<IllegalArgumentException> { create(arch = " ") }
    }

    @Test
    fun givenTorBinaryResource_whenArchContainsSpace_thenThrowsException() {
        assertThrew<IllegalArgumentException> { create(arch = " x64") }
    }

    @Test
    fun givenTorBinaryResource_whenArchContainsFslash_thenThrowsException() {
        assertThrew<IllegalArgumentException> { create(arch = "x/64") }
    }

    @Test
    fun givenTorBinaryResource_whenArchContainsNewLines_thenThrowsException() {
        assertThrew<IllegalArgumentException> { create(arch = """

            x64
        """.trimIndent()) }
    }

    @Test
    fun givenTorBinaryResource_whenInvalidSha256Sum_thenThrowsException() {
        assertThrew<IllegalArgumentException> { create(sha256Sum = "") }
    }

    @Test
    fun givenTorBinaryResource_whenEmptyManifest_thenThrowsException() {
        assertThrew<IllegalArgumentException> { create(resourceManifest = emptyList()) }
    }

    @Test
    fun givenTorBinaryResource_whenManifestItemBlank_thenThrowsException() {
        assertThrew<IllegalArgumentException> { create(
            resourceManifest = TorResourceLinuxX64.resourceManifest + listOf(" ")
        ) }
    }

    @Test
    fun givenTorBinaryResource_whenManifestItemContainsSpace_thenThrowsException() {
        assertThrew<IllegalArgumentException> { create(
            resourceManifest = TorResourceLinuxX64.resourceManifest + listOf(" aaa")
        ) }
    }

    @Test
    fun givenTorBinaryResource_whenManifestItemContainsNewLines_thenThrowsException() {
        assertThrew<IllegalArgumentException> { create(
            resourceManifest = TorResourceLinuxX64.resourceManifest + listOf("""
            
            aaa
        """.trimIndent())
        ) }
    }

    @Test
    fun givenTorBinaryResource_whenManifestItemStartsWithFslash_thenThrowsException() {
        assertThrew<IllegalArgumentException> { create(
            resourceManifest = TorResourceLinuxX64.resourceManifest + listOf("/item")
        ) }
    }

    @Test
    fun givenTorBinaryResource_whenManifestItemEndsWithFslash_thenThrowsException() {
        assertThrew<IllegalArgumentException> { create(
            resourceManifest = TorResourceLinuxX64.resourceManifest + listOf("item/")
        ) }
    }

    @Test
    fun givenTorBinaryResource_whenManifestItemDoesNotEndWithGZ_thenThrowsException() {
        assertThrew<IllegalArgumentException> { create(
            resourceManifest = TorResourceLinuxX64.resourceManifest + listOf("item")
        ) }
    }

    @Test
    fun givenTorBinaryResource_whenManifestItemHasSubDirectory_thenDoesNotThrowException() {
        create(
            resourceManifest = TorResourceLinuxX64.resourceManifest + listOf("subdir/item.gz")
        )
    }

    @Test
    fun givenTorBinaryResource_whenManifestContainsMultipleTorFiles_thenThrowException() {
        assertThrew<IllegalArgumentException> { create(
            resourceManifest = TorResourceLinuxX64.resourceManifest + listOf("subdir/tor.gz")
        ) }
    }

    @Test
    fun givenStaticTorResources_whenCheckedAgainstTorBinaryResourceRequirements_thenAreValid() {
        listOf(
            TorResourceLinuxX64,
            TorResourceLinuxX86,
            TorResourceMacosX64,
            TorResourceMacosArm64,
            TorResourceMingwX64,
            TorResourceMingwX86,
        ).forEach { resource ->
            val (os, arch) = resource.resourceDirPath.split('/').let { split ->
                val os = TorBinaryResource.OS.valueOf(
                    split[1].replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                )
                Pair(os, split[2])
            }

            TorBinaryResource.from(os, arch, resource.sha256sum, resource.resourceManifest)
        }
    }

    /**
     * Helper test function with overrides
     * */
    @Throws(IllegalArgumentException::class)
    private fun create(
        os: TorBinaryResource.OS = TorBinaryResource.OS.Linux,
        arch: String = "x64",
        sha256Sum: String = TorResourceLinuxX64.sha256sum,
        resourceManifest: List<String> = TorResourceLinuxX64.resourceManifest
    ): TorBinaryResource {
        return TorBinaryResource.from(os, arch, sha256Sum, resourceManifest)
    }

    private inline fun <reified T: Throwable> assertThrew(block: () -> Unit) {
        try {
            block.invoke()
            fail()
        } catch (t: Throwable) {
            if (t::class != T::class) {
                throw t
            }
            // pass
        }
    }
}
