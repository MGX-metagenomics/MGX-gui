package de.cebitec.mgx.gui.wizard.analysis.validator;

import java.math.BigDecimal;

/**
 *
 * @author sjaenick
 */
public class ULongValidator extends ValidatorI {

    private static final BigDecimal ULONG_MAX = new BigDecimal("18446744073709551615");

    @Override
    public boolean validate(String input) {
        BigDecimal bd;
        try {
            bd = new BigDecimal(input);
        } catch (NumberFormatException nfe) {
            error = DEFAULT_ERROR_MSG;
            return false;
        }

        if (bd.signum() == -1 || bd.compareTo(ULONG_MAX) == 1) {
            value = null;
            error = "Value exceeds range (0-" + ULONG_MAX.toString() + ")";
            return false;
        }
        value = input;
        return true;
    }
}
