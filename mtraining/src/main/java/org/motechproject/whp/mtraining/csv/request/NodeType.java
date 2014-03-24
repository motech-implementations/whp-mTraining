package org.motechproject.whp.mtraining.csv.request;

import org.apache.commons.lang.StringUtils;

public enum NodeType {
    COURSE(null),
    MODULE(COURSE),
    CHAPTER(MODULE),
    MESSAGE(CHAPTER),
    QUESTION(CHAPTER);

    private NodeType parent;

    private NodeType(NodeType parent) {
        this.parent = parent;
    }

    public boolean hasValidParentType(String parentNodeType) {
        return from(parentNodeType).equals(parent);
    }

    public static NodeType from(String nodeType) {
        try {
            return NodeType.valueOf(StringUtils.trimToEmpty(nodeType).toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public static NodeType getChildNodeType(String nodeType) {
        for (NodeType currentNodeType : NodeType.values()) {
            if (from(nodeType).equals(currentNodeType.getParent())) {
                return currentNodeType;
            }
        }
        return null;
    }

    public NodeType getParent() {
        return parent;
    }

    public static boolean isCourse(String nodeType) {
        return NodeType.COURSE.equals(from(nodeType));
    }

    public static boolean isMessage(String nodeType) {
        return NodeType.MESSAGE.equals(from(nodeType));
    }

    public static boolean isQuestion(String nodeType) {
        return NodeType.QUESTION.equals(from(nodeType));
    }

    public static boolean isChapter(String nodeType) {
        return NodeType.CHAPTER.equals(from(nodeType));
    }
}
