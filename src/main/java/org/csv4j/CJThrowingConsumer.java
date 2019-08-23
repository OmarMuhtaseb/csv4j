package org.csv4j;

/**
 * @author Omar Muhtaseb
 */
@FunctionalInterface
public interface CJThrowingConsumer<T, E extends Exception> {
    void accept(T t) throws E;
}
