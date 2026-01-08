package com.ganainy.galaxyrun.util;

/**
 * A Queue interface that only allows pushing to the queue.
 */

public interface ProtectedQueue<T> {
    void push(T t);
}
