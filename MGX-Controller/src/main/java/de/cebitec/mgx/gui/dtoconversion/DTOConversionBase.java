package de.cebitec.mgx.gui.dtoconversion;

import java.util.Date;

/**
 *
 * @author sjaenick
 */
public abstract class DTOConversionBase<T, U> {

    public abstract U toDTO(T a);

    public abstract T toModel(U dto);

    protected static Long toUnixTimeStamp(Date date) {
        if (date == null) {
            return null;
        }
        
        // seconds since 1970
        return date.getTime() / 1000L;
    }

    protected static Date toDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(1000L * timestamp);
    }
}
