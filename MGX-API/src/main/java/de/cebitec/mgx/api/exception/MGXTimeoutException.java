/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.exception;

/**
 *
 * @author sjaenick
 */
public class MGXTimeoutException extends MGXException {

    public MGXTimeoutException(String message) {
        super(message);
    }

    public MGXTimeoutException(Throwable cause) {
        super(cause);
    }
    
    
}
