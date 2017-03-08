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

import com.gmail.socraticphoenix.mirror.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Predicate;

public class Chain {
    private Object[] arr;

    public Chain(Object[] arr) {
        this.arr = arr;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isEmpty() {
        return this.arr.length == 0;
    }

    public Optional<Object> first() {
        return this.isEmpty() ? Optional.empty() : Optional.of(this.arr[0]);
    }

    public Optional<Object> last() {
        return this.isEmpty() ? Optional.empty() : Optional.of(this.arr[this.arr.length - 1]);
    }

    public <T> Optional<T> first(Class<T> clazz) {
        clazz = Reflections.boxingType(clazz);
        for (Object object : this.arr) {
            if (clazz.isInstance(object)) {
                return Optional.of(clazz.cast(object));
            }
        }

        return Optional.empty();
    }

    public <T> Optional<T> last(Class<T> clazz) {
        clazz = Reflections.boxingType(clazz);
        for (int i = this.arr.length - 1; i >= 0; i--) {
            Object object = this.arr[i];
            if (clazz.isInstance(object)) {
                return Optional.of(clazz.cast(object));
            }
        }

        return Optional.empty();
    }

    public <T> List<T> all(Class<T> clazz) {
        clazz = Reflections.boxingType(clazz);
        List<T> list = new ArrayList<>();
        for (Object object : this.arr) {
            if (clazz.isInstance(object)) {
                list.add(clazz.cast(object));
            }
        }
        return list;
    }

    public <T> Optional<T> first(Class<T> clazz, Predicate<T> condition) {
        clazz = Reflections.boxingType(clazz);
        for (Object object : this.arr) {
            if (clazz.isInstance(object)) {
                T val = clazz.cast(object);
                if (condition.test(val)) {
                    return Optional.of(val);
                }
            }
        }

        return Optional.empty();
    }

    public <T> Optional<T> last(Class<T> clazz, Predicate<T> condition) {
        clazz = Reflections.boxingType(clazz);
        for (int i = this.arr.length - 1; i >= 0; i--) {
            Object object = this.arr[i];
            if (clazz.isInstance(object)) {
                T val = clazz.cast(object);
                if (condition.test(val)) {
                    return Optional.of(val);
                }
            }
        }

        return Optional.empty();
    }

    public <T> List<T> all(Class<T> clazz, Predicate<T> condition) {
        clazz = Reflections.boxingType(clazz);
        List<T> list = new ArrayList<>();
        for (Object object : this.arr) {
            if (clazz.isInstance(object)) {
                T val = clazz.cast(object);
                if (condition.test(val)) {
                    list.add(val);
                }
            }
        }

        return list;
    }

    public Object[] content() {
        return this.arr.clone();
    }

    public Stack<Object> stack() {
        Stack<Object> stack = new Stack<>();
        for (int i = this.arr.length - 1; i >= 0; i--) {
            stack.push(this.arr[i]);
        }
        return stack;
    }

    public static class Builder {
        private List<Object> objects;

        public Builder() {
            this.objects = new ArrayList<>();
        }

        public Builder add(Object object) {
            this.objects.add(object);
            return this;
        }

        public Chain build() {
            Chain chain = new Chain(this.objects.toArray());
            this.reset();
            return chain;
        }

        public Builder reset() {
            this.objects = new ArrayList<>();
            return this;
        }

    }

}
