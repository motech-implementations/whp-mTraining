package org.motechproject.whp.mtraining.constants;

/**
 * Enum to that defines the various status of a course per enrollee
 * <p>{@link org.motechproject.mtraining.constants.CourseStatus#STARTED} : The very first time the course is started</p>
 * <p>{@link org.motechproject.mtraining.constants.CourseStatus#ONGOING} : Enrollee has already taken the first lesson/message</p>
 * <p>{@link org.motechproject.mtraining.constants.CourseStatus#COMPLETED} : Course has been completed</p>
 * <p>{@link org.motechproject.mtraining.constants.CourseStatus#CLOSED} : All formalities (like issuance of certificate etc.) been done</p>
 */

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

    public String value() {
        return value;
    }
}
