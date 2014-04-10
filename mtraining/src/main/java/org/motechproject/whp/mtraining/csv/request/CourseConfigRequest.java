package org.motechproject.whp.mtraining.csv.request;

import org.motechproject.whp.mtraining.csv.domain.CsvImportError;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.valueOf;
import static org.apache.commons.lang.StringUtils.isBlank;

public class CourseConfigRequest {
    private String courseName;
    private String courseDurationInDays;

    public CourseConfigRequest() {
    }

    public CourseConfigRequest(String courseName, String courseDurationInDays) {
        this.courseName = courseName;
        this.courseDurationInDays = courseDurationInDays;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseDurationInDays() {
        return courseDurationInDays;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseDurationInDays(String courseDurationInDays) {
        this.courseDurationInDays = courseDurationInDays;
    }

    public List<CsvImportError> validate() {
        List<CsvImportError> errors = newArrayList();
        if (isBlank(courseName))
            errors.add(new CsvImportError("CourseName", "-", "Course Name cannot be null or empty"));
        try {
            if (isBlank(courseDurationInDays) || valueOf(courseDurationInDays) < 1)
                errors.add(new CsvImportError("CourseDuration", "-", "Specify Course Duration greater than 0 days for: " + courseName));
        } catch (NumberFormatException e) {
            errors.add(new CsvImportError("CourseDuration", "-", "Specify Course Duration greater than 0 days for: " + courseName));
        }
        return errors;
    }
}
