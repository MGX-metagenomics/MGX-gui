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
    private final long reads_assembled;
    private final long n50;
    private final long numCDS;

    public Assembly(MGXMasterI master, long id, String name, long reads_assembled, long n50, long numCDS, long asmjob) {
        super(master);
        setId(id);
        this.name = name;
        this.reads_assembled = reads_assembled;
        this.n50 = n50;
        this.numCDS = numCDS;
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
    public long getReadsAssembled() {
        return reads_assembled;
    }

    @Override
    public long getN50() {
        return n50;
    }

    @Override
    public long getNumberCDS() {
        return numCDS;
    }

    @Override
    public int compareTo(AssemblyI o) {
        return this.name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return "Assembly{" + "name=" + name + '}';
    }
}
