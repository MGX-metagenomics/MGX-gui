/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.Mapping;
import de.cebitec.mgx.gui.datamodel.Reference;

/**
 *
 * @author sj
 */
public class MappingCtx {
    private final Mapping mapping;
    private final Reference reference;
    private final Job job;

    public MappingCtx(Mapping mapping, Reference reference, Job job) {
        this.mapping = mapping;
        this.reference = reference;
        this.job = job;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public Reference getReference() {
        return reference;
    }

    public Job getJob() {
        return job;
    }
}
