package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.gui.datamodel.Observation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sj
 */
public class SearchResult {

    private String seqName;
    private List<Observation> observations = null;

    public List<Observation> getObservations() {
        return observations;
    }

    public void addObservation(Observation obs) {
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
