package org.motechproject.whp.mtraining.web.model;

public class ErrorModel {
    String nodeName;
    String nodeType;
    String message;
    public ErrorModel(String nodeName, String nodeType, String message) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.message = message;
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
}
