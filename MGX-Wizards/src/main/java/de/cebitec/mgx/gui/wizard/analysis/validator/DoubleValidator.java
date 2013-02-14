package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class DoubleValidator extends ValidatorI<Double> {

    @Override
    public boolean validate(String input) {
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
