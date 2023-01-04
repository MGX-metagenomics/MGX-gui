package de.cebitec.mgx.gui.controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/OSGi/Activator.java to edit this template
 */
import de.cebitec.mgx.api.ControllerCreatorI;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author sj
 */
public class Activator implements BundleActivator {

    private ServiceRegistration<?> registerService;

    @Override
    public void start(BundleContext context) throws Exception {
        registerService = context.registerService(ControllerCreatorI.class.getName(), new ControllerCreatorImpl(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        context.ungetService(registerService.getReference());
    }

}
