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

import com.gmail.socraticphoenix.collect.coupling.Pair;
import com.gmail.socraticphoenix.mirror.Reflections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Context {
    private Map<String, Object> map;
    private Set<Map.Entry<String, Object>> entrySet;
    private Set<Map.Entry<String, Object>> reversedEntrySet;

    public Context() {
        this.map = new LinkedHashMap<>();
        this.entrySet = this.map.entrySet();
        this.reversedEntrySet = Items.reversed(this.entrySet, LinkedHashSet::new);
    }

    private Context(Map<String, Object> values) {
        this.map = values;
        this.entrySet = values.entrySet();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public Optional<Object> get(String key) {
        return Optional.ofNullable(this.map.get(key));
    }

    public <T> Optional<T> get(String key, Class<T> clazz) {
        clazz = Reflections.boxingType(clazz);
        Object object = this.map.get(key);
        if (object != null && clazz.isInstance(object)) {
            return Optional.of(clazz.cast(object));
        }

        return Optional.empty();
    }

    public <T> Optional<T> get(String key, Class<T> clazz, Predicate<T> condition) {
        clazz = Reflections.boxingType(clazz);
        Object object = this.map.get(key);
        if (object != null && clazz.isInstance(object)) {
            T val = clazz.cast(object);
            if (condition.test(val)) {
                return Optional.of(val);
            }
        }

        return Optional.empty();
    }

    public <T> Optional<T> firstSimple(Class<T> clazz) {
        clazz = Reflections.boxingType(clazz);
        for (Map.Entry<String, Object> entry : this.entrySet) {
            if (clazz.isInstance(entry.getValue())) {
                return Optional.of(clazz.cast(entry.getValue()));
            }
        }

        return Optional.empty();
    }

    public <T> Optional<Pair<String, T>> first(Class<T> clazz) {
        clazz = Reflections.boxingType(clazz);
        for (Map.Entry<String, Object> entry : this.entrySet) {
            if (clazz.isInstance(entry.getValue())) {
                return Optional.of(Pair.of(entry.getKey(), clazz.cast(entry.getValue())));
            }
        }

        return Optional.empty();
    }

    public <T> Optional<T> lastSimple(Class<T> clazz) {
        clazz = Reflections.boxingType(clazz);
        for (Map.Entry<String, Object> entry : this.reversedEntrySet) {
            if (clazz.isInstance(entry.getValue())) {
                return Optional.of(clazz.cast(entry.getValue()));
            }
        }

        return Optional.empty();
    }

    public <T> Optional<Pair<String, T>> last(Class<T> clazz) {
        clazz = Reflections.boxingType(clazz);
        for (Map.Entry<String, Object> entry : this.reversedEntrySet) {
            if (clazz.isInstance(entry.getValue())) {
                return Optional.of(Pair.of(entry.getKey(), clazz.cast(entry.getValue())));
            }
        }

        return Optional.empty();
    }

    public <T> List<T> allSimple(Class<T> clazz) {
        clazz = Reflections.boxingType(clazz);
        List<T> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : this.entrySet) {
            if (clazz.isInstance(entry.getValue())) {
                list.add(clazz.cast(entry.getValue()));
            }
        }

        return list;
    }

    public <T> List<Pair<String, T>> all(Class<T> clazz) {
        clazz = Reflections.boxingType(clazz);
        List<Pair<String, T>> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : this.entrySet) {
            if (clazz.isInstance(entry.getValue())) {
                list.add(Pair.of(entry.getKey(), clazz.cast(entry.getValue())));
            }
        }

        return list;
    }

    public <T> Optional<T> firstSimple(Class<T> clazz, BiPredicate<String, T> condition) {
        clazz = Reflections.boxingType(clazz);
        for (Map.Entry<String, Object> entry : this.entrySet) {
            if (clazz.isInstance(entry.getValue())) {
                T val = clazz.cast(entry.getValue());
                if (condition.test(entry.getKey(), val)) {
                    return Optional.of(val);
                }
            }
        }

        return Optional.empty();
    }

    public <T> Optional<Pair<String, T>> first(Class<T> clazz, BiPredicate<String, T> condition) {
        clazz = Reflections.boxingType(clazz);
        for (Map.Entry<String, Object> entry : this.entrySet) {
            if (clazz.isInstance(entry.getValue())) {
                T val = clazz.cast(entry.getValue());
                if (condition.test(entry.getKey(), val)) {
                    return Optional.of(Pair.of(entry.getKey(), val));
                }
            }
        }

        return Optional.empty();
    }

    public <T> Optional<T> lastSimple(Class<T> clazz, BiPredicate<String, T> condition) {
        clazz = Reflections.boxingType(clazz);
        for (Map.Entry<String, Object> entry : this.reversedEntrySet) {
            if (clazz.isInstance(entry.getValue())) {
                T val = clazz.cast(entry.getValue());
                if (condition.test(entry.getKey(), val)) {
                    return Optional.of(val);
                }
            }
        }

        return Optional.empty();
    }

    public <T> Optional<Pair<String, T>> last(Class<T> clazz, BiPredicate<String, T> condition) {
        clazz = Reflections.boxingType(clazz);
        for (Map.Entry<String, Object> entry : this.reversedEntrySet) {
            if (clazz.isInstance(entry.getValue())) {
                T val = clazz.cast(entry.getValue());
                if (condition.test(entry.getKey(), val)) {
                    return Optional.of(Pair.of(entry.getKey(), val));
                }
            }
        }

        return Optional.empty();
    }

    public <T> List<T> allSimple(Class<T> clazz, BiPredicate<String, T> condition) {
        clazz = Reflections.boxingType(clazz);
        List<T> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : this.entrySet) {
            if (clazz.isInstance(entry.getValue())) {
                T val = clazz.cast(entry.getValue());
                if (condition.test(entry.getKey(), val)) {
                    list.add(val);
                }
            }
        }

        return list;
    }

    public <T> List<Pair<String, T>> all(Class<T> clazz, BiPredicate<String, T> condition) {
        clazz = Reflections.boxingType(clazz);
        List<Pair<String, T>> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : this.entrySet) {
            if (clazz.isInstance(entry.getValue())) {
                T val = clazz.cast(entry.getValue());
                if (condition.test(entry.getKey(), val)) {
                    list.add(Pair.of(entry.getKey(), val));
                }
            }
        }

        return list;
    }

    public Map<String, Object> content() {
        return new LinkedHashMap<>(this.map);
    }

    public Stack<Pair<String, Object>> stack() {
        Stack<Pair<String, Object>> stack = new Stack<>();
        for (Map.Entry<String, Object> entry : this.reversedEntrySet) {
            stack.push(Pair.of(entry));
        }
        return stack;
    }

    public static class Builder {
        private Map<String, Object> map;

        public Builder() {
            this.map = new LinkedHashMap<>();
        }

        public Builder put(String key, Object object) {
            this.map.put(key, object);
            return this;
        }

        public Context build() {
            Context context = new Context(this.map);
            this.reset();
            return context;
        }

        public Builder reset() {
            this.map = new LinkedHashMap<>();
            return this;
        }
    }

}
