/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api;

import de.cebitec.gpms.rest.RESTMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import java.util.Iterator;
import java.util.ServiceLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author sj
 */
public class MGXControllerFactory {

    private static final ServiceLoader<ControllerCreatorI> loader = ServiceLoader.<ControllerCreatorI>load(ControllerCreatorI.class);

    private MGXControllerFactory() {
    }

    @SuppressWarnings("unchecked")
    public static MGXMasterI createMaster(RESTMasterI gpmsClient) throws MGXException {
        if (!OSGiContext.isOSGi()) {
            // fallback to serviceloader
            ControllerCreatorI fac = get();
            if (fac == null) {
                throw new MGXException("No ControllerCreatorI found.");
            }
            return fac.createController(gpmsClient);
        } else {
            BundleContext context = FrameworkUtil.getBundle(MGXControllerFactory.class).getBundleContext();
            ServiceReference<ControllerCreatorI> serviceReference = (ServiceReference<ControllerCreatorI>) context.getServiceReference(ControllerCreatorI.class.getName());
            ControllerCreatorI service = context.<ControllerCreatorI>getService(serviceReference);
            return service.createController(gpmsClient);
        }
    }

    private static ControllerCreatorI get() {
        Iterator<ControllerCreatorI> ps = loader.iterator();
        while (ps != null && ps.hasNext()) {
            ControllerCreatorI cc = ps.next();
            if (cc != null) {
                return cc;
            }
        }
        return null;
    }
}
