# Bumping Tor Version

 - Pull latest `tor-browser-build` master branch
 ```
 cd path/to/tor-browser-build
 git checkout master
 git pull
 make submodule-update
 ```

 - Make the git patch
 ```
 git checkout -b kmptor_<new-version-number>
 // modify tor-browser-build/projects/tor/config file's version
 git add --all
 git commit -S -m "set tor version <version>"
 git format-patch master -o ../patches/
 ```

 - Remove old git patch from `patches` dir
 - Modify `scripts/build_binaries.sh` to apply the new patch
 - Change back to `tor-browser-build` master branch
 ```
 git checkout master
 ```
 - Delete old branch
 ```
 git branch -D kmptor_<version>
 ```

 - Build new binaries
 ```
 cd ..
 ./scripts/build_binaries.sh all
 ```
