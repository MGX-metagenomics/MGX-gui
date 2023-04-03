package de.cebitec.mgx.gui.swingutils;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
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

    @Serial
    private static final long serialVersionUID = 1L;
    
    private Map<T, Boolean> selections = new HashMap<>();
    private DefaultListModel<T> model = new DefaultListModel<>();
    public static final String selectionChange = "SELECTION_CHANGED";
    private final CellRenderer cellRenderer = new CellRenderer();

    public JCheckBoxList() {
        setModel(model);
        setCellRenderer(cellRenderer);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    int index = locationToIndex(e.getPoint());
                    if (index != -1) {
                        T elem = model.getElementAt(index);
                        boolean oldVal = selections.get(elem);
                        selections.put(elem, !oldVal);
                        firePropertyChange(selectionChange, elem, !oldVal);
                        repaint();
                    }
                }
            }
        });
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        cellRenderer.setEnabled(enabled);
    }

    public void clear() {
        model.clear();
        selections.clear();
    }

    public void addElement(T elem) {
        addElement(elem, true);
    }

    public void addElement(T elem, boolean selected) {
        selections.put(elem, selected);
        model.addElement(elem);
    }

    public void selectAll() {
        for (Entry<T, Boolean> e : selections.entrySet()) {
            if (!e.getValue()) {
                e.setValue(Boolean.TRUE);
                firePropertyChange(selectionChange, e.getKey(), true);
            }
        }
        repaint();
    }

    public void deselectAll() {
        for (Entry<T, Boolean> e : selections.entrySet()) {
            if (e.getValue()) {
                e.setValue(Boolean.FALSE);
                firePropertyChange(selectionChange, e.getKey(), false);
            }
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

    public Set<T> getDeselectedEntries() {
        Set<T> elems = new HashSet<>();
        for (Entry<T, Boolean> e : selections.entrySet()) {
            if (!e.getValue()) {
                elems.add(e.getKey());
            }
        }
        return elems;
    }

    private class CellRenderer implements ListCellRenderer<T> {

        private final JCheckBox box = new JCheckBox();

        @Override
        public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {

            Boolean selected = selections.get(value);
            if (selected != null) {
                box.setSelected(selected);
            }
            box.setText(value.toString());

            if (isSelected) {
                box.setBorderPainted(true);
                box.setForeground(UIManager.getColor("List.selectionForeground"));
                box.setBackground(UIManager.getColor("List.selectionBackground"));
            } else {
                box.setBorderPainted(false);
                box.setForeground(UIManager.getColor("List.foreground"));
                box.setBackground(UIManager.getColor("List.background"));
            }
            return box;
        }

        private void setEnabled(boolean enabled) {
            box.setEnabled(enabled);
        }
    }
}
