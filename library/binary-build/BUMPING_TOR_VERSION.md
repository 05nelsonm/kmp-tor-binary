# Bumping Tor Version

- Pull latest `tor-browser-build` main branch
  ```
  cd path/to/tor-browser-build
  git checkout main
  git pull
  make submodule-update
  ```

- Remove old git patch(es) from `library/binary-build/patches` dir
- Create the git patch (if necessary)
  ```
  git checkout -b kmptor_<new-version-number>
  // modify tor-browser-build/projects/tor/config file's version
  git add --all
  git commit -S -m "set tor version <version>"
  git format-patch main -o ../patches/
  ```

- Modify `scripts/build_tor.sh` to apply the new patch
- Change back to `tor-browser-build` main branch
  ```
  git checkout main
  ```
- Delete old branch
  ```
  git branch -D kmptor_<version>
  ```

- Build new binaries
  ```
  cd ..
  ./scripts/build_tor.sh all
  ```

- Create detached signatures
  ```
  ./scripts/detached_sig_create.sh macos
  ./scripts/detached_sig_create.sh mingw
  ```

- Apply detached signatures, gzip, and update module resource files
  ```
  ./scripts/resources_create.sh all
  ``` 

- Check Android + JVM build (JS will fail b/c binaries not published to `Npmjs` yet)
  ```
  cd ../../
  ./gradlew build -PKMP_TARGETS="ANDROID,JVM"
  ```

- Finish up with [RELEASING](../../RELEASING.md) documentation.
    - After checking out the release branch, make sure to create a commit
      with the tor version bump
      ```
      git add --all
      git commit -S -m "Update tor from <old tor version> -> <new tor version>"
      ```
