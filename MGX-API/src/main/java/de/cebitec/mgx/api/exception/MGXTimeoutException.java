/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.exception;

import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public class MGXTimeoutException extends MGXException {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public MGXTimeoutException(String message) {
        super(message);
    }

    public MGXTimeoutException(Throwable cause) {
        super(cause);
    }
    
    
}
