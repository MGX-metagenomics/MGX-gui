/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.misc;

import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.SequenceI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author sj
 */
public class BulkObservationList {

    private final List<BulkObservation> obs = new ArrayList<>();

    public BulkObservationList() {
    }

    public final void addObservation(SequenceI seq, AttributeI attr, int start, int stop) {
        obs.add(new BulkObservation(seq.getId(), attr.getId(), start, stop));
    }

    public final List<BulkObservation> getObservations() {
        return Collections.unmodifiableList(obs);
    }

    public static class BulkObservation {

        private final long seqId;
        private final long attrId;
        private final int start;
        private final int stop;

        public BulkObservation(long seqId, long attrId, int start, int stop) {
            this.seqId = seqId;
            this.attrId = attrId;
            this.start = start;
            this.stop = stop;
        }

        public final long getSequenceId() {
            return seqId;
        }

        public final long getAttributeId() {
            return attrId;
        }

        public final int getStart() {
            return start;
        }

        public final int getStop() {
            return stop;
        }

    }

}
