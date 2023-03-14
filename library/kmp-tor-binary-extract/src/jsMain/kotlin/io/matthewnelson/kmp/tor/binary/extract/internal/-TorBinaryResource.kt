package io.matthewnelson.kmp.tor.binary.extract.internal

import io.matthewnelson.kmp.tor.binary.extract.TorBinaryResource

internal val TorBinaryResource.jsModuleName: String get() = "kmp-tor-resource-${osName}$arch"
