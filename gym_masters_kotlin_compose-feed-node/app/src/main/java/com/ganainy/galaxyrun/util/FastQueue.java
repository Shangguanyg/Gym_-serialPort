package com.ganainy.galaxyrun.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The fast queue is backed by an arraylist. It is meant to
 * make `clear()` operations O(1) (as no objects are actually
 * removed--we just set the `insertIndex` to 0).
 *
 * It also implements `ProtectedQueue`, which we can pass to
 * GameObjects to add to (e.g., events, DrawInstructions, etc.)
 */

public class FastQueue<T> implements ProtectedQueue<T>, Iterable<T> {

    private int insertIndex = 0;
    private ArrayList<T> elements;

    public FastQueue() {
        elements = new ArrayList<>();
    }

    public int getSize() {
        return insertIndex;
    }

    public boolean isEmpty() {
        return insertIndex == 0;
    }

    @Override
    public void push(T t) {
        if (insertIndex < elements.size()) {
            elements.set(insertIndex, t);
            insertIndex++;
        } else {
            elements.add(t);
            insertIndex++;
        }
    }

    public T peek() throws IndexOutOfBoundsException {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("Queue is empty");
        } else {
            return elements.get(0);
        }
    }

//    public T pop() {
//        if (isEmpty()) {
//            throw new IndexOutOfBoundsException("Queue is empty");
//        } else {
//            T elem = peek();
//
//        }
//    }

    protected T get(int index) throws IndexOutOfBoundsException {
        if (index < getSize()) {
            return elements.get(index);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public void clear() {
        insertIndex = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new FastQueueIterator(this);
    }

    /*
    Iterator implementation.
     */
    class FastQueueIterator implements Iterator<T> {
        private FastQueue<T> queue;
        private int currIndex;

        FastQueueIterator(FastQueue<T> queue) {
            this.queue = queue;
            currIndex = -1;
        }

        public boolean hasNext() {
            return currIndex + 1 < queue.getSize();
        }

        public T next() throws NoSuchElementException {
            if (hasNext()) {
                currIndex++;
                return queue.get(currIndex);
            }
            else {
                throw new NoSuchElementException();
            }
        }
    }
}