package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public abstract class ValidatorI<T> {
    
    protected T value = null;

    public abstract boolean validate(String input);

    public T getValue() {
        return value;
    }
}
