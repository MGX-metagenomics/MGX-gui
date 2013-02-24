
package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class LongValidator extends ValidatorI<Long> {

    @Override
    public boolean validate(String input) {
         try {
            value = Long.parseLong(input);
        } catch (NumberFormatException nfe) {
            error = DEFAULT_ERROR_MSG;
            return false;
        }
        return true;
    }
    
}