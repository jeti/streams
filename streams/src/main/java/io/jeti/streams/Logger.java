package io.jeti.streams;

public interface Logger extends Sink<String> {

    default void init() {
    }

    void log(String message);

    @Override
    default void process(String message) {
        log(message);
    }
}
