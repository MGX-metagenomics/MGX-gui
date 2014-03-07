
package de.cebitec.mgx.gui.swingutils;

import de.cebitec.mgx.gui.datamodel.AttributeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;

/**
 *
 * @author sjaenick
 */
class AttrTypeModel extends AbstractListModel<AttributeType> implements ComboBoxModel<AttributeType> {

    private final List<AttributeType> data = new ArrayList<>();
    private int selectionIdx = -1;
   

    public void setData(final Collection<AttributeType> newData) {
        data.clear();
        data.addAll(newData);
        selectionIdx = -1;
        if (getSize() > 0) {
            setSelectedItem(data.get(0));
        }
        fireContentsChanged();
    }

    public void clear() {
        data.clear();
        selectionIdx = -1;
        fireContentsChanged();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (data.contains(anItem)) {
            selectionIdx = data.indexOf(anItem);
            //itemStateChanged(new ItemEvent(null, ItemEvent.ITEM_STATE_CHANGED, anItem, ItemEvent.SELECTED));
        }
    }

    @Override
    public AttributeType getSelectedItem() {
        return selectionIdx != -1 ? data.get(selectionIdx) : null;
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public AttributeType getElementAt(int index) {
        return data.get(index);
    }

    protected void fireContentsChanged() {
        ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1);
        fireContentsChanged(e, -1, -1);
    }

}
