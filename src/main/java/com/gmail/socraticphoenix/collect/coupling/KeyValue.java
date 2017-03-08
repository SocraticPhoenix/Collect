/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 socraticphoenix@gmail.com
 * Copyright (c) 2016 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gmail.socraticphoenix.collect.coupling;

import java.util.Map;

/**
 * Represents a Key-Value mapping between two objects, and provides a basic implementation of {@link Map.Entry}. This
 * class can also be seen as a mutable version of {@link Pair}
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 *
 * @see Pair
 */
public class KeyValue<K, V> implements Map.Entry<K, V> {
    private K key;
    private V value;

    /**
     * Creates a new KeyValue representing a mapping between the given objects
     *
     * @param key   The key object
     * @param value The value object
     */
    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Creates a new KeyValue representing a mapping between the given objects
     *
     * @param key   The key object
     * @param value The value object
     *
     * @param <K>   The type of the key
     * @param <V>   The type of the value
     *
     * @return The new KeyValue
     */
    public static <K, V> KeyValue<K, V> of(K key, V value) {
        return new KeyValue<>(key, value);
    }

    /**
     * @return The key object
     */
    @Override
    public K getKey() {
        return this.key;
    }

    /**
     * @return The value object
     */
    @Override
    public V getValue() {
        return this.value;
    }

    /**
     * Sets the key object to the given value
     *
     * @param value The new key object
     *
     * @return The previous key object
     */
    public K setKey(K value) {
        K temp = this.key;
        this.key = value;
        return temp;
    }

    /**
     * Sets the value object to the given value
     *
     * @param value The new value object
     *
     * @return The previous value object
     */
    @Override
    public V setValue(V value) {
        V temp = this.value;
        this.value = value;
        return temp;
    }

}
