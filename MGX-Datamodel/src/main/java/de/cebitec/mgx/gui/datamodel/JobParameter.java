package de.cebitec.mgx.gui.datamodel;

import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class JobParameter extends Identifiable {

    private long node_id;
    private String user_name;
    private String user_desc;
    private String displayName;
    private String className;
    private Map<String, String> choices;
    private String parameter_name;
    private String parameter_value;
    private String type;
    private boolean optional;
    private String default_value = "";



    public void setDisplayName(String lDisplayName) {
        displayName = lDisplayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setClassName(String lClassName) {
        className = lClassName;
    }

    public String getClassName() {
        return className;
    }

    public void setChoices(Map<String, String> lChoices) {
        choices = lChoices;
    }

    public Map<String, String> getChoices() {
        return choices;
    }

    public String getDefaultValue() {
        return default_value;
    }

    public void setDefaultValue(String default_value) {
        this.default_value = default_value;
    }

    public long getNodeId() {
        return node_id;
    }

    public void setNodeId(long node_id) {
        this.node_id = node_id;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserDescription() {
        return user_desc;
    }

    public void setUserDescription(String user_desc) {
        this.user_desc = user_desc;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getParameterName() {
        return parameter_name;
    }

    public void setParameterName(String parameter_name) {
        this.parameter_name = parameter_name;
    }

    public String getParameterValue() {
        return parameter_value;
    }

    public void setParameterValue(String parameter_value) {
        this.parameter_value = parameter_value;
    }
}
