package org.motechproject.whp.mtraining.validator;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Validation response used by the {@link CourseStructureValidator} to represent the validation status and the errors
 */
public class CourseStructureValidationResponse {
    private boolean isValid;
    private List<String> errors;

    public CourseStructureValidationResponse() {
        this(true);
    }

    public CourseStructureValidationResponse(boolean valid) {
        isValid = valid;
        errors = new ArrayList<>();
    }

    public void addError(String error) {
        if (StringUtils.isBlank(error)) {
            return;
        }
        isValid = false;
        errors.add(error);
    }

    public String getErrorMessage() {
        return StringUtils.join(errors, ",");
    }

    public boolean isValid() {
        return isValid;
    }
}
