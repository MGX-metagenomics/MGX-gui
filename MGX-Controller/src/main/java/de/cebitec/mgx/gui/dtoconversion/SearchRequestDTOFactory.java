package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.SearchRequestI;
import de.cebitec.mgx.dto.dto.SearchRequestDTO;
import de.cebitec.mgx.dto.dto.SearchRequestDTO.Builder;
import de.cebitec.mgx.gui.datamodel.misc.SearchRequest;

/**
 *
 * @author sjaenick
 */
public class SearchRequestDTOFactory extends DTOConversionBase<SearchRequestI, SearchRequestDTO> {

    static {
        instance = new SearchRequestDTOFactory();
    }
    protected static SearchRequestDTOFactory instance;

    private SearchRequestDTOFactory() {
    }

    public static SearchRequestDTOFactory getInstance() {
        return instance;
    }

    @Override
    public SearchRequestDTO toDTO(SearchRequestI req) {
        Builder b = SearchRequestDTO.newBuilder()
                .setExact(req.isExact())
                .setTerm(req.getTerm());
        b.setSeqrunId(req.getRun().getId());
        return b.build();
    }

    @Override
    public SearchRequest toModel(MGXMasterI m, SearchRequestDTO dto) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
