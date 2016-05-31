package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author patrick
 */
public class JobRenderer extends JLabel implements ListCellRenderer<JobI>{

    public JobRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends JobI> list, JobI value, int index, boolean isSelected, boolean cellHasFocus) {
        StringBuilder code = new StringBuilder(value.getTool().getName() + " - ");
        for (JobParameterI parameter : value.getParameters()){
            code.append(parameter.getParameterName());
            code.append(": ");
            code.append(parameter.getParameterValue());
            code.append(", ");
        }
        code.deleteCharAt(code.length()-1);
        setText(code.toString());
        
        if (isSelected){
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        return this;
    }
    
}
