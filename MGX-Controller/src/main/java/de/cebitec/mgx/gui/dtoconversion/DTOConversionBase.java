package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import java.util.Date;

/**
 *
 * @author sjaenick
 */
public abstract class DTOConversionBase<T, U> {

    public abstract U toDTO(T a);

    public abstract T toModel(MGXMasterI m, U dto);

    protected static Long toUnixTimeStamp(Date date) {
        if (date == null) {
            return null;
        }

        // seconds since 1970
        return date.getTime() / 1000L;
    }

    protected static Date toDate(Long timestamp) {
        if (timestamp == null || timestamp == 0) {
            return null;
        }
        return new Date(1000L * timestamp);
    }
}
