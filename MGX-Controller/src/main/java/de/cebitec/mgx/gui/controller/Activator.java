package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.ControllerCreatorI;
import java.util.logging.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author sj
 */
public class Activator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(Activator.class.getPackage().getName());
    
    private ServiceRegistration<?> registerService;

    @Override
    public void start(BundleContext context) throws Exception {
        registerService = context.registerService(ControllerCreatorI.class.getName(), new ControllerCreatorImpl(), null);
        LOG.info("Registered MGX 2 controller.");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        context.ungetService(registerService.getReference());
        LOG.info("Unregistered MGX 2 controller.");
    }

}
