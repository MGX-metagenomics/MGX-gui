package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class SByteValidator extends ValidatorI<Byte> {

    @Override
    public boolean validate(String input) {
        try {
            value = Byte.parseByte(input);
            return true;
        } catch (NumberFormatException nfe) {
            error = DEFAULT_ERROR_MSG;
            return false;
        }
    }
}
