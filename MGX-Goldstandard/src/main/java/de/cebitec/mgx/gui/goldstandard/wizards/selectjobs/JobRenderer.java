package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.gui.goldstandard.util.JobUtils;
import java.awt.Component;
import java.io.Serial;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author patrick
 */
public class JobRenderer extends JLabel implements ListCellRenderer<JobI> {

    @Serial
    private static final long serialVersionUID = 1L;

    public JobRenderer() {
        super.setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends JobI> list, JobI value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(JobUtils.jobToString(value));

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }

}
