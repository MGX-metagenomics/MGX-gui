
package de.cebitec.mgx.gui.wizard.analysis.validator;

import de.cebitec.mgx.gui.datamodel.JobParameter;

/**
 *
 * @author sjaenick
 */
public class MultipleChoiceValidator extends ValidatorI<String> {
    
    private final JobParameter param;

    public MultipleChoiceValidator(JobParameter param) {
        this.param = param;
    }

    @Override
    public boolean validate(String input) {
        if (input == null) {
            return false;
        }
        if (param.getChoices().containsKey(input)) {
            value = input;
            return true;
        }
        error = DEFAULT_ERROR_MSG;
        return false;
    }
    
}
