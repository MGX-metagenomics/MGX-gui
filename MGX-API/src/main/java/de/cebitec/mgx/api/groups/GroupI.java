/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.model.ModelBaseI;

/**
 *
 * @author sj
 */
public interface GroupI<T extends ModelBaseI<T>> extends ModelBaseI<T> {

    public String getName();

    public String getDisplayName();

    public boolean isActive();

}
