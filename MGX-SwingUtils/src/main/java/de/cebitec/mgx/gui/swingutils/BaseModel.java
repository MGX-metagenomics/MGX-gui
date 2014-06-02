package de.cebitec.mgx.gui.swingutils;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;

/**
 *
 * @author sjaenick
 */
public abstract class BaseModel<T> extends AbstractListModel implements ComboBoxModel {

    protected List<T> content = new ArrayList<>();
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
    public T getSelectedItem() {
        if ((index >= 0) && (content.size() > index)) {
            return content.get(index);
        } else {
            return null;
        }
    }

    @Override
    public int getSize() {
        return content.size();
    }

    @Override
    public T getElementAt(int index) {
        return content.get(index);
    }

    protected void fireContentsChanged() {
        ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1);
        fireContentsChanged(e, -1, -1);
    }

    public abstract void update();
}