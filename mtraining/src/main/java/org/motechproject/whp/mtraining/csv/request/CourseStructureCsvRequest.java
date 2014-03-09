package org.motechproject.whp.mtraining.csv.request;

import static org.apache.commons.lang.StringUtils.isNotBlank;

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

    public boolean isCourse() {
        return NodeType.isCourse(nodeType);
    }

    public boolean hasParent() {
        return isNotBlank(parentNode);
    }

    public boolean isMessage() {
        return NodeType.isMessage(nodeType);
    }

    public boolean hasFileName() {
        return isNotBlank(fileName);
    }

    public boolean hasValidParentType(String parentNodeType) {
        NodeType nodeType = NodeType.from(this.nodeType);
        return nodeType.hasValidParentType(parentNodeType);
    }

    public String getChildNodeType() {
        NodeType childNodeType = NodeType.getChildNodeType(nodeType);
        return childNodeType != null ? childNodeType.name() : "";
    }

    public boolean isValidStatus() {
        return status.equalsIgnoreCase("active") || status.equalsIgnoreCase("inactive");
    }
}
