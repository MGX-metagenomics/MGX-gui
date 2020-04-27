/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;

/**
 *
 * @author sj
 */
public class AssembledSeqRun extends AssembledSeqRunI {

    public AssembledSeqRun(MGXMasterI m, AssemblyI assembly, SeqRunI run) {
        super(m, assembly, run);
    }

    @Override
    public String getName() {
        return run.getName();
    }

    @Override
    public SeqRunI getSeqRun() {
        return run;
    }

    @Override
    public AssemblyI getAssembly() {
        return assembly;
    }

    @Override
    public int compareTo(AssembledSeqRunI t) {
        return this.getName().compareTo(t.getName());
    }

    @Override
    public String toString() {
        return "AssembledSeqRun{" + getName() + '}';
    }

}
