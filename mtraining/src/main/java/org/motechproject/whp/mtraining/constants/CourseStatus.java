package org.motechproject.whp.mtraining.constants;

public enum CourseStatus {
    ONGOING("ONGOING"),
    COMPLETED("COMPLETED"),
    CLOSED("CLOSED"),
    STARTED("STARTED"),
    UNKNOWN("NOT A VALID COURSE STATUS");

    private String value;

    private CourseStatus(String value) {
        this.value = value;
    }

    public static CourseStatus enumFor(String value) {
        for (CourseStatus courseStatus : values()) {
            if (courseStatus.value.equalsIgnoreCase(value)) {
                return courseStatus;
            }
        }
        return UNKNOWN;
    }

    public boolean isClosed() {
        return CLOSED.equals(this);
    }

    public String getValue() {
        return value;
    }
}
