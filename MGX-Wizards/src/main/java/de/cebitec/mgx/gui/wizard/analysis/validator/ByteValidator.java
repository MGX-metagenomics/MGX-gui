package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class ByteValidator extends ValidatorI<Integer> {

    @Override
    public boolean validate(String input) {
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            error = DEFAULT_ERROR_MSG;
            return false;
        }
        if (value < 0 || value > 255) {
            value = null;
            error = "Value exceeds range (0-255).";
            return false;
        }
        return true;
    }
}
