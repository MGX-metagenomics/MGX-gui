package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.gui.datamodel.Sequence;
import javax.swing.AbstractListModel;

/**
 *
 * @author sjaenick
 */
public final class ResultListModel extends AbstractListModel<Sequence> {

    Sequence list[] = new Sequence[0];

    public void setResult(Sequence[] result) {
        list = result;
        fireContentsChanged(this, -1, -1);
    }

    @Override
    public int getSize() {
        return list.length;
    }

    @Override
    public Sequence getElementAt(int index) {
        return list[index];
    }
}
