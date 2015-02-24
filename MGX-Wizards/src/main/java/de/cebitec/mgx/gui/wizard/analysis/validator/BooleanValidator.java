package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class BooleanValidator extends ValidatorI {

    @Override
    public boolean validate(String input) {
        switch (input) {
            case "TRUE":
                Boolean.parseBoolean(input);
                value = input;
                return true;
            case "FALSE":
                Boolean.parseBoolean(input);
                value = input;
                return true;
            default:
                value = null;
                return false;
        }
    }

}
