# BUILD

For those who wish to verify reproducibility of the binaries being distributed 
via MavenCentral, you can do so by following along below.

<!-- TODO: Remove -->
**NOTE:** Current binaries for `macOS` are not reproducibly built because to execute on them require that
they be codesigned (which changes the sha256 output). Current binaries for `kmp-tor-binary-macosx64`
and `kmp-tor-binary-macosarm64` come from TorBrowser version `12.5a3`, signed and notarized by TorProject.
As of [[PR #81]](https://github.com/05nelsonm/kmp-tor-binary/pull/81), this has been fixed. Next version 
bump of `tor` will include reproducibly built, codesigned binaries for `macOS` and `Windows`.

- What you will need:
    - `[Linux x86_64]` or `[macOS x86_64 or aarch64]`
    - Git
    - Gradle
    - Java 11 or higher

- Clone the repository
  ```shell
  git clone https://github.com/05nelsonm/kmp-tor-binary.git
  cd kmp-tor-binary
  ```
<!-- TODO: Remove -->
- Build `tor` binaries:
    - Initialize the `tor-browser-build` submodule
      ```shell
      git submodule update --init
      ```
    - Open up the `tor-browser-build` submodule's `README` and install
      packages needed to build `tor`
      ```shell
      cat library/binary-build/tor-browser-build/README
      ```
    - Run the build script
      ```shell
      ./library/binary-build/scripts/build_binaries.sh all
      ```

<!-- TODO: Uncomment
- Build `tor` binaries:
    - This step will:
        - Build all platforms supported by `kmp-tor-binary`
        - Archive desktop binaries to the `binary-build/built` directory
        - Move android binaries to `kmp-tor-binary-android` module 
          `src/androidMain/jniLibs` directory.
    - Initialize the `tor-browser-build` submodule
      ```shell
      git submodule update --init
      ```
    - Open up the `tor-browser-build` submodule's `README` and install
      packages needed to build `tor`
      ```shell
      cat library/binary-build/tor-browser-build/README
      ```
    - Run the build script
      ```shell
      ./library/binary-build/scripts/build_tor.sh all
      ```

- Running `git diff` at this point should show **no** changes to the project.

- Resource creation:
    - This step will:
        - Apply detached signatures to `macOS`/`Windows` binaries
            - Reproducibly built binaries for `macOS` and `Windows` are codesigned
              before being packaged as resources. The [diff-cli](tools/diff-cli/README.md) tool 
              is used to create and apply detached signatures.
        - `gzip` all files
        - Move them to their respective `kmp-tor-binary-<os><arch>` module resource directory
        - Update `kmp-tor-binary-extract` constants (sha256 & manifest values)
    - Run the resource creation script
      ```shell
      ./library/binary-build/scripts/resources_create.sh all
      ```
-->

- Running `git diff` at this point should show **no** changes to the project.

That's it.
