/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.common;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.Fetcher;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.tree.TreeI;

/**
 *
 * @author sjaenick
 */
public final class HierarchyFetcher extends Fetcher<TreeI<Long>> {

    protected final AttributeTypeI attrType;
    protected final JobI job;

    public HierarchyFetcher(final AttributeTypeI attrType, final JobI job) {
        this.attrType = attrType;
        this.job = job;
    }

    @Override
    protected TreeI<Long> doInBackground() throws Exception {
        MGXMasterI master = attrType.getMaster();
        return master.Attribute().getHierarchy(attrType, job);
    }

//    @Override
//    protected void done() {
//        try {
//            result.add(get());
//        } catch (InterruptedException | ExecutionException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//    }

}
