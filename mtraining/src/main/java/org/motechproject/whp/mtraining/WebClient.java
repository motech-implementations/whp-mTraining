package org.motechproject.whp.mtraining;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Component
public class WebClient {

    public HttpResponse post(String url, String body) throws IOException {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost();
        addURIComponentsToRequest(url, request);
        StringEntity entity = new StringEntity(body, "UTF-8");
        request.setEntity(entity);
        return defaultHttpClient.execute(request);
    }

    private void addURIComponentsToRequest(String url, HttpPost request) {
        UriComponentsBuilder uriComponentsBuilder = ServletUriComponentsBuilder.fromUriString(url);
        UriComponents build = uriComponentsBuilder.build();
        String userInfo = build.getUserInfo();
        if (userInfo != null && !userInfo.isEmpty()) {
            addAuthHeader(request, userInfo);
        }
        request.setURI(build.toUri());
    }

    private void addAuthHeader(HttpUriRequest request, String userinfo) {
        request.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64(userinfo.getBytes())));
    }
}
