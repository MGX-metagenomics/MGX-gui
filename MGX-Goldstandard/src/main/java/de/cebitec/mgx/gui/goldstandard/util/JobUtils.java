package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;

/**
 *
 * @author patrick
 */
public class JobUtils {

    private JobUtils() {
    }
    
    public static String jobToString (JobI job){
        StringBuilder string = new StringBuilder(job.getTool().getName());
        if (!job.getParameters().isEmpty()) {
            string.append(" - ");
            for (JobParameterI parameter : job.getParameters()) {
                string.append(parameter.getParameterName());
                string.append(": ");
                string.append(parameter.getParameterValue());
                string.append(", ");
            }
            string.deleteCharAt(string.length() - 1);
            string.deleteCharAt(string.length() - 1);
        }
        
        return string.toString();
    }
}
