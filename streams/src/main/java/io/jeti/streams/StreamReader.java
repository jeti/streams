package io.jeti.streams;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * A {@link StreamReader} is an object that is responsible for reading objects from an
 * {@link InputStream}. However, unlike the {@link java.io.ObjectInputStream}, which only reads
 * {@link java.io.Serializable} objects, this interface is meant as a higher level
 * abstraction for reading objects from an {@link InputStream}.
 * </p>
 * Specifically, a {@link StreamReader} defines five methods:
 * {@link #preLoop(InputStream)}, {@link #readOne(Object)}, {@link #stop(Thread)},
 * {@link #preClose(InputStream, Object)}, and {@link #closed()}, which will typically be used like
 * in the following code snippet:
 * <pre>
 * void readAll( InputStream inputStream, StreamReader reader ){
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
 * In fact, this is precisely the implementation used by the {@link StreamReaderManager}.
 * From this snippet, it should be clear that a {@link StreamReader} should NOT handle looping or
 * teardown of the {@link InputStream} itself; the class which uses a {@link StreamReader}
 * (such as the {@link StreamReaderManager}) is responsible for that.
 * <p>
 * Another important feature of a {@link StreamReader} is that it
 * must define how it can be stopped. Since this is typically accomplished by interrupting the
 * {@link Thread} on which the stream reading occurs, the {@link #stop(Thread)} method is
 * given the {@link Thread} on which the stream reading occurs as an input, and the default
 * implementation is simply to interrupt this {@link Thread}.
 * </p>
 */
public interface StreamReader<S, O> {

    /**
     * Perform some setup steps before looping, such as modifying the raw
     * {@link InputStream}. For instance, you may want to wrap the {@link InputStream} in
     * a {@link java.io.BufferedInputStream}. The output of this function will be passed to the
     * {@link #readOne(Object)} method in each loop iteration.
     *
     * @param inputStream: The {@link InputStream} that we are supposed to read from.
     */
    S preLoop(InputStream inputStream) throws IOException;

    /**
     * Read a single object from the object (probably a stream) returned by the
     * {@link #preLoop(InputStream)} method.
     *
     * @param stream: The modified stream which is returned from {@link #preLoop(InputStream)}.
     */
    O readOne(S stream) throws IOException, ClassNotFoundException;

    /**
     * Stop reading from the {@link InputStream}. Typically this will be done by interrupting
     * the {@link Thread} on which the {@link StreamReader} is running.
     *
     * @param thread: The {@link Thread} on which the {@link StreamReader} is running.
     */
    default void stop(Thread thread) {
        thread.interrupt();
    }

    /**
     * Start releasing resources before the {@link InputStream} is closed.
     */
    default void preClose(InputStream inputStream, S stream) {
    }

    /**
     * Perform final cleanup now that the {@link InputStream} is closed.
     */
    default void closed() {
    }
}