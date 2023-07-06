package de.cebitec.mgx.gui.swingutils;

import java.io.Serial;
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
public abstract class BaseModel<T> extends AbstractListModel<T> implements ComboBoxModel<T> {

    @Serial
    private static final long serialVersionUID = 1L;

    protected final List<T> content = new ArrayList<>();
    // index of selected entry
    int index = -1;

    public boolean contains(T o) {
        synchronized (content) {
            return content.contains(o);
        }
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem != null) {
            synchronized (content) {
                index = content.indexOf(anItem);
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

    public void addAll(Collection<T> items) {
        if (items != null && !items.isEmpty()) {
            synchronized (content) {
                if (!content.isEmpty()) {
                    for (T t : items) {
                        if (!content.contains(t)) {
                            content.add(t);
                        }
                    }
                } else {
                    // content is empty, no check for duplicates required
                    content.addAll(items);
                }
            }
            fireContentsChanged();
        }
    }

    public void add(T e) {
        synchronized (content) {
            if (e != null && !content.contains(e)) {
                content.add(e);
                fireContentsChanged();
            }
        }
    }

    @Override
    public int getSize() {
        synchronized (content) {
            return content.size();
        }
    }

    public boolean isEmpty() {
        synchronized (content) {
            return content.isEmpty();
        }
    }

    @Override
    public T getElementAt(int index) {
        synchronized (content) {
            return content.size() > index ? content.get(index) : null;
        }
    }

    public List<T> getAll() {
        return List.copyOf(content);
    }

    protected void fireContentsChanged() {
        ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1);
        fireContentsChanged(e, -1, -1);
    }

    public void clear() {
        synchronized (content) {
            if (!content.isEmpty()) {
                content.clear();
                index = -1;
                fireContentsChanged();
            }
        }
    }

    public abstract void update();
}
