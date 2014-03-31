package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;

public class CallLogRequest extends CallDetailsRequest {

    @JsonProperty("content")
    private List<Node> nodes;

    public CallLogRequest() {
    }

    public CallLogRequest(Long callerId, String uniqueId, String sessionId, List<Node> nodes) {
        super(callerId, uniqueId, sessionId);
        this.nodes = nodes;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public ResponseStatus validate() {
        ResponseStatus validationStatus = super.validate();
        if (!validationStatus.isValid()) return validationStatus;
        for (Node node : nodes) {
            ResponseStatus validationResponse = node.validate();
            if (!validationResponse.isValid()) return validationResponse;
        }
        return OK;
    }
}