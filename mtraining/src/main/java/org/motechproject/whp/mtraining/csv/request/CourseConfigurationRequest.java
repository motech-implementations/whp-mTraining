package org.motechproject.whp.mtraining.csv.request;

import org.motechproject.whp.mtraining.csv.domain.CsvImportError;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.valueOf;
import static org.apache.commons.lang.StringUtils.isBlank;

public class CourseConfigurationRequest {
    private String courseName;
    private String courseDurationInDays;
    private String block;
    private String district;
    private String state;

    public CourseConfigurationRequest() {
    }

    public CourseConfigurationRequest(String courseName, String courseDurationInDays, String block, String district, String state) {
        this.courseName = courseName;
        this.courseDurationInDays = courseDurationInDays;
        this.state = state;
        this.district = district;
        this.block = block;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseDurationInDays() {
        return courseDurationInDays;
    }

    public String getBlock() {
        return block;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseDurationInDays(String courseDurationInDays) {
        this.courseDurationInDays = courseDurationInDays;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setState(String state) {
        this.state = state;
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
        if (isBlank(block) || isBlank(district) || isBlank(state))
            errors.add(new CsvImportError("CourseName", "-", "Missing Location details for: " + courseName + ". District Block and State must be present"));
        return errors;
    }
}
