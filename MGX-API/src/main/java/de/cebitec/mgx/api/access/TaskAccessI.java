/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXDataModelBaseI;
import java.util.UUID;

/**
 *
 * @author sj
 */
public interface TaskAccessI<T extends MGXDataModelBaseI<T>> {

    public abstract TaskI<T> get(T obj, UUID taskId, TaskI.TaskType tt) throws MGXException;

    public abstract void refresh(TaskI<T> origTask) throws MGXException;
}
