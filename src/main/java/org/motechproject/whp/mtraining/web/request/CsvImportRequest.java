package org.motechproject.whp.mtraining.web.request;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class CsvImportRequest {
    private CommonsMultipartFile fileData;

    public CsvImportRequest() {
    }

    public CsvImportRequest(CommonsMultipartFile fileData) {
        this.fileData = fileData;
    }

    public CommonsMultipartFile getFileData() {
        return fileData;
    }

    public void setFileData(CommonsMultipartFile fileData) {
        this.fileData = fileData;
    }

    public String getStringContent() {
        return new String(this.fileData.getBytes());
    }
}
