package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.api.misc.SearchRequestI;
import de.cebitec.mgx.api.model.SeqRunI;

/**
 *
 * @author sjaenick
 */
public class SearchRequest implements SearchRequestI {

    private String term;
    private boolean exact;
    private SeqRunI[] runs;

    @Override
    public boolean isExact() {
        return exact;
    }

    @Override
    public void setExact(boolean exact) {
        this.exact = exact;
    }

    @Override
    public SeqRunI[] getRuns() {
        return runs;
    }

    @Override
    public void setRuns(SeqRunI[] runs) {
        this.runs = runs;
    }

    @Override
    public String getTerm() {
        return term;
    }

    @Override
    public void setTerm(String term) {
        this.term = term;
    }
}
