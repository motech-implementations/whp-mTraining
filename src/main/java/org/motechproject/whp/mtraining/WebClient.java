package org.motechproject.whp.mtraining;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WebClient {

    public HttpResponse post(String url, String body) throws IOException {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);
        StringEntity entity = new StringEntity(body, "UTF-8");
        request.setEntity(entity);
        return defaultHttpClient.execute(request);
    }
}
