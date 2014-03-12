package org.motechproject.whp.mtraining.ivr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

public class IVRResponse {

    public static final String MISSING_FILES_DELIMITER = ",";

    private Integer responseCode;

    private String responseMessage;

    public IVRResponse(Integer responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public IVRResponse() {
    }

    public IVRResponse(Integer responseCode) {
        this.responseCode = responseCode;
        this.responseMessage = "";
    }

    public Boolean isNetworkFailure() {
        return IVRResponseCodes.NETWORK_FAILURE.equals(responseCode);
    }

    public boolean isSuccess() {
        return IVRResponseCodes.OK.equals(responseCode);
    }

    public List<String> getMissingFiles() {
        List<String> missingFiles = new ArrayList<>();

        if (isBlank(responseMessage)) {
            return missingFiles;
        }

        List<String> fileNames = Arrays.asList(responseMessage.split(MISSING_FILES_DELIMITER));
        missingFiles.addAll(fileNames);
        return missingFiles;
    }

    public Boolean hasValidationErrors() {
        return IVRResponseCodes.MISSING_FILES.equals(responseCode);
    }


    public Integer getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
