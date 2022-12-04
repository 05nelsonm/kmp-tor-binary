# CHANGELOG

## Version 4.7.11-0 (2022-12-04)
 - Updates Tor to `0.4.7.11`
 - Removes the `binary-build-jni` project
 - Relocates `tor-browser-build` submodule
 - Cleans up the binary build script
 - Updates `kotlin-components`
     - Documentation + publication script improvements

## Version 4.7.10-1 (2022-10-22)
 - Changes the version naming scheme
     - Ex: `4.7.10-1`
         - `Tor` version `0.4.7.10`
         - `kmp-tor-binary-*` sub-release `1`
     - See [Issue 36](https://github.com/05nelsonm/kmp-tor-binary/issues/36#issuecomment-1284654389)
 - Refactors binary build script to handle upstream changes to `tor-browser-build` 
   packaging
 - Packages all binary/geoip files individually instead of using an archive
     - All files are gzipped
     - Mitigates need for external dependencies to extract from `.zip` or `.tar` archives
 - Refactors `kmp-tor-binary-extract` to handle extracting gzipped files
     - Removes the `ZipArchiveExtractor` class entirely, in favor of `TorResource` and 
       `Extractor` classes to handle gzipped files.
 - Adds support for `Node.js`
     - Assets are distributed via `npmjs`

## Version 0.4.7.10 (2022-09-24)
 - Updates `tor-browser-build` submodule source
     - FROM: `https://gitweb.torproject.org/builders/tor-browser-build.git/`
     - TO: `https://gitlab.torproject.org/tpo/applications/tor-browser-build.git`
 - Updates Tor to `0.4.7.10`

## Version 0.4.7.8 (2022-06-24)
 - Updates Tor to `0.4.7.8`
 - Updates geoip files
 - Increase Android minSdk from 16 -> 21
 - Re-enable compiler flag enableCompatibilityMetadataVariant=true to support 
   non-hierarchical projects. (sorry...)

## Version 0.4.7.7 (2022-05-08)
 - Updates Tor to `0.4.7.7`
 - Updates geoip files
 - Updates Kotlin-Components
     - Bumps Kotlin version 1.6.10 -> 1.6.21

## Version 0.4.6.10 (2022-02-20)
 - Fixes zip archival packaging reproducibility
     - Manifest file ordering was not deterministic
 - Updates Tor to `0.4.6.10`
 - Updates geoip files

## Version 0.4.6.9 (2022-02-06)
 - Initial release
