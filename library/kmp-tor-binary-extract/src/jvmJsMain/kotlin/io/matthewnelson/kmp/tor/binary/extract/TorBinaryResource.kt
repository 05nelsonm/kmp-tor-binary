package io.matthewnelson.kmp.tor.binary.extract

import kotlin.jvm.JvmField
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * Allows for library consumers to provide their own packaged
 * binary resources.
 *
 * Packaging requirements:
 *  - All binary resource files must be in resource directory kmptor/lowercase-os-name/arch, e.g.
 *      - kmptor/linux/x64/file.gz
 *      - kmptor/macos/arm64/subdirectories-are-allowed/file.gz
 *      - kmptor/mingw/x86/file.gz
 *  - All binary resource files must end with '.gz' and be gzipped
 *  - Must contain 1 (one) tor file (e.g. 'tor.gz', 'Tor.gz', 'Tor.exe.gz')
 *
 * Sha256sum calculation:
 *  - Pre-gzip sha256sum (lowercase base16/hex) of each file
 *  - Each file's sha256sum appended with new line to string
 *  - The sha256sum of the string
 *  - e.g.
 *
 *
 *      (<file1>.readBytes().sha256() + '\n'
 *      + <file2>.readBytes().sha256() + '\n'
 *      + <file3>.readBytes().sha256() + '\n'
 *      ).encodeToByteArray().sha256()
 *
 * [resourceManifest] is a list of each resource file path in relation to kmptor/lowercase-os-name/arch
 *  - Paths cannot start with '.' or '/'
 *  - Paths cannot contain white space
 *  - Paths must end in '.gz'
 *  - Must be in the same order in which the sha256sum was calculated
 *  - e.g.
 *
 *
 *      listOf(
 *          "file1.gz",
 *          "subdir/file2.gz",
 *          "subdir/file3.gz",
 *          "file4.gz",
 *          "subdir/subsubdir/file5.gz",
 *          "tor.gz"
 *      )
 *
 * For NodeJS, binary files must be in a module named kmp-tor-resource-(lowercase os name)(arch)
 *  - e.g. kmp-tor-resource-linuxx64
 *
 * @see [from]
 * @sample [io.matthewnelson.kmp.tor.binary.extract.TorResourceLinuxX64.resourceManifest]
 * */
public class TorBinaryResource private constructor(
    private val os: OS,
    @JvmField
    public val arch: String,
    public override val sha256sum: String,
    public override val resourceManifest: List<String>,
): TorResource.Binaries() {

    public enum class OS {
        Linux,
        Macos,
        Mingw,
    }

    @get:JvmName("osName")
    public val osName: String get() = os.name.lowercase()

    public override val resourceDirPath: String get() = "kmptor/$osName/$arch"

    public companion object {

        @JvmStatic
        @Throws(IllegalArgumentException::class)
        public fun from(
            os: OS,
            arch: String,
            sha256sum: String,
            resourceManifest: List<String>
        ): TorBinaryResource {
            require(arch.isNotBlank()) { "arch cannot be blank" }
            require(!arch.contains('/')) { "arch cannot contain '/'" }
            require(!arch.contains(' ')) { "arch cannot contain white space" }
            require(arch.lines().size == 1) { "arch cannot contain new lines" }

            require(sha256sum.matches("[a-f0-9]{64}".toRegex())) { "invalid sha256sum" }

            require(resourceManifest.isNotEmpty()) { "resourceManifest cannot be empty" }

            val manifest = ArrayList<String>(resourceManifest.size)

            var torFileCount = 0
            for (item in resourceManifest) {
                require(item.isNotBlank()) { "manifest item '$item' cannot be blank" }
                require(!item.contains(' ')) { "manifest item '$item' cannot contain white space" }
                require(item.lines().size == 1) { "manifest item '$item' cannot contain new lines" }

                require(!item.startsWith('.')) { "manifest item '$item' cannot start with '.'" }
                require(!item.startsWith('/')) { "manifest item '$item' cannot start with '/'" }
                require(item.endsWith(".gz")) { "manifest item '$item' must end with .gz (and be gzipped)" }

                val resourceName = item
                    .substringBeforeLast('.') // remove .gz
                    .substringAfterLast('/') // remove sub-directories
                    .substringBeforeLast('.') // remove file extension

                if (resourceName.lowercase() == "tor") {
                    torFileCount++
                }

                manifest.add(item)
            }

            require(torFileCount == 1) {
                "manifest must contain a single tor file to be executed on " +
                "(e.g. 'Tor.gz', 'tor.gz', 'tor.exe.gz', ...). There were " +
                "a total of '$torFileCount' listed in the resourceManifest."
            }

            return TorBinaryResource(os, arch, sha256sum, manifest.toList())
        }
    }
}
