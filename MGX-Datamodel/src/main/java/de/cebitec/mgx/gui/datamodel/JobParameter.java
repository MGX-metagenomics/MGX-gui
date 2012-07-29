package de.cebitec.mgx.gui.datamodel;

import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class JobParameter extends Identifiable {

    long node_id;
    String user_name;
    String user_desc;
    String displayName;
    String className;
    Map<String, String> choices;
    String configitem_name;
    String configitem_value;
    String type;
    boolean optional;
    String default_value = "";

    public String getConfigItemName() {
        return configitem_name;
    }

    public void setConfigItemName(String configitem_name) {
        this.configitem_name = configitem_name;
    }

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

    public String getConfigItemValue() {
        return configitem_value;
    }

    public void setConfigItemValue(String configitem_value) {
        this.configitem_value = configitem_value;
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
}
