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

import java.util.Optional;

/**
 * Represents a "switch" between two objects, where exactly one object is always present, and one is always absent
 *
 * @param <A> The type of object A
 * @param <B> The type of object B
 */
public class Switch<A, B> {
    private A a;
    private B b;

    private Switch(A a, B b) {
        this.a = a;
        this.b = b;
    }

    /**
     * @return {@code Optional.empty()} if object A is absent, or an Optional containing object A
     */
    public Optional<A> getA() {
        return Optional.ofNullable(this.a);
    }

    /**
     * @return {@code Optional.empty()} if object B is absent, or an Optional containing object B
     */
    public Optional<B> getB() {
        return Optional.ofNullable(this.b);
    }

    /**
     * @return True if object A is present, false otherwise
     */
    public boolean containsA() {
        return this.a != null;
    }

    /**
     * @return True if object B is present, false otherwise
     */
    public boolean containsB() {
        return this.b != null;
    }

    /**
     * Creates a new Switch with a present object A
     *
     * @param a Object A
     * @param <A> The type of object A
     * @param <B> The type of object B
     * @return The created Switch
     */
    public static <A, B> Switch<A, B> ofA(A a) {
        return new Switch<>(a, null);
    }

    /**
     * Creates a new Switch with a present object B
     *
     * @param b Object B
     * @param <A> The type of object A
     * @param <B> The type of object B
     * @return The created Switch
     */
    public static <A, B> Switch<A, B> ofB(B b) {
        return new Switch<>(null, b);
    }
}
