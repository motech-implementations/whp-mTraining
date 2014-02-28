package org.motechproject.whp.mtraining.web.model;

public class ErrorModel {
    private static final String DEFAULT = "-";
    private String nodeName;
    private String nodeType;
    private String message;
    public ErrorModel(String nodeName, String nodeType, String message) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.message = message;
    }

    public ErrorModel(String message) {
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
}
