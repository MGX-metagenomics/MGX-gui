package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.gui.datamodel.Sequence;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractListModel;

/**
 *
 * @author sjaenick
 */
public final class ResultListModel extends AbstractListModel<Sequence> {

    Sequence list[] = new Sequence[0];
    private Set<Sequence> nonDefault = new HashSet<>();
    private boolean defaultValue = false;

    public synchronized void setResult(Sequence[] result) {
        list = result;
        nonDefault.clear();
        defaultValue = false;
        fireContentsChanged(this, 0, list.length - 1);
    }

    @Override
    public int getSize() {
        return list.length;
    }

    @Override
    public Sequence getElementAt(int index) {
        return list[index];
    }

    public boolean isSelected(int idx) {
        Sequence seq = getElementAt(idx);
        return nonDefault.contains(seq) ? !defaultValue : defaultValue;
    }

    public void select(int idx) {
        Sequence seq = getElementAt(idx);
        if (!defaultValue) {
            nonDefault.add(seq);
        } else {
            nonDefault.remove(seq);
        }
        fireContentsChanged(this, idx, idx);
    }

    public void deselect(int idx) {
        Sequence seq = getElementAt(idx);
        if (defaultValue) {
            nonDefault.remove(seq);
        } else {
            nonDefault.add(seq);
        }
        fireContentsChanged(this, idx, idx);
    }

    public void selectAll() {
        defaultValue = true;
        nonDefault.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public void deselectAll() {
        defaultValue = false;
        nonDefault.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public Set<Sequence> getSelectedEntries() {
        Set<Sequence> ret = new HashSet<>();
        if (defaultValue) {
            for (Sequence s : list) {
                if (!nonDefault.contains(s)) {
                    ret.add(s);
                }
            }
            return ret;
        } else {
            return nonDefault;
        }
    }
}
