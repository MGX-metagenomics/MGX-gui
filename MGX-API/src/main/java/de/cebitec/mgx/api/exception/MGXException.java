package de.cebitec.mgx.api.exception;

import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public class MGXException extends Exception {
    
    @Serial
    private static final long serialVersionUID = 1L;

    public MGXException(String message) {
        super(message);
    }

    public MGXException(Throwable cause) {
        super(cause);
    }
}
