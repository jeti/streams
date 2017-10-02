package io.jeti.streams;

import java.io.InputStream;

/**
 * A {@link StreamReaderManager} defines two methods:
 * {@link #start(InputStream, StreamReader, Sink)} and #stop(), where the
 * "start" method creates, starts, and returns a {@link StreamReaderManager}.
 * The {@link StreamReaderManager} will keep trying to use the
 * {@link StreamReader} to read from the specified {@link InputStream} until it
 * is told to {@link #stop()} or an {@link Exception} occurs.
 */
public class StreamReaderManager<S, O> {

    private final StreamReader<S, O> streamer;
    private final Thread             thread;

    private StreamReaderManager(InputStream stream, StreamReader<S, O> streamer, Sink<O> sink) {
        this.streamer = streamer;
        this.thread = new Thread(() -> {
            S modifiedStream = null;
            try {
                modifiedStream = streamer.preLoop(stream);
                while (true) {
                    sink.process(streamer.readOne(modifiedStream));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                streamer.preClose(stream, modifiedStream);
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                streamer.closed();
            }
        });
    }

    /**
     * Create, start, and return a {@link StreamReaderManager}, which will read
     * from the specified {@link InputStream} until {@link #stop()} is called,
     * or an {@link Exception} occurs.
     */
    public static <S, O> StreamReaderManager start(InputStream inputStream,
            StreamReader<S, O> reader, Sink<O> sink) {
        StreamReaderManager<S, O> manager = new StreamReaderManager<>(inputStream, reader, sink);
        manager.thread.start();
        return manager;
    }

    /**
     * Stop reading from the {@link InputStream}. A {@link StreamReader} can
     * define its own stopping behavior, but typically this call is equivalent
     * to simply interrupting the reading thread.
     */
    public void stop() {
        streamer.stop(thread);
    }
}