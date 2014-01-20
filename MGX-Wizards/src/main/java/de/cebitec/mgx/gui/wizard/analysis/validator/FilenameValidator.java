package de.cebitec.mgx.gui.wizard.analysis.validator;

import de.cebitec.mgx.gui.datamodel.MGXFile;

/**
 *
 * @author sjaenick
 */
public class FilenameValidator extends ValidatorI<String> {

    @Override
    public boolean validate(String input) {
        value = input;
        return input != null && input.startsWith(MGXFile.ROOT_PATH + MGXFile.separator) && input.length() > 2;
    }
}
