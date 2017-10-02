package io.jeti.streams;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A {@link io.jeti.streams.Sink} is a generic consumer which defines the
 * {@link Sink#process(Object)} method. If you call the
 * {@link Sink#process(Object)} method, it will execute on whichever
 * {@link Thread} it is called, blocking execution. Instead, it may be
 * preferable to offload processing to a separate {@link Thread} or
 * {@link Thread}s, and the {@link Sink}s in this class do exactly that.
 * Specifically, whenever the {@link Sink#process(Object)} method is called on a
 * {@link Sink} in this class, it will place the object in a queue (like a
 * {@link BlockingQueue}). A separate {@link Thread} (or Threads) will then pull
 * off the queued objects on a FIFO basis, and process them using the specified
 * {@link Sink}.
 */
public class Sinks {

    private static class Consumer implements Runnable {

        private final Sink sink;
        private final BlockingQueue queue;

        public Consumer(Sink sink, BlockingQueue queue) {
            this.sink = sink;
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    sink.process(queue.take());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> SingleThreadedSink newSingleThreadedSink(Sink<T> sink) {
        return new SingleThreadedSink<>(sink);
    }

    public static class SingleThreadedSink<T> implements Sink<T> {

        private final BlockingQueue<T> queue;
        private final ExecutorService  executorService;

        private SingleThreadedSink(Sink<T> sink) {
            this.queue = new LinkedBlockingQueue<>();
            this.executorService = Executors.newSingleThreadExecutor();
            this.executorService.execute(new Consumer(sink, queue));
        }

        public void stop() {
            executorService.shutdownNow();
        }

        @Override
        public void process(T obj) {
            queue.add(obj);
        }
    }

    public static <T> MultiThreadedSink newMultiThreadedSink(Sink<T> sink, int threads) {
        return new MultiThreadedSink<>(sink, threads);
    }

    public static class MultiThreadedSink<T> implements Sink<T> {

        private final BlockingQueue<T> queue;
        private final ExecutorService  executorService;

        private MultiThreadedSink(Sink<T> sink, int threads) {
            this.queue = new LinkedBlockingQueue<>();
            this.executorService = Executors.newFixedThreadPool(threads);
            for (int i = 0; i < threads; i++) {
                this.executorService.execute(new Consumer(sink, queue));
            }
        }

        public void stop() {
            executorService.shutdownNow();
        }

        @Override
        public void process(T obj) {
            queue.add(obj);
        }
    }
}