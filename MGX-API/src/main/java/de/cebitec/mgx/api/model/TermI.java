/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.model;

/**
 *
 * @author sj
 */
public abstract class TermI implements Comparable<TermI> {

    public abstract long getId();

    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract void setId(long id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract long getParentId();

    public abstract void setParentId(long parent_id);

    @Override
    public abstract String toString();

    @Override
    public int compareTo(TermI o) {
        return getName().compareTo(o.getName());
    }
}
