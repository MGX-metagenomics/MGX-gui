/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.exception;

/**
 *
 * @author sj
 */
public class MGXLoggedoutException extends MGXException {

    public MGXLoggedoutException(String message) {
        super(message);
    }

    public MGXLoggedoutException(Throwable cause) {
        super(cause);
    }
    
}
