package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.model.AttributeTypeI;
import java.awt.Component;
import java.io.Serial;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author patrick
 */
public class AttributeTypeRenderer extends JLabel implements ListCellRenderer<AttributeTypeI> {

    @Serial
    private static final long serialVersionUID = 1L;

    public AttributeTypeRenderer() {
        super.setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends AttributeTypeI> list, AttributeTypeI value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value == null) {
            setText("");
            return this;
        }

        setText(value.getName());

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
