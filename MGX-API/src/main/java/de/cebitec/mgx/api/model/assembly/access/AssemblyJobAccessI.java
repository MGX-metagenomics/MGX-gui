/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssemblyJobI;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface AssemblyJobAccessI {

    public AssemblyJobI create(Collection<SeqRunI> runsets) throws MGXException;

    public AssemblyJobI fetch(long id) throws MGXException;

    public Iterator<AssemblyJobI> fetchall() throws MGXException;
}
