package org.motechproject.whp.mtraining.csv.response;

import org.motechproject.whp.mtraining.csv.domain.CsvImportError;

import java.util.Collections;
import java.util.List;

public class CsvImportResponse {
    public static final String FAILURE_RESPONSE_STATUS = "FAILURE";
    public static final String SUCCESS_RESPONSE_STATUS = "SUCCESS";
    private String status;
    private String message;
    private List<CsvImportError> errors;

    private CsvImportResponse(String status, String message, List<CsvImportError> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public static CsvImportResponse failure(List<CsvImportError> validationErrors) {
        String errorMessage = "Could not import the CSV due to errors. Please fix the errors and try importing again.";
        return new CsvImportResponse(FAILURE_RESPONSE_STATUS, errorMessage, validationErrors);
    }

    public static CsvImportResponse success(String message) {
        return new CsvImportResponse(SUCCESS_RESPONSE_STATUS, message, Collections.EMPTY_LIST);
    }

    public boolean isSuccess() {
        return SUCCESS_RESPONSE_STATUS.equals(status);
    }

    public boolean isFailure() {
        return FAILURE_RESPONSE_STATUS.equals(status);
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<CsvImportError> getErrors() {
        return errors;
    }
}
