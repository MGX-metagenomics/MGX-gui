package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.common.ToolScope;

/**
 *
 * @author sjaenick
 */
public class Tool extends ToolI {

    protected String name;
    protected String description;
    protected Float version;
    protected String author;
    protected String url;
    protected String xml;
    protected ToolScope scope;

    public Tool(MGXMasterI m) {
        super(m);
    }

    @Override
    public ToolScope getScope() {
        return scope;
    }

    @Override
    public ToolI setScope(ToolScope scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public Tool setAuthor(String author) {
        this.author = author;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Tool setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Tool setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Tool setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public Float getVersion() {
        return version;
    }

    @Override
    public Tool setVersion(Float version) {
        this.version = version;
        return this;
    }

    @Override
    public String getDefinition() {
        return xml;
    }

    @Override
    public Tool setDefinition(String workflowContent) {
        this.xml = workflowContent;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ToolI)) {
            return false;
        }
        ToolI other = (ToolI) object;
        if ((this.id == INVALID_IDENTIFIER && other.getId() != INVALID_IDENTIFIER) || (this.id != INVALID_IDENTIFIER && this.id != other.getId())) {
            return false;
        }
        return true;
    }
}
