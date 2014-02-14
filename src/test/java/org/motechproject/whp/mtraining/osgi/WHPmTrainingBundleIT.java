package org.motechproject.whp.mtraining.osgi;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;

import java.io.IOException;

public class WHPmTrainingBundleIT extends BaseOsgiIT {

    PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 60);

    public void testThatStatusUrlIsAccessible() throws IOException, InterruptedException {
        HttpResponse httpResponse = httpClient.get(String.format("http://localhost:%s/whp-mtraining/status", TestContext.getJettyPort()));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

}
