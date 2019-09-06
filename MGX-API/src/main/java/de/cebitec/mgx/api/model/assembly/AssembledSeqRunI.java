/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly;

import de.cebitec.mgx.api.model.*;
import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;
import java.util.Objects;

/**
 *
 * @author sj
 */
public abstract class AssembledSeqRunI extends Identifiable<AssembledSeqRunI> {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(AssembledSeqRunI.class, "AssembledSeqRunI");

    protected final AssemblyI assembly;
    protected final SeqRunI run;

    public AssembledSeqRunI(MGXMasterI m, AssemblyI assembly, SeqRunI run) {
        super(m, DATA_FLAVOR);
        this.assembly = assembly;
        this.run = run;
        run.addPropertyChangeListener(this);
        assembly.addPropertyChangeListener(this);
    }

    public abstract String getName();

    public abstract SeqRunI getSeqRun();

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.assembly);
        hash = 67 * hash + Objects.hashCode(this.run);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AssembledSeqRunI other = (AssembledSeqRunI) obj;
        if (!Objects.equals(this.assembly, other.assembly)) {
            return false;
        }
        if (!Objects.equals(this.run, other.run)) {
            return false;
        }
        return true;
    }

}
