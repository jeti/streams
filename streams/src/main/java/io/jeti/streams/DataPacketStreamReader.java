package io.jeti.streams;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class DataPacketStreamReader implements StreamReader<DataInputStream, Object> {

    private final Map<String, DataPacket> dataPacketMap;
    private static final boolean bufferedDefault = true;
    private final boolean buffered;

    public DataPacketStreamReader(Map<String, DataPacket> dataPacketMap) {
        this(dataPacketMap, bufferedDefault);
    }

    public DataPacketStreamReader(Map<String, DataPacket> dataPacketMap, boolean buffered) {
        this.dataPacketMap = dataPacketMap;
        this.buffered = buffered;
    }

    @Override
    public DataInputStream preLoop(InputStream inputStream) throws IOException {
        if (buffered) {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            return new DataInputStream(bis);
        } else {
            return new DataInputStream(inputStream);
        }
    }

    @Override
    public Object readOne(DataInputStream stream) throws IOException, ClassNotFoundException {
        String className = stream.readUTF();
        DataPacket dataPacket = dataPacketMap.get(className);
        if (dataPacket != null) {
            return dataPacket.read(stream);
        } else {
            throw new ClassNotFoundException("Could not find a class with the name: " + className);
        }
    }
}
