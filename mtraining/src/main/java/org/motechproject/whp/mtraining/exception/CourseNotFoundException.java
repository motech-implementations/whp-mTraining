package org.motechproject.whp.mtraining.exception;

/**
 * Exception thrown when a Course is not found with the provided Content Id and Version.
 */

public class CourseNotFoundException extends RuntimeException {

    private String courseName;

    public CourseNotFoundException(String courseName) {
        super(String.format("Course with name %s not found", courseName));
        this.courseName = courseName;
    }

    public CourseNotFoundException() {
        super("Course not avaiable");
    }

    public String getCourseName() {
        return courseName;
    }
}
