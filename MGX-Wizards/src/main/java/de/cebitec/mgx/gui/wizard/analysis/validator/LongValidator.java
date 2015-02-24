package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class LongValidator extends ValidatorI {

    @Override
    public boolean validate(String input) {
        try {
            Long.parseLong(input);
        } catch (NumberFormatException nfe) {
            error = DEFAULT_ERROR_MSG;
            return false;
        }
        value = input;
        return true;
    }

}
