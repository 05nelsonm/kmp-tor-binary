# BUILD

For those who wish to verify reproducibility of the binaries being distributed 
via MavenCentral, you can do so by following along below.

- What you will need:
    - `Linux` or `macOS`
    - Git
    - Gradle
    - Java 11 or higher

- Clone the repository
  ```shell
  git clone https://github.com/05nelsonm/kmp-tor-binary.git
  cd kmp-tor-binary
  ```

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

- Running `git diff` at this point should show **no** changes to the project.

That's it.
