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

/**
 * Represents a Triple of three objects
 *
 * @param <A> The type of object A
 * @param <B> The type of object B
 * @param <C> The type of object C
 */
public class Triple<A, B, C> {
    private A a;
    private B b;
    private C c;

    /**
     * Creates a new Triple with the specified objects
     *
     * @param a Object A
     * @param b Object B
     * @param c Object C
     */
    private Triple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * @return Object A
     */
    public A getA() {
        return a;
    }

    /**
     * @return Object B
     */
    public B getB() {
        return b;
    }

    /**
     * @return Object C
     */
    public C getC() {
        return c;
    }

    /**
     * Creates a new Triple with the specified objects
     *
     * @param a Object A
     * @param b Object B
     * @param c Object C
     * @param <A> The type of object A
     * @param <B> The type of object B
     * @param <C> The type of object C
     * @return A new Triple
     */
    public static <A, B, C> Triple<A, B, C> of(A a, B b, C c) {
        return new Triple<>(a, b, c);
    }

}
