package io.matthewnelson.kmp.tor.binary.extract

import kotlin.jvm.JvmField
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

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
 * [loadPath] is the path to find the resources.
 *  - For JVM:
 *    - This is the path to a class named `Loader` in the module containing your
 *      custom binary resources. Simply add `private class Loader` at the classpath
 *      described below.
 *    - Defaults to `io.matthewnelson.kmp.tor.resource.(lowercase os name).(arch).Loader`
 *    - This is only utilized if `javaClass.getResourceAsStream` fails. You can add
 *      a `private class Loader` to the module containing those binary resources at the
 *      specified classpath, and reflection will be used to find it in order to load resources
 *      from that module.
 *    - e.g. "com.example.kmp.tor.resource.linux.x64.Loader"
 *  - For Nodejs:
 *    - This is the module name where the assets are located
 *    - Defaults to module named `kmp-tor-resource-(lowercase os name)(arch)`
 *    - e.g. "kmp-tor-resource-linuxx64"
 *  - [from] has more details on customization of this loadPath via passing
 *    of a prefix.
 *
 * @see [from]
 * @sample [io.matthewnelson.kmp.tor.binary.extract.TorResourceLinuxX64.resourceManifest]
 * @sample [io.matthewnelson.kmp.tor.binary.linux.x64.Loader]
 * */
public class TorBinaryResource private constructor(
    private val os: OS,
    @JvmField
    public val arch: String,
    @JvmField
    public val loadPath: String,
    public override val sha256sum: String,
    public override val resourceManifest: List<String>,
): TorResource.Binaries() {

    public enum class OS {
        Linux,
        Macos,
        Mingw;

        @get:JvmSynthetic
        internal val lowercaseName: String get() = name.lowercase()
    }

    @get:JvmName("osName")
    public val osName: String get() = os.lowercaseName

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
            return from(os, arch, null, sha256sum, resourceManifest)
        }

        /**
         * Validates parameters and returns a [TorBinaryResource].
         *
         * [loadPathPrefix] is an optional argument for creating your [loadPath] if
         * it differs from the default.
         *
         * - JVM: classpath prefix to your module's `Loader` class
         *     - e.g. loadPathPrefix = "com.example" with binaries for os = OS.Linux and arch = "aarch64"
         *       will result in a loadPath of "com.example.linux.aarch64.Loader"
         *     - Defaults to: `io.matthewnelson.kmp.tor.resrouce.(lowercase os name).(arch).Loader`
         *
         * - Nodejs: the module prefix.
         *     - e.g. loadPathPrefix = "com-example" with bianries for os = OS.Linux and arch = "aarch64"
         *       will result in a loadPath of "com-example-linuxaarch64"
         *     - Defaults to: `kmp-tor-resource-(lowercase os name)(arch)`
         * */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        public fun from(
            os: OS,
            arch: String,
            loadPathPrefix: String?,
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

            return TorBinaryResource(
                os,
                arch,
                loadPathPrefix.toLoadPath(os, arch),
                sha256sum,
                manifest.toList()
            )
        }
    }
}
