package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class IntegerValidator extends ValidatorI<Integer> {

    @Override
    public boolean validate(String input) {
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            error = DEFAULT_ERROR_MSG;
            return false;
        }
        return true;
    }
}
