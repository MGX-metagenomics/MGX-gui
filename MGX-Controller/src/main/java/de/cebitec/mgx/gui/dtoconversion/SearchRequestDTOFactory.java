package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.SearchRequestDTO;
import de.cebitec.mgx.dto.dto.SearchRequestDTO.Builder;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.misc.SearchRequest;

/**
 *
 * @author sjaenick
 */
public class SearchRequestDTOFactory extends DTOConversionBase<SearchRequest, SearchRequestDTO> {

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
    public SearchRequestDTO toDTO(SearchRequest req) {
        Builder b = SearchRequestDTO.newBuilder()
                .setExact(req.isExact())
                .setTerm(req.getTerm());
        for (SeqRun sr : req.getRuns()) {
            b.addSeqrunId(sr.getId());
        }
        return b.build();
    }

    @Override
    public SearchRequest toModel(SearchRequestDTO dto) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
