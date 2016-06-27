package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.BulkObservation;
import de.cebitec.mgx.dto.dto.BulkObservationDTO;
import de.cebitec.mgx.dto.dto.BulkObservationDTOList;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class BulkObservationDTOFactory extends DTOConversionBase<BulkObservation, BulkObservationDTO> {

    protected final static BulkObservationDTOFactory instance = new BulkObservationDTOFactory();

    private BulkObservationDTOFactory() {
    }

    public static BulkObservationDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final BulkObservationDTO toDTO(BulkObservation bo) {
        return BulkObservationDTO.newBuilder()
                .setSeqrunId(bo.getSeqRunId())
                .setSeqName(bo.getSequenceName())
                .setAttributeId(bo.getAttributeId())
                .setStart(bo.getStart())
                .setStop(bo.getStop())
                .build();
    }

    @Override
    public final BulkObservation toModel(MGXMasterI m, BulkObservationDTO dto) {
        return new BulkObservation(dto.getSeqrunId(), dto.getSeqName(),
                dto.getAttributeId(), dto.getStart(), dto.getStop());
    }

    public final BulkObservationDTOList toDTOList(List<BulkObservation> bol) {
        long runId = -1;
        String seqName = null;
        long attrId = -1;
        int start = -1;
        int stop = -1;

        //
        // sparse dto, only changes are added
        //
        BulkObservationDTOList.Builder b = BulkObservationDTOList.newBuilder();

        for (BulkObservation bo : bol) {
            BulkObservationDTO.Builder bod = BulkObservationDTO.newBuilder();

            if (bo.getSeqRunId() != runId) {
                runId = bo.getSeqRunId();
                bod = bod.setSeqrunId(runId);
            }
            
            if (!bo.getSequenceName().equals(seqName)) {
                seqName = bo.getSequenceName();
                bod = bod.setSeqName(seqName);
            }

            if (bo.getAttributeId() != attrId) {
                attrId = bo.getAttributeId();
                bod = bod.setAttributeId(attrId);
            }
            
            if (bo.getStart() != start) {
                start = bo.getStart();
                bod = bod.setStart(start);
            }
            
            if (bo.getStop() != stop) {
                stop = bo.getStop();
                bod = bod.setStop(stop);
            }
            b.addBulkObservation(bod.build());
        }
        return b.build();
    }
}
