
package de.cebitec.mgx.gui.wizard.analysis.validator;

/**
 *
 * @author sjaenick
 */
public class StringValidator extends ValidatorI<String> {

    @Override
    public boolean validate(String input) {
        value = input;
        return input != null;
    }
    
}
