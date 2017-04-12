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
        StringBuilder sb = new StringBuilder(job.getTool().getName());
        if (!job.getParameters().isEmpty()) {
            sb.append(" - ");
            for (JobParameterI parameter : job.getParameters()) {
                sb.append(parameter.getParameterName());
                sb.append(": ");
                sb.append(parameter.getParameterValue());
                sb.append(", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }
        
        return sb.toString();
    }
}
