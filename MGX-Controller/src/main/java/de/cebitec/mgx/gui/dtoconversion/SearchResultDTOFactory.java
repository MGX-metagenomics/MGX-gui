package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.dto.dto.ObservationDTO;
import de.cebitec.mgx.dto.dto.SearchResultDTO;
import de.cebitec.mgx.gui.datamodel.misc.SearchResult;

/**
 *
 * @author sj
 */
public class SearchResultDTOFactory extends DTOConversionBase<SearchResult, SearchResultDTO> {

    static {
        instance = new SearchResultDTOFactory();
    }
    protected static SearchResultDTOFactory instance;

    private SearchResultDTOFactory() {
    }

    public static SearchResultDTOFactory getInstance() {
        return instance;
    }

    @Override
    public SearchResultDTO toDTO(SearchResult a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public SearchResult toModel(MGXMasterI m, SearchResultDTO dto) {
        SearchResult sr = new SearchResult();
        sr.setSequenceName(dto.getSequenceName());
        for (ObservationDTO o : dto.getObservationList()) {
            sr.addObservation(ObservationDTOFactory.getInstance().toModel(m, o));
        }
        return sr;
    }
}
