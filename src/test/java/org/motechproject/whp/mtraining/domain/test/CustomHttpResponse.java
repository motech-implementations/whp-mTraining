package org.motechproject.whp.mtraining.domain.test;

public class CustomHttpResponse {

    private int statusCode;
    private String content;

    public CustomHttpResponse(int statusCode, String content) {
        this.statusCode = statusCode;
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContent() {
        return content;
    }
}
