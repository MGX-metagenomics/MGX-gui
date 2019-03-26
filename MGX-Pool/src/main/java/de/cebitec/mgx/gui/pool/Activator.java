/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.pool;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author sj
 */
public class Activator implements BundleActivator {

    ServiceRegistration<?> registerService;

    @Override
    public void start(BundleContext context) throws Exception {
        
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (MGXPool.isUpAndRunning()) {
            MGXPool.getInstance().shutdown();
            Logger.getLogger(MGXPool.class.getPackage().getName()).log(Level.INFO, "MGX processing pool shut down, {0} tasks completed.",
                    NumberFormat.getInstance(Locale.US).format(MGXPool.completedTaskNum()));
        }
    }

}
