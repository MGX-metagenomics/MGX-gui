/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface AttributeTypeAccessI {

    public AttributeTypeI create(String name, char valueType, char structure) throws MGXException;

    public AttributeTypeI fetch(long id) throws MGXException;

    public Iterator<AttributeTypeI> fetchall() throws MGXException;

    public Iterator<AttributeTypeI> byJob(JobI job) throws MGXException;

    public TaskI<AttributeTypeI> delete(AttributeTypeI obj) throws MGXException;
}
