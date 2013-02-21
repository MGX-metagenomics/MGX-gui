
package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class BooleanValidator extends ValidatorI<Boolean> {

    @Override
    public boolean validate(String input) {
        switch (input) {
            case "TRUE":
                value = Boolean.parseBoolean(input);
                return true;
            case "FALSE":
                value = Boolean.parseBoolean(input);
               return true;
            default:
                return false;
        }
    }
    
}
