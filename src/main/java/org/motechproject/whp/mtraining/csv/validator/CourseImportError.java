package org.motechproject.whp.mtraining.csv.validator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CourseImportError {
    private static final String DEFAULT = "-";
    private String nodeName;
    private String nodeType;
    private String message;

    public CourseImportError(String nodeName, String nodeType, String message) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.message = message;
    }

    public CourseImportError(String message) {
        this.message = message;
        this.nodeName = DEFAULT;
        this.nodeType = "-";
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeType() {
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
