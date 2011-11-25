package de.cebitec.mgx.gui.dtoadapter;

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
        return date.getTime() / 1000L;
    }

    protected static Date toDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp);
    }
}
