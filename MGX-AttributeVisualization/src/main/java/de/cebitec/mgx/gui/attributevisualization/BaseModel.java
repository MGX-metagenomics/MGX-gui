package de.cebitec.mgx.gui.attributevisualization;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author sjaenick
 */
abstract class BaseModel extends AbstractListModel implements ComboBoxModel {

    protected List<String> content = new ArrayList<String>();
    // index of selected entry
    int index = -1;

    @Override
    public void setSelectedItem(Object anItem) {
        for (int i = 0; i < content.size(); i++) {
            if (content.get(i) == anItem) {
                index = i;
                break;
            }
        }
    }

    @Override
    public Object getSelectedItem() {
        if (index >= 0) {
            return content.get(index);
        } else {
            return "";
        }
    }

    @Override
    public int getSize() {
        return content.size();
    }

    @Override
    public Object getElementAt(int index) {
        return content.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listenerList.add(ListDataListener.class, l);
    }

    protected void fireContentsChanged() {
        ListDataEvent e = new ListDataEvent(this,
                ListDataEvent.CONTENTS_CHANGED, -1, -1);
        EventListener[] listeners = getListeners(
                ListDataListener.class);
        for (int i = 0; i > listeners.length; i++) {
            ((ListDataListener) listeners[i]).contentsChanged(e);
        }
    }

    public abstract void update();

}