/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import java.util.Map;

/**
 *
 * @author sj
 */
public abstract class JobParameterI implements Comparable<JobParameterI> {

    //
    //public static final DataFlavor DATA_FLAVOR = new DataFlavor(JobParameterI.class, "JobParameterI");
    protected long id = Identifiable.INVALID_IDENTIFIER;

    public JobParameterI() { //MGXMasterI m) {
        //super(m, DATA_FLAVOR);
        super();
    }

    public final void setId(long id) {
        this.id = id;
    }

    public final long getId() {
        return id;
    }

    public abstract void setDisplayName(String lDisplayName);

    public abstract String getDisplayName();

    public abstract void setClassName(String lClassName);

    public abstract String getClassName();

    public abstract void setChoices(Map<String, String> lChoices);

    public abstract Map<String, String> getChoices();

    public abstract String getDefaultValue();

    public abstract void setDefaultValue(String default_value);

    public abstract long getNodeId();

    public abstract void setNodeId(long node_id);

    public abstract boolean isOptional();

    public abstract void setOptional(boolean optional);

    public abstract String getType();

    public abstract void setType(String type);

    public abstract String getUserDescription();

    public abstract void setUserDescription(String user_desc);

    public abstract String getUserName();

    public abstract void setUserName(String user_name);

    public abstract String getParameterName();

    public abstract void setParameterName(String parameter_name);

    public abstract String getParameterValue();

    public abstract void setParameterValue(String parameter_value);

    @Override
    public abstract int compareTo(JobParameterI o);

}
