/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.kmp.tor.binary.util

import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

@InternalKmpTorBinaryApi
public class ImmutableSet<T> private constructor(
    delegate: MutableSet<T>
): Set<T> {
    private val delegate = delegate.toSet()

    override val size: Int get() = delegate.size
    override fun isEmpty(): Boolean = delegate.isEmpty()
    override fun iterator(): Iterator<T> = delegate.iterator()
    override fun containsAll(elements: Collection<T>): Boolean = delegate.containsAll(elements)
    override fun contains(element: T): Boolean = delegate.contains(element)

    @InternalKmpTorBinaryApi
    public companion object {

        @JvmStatic
        public fun <T> MutableSet<T>.toImmutableSet(): ImmutableSet<T> = ImmutableSet(this)
    }
}

@InternalKmpTorBinaryApi
public class ImmutableMap<K, V> private constructor(
    delegate: MutableMap<K, V>
): Map<K, V> {
    private val delegate = delegate.toMap()

    override val entries: Set<Map.Entry<K, V>> get() = delegate.entries
    override val keys: Set<K> get() = delegate.keys
    override val size: Int get() = delegate.size
    override val values: Collection<V> get() = delegate.values
    override fun isEmpty(): Boolean = delegate.isEmpty()
    override operator fun get(key: K): V? = delegate[key]
    override fun containsValue(value: V): Boolean = delegate.containsValue(value)
    override fun containsKey(key: K): Boolean = delegate.containsKey(key)

    @InternalKmpTorBinaryApi
    public companion object {

        @JvmStatic
        public fun <K, V> MutableMap<K, V>.toImmutableMap(): ImmutableMap<K, V> = ImmutableMap(this)
    }
}
