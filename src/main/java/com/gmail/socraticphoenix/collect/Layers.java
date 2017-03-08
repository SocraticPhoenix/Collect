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
import java.util.List;

public class Layers<T> {
    private List<List<T>> layers;

    public Layers() {
        this.layers = new ArrayList<>();
    }

    public void add(T val, int layer) {
        this.ensureExistence(layer);
        this.layers.get(layer).add(val);
    }

    public T get(int layer, int index) {
        return this.layers.get(layer).get(index);
    }

    public List<T> get(int layer) {
        this.ensureExistence(layer);
        return this.layers.get(layer);
    }

    public List<List<T>> stacks() {
        List<List<T>> result = new ArrayList<>();
        for(T val : this.get(0)) {
            result.add(Items.buildList(val));
        }

        for(int i = 1; i < this.layers.size(); i++) {
            List<T> layer = this.layers.get(i);
            List<List<T>> newResult = new ArrayList<>();
            for(List<T> resultPiece : result) {
                for(T val : layer) {
                    List<T> newPiece = new ArrayList<>();
                    newPiece.addAll(resultPiece);
                    newPiece.add(val);
                    newResult.add(newPiece);
                }
            }
            result = newResult;
        }
        return result;
    }

    private void ensureExistence(int layer) {
        while (this.layers.size() <= layer) {
            this.layers.add(new ArrayList<>());
        }
    }

}
