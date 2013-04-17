package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.gui.search.util.ResultListModel;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

/**
 *
 * @author pbelmann
 *
 * adapted based on code taken from
 * http://blog.mynotiz.de/programmieren/java-checkbox-in-jlist-1061/
 *
 */
public class JCheckBoxList<T> extends JList<T> {

    // private Map<T, Boolean> selections = new TreeMap<>();
    // public static final String selectionChange = "SELECTION_CHANGED";
    public JCheckBoxList() {
        super();
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    @Override
    public void setModel(ListModel<T> m) {
        if (!(m instanceof ResultListModel)) {
            return;
        }
        final ResultListModel model = (ResultListModel) m;
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    boolean oldVal = model.isSelected(index);
                    if (oldVal) {
                        model.deselect(index);
                    } else {
                        model.select(index);
                    }
                    //firePropertyChange(selectionChange, oldVal, !oldVal);
                    fireSelectionValueChanged(index, index, false);
                    repaint();
                }
            }
        };
        addMouseListener(adapter);
        setCellRenderer(new CellRenderer(model));
        super.setModel(m);
    }

//    public void selectAll() {
//        for (Entry<T, Boolean> e : selections.entrySet()) {
//            e.setValue(Boolean.TRUE);
//        }
//        firePropertyChange(selectionChange, false, true);
//        repaint();
//    }
//
//    public void deselectAll() {
//        for (Entry<T, Boolean> e : selections.entrySet()) {
//            e.setValue(Boolean.FALSE);
//        }
//        firePropertyChange(selectionChange, true, false);
//        repaint();
//    }
//
//    public Set<T> getSelectedEntries() {
//        Set<T> elems = new HashSet<>();
//        for (Entry<T, Boolean> e : selections.entrySet()) {
//            if (e.getValue()) {
//                elems.add(e.getKey());
//            }
//        }
//        return elems;
//    }
    /*
     * 
     * CellRenderer fuer die Darstellung der Checkbox.
     */
    protected class CellRenderer implements ListCellRenderer<T> {

        private JCheckBox box = new JCheckBox();
        private final ResultListModel model;

        public CellRenderer(ResultListModel model) {
            this.model = model;
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {

            Boolean selected = model.isSelected(index);
            box.setSelected(selected.booleanValue());
            box.setText(value.toString());


            if (isSelected) {
                // checkbox.setBorderPainted(true);
                // checkbox.setForeground(UIManager.getColor("List.selectionForeground"));
                // checkbox.setBackground(UIManager.getColor("List.selectionBackground"));
            } else {
                // checkbox.setBorderPainted(false);
                // checkbox.setForeground(UIManager.getColor("List.foreground"));
                box.setBackground(UIManager.getColor("List.background"));
            }

            return box;
        }
    }
}
