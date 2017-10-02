package io.jeti.streams;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 * A {@link StreamWriter} is an object that is responsible for writing objects to an
 * {@link OutputStream}. However, unlike the {@link java.io.ObjectOutputStream}, which only writes
 * {@link java.io.Serializable} objects, this interface is meant as a higher level
 * abstraction for writing objects to an {@link OutputStream}.
 * </p>
 * Specifically, a {@link StreamWriter} defines five methods:
 * {@link #preLoop(OutputStream)}, {@link #writeOne(Object, Object)}, {@link #stop(Thread)},
 * {@link #preClose(OutputStream, Object)}, and {@link #closed()}, which will typically be used like
 * in the following code snippet:
 * <pre>
 * void writeAll( OutputStream outputStream, StreamWriter writer ){
 *      S modifiedStream = null;
 *      try{
 *          modifiedStream = reader.preLoop(inputStream);
 *          while(true){
 *              O obj = reader.readOne(modifiedStream);
 *              // Handle the read object...
 *          }
 *      } catch ( Exception e ){
 *          e.printStackTrace();
 *      } finally {
 *          reader.preClose( inputStream, modifiedStream );
 *          if (inputStream != null){
 *              try {
 *                  inputStream.close();
 *              } catch (Exception e){
 *                  e.printStackTrace();
 *              }
 *          }
 *          reader.closed();
 *      }
 * }</pre>
 * In fact, this is precisely the implementation used by the {@link StreamWriterManager}.
 * From this snippet, it should be clear that a {@link StreamWriter} should NOT handle looping or
 * teardown of the {@link OutputStream} itself; the class which uses a {@link StreamWriter}
 * (such as the {@link StreamWriterManager}) is responsible for that.
 * <p>
 * Another important feature of a {@link StreamWriter} is that it
 * must define how it can be stopped. Since this is typically accomplished by interrupting the
 * {@link Thread} on which the stream writing occurs, the {@link #stop(Thread)} method is
 * given the {@link Thread} on which the stream writing occurs as an input, and the default
 * implementation is simply to interrupt this {@link Thread}.
 * </p>
 */
public interface StreamWriter<S, O> {

    /**
     * Perform some setup steps before looping, such as modifying the raw
     * {@link OutputStream}. For instance, you may want to wrap the {@link OutputStream} in
     * a {@link java.io.BufferedOutputStream}. The output of this function will be passed to the
     * {@link #writeOne(Object, Object)}  method in each loop iteration.
     *
     * @param outputStream: The {@link OutputStream} that we are supposed to write to.
     */
    S preLoop(OutputStream outputStream) throws IOException;

    /**
     * Write a single object to the object (probably a stream) returned by the
     * {@link #preLoop(OutputStream)} method.
     *
     * @param stream: The modified stream which is returned from {@link #preLoop(OutputStream)}.
     */
    void writeOne(S stream, O objectToWrite) throws IOException;

    /**
     * Stop writing to the {@link OutputStream}. Typically this will be done by interrupting
     * the {@link Thread} on which the {@link StreamWriter} is running.
     *
     * @param thread: The {@link Thread} on which the {@link StreamWriter} is running.
     */
    default void stop(Thread thread) {
        thread.interrupt();
    }

    /**
     * Start releasing resources before the {@link OutputStream} is closed.
     */
    default void preClose(OutputStream outputStream, S stream) {
    }

    /**
     * Perform final cleanup now that the {@link OutputStream} is closed.
     */
    default void closed() {
    }
}