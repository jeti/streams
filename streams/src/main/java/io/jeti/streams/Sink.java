package io.jeti.streams;

public interface Sink<T> {

    void process(T obj);
}
