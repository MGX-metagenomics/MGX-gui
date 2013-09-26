package de.cebitec.mgx.gui.wizard.analysis.validator;

import de.cebitec.mgx.gui.datamodel.JobParameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class MultipleChoiceValidator extends ValidatorI<String> {

    private final Map<String, String> choices;
    private String MODE = "CHOICES";

    public MultipleChoiceValidator(JobParameter param) {
        choices = param.getChoices();
    }

    public MultipleChoiceValidator(JobParameter param, Map<?, String> options) {
        choices = new HashMap<>();
        for (Entry<?, String> e : options.entrySet()) {
            choices.put(e.getKey().toString(), e.getValue());
        }
        MODE = "OPTIONS";
    }

    @Override
    public boolean validate(String input) {
        if (input == null) {
            return false;
        }
        if (choices.containsKey(input)) {
            if (MODE.equals("CHOICES")) {
                value = input;
            } else {
                value = choices.get(input);
            }
            return true;
        }
        error = DEFAULT_ERROR_MSG;
        return false;
    }
}
