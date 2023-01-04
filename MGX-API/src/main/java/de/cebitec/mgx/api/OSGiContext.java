/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 * @author sj
 */
public class OSGiContext implements BundleActivator {

    private static boolean isOSGI = false;

    @Override
    public void start(BundleContext context) throws Exception {
        isOSGI = true;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
    
    public final static boolean isOSGi() {
        return isOSGI;
    }

}
