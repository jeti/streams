package io.jeti.streams;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DataPacketStreamWriter implements StreamWriter<DataOutputStream, DataPacket> {

    @Override
    public DataOutputStream preLoop(OutputStream outputStream) throws IOException {
        return new DataOutputStream(outputStream);
    }

    @Override
    public void writeOne(DataOutputStream stream, DataPacket objectToWrite) throws IOException {
        stream.writeUTF(objectToWrite.getClass().getName());
        objectToWrite.write(stream);
    }
}

