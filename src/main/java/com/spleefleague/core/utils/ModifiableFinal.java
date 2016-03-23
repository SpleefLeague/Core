package com.spleefleague.core.utils;

/**
 * Created by Josh on 05/02/2016.
 */
public class ModifiableFinal<T> {

    private T value;

    public ModifiableFinal() {
    }

    public ModifiableFinal(T initialValue) {
        this.value = initialValue;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T newValue) {
        this.value = newValue;
    }

}
