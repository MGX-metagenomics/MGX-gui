package de.cebitec.mgx.gui.wizard.analysis.validator;

import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.JobParameterI;
import java.util.Collection;
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

    public MultipleChoiceValidator(JobParameterI param) {
        choices = param.getChoices();
    }

    public MultipleChoiceValidator(JobParameterI param, Collection<? extends Identifiable> allowedObjs) {
        choices = new HashMap<>();
        for (Identifiable i : allowedObjs) {
            choices.put(i.toString(), String.valueOf(i.getId()));
        }
        MODE = "OPTIONS";
    }

    @Override
    public boolean validate(String input) {
        if (input == null) {
            return false;
        }
        if (MODE.equals("CHOICES") && choices.containsKey(input)) {
            value = input;
            return true;
        }
        if (MODE.equals("OPTIONS")) {
            for (Entry<String, String> e : choices.entrySet()) {
                if (e.getKey().toString().equals(input)) {
                    value = e.getValue();
                    return true;
                }
            }
        }

        error = DEFAULT_ERROR_MSG;
        return false;
    }
}
