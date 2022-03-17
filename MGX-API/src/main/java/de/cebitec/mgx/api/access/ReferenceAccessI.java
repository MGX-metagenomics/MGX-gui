/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.ReferenceRegionI;
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface ReferenceAccessI extends AccessBaseI<MGXReferenceI> {

    public String getSequence(final MGXReferenceI ref, int from, int to) throws MGXException;

    //public Iterator<ReferenceRegionI> byReferenceInterval(MGXReferenceI ref, int from, int to) throws MGXException;

    public Iterator<MGXReferenceI> listGlobalReferences() throws MGXException;
    
    public UploadBaseI createUploader(File localFile) throws MGXException;

    public TaskI<MGXReferenceI> installGlobalReference(MGXReferenceI obj) throws MGXException;
}
