package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.api.model.ObservationI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sj
 */
public class SearchResult {

    private String seqName;
    private List<ObservationI> observations = null;

    public List<ObservationI> getObservations() {
        return observations;
    }

    public void addObservation(ObservationI obs) {
        if (observations == null) {
            observations = new ArrayList<>();
        }
        observations.add(obs);
    }

    public String getSequenceName() {
        return seqName;
    }

    public void setSequenceName(String seqName) {
        this.seqName = seqName;
    }
}
