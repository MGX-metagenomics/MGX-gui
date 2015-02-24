package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class ByteValidator extends ValidatorI {

    @Override
    public boolean validate(String input) {
        Integer v;
        try {
            v = Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            error = DEFAULT_ERROR_MSG;
            return false;
        }
        if (v < 0 || v > 255) {
            value = null;
            error = "Value exceeds range (0-255).";
            return false;
        }
        value = input;
        return true;
    }
}
