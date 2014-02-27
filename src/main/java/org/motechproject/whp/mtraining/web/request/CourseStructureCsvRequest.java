package org.motechproject.whp.mtraining.web.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CourseStructureCsvRequest {
    private String nodeName;
    private String nodeType;
    private String status;
    private String parentNode;
    private String description;
    private String fileName;

    public CourseStructureCsvRequest() {
    }

    public CourseStructureCsvRequest(String nodeName, String nodeType, String status, String parentNode, String description, String fileName) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.status = status;
        this.parentNode = parentNode;
        this.description = description;
        this.fileName = fileName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getStatus() {
        return status;
    }

    public String getParentNode() {
        return parentNode;
    }

    public String getDescription() {
        return description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setParentNode(String parentNode) {
        this.parentNode = parentNode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
