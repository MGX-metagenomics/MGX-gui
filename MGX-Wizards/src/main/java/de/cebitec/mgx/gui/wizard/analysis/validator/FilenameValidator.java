package de.cebitec.mgx.gui.wizard.analysis.validator;

import de.cebitec.mgx.api.model.MGXFileI;

/**
 *
 * @author sjaenick
 */
public class FilenameValidator extends ValidatorI<String> {

    @Override
    public boolean validate(String input) {
        value = input;
        return input != null && input.startsWith(MGXFileI.ROOT_PATH + MGXFileI.separator) && input.length() > 2;
    }
}
