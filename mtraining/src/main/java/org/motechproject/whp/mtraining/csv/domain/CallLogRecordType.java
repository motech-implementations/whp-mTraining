package org.motechproject.whp.mtraining.csv.domain;

import org.apache.commons.lang.StringUtils;

public enum CallLogRecordType {
    COURSE(null),
    MODULE(COURSE),
    CHAPTER(MODULE),
    MESSAGE(CHAPTER),
    QUESTION(CHAPTER);

    private CallLogRecordType parent;

    private CallLogRecordType(CallLogRecordType parent) {
        this.parent = parent;
    }

    public boolean hasValidParentType(String parentNodeType) {
        return from(parentNodeType).equals(parent);
    }

    public static CallLogRecordType from(String nodeType) {
        try {
            return CallLogRecordType.valueOf(StringUtils.trimToEmpty(nodeType).toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public static CallLogRecordType getChildNodeType(String nodeType) {
        for (CallLogRecordType currentCallLogRecordType : CallLogRecordType.values()) {
            if (from(nodeType).equals(currentCallLogRecordType.getParent())) {
                return currentCallLogRecordType;
            }
        }
        return null;
    }

    public CallLogRecordType getParent() {
        return parent;
    }

    public static boolean isCourse(String nodeType) {
        return CallLogRecordType.COURSE.equals(from(nodeType));
    }

    public static boolean isMessage(String nodeType) {
        return CallLogRecordType.MESSAGE.equals(from(nodeType));
    }

    public static boolean isQuestion(String nodeType) {
        return CallLogRecordType.QUESTION.equals(from(nodeType));
    }

    public static boolean isChapter(String nodeType) {
        return CallLogRecordType.CHAPTER.equals(from(nodeType));
    }
}
