package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class DoubleValidator extends ValidatorI<Double> {

    @Override
    public boolean validate(String input) {
        System.err.println("validating "+input);
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException nfe) {
            error = DEFAULT_ERROR_MSG;
            System.err.println("false");
            return false;
        }
        System.err.println("true");
        return true;
    }

}
