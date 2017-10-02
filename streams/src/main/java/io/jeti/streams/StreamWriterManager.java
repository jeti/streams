package io.jeti.streams;

import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

/**
 * A {@link StreamWriterManager} defines two methods:
 * {@link #start(OutputStream, StreamWriter, BlockingQueue)} and {@link #stop()}
 * , where the "start" method creates, starts, and returns a
 * {@link StreamWriterManager}. To write objects with the {@link StreamWriter},
 * simply add them to the {@link BlockingQueue}. An internal thread in the
 * {@link StreamWriterManager} will then take objects off of the queue as fast
 * as possible and pass them to the {@link StreamWriter}. The
 * {@link StreamWriterManager} will keep using the {@link StreamWriter} to write
 * to the specified {@link OutputStream} until it is told to {@link #stop()} or
 * an {@link Exception} occurs.
 */
public class StreamWriterManager<S, O> {

    private final StreamWriter<S, O> streamer;
    private final Thread             thread;

    private StreamWriterManager(OutputStream stream, StreamWriter<S, O> streamer,
            BlockingQueue<O> queue) {
        this.streamer = streamer;
        this.thread = new Thread(() -> {
            S modifiedStream = null;
            try {
                modifiedStream = streamer.preLoop(stream);
                while (true) {
                    streamer.writeOne(modifiedStream, queue.take());
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
     * Create, start, and return a {@link StreamWriterManager}, which will take
     * objects from the {@link BlockingQueue} until {@link #stop()} is called,
     * or an {@link Exception} occurs.
     */
    public static <S, O> StreamWriterManager start(OutputStream outputStream,
            StreamWriter<S, O> writer, BlockingQueue<O> queue) {
        StreamWriterManager<S, O> manager = new StreamWriterManager<>(outputStream, writer, queue);
        manager.thread.start();
        return manager;
    }

    /**
     * Stop writing to the {@link OutputStream}. A {@link StreamWriter} can
     * define its own stopping behavior, but typically this call is equivalent
     * to simply interrupting the writing thread.
     */
    public void stop() {
        streamer.stop(thread);
    }
}