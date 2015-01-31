/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.utils.collections;

import java.util.Iterator;

/**
 *
 * @author Jonas
 */
public class FixedSizeList<T> implements Iterable<T> {

    private final T[] array;
    private int currentPos = 0;

    public FixedSizeList(int size) {
        array = (T[]) new Object[size];
    }

    public void add(T e) {
        if(currentPos == array.length) {
            System.arraycopy(array, 1, array, 0, currentPos - 1);
            array[currentPos - 1] = e;
        }
        else {
            array[currentPos] = e;
            currentPos++;
        }
    }
    
    public T get(int index) {
        if(currentPos >= index)
            return array[index];
        return null;
    }
    
    public void remove(int index) {
        if(currentPos >= index) {
            if(currentPos == array.length) {
                currentPos--;
                array[currentPos] = null;
                System.arraycopy(array, index + 1, array, index, currentPos - index);
            }
            else {
                array[currentPos] = null;
                System.arraycopy(array, index + 1, array, index, currentPos - index);
                currentPos--;
            }
        }
    }
    
    public void remove(T element) {
        remove(getIndex(element));
    }
    
    public int getIndex(T element) {
        for(int i = 0; i < currentPos; i++) {
            if(element.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }
    
    public void call(int index) {
        if(currentPos >= index) {  
            if(currentPos == array.length) { 
                T temp = get(index);
                System.arraycopy(array, index + 1, array, index, currentPos - index - 1);
                array[currentPos - 1] = temp;
            }
            else {
                T temp = get(index);
                System.arraycopy(array, index + 1, array, index, currentPos - index);
                array[currentPos - 1] = temp;
            }
        }
    }
    
    public void call(T element) {
        call(getIndex(element));
    }

    @Override
    public Iterator<T> iterator() {
        T[] current = (T[]) new Object[currentPos];
        System.arraycopy(array, 0, current, 0, currentPos);
        return new FixedSizeListIterator(current);
    }

    private class FixedSizeListIterator implements Iterator<T> {

        private final T[] array;
        private int currentPos = 0;

        protected FixedSizeListIterator(T[] array) {
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return array.length > currentPos;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                return null;
            }
            return array[currentPos++];
        }
    }
}
