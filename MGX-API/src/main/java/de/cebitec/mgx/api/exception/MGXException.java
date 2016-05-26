package de.cebitec.mgx.api.exception;

/**
 *
 * @author sjaenick
 */
public class MGXException extends Exception {

    public MGXException(String message) {
        super(message);
    }

    public MGXException(Throwable cause) {
        super(cause);
    }
}
