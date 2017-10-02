package io.jeti.streams;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class SerializableStreamWriter implements StreamWriter<ObjectOutputStream, Serializable> {

    @Override
    public ObjectOutputStream preLoop(OutputStream outputStream) throws IOException {
        return new ObjectOutputStream(outputStream);
    }

    @Override
    public void writeOne(ObjectOutputStream stream, Serializable objectToWrite) throws IOException {
        stream.writeObject(objectToWrite);

    }
}
