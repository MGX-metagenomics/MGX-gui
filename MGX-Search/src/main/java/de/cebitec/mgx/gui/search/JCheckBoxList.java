package de.cebitec.mgx.gui.search;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.DefaultListModel;
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

    /*
     * Die Auswahl, die vom User getroffen wird.
     */
    private Map<T, Boolean> selections = new HashMap<>();
    /*
     * ListModel 
     */
    private DefaultListModel<T> model = new DefaultListModel<>();
    /*
     * Property Name bei der Auswahl.
     */
    public static final String selectionChange = "SELECTION_CHANGED";

    @Override
    public void setModel(final ListModel<T> model) {

        for (int i = 0; i < model.getSize(); i++) {
            this.addElement(model.getElementAt(i));
        }
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    T elem = model.getElementAt(index);
                    boolean oldVal = selections.get(elem);
                    selections.put(elem, !oldVal);
                    firePropertyChange(selectionChange, oldVal, !oldVal);
                    repaint();
                }
            }
        };
        addMouseListener(adapter);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        super.setModel(model);
        setCellRenderer(new CellRenderer());
    }

    /*
     * Auswahl wird entfernt.
     */
    public void clear() {
        model.clear();
        selections.clear();
    }


    /*
     * Fuegt ein Element zum Model hinzu.
     * 
     * @param T Element, was zum Model hinzugefuegt werden soll.
     */
    public void addElement(T elem) {
        selections.put(elem, Boolean.FALSE);
        model.addElement(elem);
    }

    /*
     * Selektiert alle Elemente in der Liste.
     */
    public void selectAll() {
        for (Entry<T, Boolean> e : selections.entrySet()) {
            e.setValue(Boolean.TRUE);
        }
        firePropertyChange(selectionChange, false, true);
        repaint();
    }

    /*
     * Entfernt die Auswahl bei allen Elementen.
     */
    public void deselectAll() {
        for (Entry<T, Boolean> e : selections.entrySet()) {
            e.setValue(Boolean.FALSE);
        }
        firePropertyChange(selectionChange, true, false);
        repaint();
    }

    /*
     * Gibt die Auswahl zurueck.
     * 
     * @return Set<T> Menge der Selektierten Elemente. 
     */
    public Set<T> getSelectedEntries() {
        Set<T> elems = new HashSet<>();
        for (Entry<T, Boolean> e : selections.entrySet()) {
            if (e.getValue()) {
                elems.add(e.getKey());
            }
        }
        return elems;
    }

    /*
     * 
     * CellRenderer fuer die Darstellung der Checkbox.
     */
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
