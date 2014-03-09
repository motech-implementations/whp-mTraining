package org.motechproject.whp.mtraining.domain.test;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponse;

import java.io.IOException;

public class CustomHttpResponseHandler implements ResponseHandler<CustomHttpResponse> {

    public CustomHttpResponse handleResponse(final HttpResponse response)
            throws IOException {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }

        HttpEntity entity = response.getEntity();
        String content = entity == null ? null : EntityUtils.toString(entity);
        return new CustomHttpResponse(statusLine.getStatusCode(), content);
    }

}
