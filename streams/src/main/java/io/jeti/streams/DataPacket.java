package io.jeti.streams;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * An interface to be implemented from classes that can be written to a
 * {@link DataOutputStream} (via {@link #write(DataOutputStream)}) and read from
 * a {@link DataInputStream} (via {@link #read(DataInputStream)}). This
 * interface also implements {@link Serializable} so that such objects can also
 * be serialized that way, if desired.
 */
public interface DataPacket<Type> extends Serializable {

    /**
     * Write the object fields to a {@link DataOutputStream}. You should write
     * all of the values that you will need to construct the object in the
     * {@link #read(DataInputStream)} method.
     */
    void write(DataOutputStream stream) throws IOException;

    /**
     * Read the object from a {@link DataInputStream}. You should assume
     * that the input stream was generated by the
     * {@link #write(DataOutputStream)} method.
     */
    Type read(DataInputStream stream) throws IOException;
}