package de.cebitec.mgx.gui.attributevisualization.exportwizard;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

/**
 *
 * @author sjaenick
 *
 * adapted based on code taken from
 * http://blog.mynotiz.de/programmieren/java-checkbox-in-jlist-1061/
 *
 */
public class JCheckBoxList<T> extends JList<T> {

    private Map<T, Boolean> selections = new HashMap<>();
    private DefaultListModel<T> model = new DefaultListModel<>();

    public JCheckBoxList() {
        setModel(model);
        setCellRenderer(new CellRenderer());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    T elem = model.getElementAt(index);
                    boolean oldVal = selections.get(elem);
                    selections.put(elem, !oldVal);
                    repaint();
                }
            }
        });
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    public void clear() {
        model.clear();
        selections.clear();
    }

    public void addElement(T elem) {
        selections.put(elem, Boolean.TRUE);
        model.addElement(elem);
    }

    public void selectAll() {
        for (Entry<T, Boolean> e : selections.entrySet()) {
            e.setValue(Boolean.TRUE);
        }
        repaint();
    }

    public void deselectAll() {
        for (Entry<T, Boolean> e : selections.entrySet()) {
            e.setValue(Boolean.FALSE);
        }
        repaint();
    }

    public Set<T> getSelectedEntries() {
        Set<T> elems = new HashSet<>();
        for (Entry<T, Boolean> e : selections.entrySet()) {
            if (e.getValue()) {
                elems.add(e.getKey());
            }
        }
        return elems;
    }

    protected class CellRenderer implements ListCellRenderer<T> {

        private JCheckBox box = new JCheckBox();

        @Override
        public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {

            Boolean selected = selections.get(value);
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
