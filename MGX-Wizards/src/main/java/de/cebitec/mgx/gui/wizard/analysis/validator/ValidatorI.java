package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public abstract class ValidatorI {
    
    protected String value = null;
    protected static String DEFAULT_ERROR_MSG = "Invalid value.";
    protected String error = DEFAULT_ERROR_MSG;

    public abstract boolean validate(String input);

    public String getValue() {
        return value;
    }
    
    public String getError() {
        return error;
    }
}
