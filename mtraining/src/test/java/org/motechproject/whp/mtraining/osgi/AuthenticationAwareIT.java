package org.motechproject.whp.mtraining.osgi;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class AuthenticationAwareIT extends BaseOsgiIT {

    private static final Integer TRIES_COUNT = 100;
    private static final String PERMISSION_NAME = "test-permission";
    private static final String ROLE_NAME = "test-role";
    private static final String SECURITY_ADMIN = "Security Admin";
    private static final String USER_NAME = "whp";
    private static final String USER_PASSWORD = "whp";
    private static final String USER_EMAIL = "whp-test@email.com";
    private static final String USER_EXTERNAL_ID = "test-externalId";
    private static final Locale USER_LOCALE = Locale.ENGLISH;
    private static final String BUNDLE_NAME = "bundle";

    protected HttpUriRequest httpRequestWithAuthHeaders(String url, String httpMethod) {
        HttpUriRequest httpUriRequest = null;
        if ("GET".equalsIgnoreCase(httpMethod)) {
            httpUriRequest = new HttpGet(url);
        }
        if ("POST".equalsIgnoreCase(httpMethod)) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpUriRequest = httpPost;
        }

        addAuthHeader(httpUriRequest, USER_NAME, USER_PASSWORD);
        return httpUriRequest;
    }


    @Override
    public void onSetUp() throws InterruptedException, IOException {
        MotechPermissionService permissions = getService(MotechPermissionService.class);
        MotechRoleService roles = getService(MotechRoleService.class);
        MotechUserService users = getService(MotechUserService.class);

        PermissionDto permission = new PermissionDto(PERMISSION_NAME, BUNDLE_NAME);
        RoleDto role = new RoleDto(ROLE_NAME, Arrays.asList(PERMISSION_NAME));

        permissions.addPermission(permission);
        roles.createRole(role);
        users.register(USER_NAME, USER_PASSWORD, USER_EMAIL, USER_EXTERNAL_ID, Arrays.asList(ROLE_NAME, SECURITY_ADMIN), USER_LOCALE);
    }


    private void addAuthHeader(HttpUriRequest request, String userName, String password) {
        request.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));
    }


    private <T> T getService(Class<T> clazz) throws InterruptedException {
        T service = clazz.cast(bundleContext.getService(getServiceReference(clazz)));

        assertNotNull(String.format("Service %s is not available", clazz.getName()), service);

        return service;
    }

    private <T> ServiceReference getServiceReference(Class<T> clazz) throws InterruptedException {
        ServiceReference serviceReference;
        int tries = 0;

        do {
            serviceReference = bundleContext.getServiceReference(clazz.getName());
            ++tries;
            Thread.sleep(2000);
        } while (serviceReference == null && tries < TRIES_COUNT);

        assertNotNull(String.format("Not found service reference for %s", clazz.getName()), serviceReference);

        return serviceReference;
    }

}
