package org.motechproject.whp.mtraining;

import java.util.Map;

public class RequestInfo {

    private final String contextPath;
    private final Map<String, String> map;

    public RequestInfo(String contextPath, Map<String, String> map) {
        this.contextPath = contextPath;
        this.map = map;
    }

    public Map<String, String> getRequestData() {
        return map;
    }

    public String getContextPath() {
        return contextPath;
    }
}
