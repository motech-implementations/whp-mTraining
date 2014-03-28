package org.motechproject.whp.mtraining.csv.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import static org.apache.commons.lang.StringUtils.isBlank;

public class CsvImportError {
    private static final String DEFAULT = "-";
    private String nodeName;
    private String nodeType;
    private String message;

    public CsvImportError(String nodeName, String nodeType, String message) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.message = message;
    }

    public CsvImportError(String message) {
        this.message = message;
        this.nodeName = DEFAULT;
        this.nodeType = "-";
    }

    public String getNodeName() {
        if (isBlank(nodeName)) {
            nodeName = "-";
        }
        return nodeName;
    }

    public String getNodeType() {
        if (isBlank(nodeType)) {
            nodeType = "-";
        }
        return nodeType;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
