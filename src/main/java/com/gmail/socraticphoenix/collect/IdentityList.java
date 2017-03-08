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
package com.gmail.socraticphoenix.collect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A list that uses '==' for comparisons, instead of {@link Object#equals(Object)}. Other than features that use this
 * equality check, this list extends {@link ArrayList} for all operations
 *
 * @param <E> The type of the list.
 *
 * @see ArrayList
 */
public class IdentityList<E> extends ArrayList<E> {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public IdentityList() {
        super();
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator
     *
     * @param c the collection whose elements are to be placed into this list
     *
     * @throws NullPointerException if the specified collection is null
     */
    public IdentityList(final Collection<? extends E> c) {
        super(c);
    }

    /**
     * Constructs an empty list with the specified initial capacity
     *
     * @param initialCapacity the initial capacity of the list
     *
     * @throws IllegalArgumentException if the specified initial capacity
     *                                  is negative
     */
    public IdentityList(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Removes the first occurrence of the specified element from this list, if it is present. If the list does not
     * contain the element, it is unchanged. More formally, removes the element with the lowest index i such that
     * (o==null ? get(i)==null : o == get(i)) (if such an element exists). Returns true if this list contained the
     * specified element (or equivalently, if this list changed as a result of the call)
     *
     * @param o element to be removed from this list, if present
     *
     * @return true if this list contained the specified element
     */
    @Override
    public boolean remove(final Object o) {
        return super.remove(this.indexOf(o)) != null;
    }

    /**
     * Returns true if this list contains the specified element. More formally, returns true if and only if this list
     * contains at least one element e such that (o==null ? e==null : o == e)
     *
     * @param o element whose presence in this list is to be tested
     *
     * @return true if this list contains the specified element
     */
    @Override
    public boolean contains(final Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element in this list, or -1 if this list does not
     * contain the element. More formally, returns the lowest index i such that (o==null ? get(i)==null : o == get(i)),
     * or -1 if there is no such index.
     *
     * @param o element to search for
     *
     * @return the index of the first occurrence of the specified element in this list, or -1 if this list does not
     * contain the element
     */
    @Override
    public int indexOf(final Object o) {
        for (int i = 0; i < size(); i++)
            if (o == get(i))
                return i;
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element in this list, or -1 if this list does not
     * contain the element. More formally, returns the highest index i such that (o==null ? get(i)==null : o == get(i)),
     * or -1 if there is no such index.
     *
     * @param o element to search for
     *
     * @return the index of the last occurrence of the specified element in this list, or -1 if this list does not
     * contain the element
     */
    @Override
    public int lastIndexOf(final Object o) {
        for (int i = size() - 1; i >= 0; i--)
            if (o == get(i))
                return i;
        return -1;
    }

}