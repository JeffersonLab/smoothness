package org.jlab.smoothness.presentation.util;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ryans
 */
public class ServletUtil {

    private ServletUtil() {
        // Can't instantiate publicly
    }

    public static String getCurrentUrl(HttpServletRequest request, Map<String, String> params) {
        Map<String, List<String>> advancedParams = new LinkedHashMap<String, List<String>>();
        for (String s : params.keySet()) {
            List<String> list = new ArrayList<String>();
            list.add(params.get(s));
            advancedParams.put(s, list);
        }

        return getCurrentUrlAdvanced(request, advancedParams);
    }

    public static String getCurrentUrlAdvanced(HttpServletRequest request,
            Map<String, List<String>> advancedParams) {
        return buildUrl(request.getContextPath(), request.getServletPath(), advancedParams, "UTF-8");
    }

    public static String buildUrl(String contextPath, String servletPath,
            Map<String, List<String>> params, String encoding) {
        return contextPath + servletPath + buildQueryString(params, encoding);
    }

    public static String buildQueryString(Map<String, List<String>> params, String encoding) {
        StringBuilder builder = new StringBuilder();

        for (String key : params.keySet()) {
            if (key == null || key.isEmpty()) {
                continue;
            }
            List<String> values = params.get(key);
            for (String value : values) {
                if (value == null) {
                    value = "";
                }
                builder.append("&");
                try {
                    builder.append(URLEncoder.encode(key, encoding));
                    builder.append("=");
                    builder.append(URLEncoder.encode(value, encoding));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("JVM doesn't support encoding: " + encoding, e);
                }
            }
        }

        if (builder.length() > 0) {
            builder.replace(0, 1, "?");
        }

        return builder.toString();
    }
}
