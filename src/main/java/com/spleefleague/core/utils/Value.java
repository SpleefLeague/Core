package com.spleefleague.core.utils;

/**
 * Created by Josh on 05/02/2016.
 */
public class Value<T> {

    private T value;

    public Value() {
    }

    public Value(T initialValue) {
        this.value = initialValue;
    }

    public T get() {
        return this.value;
    }

    public void set(T newValue) {
        this.value = newValue;
    }

}
