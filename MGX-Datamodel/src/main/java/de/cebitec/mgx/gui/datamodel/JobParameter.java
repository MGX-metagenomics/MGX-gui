package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.model.JobParameterI;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class JobParameter extends JobParameterI {

    private long node_id;
    private String user_name;
    private String user_desc;
    private String displayName;
    private String className;
    private Map<String, String> choices;
    private String parameter_name;
    private String parameter_value = null;
    private String type;
    private boolean optional;
    private String default_value = "";

    public JobParameter() { //MGXMasterI m) {
        super();
    }

    @Override
    public void setDisplayName(String lDisplayName) {
        displayName = lDisplayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setClassName(String lClassName) {
        className = lClassName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setChoices(Map<String, String> lChoices) {
        choices = lChoices;
    }

    @Override
    public Map<String, String> getChoices() {
        return choices;
    }

    @Override
    public String getDefaultValue() {
        return default_value;
    }

    @Override
    public void setDefaultValue(String default_value) {
        this.default_value = default_value;
    }

    @Override
    public long getNodeId() {
        return node_id;
    }

    @Override
    public void setNodeId(long node_id) {
        this.node_id = node_id;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getUserDescription() {
        return user_desc;
    }

    @Override
    public void setUserDescription(String user_desc) {
        this.user_desc = user_desc;
    }

    @Override
    public String getUserName() {
        return user_name;
    }

    @Override
    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    @Override
    public String getParameterName() {
        return parameter_name;
    }

    @Override
    public void setParameterName(String parameter_name) {
        this.parameter_name = parameter_name;
    }

    @Override
    public String getParameterValue() {
        return parameter_value;
    }

    @Override
    public void setParameterValue(String parameter_value) {
        this.parameter_value = parameter_value;
    }

    @Override
    public int compareTo(JobParameterI o) {
        return Long.compare(node_id, o.getNodeId());
    }
}
