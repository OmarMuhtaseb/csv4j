package org.csv4j.exception;

/**
 * An exception class for CSV4J
 *
 * @author Omar Muhtaseb
 */
public class CJException extends RuntimeException {

    public CJException(Exception exception) {
        super(exception);
    }

    public CJException(String msg) {
        throw new RuntimeException(msg);
    }
}
