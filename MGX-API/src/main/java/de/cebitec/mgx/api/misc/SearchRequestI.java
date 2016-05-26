/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.misc;

import de.cebitec.mgx.api.model.SeqRunI;

/**
 *
 * @author sj
 */
public interface SearchRequestI {

    SeqRunI[] getRuns();

    String getTerm();

    boolean isExact();

    void setExact(boolean exact);

    void setRuns(SeqRunI[] runs);

    void setTerm(String term);
    
}
