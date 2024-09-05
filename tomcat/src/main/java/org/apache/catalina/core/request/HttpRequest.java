package org.apache.catalina.core.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    public final String request;

    public HttpRequest(String request) {
        this.request = request;
    }

    public String getMethod() {
        String startLine = getStartLine();
        return startLine.split(" ")[0];
    }

    public String getQueryString() {
        String path = getStartLine().split(" ")[1];
        int i = path.lastIndexOf("?");
        if (i == -1) {
            return "";
        }
        return path.substring(i + 1);
    }

    public String getRequestURI() { // 리소스만
        String requestTarget = getRequestTarget();
        int queryStringIndex = requestTarget.indexOf('?');
        if (queryStringIndex == -1) {
            return requestTarget;
        }
        return requestTarget.substring(0, queryStringIndex);
    }

    public StringBuffer getRequestURL() { // 전체 다
        String[] startLine = getStartLine().split(" ");
        StringBuffer sb = new StringBuffer();
        sb.append(getScheme()).append("://").append(getServerName()).append(":").append(getLocalPort());
        sb.append(startLine[1]);
        return sb;
    }

    private String getRequestTarget() {
        return getStartLine().split(" ")[1];
    }

    private String getStartLine() {
        return request.split("\n")[0];
    }

    private String getRequestBody() {
        String[] requestContent = request.split("\r\n\r\n");
        if (requestContent.length > 1) {
            return requestContent[1];
        }
        return "";
    }

    public String getServletPath() {
        return "/";
    }

    public String getParameter(String s) {
        String[] parameterValues = getParameterValues(s);
        if (parameterValues.length == 0) {
            return null;
        }
        return getParameterValues(s)[0];
    }

    public Enumeration<String> getParameterNames() {
        Map<String, String[]> map = getParameterMap();
        return Collections.enumeration(map.keySet());
    }

    public String[] getParameterValues(String s) {
        Map<String, String[]> map = getParameterMap();
        if (map.containsKey(s)) {
            return map.get(s);
        }
        return new String[0];
    }


    public Map<String, String[]> getParameterMap() { // TODO getRequestURL 잘못 쓰임
        Map<String, String[]> map = new LinkedHashMap<>();
        if (getMethod().equals("GET")) {
            addRequestBodyParam(map);
            addQueryStringParam(map);
            return map;
        }
        if (getMethod().equals("POST")) {
            addQueryStringParam(map);
            addRequestBodyParam(map);
            return map;
        }
        return map;
    }

    private void addRequestBodyParam(Map<String, String[]> map) {
        String requestBody = getRequestBody();
        parseParam(requestBody, map);
    }

    private void addQueryStringParam(Map<String, String[]> map) {
        String requestUrl = getRequestURL().toString();
        int i = requestUrl.indexOf('?');
        if (i == -1) {
            return;
        }
        String query = requestUrl.substring(i + 1);
        parseParam(query, map);
    }

    private void parseParam(String query, Map<String, String[]> map) {
        if (query.isEmpty()) {
            return;
        }

        String[] params = query.split("&");
        for (String param : params) {
            String[] split = param.split("=");
            String key = split[0];
            String[] values = Arrays.stream(split[1].split(","))
                    .map(value -> URLDecoder.decode(value, StandardCharsets.UTF_8))
                    .toArray(String[]::new);
            map.put(key, values);
        }
    }

    public String getScheme() {
        return "http";
    }


    public String getServerName() {
        return "localhost";
    }

    public int getLocalPort() {
        return 8080;
    }
}
