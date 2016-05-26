/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.ToolI;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface ToolAccessI {

    public ToolI ByJob(JobI job) throws MGXException;

    public Iterator<ToolI> listGlobalTools() throws MGXException;

    public ToolI create(String name, String description, String author, String webSite, float version, String xmlData) throws MGXException;

    public long installTool(long id) throws MGXException;

    public Collection<JobParameterI> getAvailableParameters(long toolId, boolean isGlobalTool) throws MGXException;

    //public Collection<JobParameterI> getAvailableParameters(ToolI tool) throws MGXException;

    public Collection<JobParameterI> getAvailableParameters(String toolXml) throws MGXException;

    public String getXMLDefinition(ToolI tool) throws MGXException;

    public ToolI fetch(long id) throws MGXException;

    public Iterator<ToolI> fetchall() throws MGXException;

//    public void update(ToolI obj) throws MGXException;

    public TaskI<ToolI> delete(ToolI obj) throws MGXException;
}
