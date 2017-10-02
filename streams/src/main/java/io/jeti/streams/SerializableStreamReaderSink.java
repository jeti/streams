package io.jeti.streams;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class SerializableStreamReaderSink implements StreamReader<ObjectInputStream, Serializable> {

    private final Sink sink;
    private final boolean        buffered;
    private static final boolean bufferedDefault = true;

    public SerializableStreamReaderSink(Sink sink) {
        this(sink, bufferedDefault);
    }

    public SerializableStreamReaderSink(Sink sink, boolean buffered) {
        if (sink == null) {
            throw new NullPointerException("The sink cannot be null");
        }
        this.sink = sink;
        this.buffered = buffered;
    }

    @Override
    public ObjectInputStream preLoop(InputStream inputStream) throws IOException {
        if (buffered) {
            return new ObjectInputStream(new BufferedInputStream(inputStream));
        } else {
            return new ObjectInputStream(inputStream);
        }
    }

    @Override
    public Serializable readOne(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        Serializable obj = (Serializable) stream.readObject();
        sink.process(obj);
        return obj;
    }
}