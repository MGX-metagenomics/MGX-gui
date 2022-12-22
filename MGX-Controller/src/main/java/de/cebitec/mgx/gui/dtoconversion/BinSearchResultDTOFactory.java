package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.BinSearchResultI;
import de.cebitec.mgx.dto.dto.BinSearchResultDTO;
import de.cebitec.mgx.gui.datamodel.misc.BinSearchResult;

/**
 *
 * @author sj
 */
public class BinSearchResultDTOFactory extends DTOConversionBase<BinSearchResultI, BinSearchResultDTO> {

    static {
        instance = new BinSearchResultDTOFactory();
    }
    protected static BinSearchResultDTOFactory instance;

    private BinSearchResultDTOFactory() {
    }

    public static BinSearchResultDTOFactory getInstance() {
        return instance;
    }

    @Override
    public BinSearchResultDTO toDTO(BinSearchResultI a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public BinSearchResultI toModel(MGXMasterI m, BinSearchResultDTO dto) {
        BinSearchResultI sr = new BinSearchResult(
                dto.getContigId(),
                dto.getContigName(),
                dto.getRegionId(),
                dto.getAttributeName(),
                dto.getAttributeTypeValue());
        return sr;
    }
}
