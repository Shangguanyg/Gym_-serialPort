package com.ganainy.galaxyrun.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The CircularBuffer a.k.a. "RingBuffer" is a fixed-size stack that
 * "circles around" and overwrites elements in FIFO order once full.
 *
 * For example:
 *
 * CircularBuffer<Integer> buffer = new CircularBuffer<>(3);
 * buffer.push(0)  // [0]
 * buffer.push(1)  // [0, 1]
 * buffer.push(2)  // [0, 1, 2]
 * buffer.push(3)  // [3, 1, 2]
 * buffer.push(4)  // [3, 4, 2]
 * buffer.popFront()  // [3, 4]
 * buffer.popFront()  // [4]
 * buffer.popFront()  // []
 *
 * You can also use the Iterable interface to conveniently iterate over
 * the elements in a buffer, e.g.:
 *
 * sum = 0;
 * for (Integer elem : buffer) {
 *     sum += elem;
 * }
 */
public class CircularBuffer<T> implements Iterable<T> {
    // ArrayList backing the buffer.
    private final ArrayList<T> buff;
    // Capacity of the buffer. This is set at construction and cannot be
    // changed.
    private final int capacity;
    // Index of first element (inclusive).
    private int start;
    // The number of elements currently stored in the buffer.
    private int size;

    // Constructs a CircularBuffer with the fixed `capacity`.
    // `capacity` must be greater than zero.
    public CircularBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than 0; was " + capacity);
        }
        this.capacity = capacity;
        // Initialize `buff` with the proper capacity. Note that `buff` will start
        // with zero elements; we must add elements manually to grow to `capacity`.
        buff = new ArrayList<>(this.capacity);
    }

    public int getCapacity() {
        return capacity;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public void push(T elem) {
        if (isFull()) {
            // The next element will "wrap around", overwriting `start`.
            buff.set(start, elem);
            start = (start + 1) % capacity;
        } else {
            int writeIndex = (start + size) % capacity;
            // Handle the possibility that the ArrayList hasn't reached its full size yet.
            if (buff.size() < capacity) {
                buff.add(elem);
            } else {
                buff.set(writeIndex, elem);
            }
            ++size;
        }
    }

    public T popFront() throws IndexOutOfBoundsException {
        if (size == 0) {
            throw new IndexOutOfBoundsException("Can't pop an empty buffer.");
        }
        T elem = buff.get(start);
        start = (start + 1) % capacity;
        --size;
        return elem;
    }

    public T peekFront() throws IndexOutOfBoundsException {
        if (size == 0) {
            throw new IndexOutOfBoundsException("Can't peek an empty buffer.");
        }
        return buff.get(start);
    }

    public T get(int index) throws IndexOutOfBoundsException {
        if (index >= size) {
            throw new IndexOutOfBoundsException("index " + index + " out of bounds for a buffer " +
                    "of size " + size);
        }
        return buff.get((start + index) % capacity);
    }

    @Override
    public Iterator<T> iterator() {
        return new CircularBufferIterator(this);
    }

    class CircularBufferIterator implements Iterator<T> {
        private final CircularBuffer<T> buffer;
        private int currIndex;

        CircularBufferIterator(CircularBuffer<T> buffer) {
            this.buffer = buffer;
            currIndex = -1;
        }

        public boolean hasNext() {
            return currIndex + 1 < buffer.size;
        }

        public T next() throws NoSuchElementException {
            if (hasNext()) {
                currIndex++;
                return buffer.get(currIndex);
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
