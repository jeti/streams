package io.jeti.streams;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class SerializableStreamReader implements StreamReader<ObjectInputStream, Serializable> {

    private final boolean buffered;
    private static final boolean bufferedDefault = true;

    public SerializableStreamReader() {
        this(bufferedDefault);
    }

    public SerializableStreamReader(boolean buffered) {
        this.buffered = buffered;
    }

    @Override
    public ObjectInputStream preLoop(InputStream inputStream) throws IOException {
        if (buffered) {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            return new ObjectInputStream(bis);
        } else {
            return new ObjectInputStream(inputStream);
        }
    }

    @Override
    public Serializable readOne(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        return (Serializable) stream.readObject();
    }
}