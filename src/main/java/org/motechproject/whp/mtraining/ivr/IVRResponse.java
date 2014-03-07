package org.motechproject.whp.mtraining.ivr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

public class IVRResponse {

    public static final String MISSING_FILES_DELIMITER = ",";
    private boolean success = false;
    private boolean isNetworkFailure = false;

    private Map<String, String> errors = new HashMap<>();

    public IVRResponse() {
    }

    public IVRResponse(boolean isSuccess) {
        this.success = isSuccess;
    }

    public void markNetworkFailure() {
        this.isNetworkFailure = true;
    }

    public Boolean isNetworkFailure() {
        return isNetworkFailure;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getMissingFiles() {
        List<String> missingFiles = new ArrayList<>();
        if (errors.isEmpty()) {
            return missingFiles;
        }

        String commaLimitedMissingFilesString = errors.get("missingFiles");
        if (isBlank(commaLimitedMissingFilesString)) {
            return missingFiles;
        }

        List<String> fileNames = Arrays.asList(commaLimitedMissingFilesString.split(MISSING_FILES_DELIMITER));
        missingFiles.addAll(fileNames);
        return missingFiles;
    }

    public Boolean hasValidationErrors() {
        return !errors.isEmpty();
    }

    public void markSuccess() {
        this.success = true;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
