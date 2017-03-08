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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class provides convenience methods for manipulating arrays, maps, and collections. By contract, methods in this
 * class will not modify the input array/collection/map unless explicitly stated in their javadoc. Methods that do
 * modify their input include: <ul><li>swap</li><li>shuffle</li></ul>Furthermore, any method that deals with indices of
 * a general Collection are guaranteed to work properly for all ordered collections, but may not function as expected
 * for unordered collections. For example, {@link Items#swap(Collection, int, int)} will not have the intended effect
 * on a {@link HashSet}. Finally, this class is optimized for Lists, specifically RandomAcess lists. LinkedList, and
 * other
 * sequential collections, may take nearly x<sup>4</sup> time to run
 */
public class Items {

    private Items() {

    }

    /**
     * Creates a new collection from the given supplier, and populates it with values from the given collection, if a
     * value in the collection implements cloneable, the value is cloned
     *
     * @param other                 The collection to deeply clone
     * @param collectionConstructor The supplier to use when creating the new collection
     * @param <T>                   The type of the collection's elements
     * @param <C>                   the type of the resulting collection
     *
     * @return The deeply cloned collection
     */
    public static <T, C extends Collection<T>> C deepClone(Collection<T> other, Supplier<C> collectionConstructor) {
        C result = collectionConstructor.get();
        other.forEach(value -> {
            try {
                if (value instanceof Cloneable && Modifier.isPublic(value.getClass().getMethod("clone").getModifiers())) {
                    result.add((T) value.getClass().getMethod("clone").invoke(value));
                } else {
                    result.add(value);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                result.add(value);
            }
        });

        return result;
    }

    /**
     * Creates a new collection from the given supplier, and populates it with values from the given collection
     *
     * @param other                 The collection to loosely clone
     * @param collectionConstructor The supplier to use when creating the new collection
     * @param <T>                   The type of the collection's elements
     * @param <C>                   the type of the resulting collection
     *
     * @return The loosely cloned collection
     */
    public static <T, C extends Collection<T>> C looseClone(Collection<T> other, Supplier<C> collectionConstructor) {
        C result = collectionConstructor.get();
        result.addAll(other);
        return result;
    }

    /**
     * Creates a new ArrayList, and populates it with values from the given list, if a value in the list implements
     * cloneable, the value is cloned
     *
     * @param other The collection to deeply clone
     * @param <T>   The type of the list's elements
     *
     * @return The deeply cloned list
     */
    public static <T> List<T> deepClone(List<T> other) {
        return Items.deepClone(other, ArrayList::new);
    }

    /**
     * Creates a new ArrayList and populates it with values from the given list
     *
     * @param other The list to loosely clone
     * @param <T>   The type of the list's elements
     *
     * @return The loosely cloned list
     */
    public static <T> List<T> looseClone(List<T> other) {
        return Items.looseClone(other, ArrayList::new);
    }

    /**
     * Creates a new collection using the given Supplier and populates it with values from the given array
     *
     * @param collectionConstructor The supplier to use when creating the new collection
     * @param vals                  The values to populate the new collection with
     * @param <T>                   The type of the collection's elements
     * @param <C>                   The type of collection
     *
     * @return The new collection
     */
    public static <T, C extends Collection<T>> C buildCollection(Supplier<C> collectionConstructor, T... vals) {
        C result = collectionConstructor.get();
        for (T val : vals) {
            result.add(val);
        }
        return result;
    }

    /**
     * Builds an {@link ArrayList} containing the given values. This method is functionally equivalent to {@link
     * Arrays#asList(Object[])}, but the created list has a mutable size
     *
     * @param vals The values the list should contain
     * @param <T>  The type of list
     *
     * @return The new list
     */
    public static <T> List<T> buildList(T... vals) {
        return Items.buildCollection(ArrayList::new, vals);
    }

    /**
     * Creates a new map using the given Supplier and populates it with key-value pairings from the given array
     *
     * @param mapConstructor The supplier to use when creating the new map
     * @param vals           The entries the map should contain
     * @param <K>            The type of the keys
     * @param <V>            The type of the values
     * @param <C>            The type of map
     *
     * @return The new map
     */
    public static <K, V, C extends Map<K, V>> C buildMap(Supplier<C> mapConstructor, Map.Entry<K, V>... vals) {
        C result = mapConstructor.get();
        for (Map.Entry<K, V> val : vals) {
            result.put(val.getKey(), val.getValue());
        }
        return result;
    }

    /**
     * Compares the two arrays and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Byte#compare(byte, byte)
     * Byte.compare(a[index], b[index])} to that value. Finally, {@code a.length - b.length} is added to the result
     * and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a The left-hand array
     * @param b The right-hand array
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static int compare(byte[] a, byte[] b) {
        if (a == b || a == null || b == null) {
            return a == b ? 0 : a == null ? -1 : 1;
        }
        int res = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            res += Byte.compare(a[i], b[i]);
        }
        res += a.length - b.length;
        return res;
    }

    /**
     * Compares the two arrays and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Character#compare(char, char)
     * Character.compare(a[index], b[index])} to that value. Finally, {@code a.length - b.length} is added to the result
     * and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a The left-hand array
     * @param b The right-hand array
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static int compare(char[] a, char[] b) {
        if (a == b || a == null || b == null) {
            return a == b ? 0 : a == null ? -1 : 1;
        }
        int res = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            res += Character.compare(a[i], b[i]);
        }
        res += a.length - b.length;
        return res;
    }

    /**
     * Compares the two arrays and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Short#compare(short, short)
     * Short.compare(a[index], b[index])} to that value. Finally, {@code a.length - b.length} is added to the result
     * and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a The left-hand array
     * @param b The right-hand array
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static int compare(short[] a, short[] b) {
        if (a == b || a == null || b == null) {
            return a == b ? 0 : a == null ? -1 : 1;
        }
        int res = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            res += Short.compare(a[i], b[i]);
        }
        res += a.length - b.length;
        return res;
    }

    /**
     * Compares the two arrays and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Integer#compare(int, int)
     * Integer.compare(a[index], b[index])} to that value. Finally, {@code a.length - b.length} is added to the result
     * and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a The left-hand array
     * @param b The right-hand array
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static int compare(int[] a, int[] b) {
        if (a == b || a == null || b == null) {
            return a == b ? 0 : a == null ? -1 : 1;
        }
        int res = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            res += Integer.compare(a[i], b[i]);
        }
        res += a.length - b.length;
        return res;
    }

    /**
     * Compares the two arrays and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Long#compare(long, long)
     * Long.compare(a[index], b[index])} to that value. Finally, {@code a.length - b.length} is added to the result
     * and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a The left-hand array
     * @param b The right-hand array
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static int compare(long[] a, long[] b) {
        if (a == b || a == null || b == null) {
            return a == b ? 0 : a == null ? -1 : 1;
        }
        int res = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            res += Long.compare(a[i], b[i]);
        }
        res += a.length - b.length;
        return res;
    }

    /**
     * Compares the two arrays and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Float#compare(float, float)
     * Float.compare(a[index], b[index])} to that value. Finally, {@code a.length - b.length} is added to the result
     * and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a The left-hand array
     * @param b The right-hand array
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static int compare(float[] a, float[] b) {
        if (a == b || a == null || b == null) {
            return a == b ? 0 : a == null ? -1 : 1;
        }
        int res = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            res += Float.compare(a[i], b[i]);
        }
        res += a.length - b.length;
        return res;
    }

    /**
     * Compares the two arrays and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Double#compare(double, double)
     * Double.compare(a[index], b[index])} to that value. Finally, {@code a.length - b.length} is added to the result
     * and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a The left-hand array
     * @param b The right-hand array
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static int compare(double[] a, double[] b) {
        if (a == b || a == null || b == null) {
            return a == b ? 0 : a == null ? -1 : 1;
        }
        int res = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            res += Double.compare(a[i], b[i]);
        }
        res += a.length - b.length;
        return res;
    }

    /**
     * Compares the two arrays and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Boolean#compare(boolean,
     * boolean)
     * Boolean.compare(a[index], b[index])} to that value. Finally, {@code a.length - b.length} is added to the result
     * and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a The left-hand array
     * @param b The right-hand array
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static int compare(boolean[] a, boolean[] b) {
        if (a == b || a == null || b == null) {
            return a == b ? 0 : a == null ? -1 : 1;
        }
        int res = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            res += Boolean.compare(a[i], b[i]);
        }
        res += a.length - b.length;
        return res;
    }

    /**
     * Compares the two arrays and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Comparator#compare(Object,
     * Object)  comparator.compare(a[i], b[i])} to that value. Finally, {@code a.length - b.length} is added to the
     * result
     * and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a          The left-hand array
     * @param b          The right-hand array
     * @param comparator The comparator to use while comparing values
     * @param <T>        The type of the array's elements
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static <T> int compare(T[] a, T[] b, Comparator<T> comparator) {
        if (a == b || a == null || b == null) {
            return a == b ? 0 : a == null ? -1 : 1;
        }
        int res = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            res += comparator.compare(a[i], b[i]);
        }
        res += a.length - b.length;
        return res;
    }

    /**
     * Compares the two arrays and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Comparable#compareTo(Object)
     * a[i].compareTo(b[i])} to that value. Finally, {@code a.length - b.length} is added to the result
     * and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a   The left-hand array
     * @param b   The right-hand array
     * @param <T> The type of the arrays' elements
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static <T extends Comparable<T>> int compare(T[] a, T[] b) {
        return Items.compare(a, b, Comparable::compareTo);
    }


    /**
     * Compares the two collections and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Comparator#compare(Object,
     * Object) comparator.compare(a[i], b[i])} to that value. Finally, {@code a.size() - b.size()} is added to the
     * result and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a          The left-hand collection
     * @param b          The right-hand collection
     * @param comparator The comparator to use while comparing values
     * @param <T>        The type of the collection's elements
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static <T> int compare(Collection<T> a, Collection<T> b, Comparator<T> comparator) {
        if (a == b || a == null || b == null) {
            return a == b ? 0 : a == null ? -1 : 1;
        }
        int res = 0;
        Iterator<T> aIt = a.iterator();
        Iterator<T> bIt = b.iterator();
        for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
            T aV = aIt.next();
            T bV = bIt.next();
            res += comparator.compare(aV, bV);
        }
        res += a.size() - b.size();
        return res;
    }

    /**
     * Compares the two collections and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Comparable#compareTo(Object)
     * a[i].compareTo(b[i])} to that value. Finally, {@code a.size() - b.size()} is added to the result
     * and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a   The left-hand collection
     * @param b   The right-hand collection
     * @param <T> The type of the collections' elements
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static <T extends Comparable<T>> int compare(Collection<T> a, Collection<T> b) {
        return Items.compare(a, b, Comparable::compareTo);
    }

    /**
     * Compares the two lists and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Comparator#compare(Object,
     * Object) comparator.compare(a[i], b[i])} to that value. Finally, {@code a.size() - b.size()} is added to the
     * result and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a          The left-hand list
     * @param b          The right-hand list
     * @param comparator The comparator to use while comparing values
     * @param <T>        The type of the list's elements
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static <T> int compare(List<T> a, List<T> b, Comparator<T> comparator) {
        if (a == b || a == null || b == null) {
            return a == b ? 0 : a == null ? -1 : 1;
        }
        int res = 0;
        for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
            res += comparator.compare(a.get(i), b.get(i));
        }
        res += a.size() - b.size();
        return res;
    }

    /**
     * Compares the two lists and returns a negative integer if {@code a < b}, zero if {@code a = b} or a positive
     * integer if {@code a > b}. The return value is calculated by initializing an integer return value, and looping
     * from zero to {@code Math.min(a.length, b.length)} and adding the result of {@link Comparable#compareTo(Object)
     * a[i].compareTo(b[i])} to that value. Finally, {@code a.size() - b.size()} is added to the result
     * and it is returned.
     * <p>
     * If {@code a == b}, zero is returned, and if {@code a == null} or {@code b == null}, the null value will be
     * considered less than the nonull value
     * </p>
     *
     * @param a   The left-hand list
     * @param b   The right-hand list
     * @param <T> The type of the lists' elements
     *
     * @return a negative integer if {@code a < b}, a positive integer if {@code a > b}, or zero if {@code a = b}
     */
    public static <T extends Comparable<T>> int compare(List<T> a, List<T> b) {
        return Items.compare(a, b, Comparable::compareTo);
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int indexOf(byte a, byte... array) {
        int ind = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int lastIndexOf(byte a, byte... array) {
        int ind = -1;
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int indexOf(char a, char... array) {
        int ind = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int lastIndexOf(char a, char... array) {
        int ind = -1;
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int indexOf(short a, short... array) {
        int ind = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int lastIndexOf(short a, short... array) {
        int ind = -1;
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int indexOf(int a, int... array) {
        int ind = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int lastIndexOf(int a, int... array) {
        int ind = -1;
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int indexOf(long a, long... array) {
        int ind = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int lastIndexOf(long a, long... array) {
        int ind = -1;
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int indexOf(float a, float... array) {
        int ind = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int lastIndexOf(float a, float... array) {
        int ind = -1;
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int indexOf(double a, double... array) {
        int ind = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int lastIndexOf(double a, double... array) {
        int ind = -1;
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int indexOf(boolean a, boolean... array) {
        int ind = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static int lastIndexOf(boolean a, boolean... array) {
        int ind = -1;
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == a) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     * @param <T>   the type of the array's elements
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static <T> int indexOf(T a, T... array) {
        int ind = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(a)) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Searches the given {@code array} for the given element, if the element is found, the index of it's first
     * appearance is returned
     *
     * @param a     The element to search for
     * @param array The array to search in
     * @param <T>   the type of the array's elements
     *
     * @return The smallest index of {@code a} in {@code array}, or -1 if {@code a} was not found in {@code array}
     */
    public static <T> int lastIndexOf(T a, T... array) {
        int ind = -1;
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i].equals(a)) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    /**
     * Checks if {@code a} is an element of {@code array}
     *
     * @param a     The element to check for
     * @param array The array to check in
     *
     * @return True if {@code array} contains {@code a}, false otherwise
     */
    public static boolean contains(byte a, byte... array) {
        return Items.indexOf(a, array) != -1;
    }

    /**
     * Checks if {@code a} is an element of {@code array}
     *
     * @param a     The element to check for
     * @param array The array to check in
     *
     * @return True if {@code array} contains {@code a}, false otherwise
     */
    public static boolean contains(char a, char... array) {
        return Items.indexOf(a, array) != -1;
    }

    /**
     * Checks if {@code a} is an element of {@code array}
     *
     * @param a     The element to check for
     * @param array The array to check in
     *
     * @return True if {@code array} contains {@code a}, false otherwise
     */
    public static boolean contains(short a, short... array) {
        return Items.indexOf(a, array) != -1;
    }

    /**
     * Checks if {@code a} is an element of {@code array}
     *
     * @param a     The element to check for
     * @param array The array to check in
     *
     * @return True if {@code array} contains {@code a}, false otherwise
     */
    public static boolean contains(int a, int... array) {
        return Items.indexOf(a, array) != -1;
    }

    /**
     * Checks if {@code a} is an element of {@code array}
     *
     * @param a     The element to check for
     * @param array The array to check in
     *
     * @return True if {@code array} contains {@code a}, false otherwise
     */
    public static boolean contains(long a, long... array) {
        return Items.indexOf(a, array) != -1;
    }

    /**
     * Checks if {@code a} is an element of {@code array}
     *
     * @param a     The element to check for
     * @param array The array to check in
     *
     * @return True if {@code array} contains {@code a}, false otherwise
     */
    public static boolean contains(double a, double... array) {
        return Items.indexOf(a, array) != -1;
    }

    /**
     * Checks if {@code a} is an element of {@code array}
     *
     * @param a     The element to check for
     * @param array The array to check in
     *
     * @return True if {@code array} contains {@code a}, false otherwise
     */
    public static boolean contains(float a, float... array) {
        return Items.indexOf(a, array) != -1;
    }

    /**
     * Checks if {@code a} is an element of {@code array}
     *
     * @param a     The element to check for
     * @param array The array to check in
     *
     * @return True if {@code array} contains {@code a}, false otherwise
     */
    public static boolean contains(boolean a, boolean... array) {
        return Items.indexOf(a, array) != -1;
    }

    /**
     * Checks if {@code a} is an element of {@code array}
     *
     * @param a     The element to check for
     * @param array The array to check in
     * @param <T>   The type of the array's elements
     *
     * @return True if {@code array} contains {@code a}, false otherwise
     */
    public static <T> boolean contains(T a, T... array) {
        return Items.indexOf(a, array) != -1;
    }

    /**
     * Creates a new array with the same length as {@code array}, and populates it with the elements from {@code array},
     * in reverse order. Does not modify the input {@code array}
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static byte[] reversed(byte... array) {
        byte[] result = new byte[array.length];
        int c = 0;
        for (int i = array.length - 1; i >= 0; i--, c++) {
            result[c] = array[i];
        }
        return result;
    }

    /**
     * Reverses the elements in the array. Note that this method <b>does</b> modify the input array
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static byte[] reverse(byte... array) {
        for (int i = 0, mid = array.length >> 1, j = array.length - 1; i < mid; i++, j--) {
            Items.swap(array, i, j);
        }
        return array;
    }

    /**
     * Creates a new array with the same length as {@code array}, and populates it with the elements from {@code array},
     * in reverse order. Does not modify the input {@code array}
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static char[] reversed(char... array) {
        char[] result = new char[array.length];
        int c = 0;
        for (int i = array.length - 1; i >= 0; i--, c++) {
            result[c] = array[i];
        }
        return result;
    }

    /**
     * Reverses the elements in the array. Note that this method <b>does</b> modify the input array
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static char[] reverse(char... array) {
        for (int i = 0, mid = array.length >> 1, j = array.length - 1; i < mid; i++, j--) {
            Items.swap(array, i, j);
        }
        return array;
    }

    /**
     * Creates a new array with the same length as {@code array}, and populates it with the elements from {@code array},
     * in reverse order. Does not modify the input {@code array}
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static short[] reversed(short... array) {
        short[] result = new short[array.length];
        int c = 0;
        for (int i = array.length - 1; i >= 0; i--, c++) {
            result[c] = array[i];
        }
        return result;
    }

    /**
     * Reverses the elements in the array. Note that this method <b>does</b> modify the input array
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static short[] reverse(short... array) {
        for (int i = 0, mid = array.length >> 1, j = array.length - 1; i < mid; i++, j--) {
            Items.swap(array, i, j);
        }
        return array;
    }

    /**
     * Creates a new array with the same length as {@code array}, and populates it with the elements from {@code array},
     * in reverse order. Does not modify the input {@code array}
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static int[] reversed(int... array) {
        int[] result = new int[array.length];
        int c = 0;
        for (int i = array.length - 1; i >= 0; i--, c++) {
            result[c] = array[i];
        }
        return result;
    }

    /**
     * Reverses the elements in the array. Note that this method <b>does</b> modify the input array
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static int[] reverse(int... array) {
        for (int i = 0, mid = array.length >> 1, j = array.length - 1; i < mid; i++, j--) {
            Items.swap(array, i, j);
        }
        return array;
    }

    /**
     * Creates a new array with the same length as {@code array}, and populates it with the elements from {@code array},
     * in reverse order. Does not modify the input {@code array}
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static long[] reversed(long... array) {
        long[] result = new long[array.length];
        int c = 0;
        for (int i = array.length - 1; i >= 0; i--, c++) {
            result[c] = array[i];
        }
        return result;
    }

    /**
     * Reverses the elements in the array. Note that this method <b>does</b> modify the input array
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static long[] reverse(long... array) {
        for (int i = 0, mid = array.length >> 1, j = array.length - 1; i < mid; i++, j--) {
            Items.swap(array, i, j);
        }
        return array;
    }

    /**
     * Creates a new array with the same length as {@code array}, and populates it with the elements from {@code array},
     * in reverse order. Does not modify the input {@code array}
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static float[] reversed(float... array) {
        float[] result = new float[array.length];
        int c = 0;
        for (int i = array.length - 1; i >= 0; i--, c++) {
            result[c] = array[i];
        }
        return result;
    }

    /**
     * Reverses the elements in the array. Note that this method <b>does</b> modify the input array
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static float[] reverse(float... array) {
        for (int i = 0, mid = array.length >> 1, j = array.length - 1; i < mid; i++, j--) {
            Items.swap(array, i, j);
        }
        return array;
    }

    /**
     * Creates a new array with the same length as {@code array}, and populates it with the elements from {@code array},
     * in reverse order. Does not modify the input {@code array}
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static double[] reversed(double... array) {
        double[] result = new double[array.length];
        int c = 0;
        for (int i = array.length - 1; i >= 0; i--, c++) {
            result[c] = array[i];
        }
        return result;
    }

    /**
     * Reverses the elements in the array. Note that this method <b>does</b> modify the input array
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static double[] reverse(double... array) {
        for (int i = 0, mid = array.length >> 1, j = array.length - 1; i < mid; i++, j--) {
            Items.swap(array, i, j);
        }
        return array;
    }

    /**
     * Creates a new array with the same length as {@code array}, and populates it with the elements from {@code array},
     * in reverse order. Does not modify the input {@code array}
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static boolean[] reversed(boolean... array) {
        boolean[] result = new boolean[array.length];
        int c = 0;
        for (int i = array.length - 1; i >= 0; i--, c++) {
            result[c] = array[i];
        }
        return result;
    }

    /**
     * Reverses the elements in the array. Note that this method <b>does</b> modify the input array
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static boolean[] reverse(boolean... array) {
        for (int i = 0, mid = array.length >> 1, j = array.length - 1; i < mid; i++, j--) {
            Items.swap(array, i, j);
        }
        return array;
    }

    /**
     * Creates a new array with the same length as {@code array}, and populates it with the elements from {@code array},
     * in reverse order. Does not modify the input {@code array}
     *
     * @param array The array to reverse
     * @param <T>   The type of array
     *
     * @return The reversed array
     */
    @SafeVarargs
    public static <T> T[] reversed(T... array) {
        T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length);
        int c = 0;
        for (int i = array.length - 1; i >= 0; i--, c++) {
            result[c] = array[i];
        }
        return result;
    }

    /**
     * Reverses the elements in the array. Note that this method <b>does</b> modify the input array
     *
     * @param array The array to reverse
     *
     * @return The reversed array
     */
    public static <T> T[] reverse(T... array) {
        for (int i = 0, mid = array.length >> 1, j = array.length - 1; i < mid; i++, j--) {
            Items.swap(array, i, j);
        }
        return array;
    }

    /**
     * Creates a new collection using the given Supplier and populates it with the elements from {@code collection}, in
     * reverse order. Does not modify the input {@code collection}
     *
     * @param collection            The collection to reverse
     * @param collectionConstructor The supplier to use when creating the new collection
     * @param <T>                   The type of the collection's elements
     * @param <C>                   The type of the resulting collection
     *
     * @return The reversed collection
     */
    public static <T, C extends Collection<T>> C reversed(Collection<T> collection, Supplier<C> collectionConstructor) {
        List<T> temp = new ArrayList<>();
        collection.forEach(val -> temp.add(0, val));
        C result = collectionConstructor.get();
        result.addAll(temp);
        return result;
    }

    /**
     * Reverses the elements in the collection. Note that this method <b>does</b> modify the input collection
     *
     * @param collection The collection to reverse
     * @param <T>        The type of the collection's elements
     * @param <C>        The type of the collection
     *
     * @return The reversed collection
     */
    public static <T, C extends Collection<T>> C reverse(C collection) {
        List<T> temp = new ArrayList<T>();
        collection.forEach(val -> temp.add(0, val));
        collection.clear();
        temp.forEach(collection::add);
        return collection;
    }

    /**
     * Reverses the elements in the list. Note that this method <b>does</b> modify the input list
     *
     * @param list The list to reverse
     *
     * @return The reversed list
     */
    public static <T> List<T> reverse(List<T> list) {
        for (int i = 0, mid = list.size() >> 1, j = list.size() - 1; i < mid; i++, j--) {
            Items.swap(list, i, j);
        }
        return list;
    }

    /**
     * Creates a new array list, and populates it with the elements from {@code list},
     * in reverse order. Does not modify the input {@code list}
     *
     * @param list The list to reverse
     * @param <T>  The type of list
     *
     * @return The reversed list
     */
    public static <T> List<T> reversed(List<T> list) {
        return Items.reverse(Items.looseClone(list));
    }

    /**
     * Determines the smallest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the smallest value in
     *
     * @return The smallest value of {@code input}
     */
    public static byte minimum(byte... input) {
        if (input.length == 0) {
            return 0;
        } else {
            byte small = input[0];
            for (int i = 1; i < input.length; i++) {
                byte next = input[i];
                if (next < small) {
                    small = next;
                }
            }
            return small;
        }
    }

    /**
     * Determines the largest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the largest value in
     *
     * @return The largest value of {@code input}
     */
    public static byte maximum(byte... input) {
        if (input.length == 0) {
            return 0;
        } else {
            byte large = input[0];
            for (int i = 1; i < input.length; i++) {
                byte next = input[i];
                if (next > large) {
                    large = next;
                }
            }
            return large;
        }
    }

    /**
     * Determines the smallest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the smallest value in
     *
     * @return The smallest value of {@code input}
     */
    public static char minimum(char... input) {
        if (input.length == 0) {
            return 0;
        } else {
            char small = input[0];
            for (int i = 1; i < input.length; i++) {
                char next = input[i];
                if (next < small) {
                    small = next;
                }
            }
            return small;
        }
    }

    /**
     * Determines the largest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the largest value in
     *
     * @return The largest value of {@code input}
     */
    public static char maximum(char... input) {
        if (input.length == 0) {
            return 0;
        } else {
            char large = input[0];
            for (int i = 1; i < input.length; i++) {
                char next = input[i];
                if (next > large) {
                    large = next;
                }
            }
            return large;
        }
    }

    /**
     * Determines the smallest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the smallest value in
     *
     * @return The smallest value of {@code input}
     */
    public static short minimum(short... input) {
        if (input.length == 0) {
            return 0;
        } else {
            short small = input[0];
            for (int i = 1; i < input.length; i++) {
                short next = input[i];
                if (next < small) {
                    small = next;
                }
            }
            return small;
        }
    }

    /**
     * Determines the largest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the largest value in
     *
     * @return The largest value of {@code input}
     */
    public static short maximum(short... input) {
        if (input.length == 0) {
            return 0;
        } else {
            short large = input[0];
            for (int i = 1; i < input.length; i++) {
                short next = input[i];
                if (next > large) {
                    large = next;
                }
            }
            return large;
        }
    }

    /**
     * Determines the smallest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the smallest value in
     *
     * @return The smallest value of {@code input}
     */
    public static int minimum(int... input) {
        if (input.length == 0) {
            return 0;
        } else {
            int small = input[0];
            for (int i = 1; i < input.length; i++) {
                int next = input[i];
                if (next < small) {
                    small = next;
                }
            }
            return small;
        }
    }

    /**
     * Determines the largest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the largest value in
     *
     * @return The largest value of {@code input}
     */
    public static int maximum(int... input) {
        if (input.length == 0) {
            return 0;
        } else {
            int large = input[0];
            for (int i = 1; i < input.length; i++) {
                int next = input[i];
                if (next > large) {
                    large = next;
                }
            }
            return large;
        }
    }

    /**
     * Determines the smallest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the smallest value in
     *
     * @return The smallest value of {@code input}
     */
    public static long minimum(long... input) {
        if (input.length == 0) {
            return 0;
        } else {
            long small = input[0];
            for (int i = 1; i < input.length; i++) {
                long next = input[i];
                if (next < small) {
                    small = next;
                }
            }
            return small;
        }
    }

    /**
     * Determines the largest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the largest value in
     *
     * @return The largest value of {@code input}
     */
    public static long maximum(long... input) {
        if (input.length == 0) {
            return 0;
        } else {
            long large = input[0];
            for (int i = 1; i < input.length; i++) {
                long next = input[i];
                if (next > large) {
                    large = next;
                }
            }
            return large;
        }
    }

    /**
     * Determines the smallest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the smallest value in
     *
     * @return The smallest value of {@code input}
     */
    public static float minimum(float... input) {
        if (input.length == 0) {
            return 0;
        } else {
            float small = input[0];
            for (int i = 1; i < input.length; i++) {
                float next = input[i];
                if (next < small) {
                    small = next;
                }
            }
            return small;
        }
    }

    /**
     * Determines the largest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the largest value in
     *
     * @return The largest value of {@code input}
     */
    public static float maximum(float... input) {
        if (input.length == 0) {
            return 0;
        } else {
            float large = input[0];
            for (int i = 1; i < input.length; i++) {
                float next = input[i];
                if (next > large) {
                    large = next;
                }
            }
            return large;
        }
    }

    /**
     * Determines the smallest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the smallest value in
     *
     * @return The smallest value of {@code input}
     */
    public static double minimum(double... input) {
        if (input.length == 0) {
            return 0;
        } else {
            double small = input[0];
            for (int i = 1; i < input.length; i++) {
                double next = input[i];
                if (next < small) {
                    small = next;
                }
            }
            return small;
        }
    }

    /**
     * Determines the largest value in {@code input} and returns it. Returns 0 if {@code input.length == 0}
     *
     * @param input The array to locate the largest value in
     *
     * @return The largest value of {@code input}
     */
    public static double maximum(double... input) {
        if (input.length == 0) {
            return 0;
        } else {
            double large = input[0];
            for (int i = 1; i < input.length; i++) {
                double next = input[i];
                if (next > large) {
                    large = next;
                }
            }
            return large;
        }
    }

    /**
     * Determines the smallest value in {@code input} and returns it. Returns false if {@code input.length == 0}
     *
     * @param input The array to locate the smallest value in
     *
     * @return The smallest value of {@code input}
     */
    public static boolean minimum(boolean... input) {
        if (input.length == 0) {
            return false;
        } else {
            boolean small = input[0];
            for (int i = 1; i < input.length; i++) {
                boolean next = input[i];
                if (Boolean.compare(next, small) < 0) {
                    small = next;
                }
            }
            return small;
        }
    }

    /**
     * Determines the largest value in {@code input} and returns it. Returns false if {@code input.length == 0}
     *
     * @param input The array to locate the largest value in
     *
     * @return The largest value of {@code input}
     */
    public static boolean maximum(boolean... input) {
        if (input.length == 0) {
            return false;
        } else {
            boolean large = input[0];
            for (int i = 1; i < input.length; i++) {
                boolean next = input[i];
                if (Boolean.compare(next, large) > 0) {
                    large = next;
                }
            }
            return large;
        }
    }

    /**
     * Determines the smallest value in {@code input} and returns it. Returns null if {@code input.length == 0}
     *
     * @param input The array to locate the smallest value in
     * @param <T>   The type of the array's elements
     *
     * @return The smallest value of {@code input}
     */
    public static <T extends Comparable<T>> T minimum(T[] input) {
        return Items.minimum(input, Comparable::compareTo);
    }

    /**
     * Determines the largest value in {@code input} and returns it. Returns null if {@code input.length == 0}
     *
     * @param input The array to locate the largest value in
     * @param <T>   the type of the array's elements
     *
     * @return The largest value of {@code input}
     */
    public static <T extends Comparable<T>> T maximum(T[] input) {
        return Items.maximum(input, Comparable::compareTo);
    }


    /**
     * Determines the smallest value in {@code input} and returns it. Returns null if {@code input.size() == 0}
     *
     * @param input The collection to locate the smallest value in
     * @param <T>   The type of the collection's elements
     *
     * @return The smallest value of {@code input}
     */
    public static <T extends Comparable<T>> T minimum(Collection<T> input) {
        return Items.minimum(input, Comparable::compareTo);
    }

    /**
     * Determines the largest value in {@code input} and returns it. Returns null if {@code input.size() == 0}
     *
     * @param input The array to locate the largest value in
     * @param <T>   The type of collection
     *
     * @return The largest value of {@code input}
     */
    public static <T extends Comparable<T>> T maximum(Collection<T> input) {
        return Items.maximum(input, Comparable::compareTo);
    }

    /**
     * Determines the smallest value in {@code input} and returns it. Returns null if {@code input.length == 0}
     *
     * @param input      The array to locate the smallest value in
     * @param comparator The comparator to use when determine whether or not values are less than each other
     * @param <T>        the type of the array's elements
     *
     * @return The smallest value of {@code input}
     */
    public static <T> T minimum(T[] input, Comparator<T> comparator) {
        if (input.length == 0) {
            return null;
        } else {
            T small = input[0];
            for (int i = 1; i < input.length; i++) {
                T next = input[i];
                if (comparator.compare(next, small) < 0) {
                    small = next;
                }
            }
            return small;
        }
    }

    /**
     * Determines the largest value in {@code input} and returns it. Returns null if {@code input.length == 0}
     *
     * @param input      The array to locate the largest value in
     * @param comparator The comparator to use when determine whether or not values are less than each other
     * @param <T>        the type of the array's elements
     *
     * @return The largest value of {@code input}
     */
    public static <T> T maximum(T[] input, Comparator<T> comparator) {
        if (input.length == 0) {
            return null;
        } else {
            T big = input[0];
            for (int i = 1; i < input.length; i++) {
                T next = input[i];
                if (comparator.compare(next, big) > 0) {
                    big = next;
                }
            }
            return big;
        }
    }

    /**
     * Determines the smallest value in {@code input} and returns it. Returns null if {@code input.size() == 0}
     *
     * @param input      The collection to locate the smallest value in
     * @param comparator The comparator to use when determine whether or not values are less than each other
     * @param <T>        The type of the collection's elements
     *
     * @return The smallest value of {@code input}
     */
    public static <T> T minimum(Collection<T> input, Comparator<T> comparator) {
        if (input.size() == 0) {
            return null;
        } else {
            Iterator<T> iterator = input.iterator();
            T small = iterator.next();
            while (iterator.hasNext()) {
                T next = iterator.next();
                if (comparator.compare(next, small) < 0) {
                    small = next;
                }
            }
            return small;
        }
    }


    /**
     * Determines the largest value in {@code input} and returns it. Returns null if {@code input.size() == 0}
     *
     * @param input      The collection to locate the largest value in
     * @param comparator The comparator to use when determine whether or not values are less than each other
     * @param <T>        The type of the collection's elements
     *
     * @return The largest value of {@code input}
     */
    public static <T> T maximum(Collection<T> input, Comparator<T> comparator) {
        if (input.size() == 0) {
            return null;
        } else {
            Iterator<T> iterator = input.iterator();
            T big = iterator.next();
            while (iterator.hasNext()) {
                T next = iterator.next();
                if (comparator.compare(next, big) > 0) {
                    big = next;
                }
            }
            return big;
        }
    }

    /**
     * Selects a random element from the array, using the given random, and returns it. If {@code array.length == 0}, 0
     * will be returned
     *
     * @param array  The array to select from
     * @param random The random to use
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static byte randomElement(byte[] array, Random random) {
        return array.length == 0 ? 0 : array[random.nextInt(array.length)];
    }

    /**
     * Selects a random element from the array, and returns it. If {@code array.length == 0}, 0 will be returned. This
     * is functionally equivalent to:
     * <pre>
     *     Items.randomElement(array, new Random())
     * </pre>
     *
     * @param array The array to select from
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static byte randomElement(byte[] array) {
        return Items.randomElement(array, new Random());
    }

    /**
     * Selects a random element from the array, using the given random, and returns it. If {@code array.length == 0}, 0
     * will be returned
     *
     * @param array  The array to select from
     * @param random The random to use
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static char randomElement(char[] array, Random random) {
        return array.length == 0 ? 0 : array[random.nextInt(array.length)];
    }

    /**
     * Selects a random element from the array, and returns it. If {@code array.length == 0}, 0 will be returned. This
     * is functionally equivalent to:
     * <pre>
     *     Items.randomElement(array, new Random())
     * </pre>
     *
     * @param array The array to select from
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static char randomElement(char[] array) {
        return Items.randomElement(array, new Random());
    }

    /**
     * Selects a random element from the array, using the given random, and returns it. If {@code array.length == 0}, 0
     * will be returned
     *
     * @param array  The array to select from
     * @param random The random to use
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static short randomElement(short[] array, Random random) {
        return array.length == 0 ? 0 : array[random.nextInt(array.length)];
    }

    /**
     * Selects a random element from the array, and returns it. If {@code array.length == 0}, 0 will be returned. This
     * is functionally equivalent to:
     * <pre>
     *     Items.randomElement(array, new Random())
     * </pre>
     *
     * @param array The array to select from
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static short randomElement(short[] array) {
        return Items.randomElement(array, new Random());
    }

    /**
     * Selects a random element from the array, using the given random, and returns it. If {@code array.length == 0}, 0
     * will be returned
     *
     * @param array  The array to select from
     * @param random The random to use
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static int randomElement(int[] array, Random random) {
        return array.length == 0 ? 0 : array[random.nextInt(array.length)];
    }

    /**
     * Selects a random element from the array, and returns it. If {@code array.length == 0}, 0 will be returned. This
     * is functionally equivalent to:
     * <pre>
     *     Items.randomElement(array, new Random())
     * </pre>
     *
     * @param array The array to select from
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static int randomElement(int[] array) {
        return Items.randomElement(array, new Random());
    }

    /**
     * Selects a random element from the array, using the given random, and returns it. If {@code array.length == 0}, 0
     * will be returned
     *
     * @param array  The array to select from
     * @param random The random to use
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static long randomElement(long[] array, Random random) {
        return array.length == 0 ? 0 : array[random.nextInt(array.length)];
    }

    /**
     * Selects a random element from the array, and returns it. If {@code array.length == 0}, 0 will be returned. This
     * is functionally equivalent to:
     * <pre>
     *     Items.randomElement(array, new Random())
     * </pre>
     *
     * @param array The array to select from
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static long randomElement(long[] array) {
        return Items.randomElement(array, new Random());
    }

    /**
     * Selects a random element from the array, using the given random, and returns it. If {@code array.length == 0}, 0
     * will be returned
     *
     * @param array  The array to select from
     * @param random The random to use
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static float randomElement(float[] array, Random random) {
        return array.length == 0 ? 0 : array[random.nextInt(array.length)];
    }

    /**
     * Selects a random element from the array, and returns it. If {@code array.length == 0}, 0 will be returned. This
     * is functionally equivalent to:
     * <pre>
     *     Items.randomElement(array, new Random())
     * </pre>
     *
     * @param array The array to select from
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static float randomElement(float[] array) {
        return Items.randomElement(array, new Random());
    }

    /**
     * Selects a random element from the array, using the given random, and returns it. If {@code array.length == 0}, 0
     * will be returned
     *
     * @param array  The array to select from
     * @param random The random to use
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static double randomElement(double[] array, Random random) {
        return array.length == 0 ? 0 : array[random.nextInt(array.length)];
    }

    /**
     * Selects a random element from the array, and returns it. If {@code array.length == 0}, 0 will be returned. This
     * is functionally equivalent to:
     * <pre>
     *     Items.randomElement(array, new Random())
     * </pre>
     *
     * @param array The array to select from
     *
     * @return The randomly selected element, or 0 if {@code array.length == 0}
     */
    public static double randomElement(double[] array) {
        return Items.randomElement(array, new Random());
    }

    /**
     * Selects a random element from the array, using the given random, and returns it. If {@code array.length == 0},
     * false will be returned
     *
     * @param array  The array to select from
     * @param random The random to use
     *
     * @return The randomly selected element, or false if {@code array.length == 0}
     */
    public static boolean randomElement(boolean[] array, Random random) {
        return array.length != 0 && array[random.nextInt(array.length)];
    }

    /**
     * Selects a random element from the array, and returns it. If {@code array.length == 0}, false will be returned.
     * This is functionally equivalent to:
     * <pre>
     *     Items.randomElement(array, new Random())
     * </pre>
     *
     * @param array The array to select from
     *
     * @return The randomly selected element, or false if {@code array.length == 0}
     */
    public static boolean randomElement(boolean[] array) {
        return Items.randomElement(array, new Random());
    }

    /**
     * Selects a random  element from the collection, using the given random, and returns it. If {@code
     * collection.isEmpty()}, null will be returned
     *
     * @param collection The collection to select from
     * @param random     The random to use
     * @param <T>        The type of the collection's elements
     * @param <C>        The type of the collection
     *
     * @return The randomly selected element, or null if {@code collection.isEmpty()}
     */
    public static <T, C extends Collection<T>> T randomElement(C collection, Random random) {
        if (collection.isEmpty()) {
            return null;
        } else {
            int iterations = random.nextInt(collection.size());
            Iterator<T> iterator = collection.iterator();
            for (int i = 0; i < iterations - 1; i++) {
                iterator.next();
            }
            return iterator.next();
        }
    }

    /**
     * Selects a random element from the collection, and returns it. If {@code
     * collection.isEmpty()}, null will be returned. This is functionally equivalent to:
     * <pre>
     *     Items.randomElement(collection, new Random())
     * </pre>
     *
     * @param collection The collection to select from
     * @param <T>        The type of the collection's elements
     * @param <C>        The type of the collection
     *
     * @return The randomly selected element, or null if {@code collection.isEmpty()}
     */
    public static <T, C extends Collection<T>> T randomElement(C collection) {
        return Items.randomElement(collection, new Random());
    }

    /**
     * Selects a random  element from the list, using the given random, and returns it. If {@code
     * list.isEmpty()}, null will be returned
     *
     * @param list   The list to select from
     * @param random The random to use
     * @param <T>    The type of the list's elements
     *
     * @return The randomly selected element, or null if {@code list.isEmpty()}
     */
    public static <T> T randomElement(List<T> list, Random random) {
        return list.isEmpty() ? null : list.get(random.nextInt(list.size()));
    }

    /**
     * Selects a random element from the list, and returns it. If {@code
     * list.isEmpty()}, null will be returned. This is functionally equivalent to:
     * <pre>
     *     Items.randomElement(list, new Random())
     * </pre>
     *
     * @param list The list to select from
     * @param <T>  The type of the lists's elements
     *
     * @return The randomly selected element, or null if {@code list.isEmpty()}
     */
    public static <T> T randomElement(List<T> list) {
        return Items.randomElement(list, new Random());
    }

    /**
     * Swaps the elements in the array at the specified indices. If the indices are equal, this method will do nothing.
     * Note that this method <b>does</b> modify the input array
     *
     * @param array The array to swap elements in
     * @param a     The index of one element
     * @param b     The index of the other element
     *
     * @return {@code array}
     *
     * @throws IndexOutOfBoundsException if either index is out of bounds of the array
     */
    public static byte[] swap(byte[] array, int a, int b) {
        if (a < 0 || a >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(a));
        } else if (b < 0 || b >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(b));
        } else {
            byte temp = array[a];
            array[a] = array[b];
            array[b] = temp;
            return array;
        }
    }

    /**
     * Swaps the elements in the array at the specified indices. If the indices are equal, this method will do nothing.
     * Note that this method <b>does</b> modify the input array
     *
     * @param array The array to swap elements in
     * @param a     The index of one element
     * @param b     The index of the other element
     *
     * @return {@code array}
     *
     * @throws IndexOutOfBoundsException if either index is out of bounds of the array
     */
    public static char[] swap(char[] array, int a, int b) {
        if (a < 0 || a >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(a));
        } else if (b < 0 || b >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(b));
        } else {
            char temp = array[a];
            array[a] = array[b];
            array[b] = temp;
            return array;
        }
    }

    /**
     * Swaps the elements in the array at the specified indices. If the indices are equal, this method will do nothing.
     * Note that this method <b>does</b> modify the input array
     *
     * @param array The array to swap elements in
     * @param a     The index of one element
     * @param b     The index of the other element
     *
     * @return {@code array}
     *
     * @throws IndexOutOfBoundsException if either index is out of bounds of the array
     */
    public static short[] swap(short[] array, int a, int b) {
        if (a < 0 || a >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(a));
        } else if (b < 0 || b >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(b));
        } else {
            short temp = array[a];
            array[a] = array[b];
            array[b] = temp;
            return array;
        }
    }

    /**
     * Swaps the elements in the array at the specified indices. If the indices are equal, this method will do nothing.
     * Note that this method <b>does</b> modify the input array
     *
     * @param array The array to swap elements in
     * @param a     The index of one element
     * @param b     The index of the other element
     *
     * @return {@code array}
     *
     * @throws IndexOutOfBoundsException if either index is out of bounds of the array
     */
    public static int[] swap(int[] array, int a, int b) {
        if (a < 0 || a >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(a));
        } else if (b < 0 || b >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(b));
        } else {
            int temp = array[a];
            array[a] = array[b];
            array[b] = temp;
            return array;
        }
    }

    /**
     * Swaps the elements in the array at the specified indices. If the indices are equal, this method will do nothing.
     * Note that this method <b>does</b> modify the input array
     *
     * @param array The array to swap elements in
     * @param a     The index of one element
     * @param b     The index of the other element
     *
     * @return {@code array}
     *
     * @throws IndexOutOfBoundsException if either index is out of bounds of the array
     */
    public static long[] swap(long[] array, int a, int b) {
        if (a < 0 || a >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(a));
        } else if (b < 0 || b >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(b));
        } else {
            long temp = array[a];
            array[a] = array[b];
            array[b] = temp;
            return array;
        }
    }

    /**
     * Swaps the elements in the array at the specified indices. If the indices are equal, this method will do nothing.
     * Note that this method <b>does</b> modify the input array
     *
     * @param array The array to swap elements in
     * @param a     The index of one element
     * @param b     The index of the other element
     *
     * @return {@code array}
     *
     * @throws IndexOutOfBoundsException if either index is out of bounds of the array
     */
    public static float[] swap(float[] array, int a, int b) {
        if (a < 0 || a >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(a));
        } else if (b < 0 || b >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(b));
        } else {
            float temp = array[a];
            array[a] = array[b];
            array[b] = temp;
            return array;
        }
    }

    /**
     * Swaps the elements in the array at the specified indices. If the indices are equal, this method will do nothing.
     * Note that this method <b>does</b> modify the input array
     *
     * @param array The array to swap elements in
     * @param a     The index of one element
     * @param b     The index of the other element
     *
     * @return {@code array}
     *
     * @throws IndexOutOfBoundsException if either index is out of bounds of the array
     */
    public static double[] swap(double[] array, int a, int b) {
        if (a < 0 || a >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(a));
        } else if (b < 0 || b >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(b));
        } else {
            double temp = array[a];
            array[a] = array[b];
            array[b] = temp;
            return array;
        }
    }

    /**
     * Swaps the elements in the array at the specified indices. If the indices are equal, this method will do nothing.
     * Note that this method <b>does</b> modify the input array
     *
     * @param array The array to swap elements in
     * @param a     The index of one element
     * @param b     The index of the other element
     *
     * @return {@code array}
     *
     * @throws IndexOutOfBoundsException if either index is out of bounds of the array
     */
    public static boolean[] swap(boolean[] array, int a, int b) {
        if (a < 0 || a >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(a));
        } else if (b < 0 || b >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(b));
        } else {
            boolean temp = array[a];
            array[a] = array[b];
            array[b] = temp;
            return array;
        }
    }

    /**
     * Swaps the elements in the array at the specified indices. If the indices are equal, this method will do nothing.
     * Note that this method <b>does</b> modify the input array
     *
     * @param array The array to swap elements in
     * @param a     The index of one element
     * @param b     The index of the other element
     * @param <T>   The type of the array's elements
     *
     * @return {@code array}
     *
     * @throws IndexOutOfBoundsException if either index is out of bounds of the array
     */
    public static <T> T[] swap(T[] array, int a, int b) {
        if (a < 0 || a >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(a));
        } else if (b < 0 || b >= array.length) {
            throw new IndexOutOfBoundsException(String.valueOf(b));
        } else {
            T temp = array[a];
            array[a] = array[b];
            array[b] = temp;
            return array;
        }
    }

    /**
     * Swaps the elements in the array at the specified indices. If the indices are equal, this method will do nothing.
     * Note that this method <b>does</b> modify the input list
     *
     * @param list The array to swap elements in
     * @param a    The index of one element
     * @param b    The index of the other element
     *
     * @return {@code list}
     *
     * @throws IndexOutOfBoundsException if either index is out of bounds of the list
     */
    public static <T> List<T> swap(List<T> list, int a, int b) {
        if (a < 0 || a >= list.size()) {
            throw new IndexOutOfBoundsException(String.valueOf(a));
        } else if (b < 0 || b >= list.size()) {
            throw new IndexOutOfBoundsException(String.valueOf(b));
        } else {
            T temp = list.get(a);
            list.set(a, list.get(b));
            list.set(b, temp);
            return list;
        }
    }

    /**
     * Swaps the elements in the collection at the specified indices. If the indices are equal, this method will do
     * nothing. Note that this method <b>does</b> modify the input collection
     *
     * @param collection The collection to swap elements in
     * @param a          The index of one element
     * @param b          The index of the other element
     * @param <T>        The type of the collection's elements
     * @param <C>        The type of the collection
     *
     * @return {@code collection}
     *
     * @throws IndexOutOfBoundsException if either index is out of bounds of the collection
     */
    public static <T, C extends Collection<T>> C swap(C collection, int a, int b) {
        if (a < 0 || a >= collection.size()) {
            throw new IndexOutOfBoundsException(String.valueOf(a));
        } else if (b < 0 || b >= collection.size()) {
            throw new IndexOutOfBoundsException(String.valueOf(b));
        } else {
            Object[] swapped = Items.swap(collection.toArray(), a, b);
            collection.clear();
            for (Object o : swapped) {
                collection.add((T) o);
            }
            return collection;
        }
    }

    /**
     * Randomly shuffles the elements in the array, using the given random. Note that this method <b>does</b> modify the
     * input array
     *
     * @param array  The array to shuffle
     * @param random The random to use
     *
     * @return {@code array}
     */
    public static byte[] shuffle(byte[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            Items.swap(array, i - 1, random.nextInt(i));
        }
        return array;
    }

    /**
     * Randomly shuffles the elements in the array. Note that this method <b>does</b> modify the input array. This is
     * functionally equivalent to:
     * <pre>
     *     Items.shuffle(array, new Random())
     * </pre>
     *
     * @param array The array to shuffle
     *
     * @return {@code array}
     */
    public static byte[] shuffle(byte[] array) {
        return Items.shuffle(array, new Random());
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array
     *
     * @param array  The array to randomize
     * @param random The random to use
     *
     * @return A randomized array
     */
    public static byte[] randomized(byte[] array, Random random) {
        return Items.shuffle(array.clone(), random);
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array. This method is
     * functionally equivalent to:
     * <pre>
     *     Items.randomized(array, new Random())
     * </pre>
     *
     * @param array The array to randomize
     *
     * @return A randomized array
     */
    public static byte[] randomized(byte[] array) {
        return Items.randomized(array, new Random());
    }

    /**
     * Randomly shuffles the elements in the array, using the given random. Note that this method <b>does</b> modify the
     * input array
     *
     * @param array  The array to shuffle
     * @param random The random to use
     *
     * @return {@code array}
     */
    public static char[] shuffle(char[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            Items.swap(array, i - 1, random.nextInt(i));
        }
        return array;
    }

    /**
     * Randomly shuffles the elements in the array. Note that this method <b>does</b> modify the input array. This is
     * functionally equivalent to:
     * <pre>
     *     Items.shuffle(array, new Random())
     * </pre>
     *
     * @param array The array to shuffle
     *
     * @return {@code array}
     */
    public static char[] shuffle(char[] array) {
        return Items.shuffle(array, new Random());
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array
     *
     * @param array  The array to randomize
     * @param random The random to use
     *
     * @return A randomized array
     */
    public static char[] randomized(char[] array, Random random) {
        return Items.shuffle(array.clone(), random);
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array. This method is
     * functionally equivalent to:
     * <pre>
     *     Items.randomized(array, new Random())
     * </pre>
     *
     * @param array The array to randomize
     *
     * @return A randomized array
     */
    public static char[] randomized(char[] array) {
        return Items.randomized(array, new Random());
    }

    /**
     * Randomly shuffles the elements in the array, using the given random. Note that this method <b>does</b> modify the
     * input array
     *
     * @param array  The array to shuffle
     * @param random The random to use
     *
     * @return {@code array}
     */
    public static short[] shuffle(short[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            Items.swap(array, i - 1, random.nextInt(i));
        }
        return array;
    }

    /**
     * Randomly shuffles the elements in the array. Note that this method <b>does</b> modify the input array. This is
     * functionally equivalent to:
     * <pre>
     *     Items.shuffle(array, new Random())
     * </pre>
     *
     * @param array The array to shuffle
     *
     * @return {@code array}
     */
    public static short[] shuffle(short[] array) {
        return Items.shuffle(array, new Random());
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array
     *
     * @param array  The array to randomize
     * @param random The random to use
     *
     * @return A randomized array
     */
    public static short[] randomized(short[] array, Random random) {
        return Items.shuffle(array.clone(), random);
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array. This method is
     * functionally equivalent to:
     * <pre>
     *     Items.randomized(array, new Random())
     * </pre>
     *
     * @param array The array to randomize
     *
     * @return A randomized array
     */
    public static short[] randomized(short[] array) {
        return Items.randomized(array, new Random());
    }

    /**
     * Randomly shuffles the elements in the array, using the given random. Note that this method <b>does</b> modify the
     * input array
     *
     * @param array  The array to shuffle
     * @param random The random to use
     *
     * @return {@code array}
     */
    public static int[] shuffle(int[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            Items.swap(array, i - 1, random.nextInt(i));
        }
        return array;
    }

    /**
     * Randomly shuffles the elements in the array. Note that this method <b>does</b> modify the input array. This is
     * functionally equivalent to:
     * <pre>
     *     Items.shuffle(array, new Random())
     * </pre>
     *
     * @param array The array to shuffle
     *
     * @return {@code array}
     */
    public static int[] shuffle(int[] array) {
        return Items.shuffle(array, new Random());
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array
     *
     * @param array  The array to randomize
     * @param random The random to use
     *
     * @return A randomized array
     */
    public static int[] randomized(int[] array, Random random) {
        return Items.shuffle(array.clone(), random);
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array. This method is
     * functionally equivalent to:
     * <pre>
     *     Items.randomized(array, new Random())
     * </pre>
     *
     * @param array The array to randomize
     *
     * @return A randomized array
     */
    public static int[] randomized(int[] array) {
        return Items.randomized(array, new Random());
    }

    /**
     * Randomly shuffles the elements in the array, using the given random. Note that this method <b>does</b> modify the
     * input array
     *
     * @param array  The array to shuffle
     * @param random The random to use
     *
     * @return {@code array}
     */
    public static long[] shuffle(long[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            Items.swap(array, i - 1, random.nextInt(i));
        }
        return array;
    }

    /**
     * Randomly shuffles the elements in the array. Note that this method <b>does</b> modify the input array. This is
     * functionally equivalent to:
     * <pre>
     *     Items.shuffle(array, new Random())
     * </pre>
     *
     * @param array The array to shuffle
     *
     * @return {@code array}
     */
    public static long[] shuffle(long[] array) {
        return Items.shuffle(array, new Random());
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array
     *
     * @param array  The array to randomize
     * @param random The random to use
     *
     * @return A randomized array
     */
    public static long[] randomized(long[] array, Random random) {
        return Items.shuffle(array.clone(), random);
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array. This method is
     * functionally equivalent to:
     * <pre>
     *     Items.randomized(array, new Random())
     * </pre>
     *
     * @param array The array to randomize
     *
     * @return A randomized array
     */
    public static long[] randomized(long[] array) {
        return Items.randomized(array, new Random());
    }

    /**
     * Randomly shuffles the elements in the array, using the given random. Note that this method <b>does</b> modify the
     * input array
     *
     * @param array  The array to shuffle
     * @param random The random to use
     *
     * @return {@code array}
     */
    public static float[] shuffle(float[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            Items.swap(array, i - 1, random.nextInt(i));
        }
        return array;
    }

    /**
     * Randomly shuffles the elements in the array. Note that this method <b>does</b> modify the input array. This is
     * functionally equivalent to:
     * <pre>
     *     Items.shuffle(array, new Random())
     * </pre>
     *
     * @param array The array to shuffle
     *
     * @return {@code array}
     */
    public static float[] shuffle(float[] array) {
        return Items.shuffle(array, new Random());
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array
     *
     * @param array  The array to randomize
     * @param random The random to use
     *
     * @return A randomized array
     */
    public static float[] randomized(float[] array, Random random) {
        return Items.shuffle(array.clone(), random);
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array. This method is
     * functionally equivalent to:
     * <pre>
     *     Items.randomized(array, new Random())
     * </pre>
     *
     * @param array The array to randomize
     *
     * @return A randomized array
     */
    public static float[] randomized(float[] array) {
        return Items.randomized(array, new Random());
    }

    /**
     * Randomly shuffles the elements in the array, using the given random. Note that this method <b>does</b> modify the
     * input array
     *
     * @param array  The array to shuffle
     * @param random The random to use
     *
     * @return {@code array}
     */
    public static double[] shuffle(double[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            Items.swap(array, i - 1, random.nextInt(i));
        }
        return array;
    }

    /**
     * Randomly shuffles the elements in the array. Note that this method <b>does</b> modify the input array. This is
     * functionally equivalent to:
     * <pre>
     *     Items.shuffle(array, new Random())
     * </pre>
     *
     * @param array The array to shuffle
     *
     * @return {@code array}
     */
    public static double[] shuffle(double[] array) {
        return Items.shuffle(array, new Random());
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array
     *
     * @param array  The array to randomize
     * @param random The random to use
     *
     * @return A randomized array
     */
    public static double[] randomized(double[] array, Random random) {
        return Items.shuffle(array.clone(), random);
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array. This method is
     * functionally equivalent to:
     * <pre>
     *     Items.randomized(array, new Random())
     * </pre>
     *
     * @param array The array to randomize
     *
     * @return A randomized array
     */
    public static double[] randomized(double[] array) {
        return Items.randomized(array, new Random());
    }

    /**
     * Randomly shuffles the elements in the array, using the given random. Note that this method <b>does</b> modify the
     * input array
     *
     * @param array  The array to shuffle
     * @param random The random to use
     *
     * @return {@code array}
     */
    public static boolean[] shuffle(boolean[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            Items.swap(array, i - 1, random.nextInt(i));
        }
        return array;
    }

    /**
     * Randomly shuffles the elements in the array. Note that this method <b>does</b> modify the input array. This is
     * functionally equivalent to:
     * <pre>
     *     Items.shuffle(array, new Random())
     * </pre>
     *
     * @param array The array to shuffle
     *
     * @return {@code array}
     */
    public static boolean[] shuffle(boolean[] array) {
        return Items.shuffle(array, new Random());
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array
     *
     * @param array  The array to randomize
     * @param random The random to use
     *
     * @return A randomized array
     */
    public static boolean[] randomized(boolean[] array, Random random) {
        return Items.shuffle(array.clone(), random);
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array. This method is
     * functionally equivalent to:
     * <pre>
     *     Items.randomized(array, new Random())
     * </pre>
     *
     * @param array The array to randomize
     *
     * @return A randomized array
     */
    public static boolean[] randomized(boolean[] array) {
        return Items.randomized(array, new Random());
    }

    /**
     * Randomly shuffles the elements in the array, using the given random. Note that this method <b>does</b> modify the
     * input array
     *
     * @param array  The array to shuffle
     * @param random The random to use
     *
     * @return {@code array}
     */
    public static <T> T[] shuffle(T[] array, Random random) {
        for (int i = array.length; i > 1; i--) {
            Items.swap(array, i - 1, random.nextInt(i));
        }
        return array;
    }

    /**
     * Randomly shuffles the elements in the array. Note that this method <b>does</b> modify the input array. This is
     * functionally equivalent to:
     * <pre>
     *     Items.shuffle(array, new Random())
     * </pre>
     *
     * @param array The array to shuffle
     * @param <T>   The type of the array's elements
     *
     * @return {@code array}
     */
    public static <T> T[] shuffle(T[] array) {
        return Items.shuffle(array, new Random());
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array
     *
     * @param array  The array to randomize
     * @param random The random to use
     * @param <T>    The type of the array's elements
     *
     * @return A randomized array
     */
    public static <T> T[] randomized(T[] array, Random random) {
        return Items.shuffle(array.clone(), random);
    }

    /**
     * Creates a new array and populates it with elements from the array in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input array. This method is
     * functionally equivalent to:
     * <pre>
     *     Items.randomized(array, new Random())
     * </pre>
     *
     * @param array The array to randomize
     * @param <T>   The type of the array's elements
     *
     * @return A randomized array
     */
    public static <T> T[] randomized(T[] array) {
        return Items.randomized(array, new Random());
    }

    /**
     * Randomly shuffles the elements in the collection, using the given random. Note that this method <b>does</b>
     * modify the
     * input collection
     *
     * @param collection The collection to shuffle
     * @param random     The random to use
     * @param <T>        The type of the collection's elements
     * @param <C>        The type of the collection
     *
     * @return {@code collection}
     */
    public static <T, C extends Collection<T>> C shuffle(C collection, Random random) {
        for (int i = collection.size(); i > 1; i--) {
            Items.swap(collection, i - 1, random.nextInt(i));
        }
        return collection;
    }

    /**
     * Randomly shuffles the elements in the collection. Note that this method <b>does</b> modify the input collection.
     * This is
     * functionally equivalent to:
     * <pre>
     *     Items.shuffle(collection, new Random())
     * </pre>
     *
     * @param collection The collection to shuffle
     * @param <T>        The type of the collection's elements
     * @param <C>        The type of the collection
     *
     * @return {@code collection}
     */
    public static <T, C extends Collection<T>> C shuffle(C collection) {
        return Items.shuffle(collection, new Random());
    }

    /**
     * Creates a new collection and populates it with elements from the collection in a random order, using the given
     * random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input collection
     *
     * @param collection The collection to randomize
     * @param random     The random to use
     * @param <T>        The type of the collection's elements
     * @param <C>        The type of the collection
     *
     * @return A randomized collection
     */
    public static <T, C extends Collection<T>> C randomized(Collection<T> collection, Random random, Supplier<C> collectionConstructor) {
        return Items.shuffle(Items.looseClone(collection, collectionConstructor), random);
    }

    /**
     * Creates a new collection and populates it with elements from the collection in a random order, using the given
     * random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input collection. This method is
     * functionally equivalent to:
     * <pre>
     *     Items.randomized(collection, new Random())
     * </pre>
     *
     * @param collection The collection to randomize
     * @param <T>        The type of the collection's elements
     * @param <C>        The type of the collection
     *
     * @return A randomized collection
     */
    public static <T, C extends Collection<T>> C randomized(Collection<T> collection, Supplier<C> collectionConstructor) {
        return Items.randomized(collection, new Random(), collectionConstructor);
    }

    /**
     * Randomly shuffles the elements in the list, using the given random. Note that this method <b>does</b> modify the
     * input list
     *
     * @param list   The list to shuffle
     * @param random The random to use
     * @param <T>    The type of the list's elements
     * @param <C>    The type of the list
     *
     * @return {@code list}
     */
    public static <T, C extends List<T>> C shuffle(C list, Random random) {
        for (int i = list.size(); i > 1; i--) {
            Items.swap(list, i - 1, random.nextInt(i));
        }
        return list;
    }

    /**
     * Randomly shuffles the elements in the list. Note that this method <b>does</b> modify the input list. This is
     * functionally equivalent to:
     * <pre>
     *     Items.shuffle(list, new Random())
     * </pre>
     *
     * @param list The list to shuffle
     * @param <T>  The type of the list's elements
     * @param <C>  The type of the list
     *
     * @return {@code list}
     */
    public static <T, C extends List<T>> C shuffle(C list) {
        return Items.shuffle(list, new Random());
    }

    /**
     * Creates a new list and populates it with elements from the list in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input list
     *
     * @param list   The list to randomize
     * @param random The random to use
     * @param <T>    The type of the list's elements
     *
     * @return A randomized list
     */
    public static <T> List<T> randomized(List<T> list, Random random) {
        return Items.shuffle(Items.looseClone(list), random);
    }

    /**
     * Creates a new list and populates it with elements from the list in a random order, using the given random. This
     * method is similar to {@code Items.shuffle}, although it does not modify the input list. This method is
     * functionally equivalent to:
     * <pre>
     *     Items.randomized(list, new Random())
     * </pre>
     *
     * @param list The list to randomize
     * @param <T>  The type of the list's elements
     *
     * @return A randomized list
     */
    public static <T> List<T> randomized(List<T> list) {
        return Items.randomized(list, new Random());
    }

    /**
     * Removes all the leading zeroes from the given array. This method does not modify the input array
     *
     * @param arr The array to remove leading zeroes from
     *
     * @return The modified array
     */
    public static byte[] removeLeadingZeroes(byte[] arr) {
        int c = 0;
        int len = 0;
        while (c < arr.length && arr[c] == 0) {
            len++;
            c++;
        }
        byte[] result = new byte[arr.length - len];
        System.arraycopy(arr, len, result, 0, result.length);
        return result;
    }

    /**
     * Removes all the leading zeroes from the given array. This method does not modify the input array
     *
     * @param arr The array to remove leading zeroes from
     *
     * @return The modified array
     */
    public static char[] removeLeadingZeroes(char[] arr) {
        int c = 0;
        int len = 0;
        while (c < arr.length && arr[c] == 0) {
            len++;
            c++;
        }
        char[] result = new char[arr.length - len];
        System.arraycopy(arr, len, result, 0, result.length);
        return result;
    }

    /**
     * Removes all the leading zeroes from the given array. This method does not modify the input array
     *
     * @param arr The array to remove leading zeroes from
     *
     * @return The modified array
     */
    public static short[] removeLeadingZeroes(short[] arr) {
        int c = 0;
        int len = 0;
        while (c < arr.length && arr[c] == 0) {
            len++;
            c++;
        }
        short[] result = new short[arr.length - len];
        System.arraycopy(arr, len, result, 0, result.length);
        return result;
    }

    /**
     * Removes all the leading zeroes from the given array. This method does not modify the input array
     *
     * @param arr The array to remove leading zeroes from
     *
     * @return The modified array
     */
    public static int[] removeLeadingZeroes(int[] arr) {
        int c = 0;
        int len = 0;
        while (c < arr.length && arr[c] == 0) {
            len++;
            c++;
        }
        int[] result = new int[arr.length - len];
        System.arraycopy(arr, len, result, 0, result.length);
        return result;
    }

    /**
     * Removes all the leading zeroes from the given array. This method does not modify the input array
     *
     * @param arr The array to remove leading zeroes from
     *
     * @return The modified array
     */
    public static long[] removeLeadingZeroes(long[] arr) {
        int c = 0;
        int len = 0;
        while (c < arr.length && arr[c] == 0) {
            len++;
            c++;
        }
        long[] result = new long[arr.length - len];
        System.arraycopy(arr, len, result, 0, result.length);
        return result;
    }

    /**
     * Removes all the leading zeroes from the given array. This method does not modify the input array
     *
     * @param arr The array to remove leading zeroes from
     *
     * @return The modified array
     */
    public static float[] removeLeadingZeroes(float[] arr) {
        int c = 0;
        int len = 0;
        while (c < arr.length && arr[c] == 0) {
            len++;
            c++;
        }
        float[] result = new float[arr.length - len];
        System.arraycopy(arr, len, result, 0, result.length);
        return result;
    }

    /**
     * Removes all the leading zeroes from the given array. This method does not modify the input array
     *
     * @param arr The array to remove leading zeroes from
     *
     * @return The modified array
     */
    public static double[] removeLeadingZeroes(double[] arr) {
        int c = 0;
        int len = 0;
        while (c < arr.length && arr[c] == 0) {
            len++;
            c++;
        }
        double[] result = new double[arr.length - len];
        System.arraycopy(arr, len, result, 0, result.length);
        return result;
    }

    /**
     * Removes all the leading zeroes from the given array. This method does not modify the input array
     *
     * @param arr The array to remove leading zeroes from
     *
     * @return The modified array
     */
    public static boolean[] removeLeadingZeroes(boolean[] arr) {
        int c = 0;
        int len = 0;
        while (c < arr.length && !arr[c]) {
            len++;
            c++;
        }
        boolean[] result = new boolean[arr.length - len];
        System.arraycopy(arr, len, result, 0, result.length);
        return result;
    }

    /**
     * Generates an array beginning with {@code a}, and populates it values incremented (or decremented) by {@code inc},
     * so that the final value of the array is as close to {@code b} as possible, without going past {@code b}. This
     * method recognizes directionalities of either left or right. If a &#60; b, the directionality is right, if a &#62;
     * b, the directionality is left. If the array is 'travelling' right, it means that values will be increasing by
     * {@code inc} from the beginning of the array to the end. If the array is travelling left, it means that values
     * will be decreasing by {@code inc} from the beginning of the array to the end. If the array is travelling right,
     * the last value will be the greatest value that satisfies the inequality {@code a + inc * x <= b}, and if the
     * array is travelling left, the last value will be the smallest value that satisfies the inequality {@code a + inc
     * * x >= b}
     *
     * @param a   The initial value of the array
     * @param b   The upper or lower bound of the range
     * @param inc The amount to increment or decrement values by
     *
     * @return The range array
     */
    public static byte[] range(byte a, byte b, byte inc) {
        if (inc == 0) {
            return new byte[]{a, b};
        } else {
            byte max = a < b ? b : a;
            byte min = a < b ? a : b;
            inc = (byte) (a < b ? inc : -inc);
            int size = (int) (Items.abs((int) (max - min) / inc) + 1);
            byte[] result = new byte[size];
            for (int i = 0; i < size; i++) {
                result[i] = (byte) ((inc * i) + a);
            }
            return result;
        }
    }

    /**
     * Generates a range between a and b, incrementing by 1
     *
     * @param a The initial value of the array
     * @param b The upper or lower bound of the range
     *
     * @return The range array
     *
     * @see Items#range(byte, byte, byte)
     */
    public static byte[] range(byte a, byte b) {
        return Items.range(a, b, (byte) 1);
    }

    /**
     * Generates an array beginning with {@code a}, and populates it values incremented (or decremented) by {@code inc},
     * so that the final value of the array is as close to {@code b} as possible, without going past {@code b}. This
     * method recognizes directionalities of either left or right. If a &#60; b, the directionality is right, if a &#62;
     * b, the directionality is left. If the array is 'travelling' right, it means that values will be increasing by
     * {@code inc} from the beginning of the array to the end. If the array is travelling left, it means that values
     * will be decreasing by {@code inc} from the beginning of the array to the end. If the array is travelling right,
     * the last value will be the greatest value that satisfies the inequality {@code a + inc * x <= b}, and if the
     * array is travelling left, the last value will be the smallest value that satisfies the inequality {@code a + inc
     * * x >= b}
     *
     * @param a   The initial value of the array
     * @param b   The upper or lower bound of the range
     * @param inc The amount to increment or decrement values by
     *
     * @return The range array
     */
    public static short[] range(short a, short b, short inc) {
        if (inc == 0) {
            return new short[]{a, b};
        } else {
            short max = a < b ? b : a;
            short min = a < b ? a : b;
            inc = (short) (a < b ? inc : -inc);
            int size = (int) (Items.abs((int) (max - min) / inc) + 1);
            short[] result = new short[size];
            for (int i = 0; i < size; i++) {
                result[i] = (short) ((inc * i) + a);
            }
            return result;
        }
    }

    /**
     * Generates a range between a and b, incrementing by 1
     *
     * @param a The initial value of the array
     * @param b The upper or lower bound of the range
     *
     * @return The range array
     *
     * @see Items#range(short, short, short)
     */
    public static short[] range(short a, short b) {
        return Items.range(a, b, (short) 1);
    }

    /**
     * Generates an array beginning with {@code a}, and populates it values incremented (or decremented) by {@code inc},
     * so that the final value of the array is as close to {@code b} as possible, without going past {@code b}. This
     * method recognizes directionalities of either left or right. If a &#60; b, the directionality is right, if a &#62;
     * b, the directionality is left. If the array is 'travelling' right, it means that values will be increasing by
     * {@code inc} from the beginning of the array to the end. If the array is travelling left, it means that values
     * will be decreasing by {@code inc} from the beginning of the array to the end. If the array is travelling right,
     * the last value will be the greatest value that satisfies the inequality {@code a + inc * x <= b}, and if the
     * array is travelling left, the last value will be the smallest value that satisfies the inequality {@code a + inc
     * * x >= b}
     *
     * @param a   The initial value of the array
     * @param b   The upper or lower bound of the range
     * @param inc The amount to increment or decrement values by
     *
     * @return The range array
     */
    public static char[] range(char a, char b, char inc) {
        if (inc == 0) {
            return new char[]{a, b};
        } else {
            char max = a < b ? b : a;
            char min = a < b ? a : b;
            inc = (char) (a < b ? inc : -inc);
            int size = (int) (Items.abs((int) (max - min) / inc) + 1);
            char[] result = new char[size];
            for (int i = 0; i < size; i++) {
                result[i] = (char) ((inc * i) + a);
            }
            return result;
        }
    }

    /**
     * Generates a range between a and b, incrementing by 1
     *
     * @param a The initial value of the array
     * @param b The upper or lower bound of the range
     *
     * @return The range array
     *
     * @see Items#range(char, char, char)
     */
    public static char[] range(char a, char b) {
        return Items.range(a, b, (char) 1);
    }

    /**
     * Generates an array beginning with {@code a}, and populates it values incremented (or decremented) by {@code inc},
     * so that the final value of the array is as close to {@code b} as possible, without going past {@code b}. This
     * method recognizes directionalities of either left or right. If a &#60; b, the directionality is right, if a &#62;
     * b, the directionality is left. If the array is 'travelling' right, it means that values will be increasing by
     * {@code inc} from the beginning of the array to the end. If the array is travelling left, it means that values
     * will be decreasing by {@code inc} from the beginning of the array to the end. If the array is travelling right,
     * the last value will be the greatest value that satisfies the inequality {@code a + inc * x <= b}, and if the
     * array is travelling left, the last value will be the smallest value that satisfies the inequality {@code a + inc
     * * x >= b}
     *
     * @param a   The initial value of the array
     * @param b   The upper or lower bound of the range
     * @param inc The amount to increment or decrement values by
     *
     * @return The range array
     */
    public static int[] range(int a, int b, int inc) {
        if (inc == 0) {
            return new int[]{a, b};
        } else {
            int max = a < b ? b : a;
            int min = a < b ? a : b;
            inc = (int) (a < b ? inc : -inc);
            int size = (int) (Items.abs((int) (max - min) / inc) + 1);
            int[] result = new int[size];
            for (int i = 0; i < size; i++) {
                result[i] = (int) ((inc * i) + a);
            }
            return result;
        }
    }

    /**
     * Generates a range between a and b, incrementing by 1
     *
     * @param a The initial value of the array
     * @param b The upper or lower bound of the range
     *
     * @return The range array
     *
     * @see Items#range(int, int, int)
     */
    public static int[] range(int a, int b) {
        return Items.range(a, b, 1);
    }

    /**
     * Generates an array beginning with {@code a}, and populates it values incremented (or decremented) by {@code inc},
     * so that the final value of the array is as close to {@code b} as possible, without going past {@code b}. This
     * method recognizes directionalities of either left or right. If a &#60; b, the directionality is right, if a &#62;
     * b, the directionality is left. If the array is 'travelling' right, it means that values will be increasing by
     * {@code inc} from the beginning of the array to the end. If the array is travelling left, it means that values
     * will be decreasing by {@code inc} from the beginning of the array to the end. If the array is travelling right,
     * the last value will be the greatest value that satisfies the inequality {@code a + inc * x <= b}, and if the
     * array is travelling left, the last value will be the smallest value that satisfies the inequality {@code a + inc
     * * x >= b}
     *
     * @param a   The initial value of the array
     * @param b   The upper or lower bound of the range
     * @param inc The amount to increment or decrement values by
     *
     * @return The range array
     */
    public static long[] range(long a, long b, long inc) {
        if (inc == 0) {
            return new long[]{a, b};
        } else {
            long max = a < b ? b : a;
            long min = a < b ? a : b;
            inc = (long) (a < b ? inc : -inc);
            int size = (int) (Items.abs((int) (max - min) / inc) + 1);
            long[] result = new long[size];
            for (int i = 0; i < size; i++) {
                result[i] = (long) ((inc * i) + a);
            }
            return result;
        }
    }

    /**
     * Generates a range between a and b, incrementing by 1
     *
     * @param a The initial value of the array
     * @param b The upper or lower bound of the range
     *
     * @return The range array
     *
     * @see Items#range(long, long, long)
     */
    public static long[] range(long a, long b) {
        return Items.range(a, b, 1);
    }

    /**
     * Generates an array beginning with {@code a}, and populates it values incremented (or decremented) by {@code inc},
     * so that the final value of the array is as close to {@code b} as possible, without going past {@code b}. This
     * method recognizes directionalities of either left or right. If a &#60; b, the directionality is right, if a &#62;
     * b, the directionality is left. If the array is 'travelling' right, it means that values will be increasing by
     * {@code inc} from the beginning of the array to the end. If the array is travelling left, it means that values
     * will be decreasing by {@code inc} from the beginning of the array to the end. If the array is travelling right,
     * the last value will be the greatest value that satisfies the inequality {@code a + inc * x <= b}, and if the
     * array is travelling left, the last value will be the smallest value that satisfies the inequality {@code a + inc
     * * x >= b}
     *
     * @param a   The initial value of the array
     * @param b   The upper or lower bound of the range
     * @param inc The amount to increment or decrement values by
     *
     * @return The range array
     */
    public static float[] range(float a, float b, float inc) {
        if (inc == 0) {
            return new float[]{a, b};
        } else {
            float max = a < b ? b : a;
            float min = a < b ? a : b;
            inc = (float) (a < b ? inc : -inc);
            int size = (int) (Items.abs((int) (max - min) / inc) + 1);
            float[] result = new float[size];
            for (int i = 0; i < size; i++) {
                result[i] = (float) ((inc * i) + a);
            }
            return result;
        }
    }

    /**
     * Generates a range between a and b, incrementing by 1
     *
     * @param a The initial value of the array
     * @param b The upper or lower bound of the range
     *
     * @return The range array
     *
     * @see Items#range(float, float, float)
     */
    public static float[] range(float a, float b) {
        return Items.range(a, b, 1);
    }

    /**
     * Generates an array beginning with {@code a}, and populates it values incremented (or decremented) by {@code inc},
     * so that the final value of the array is as close to {@code b} as possible, without going past {@code b}. This
     * method recognizes directionalities of either left or right. If a &#60; b, the directionality is right, if a &#62;
     * b, the directionality is left. If the array is 'travelling' right, it means that values will be increasing by
     * {@code inc} from the beginning of the array to the end. If the array is travelling left, it means that values
     * will be decreasing by {@code inc} from the beginning of the array to the end. If the array is travelling right,
     * the last value will be the greatest value that satisfies the inequality {@code a + inc * x <= b}, and if the
     * array is travelling left, the last value will be the smallest value that satisfies the inequality {@code a + inc
     * * x >= b}
     *
     * @param a   The initial value of the array
     * @param b   The upper or lower bound of the range
     * @param inc The amount to increment or decrement values by
     *
     * @return The range array
     */
    public static double[] range(double a, double b, double inc) {
        if (inc == 0) {
            return new double[]{a, b};
        } else {
            double max = a < b ? b : a;
            double min = a < b ? a : b;
            inc = (double) (a < b ? inc : -inc);
            int size = (int) (Items.abs((int) (max - min) / inc) + 1);
            double[] result = new double[size];
            for (int i = 0; i < size; i++) {
                result[i] = (double) ((inc * i) + a);
            }
            return result;
        }
    }

    /**
     * Generates a range between a and b, incrementing by 1
     *
     * @param a The initial value of the array
     * @param b The upper or lower bound of the range
     *
     * @return The range array
     *
     * @see Items#range(double, double, double)
     */
    public static double[] range(double a, double b) {
        return Items.range(a, b, 1);
    }

    private static int abs(int i) {
        return i < 0 ? -i : i;
    }

    private static float abs(float i) {
        return i < 0 ? -i : i;
    }

    private static double abs(double i) {
        return i < 0 ? -i : i;
    }

    public static void transform(Object[] array, Function<Object, Object> action) {
        Items.transform(array, action, new IdentityList<>());
    }

    private static void transform(Object[] array, Function<Object, Object> action, List<Object> seen) {
        if (!seen.contains(array)) {
            seen.add(array);
            for (int i = 0; i < array.length; i++) {
                Object obj = array[i];
                if (obj != null && obj.getClass().isArray() && obj.getClass().getComponentType().isAssignableFrom(Object[].class)) {
                    Items.transform((Object[]) obj, action, seen);
                } else {
                    array[i] = action.apply(obj);
                }
            }
        }
    }

    public static void traverse(Object[] array, Consumer<Object> action) {
        Items.traverse(array, action, new IdentityList<>());
    }

    private static void traverse(Object[] array, Consumer<Object> action, List<Object> seen) {
        if (!seen.contains(array)) {
            seen.add(array);
            for (Object obj : array) {
                if (obj != null && obj.getClass().isArray() && obj.getClass().getComponentType().isAssignableFrom(Object[].class)) {
                    Items.traverse((Object[]) obj, action, seen);
                } else {
                    action.accept(obj);
                }
            }
        }
    }


}
