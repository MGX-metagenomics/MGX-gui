package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.gui.datamodel.SeqRun;

/**
 *
 * @author sjaenick
 */
public class SearchRequest {

    private String term;
    private boolean exact;
    private SeqRun[] runs;

    public boolean isExact() {
        return exact;
    }

    public void setExact(boolean exact) {
        this.exact = exact;
    }

    public SeqRun[] getRuns() {
        return runs;
    }

    public void setRuns(SeqRun[] runs) {
        this.runs = runs;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
