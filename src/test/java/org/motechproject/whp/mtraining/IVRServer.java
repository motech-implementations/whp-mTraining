package org.motechproject.whp.mtraining;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHolder;
import org.motechproject.commons.api.MotechException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IVRServer {

    public static final String DEFAULT_RESPONSE = "{\"success\":true}";
    private final Server server;

    private Map<String, RequestInfo> requests = new HashMap<>();

    public IVRServer(int port, String contextPath) {
        server = new Server(port);
        Context context = new Context(server, contextPath);
        context.addServlet(new ServletHolder(createServlet()), "/*");
        server.setHandler(context);
    }


    public IVRServer start() {
        try {
            server.start();
        } catch (Exception e) {
            throw new MotechException("Stub Sever Could not be started", e);
        }
        return this;
    }


    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new MotechException("Stub Server could not be stopped", e);
        }
    }


    public boolean waitingForRequests() {
        return requests.isEmpty();
    }


    public RequestInfo detailForRequest(String contextPath) {
        return requests.get(contextPath);
    }

    private DefaultServlet createServlet() {
        return new DefaultServlet() {

            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                requests.put(request.getContextPath(), collectRequestInfo(request));
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(DEFAULT_RESPONSE);
                Request baseRequest = (Request) request;
                baseRequest.setHandled(true);
            }

            private RequestInfo collectRequestInfo(HttpServletRequest request) throws IOException {
                Map<String, String> map = new HashMap<>();
                if (request.getMethod().equalsIgnoreCase("Get")) {
                    String queryString = request.getQueryString();
                    if (queryString != null) {
                        collectQueryParameters(map, queryString);
                    }
                }
                if (request.getMethod().equalsIgnoreCase("Post")) {
                    BufferedReader reader = request.getReader();
                    String postBody = IOUtils.toString(reader);
                    map.put("post_body", postBody);
                }
                return new RequestInfo(request.getContextPath(), map);
            }

            private void collectQueryParameters(Map<String, String> map, String queryString) {
                String[] queryParameters = queryString.split("&");
                for (String queryParam : queryParameters) {
                    String[] keyValuePair = queryParam.split("=");
                    String key = keyValuePair[0];
                    String value = (keyValuePair.length == 2) ? keyValuePair[1] : StringUtils.EMPTY;
                    map.put(key, value);
                }
            }
        };
    }


}