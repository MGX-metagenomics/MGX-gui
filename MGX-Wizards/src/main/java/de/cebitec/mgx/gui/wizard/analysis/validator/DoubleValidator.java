package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class DoubleValidator extends ValidatorI {

    @Override
    public boolean validate(String input) {
        try {
            Double.parseDouble(input);
        } catch (NumberFormatException nfe) {
            error = DEFAULT_ERROR_MSG;
            return false;
        }
        value = input;
        return true;
    }

}
