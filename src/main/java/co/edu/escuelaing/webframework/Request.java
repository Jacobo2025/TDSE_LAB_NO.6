package co.edu.escuelaing.webframework;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private final String method;
    private final String path;
    private final Map<String, String> params;

    public Request(String method, String rawTarget) {
        this.method = method;

        int qIndex = rawTarget.indexOf('?');

        if (qIndex != -1) {
            this.path = rawTarget.substring(0, qIndex);

            String queryString = rawTarget.substring(qIndex + 1);
            this.params = parseQuery(queryString);
        } else {
            this.path = rawTarget;
            this.params = new HashMap<>();
        }
    }

    private static Map<String, String> parseQuery(String queryString) {
        Map<String, String> params = new HashMap<>();

        if (queryString == null || queryString.isBlank()) {
            return params;
        }

        for (String pair : queryString.split("&")) {
            String[] kv = pair.split("=", 2);

            String key = URLDecoder.decode(
                    kv[0],
                    StandardCharsets.UTF_8
            );

            String value = kv.length > 1
                    ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
                    : "";

            params.put(key, value);
        }

        return params;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getValue(String name) {
        return params.get(name);
    }
}

