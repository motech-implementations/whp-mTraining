package org.motechproject.whp.mtraining;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class WebClient {

    public HttpResponse post(String url, String body, Properties headers) throws IOException {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);
        for (Object key : headers.keySet()) {
            String headerName = key.toString();
            request.addHeader(headerName, headers.getProperty(headerName));
        }
        StringEntity entity = new StringEntity(body, "UTF-8");
        request.setEntity(entity);
        return defaultHttpClient.execute(request);
    }

}
