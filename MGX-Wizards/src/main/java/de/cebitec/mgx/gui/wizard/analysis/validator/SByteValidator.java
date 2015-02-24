package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class SByteValidator extends ValidatorI {

    @Override
    public boolean validate(String input) {
        Byte b;
        try {
            b = Byte.parseByte(input);
        } catch (NumberFormatException nfe) {
            error = DEFAULT_ERROR_MSG;
            return false;
        }
        value = input;
        return true;
    }
}
