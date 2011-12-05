/*
 * AsyncRequestEvent.java
 *
 * Created on December 19, 2006, 12:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.http.async.event;

/**
 *
 * @author rbair
 */
public class AsyncRequestAdapter implements AsyncRequestListener {
    @Override
    public void onLoad() {}
    @Override
    public void onError() {}
    @Override
    public void onProgress() {}
    @Override
    public void onAbort() {}
    @Override
    public void onTimeout() {}
}
