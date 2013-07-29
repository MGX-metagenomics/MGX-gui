
package de.cebitec.mgx.gui.util;

/**
 *
 * @author sj
 */
public class Reference<T> {
    
    private T value;

    public Reference(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
    
}
