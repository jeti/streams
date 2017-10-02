package io.jeti.streams;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class DataPacketStreamReaderSink implements StreamReader<DataInputStream, Object> {

    private final Sink                    sink;
    private final Map<String, DataPacket> dataPacketMap;
    private final boolean                 buffered;
    private static final boolean          bufferedDefault = true;

    public DataPacketStreamReaderSink(Sink sink, Map<String, DataPacket> dataPacketMap) {
        this(sink, dataPacketMap, bufferedDefault);
    }

    public DataPacketStreamReaderSink(Sink sink, Map<String, DataPacket> dataPacketMap,
            boolean buffered) {
        if (sink == null) {
            throw new NullPointerException("The sink cannot be null");
        }
        this.sink = sink;
        this.dataPacketMap = dataPacketMap;
        this.buffered = buffered;
    }

    @Override
    public DataInputStream preLoop(InputStream inputStream) throws IOException {
        if (buffered) {
            return new DataInputStream(new BufferedInputStream(inputStream));
        } else {
            return new DataInputStream(inputStream);
        }
    }

    @Override
    public Object readOne(DataInputStream stream) throws IOException, ClassNotFoundException {
        String className = stream.readUTF();
        DataPacket dataPacket = dataPacketMap.get(className);
        if (dataPacket != null) {
            Object obj = dataPacket.read(stream);
            sink.process(obj);
            return obj;
        } else {
            throw new ClassNotFoundException("Could not find a class with the name: " + className);
        }
    }
}
