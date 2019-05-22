/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;

/**
 *
 * @author sj
 */
public class Assembly extends AssemblyI {
    
    private final String name;
    private final long jobId;

    public Assembly(MGXMasterI master, long id, String name, long asmjob) {
        super(master);
        setId(id);
        this.name = name;
        this.jobId = asmjob;
    }

    @Override
    public long getAssemblyJobId() {
        return jobId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(AssemblyI o) {
        return this.name.compareTo(o.getName());
    }

}
