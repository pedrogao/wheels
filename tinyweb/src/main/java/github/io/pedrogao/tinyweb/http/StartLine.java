package github.io.pedrogao.tinyweb.http;

import github.io.pedrogao.tinyweb.socket.ISocketWrapper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class StartLine {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(StartLine.class);

    static final String START_LINE_PATTERN = "^([A-Z]{3,8}) /(.*) HTTP/(1.1|1.0)$";

    static final Pattern startLineRegex = Pattern.compile(START_LINE_PATTERN);

    private Verb verb;

    private HttpVersion version;

    private String rawValue;

    private String urlPath;

    private Map<String, List<String>> urlParams;

    public static StartLine parse(ISocketWrapper sw) {
        final String startLineStr;
        try {
            startLineStr = sw.readLine();
        } catch (IOException e) {
            log.error("Error reading start line", e);
            throw new RuntimeException(e);
        }
        return new StartLine(startLineStr);
    }

    public StartLine(String rawValue) {
        this.rawValue = rawValue;

        var matcher = startLineRegex.matcher(rawValue);
        var doesMatch = matcher.matches();
        if (!doesMatch) {
            throw new IllegalArgumentException("Invalid start line: " + rawValue);
        }

        extractVerb(matcher.group(1));
        extractUrlPathAndParams(matcher.group(2));
        extractHttpVersion(matcher.group(3));
    }

    private void extractHttpVersion(String group) {
        if (group.equals("1.1")) {
            this.version = HttpVersion.HTTP_1_1;
        } else if (group.equals("1.0")) {
            this.version = HttpVersion.HTTP_1_0;
        } else {
            throw new IllegalArgumentException("Unsupported HTTP version: " + group);
        }
    }

    private void extractUrlPathAndParams(String group) {
        var parts = group.split("\\?");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Invalid URL path: " + group);
        }
        this.urlPath = parts[0];

        if (parts.length > 1) {
            this.urlParams = extractUrlParams(parts[1]);
        }
    }

    private Map<String, List<String>> extractUrlParams(String part) {
        final var tokenizer = new StringTokenizer(part, "&");
        final var params = new HashMap<String, List<String>>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String[] keyValue = token.split("=", 2);
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("Invalid URL param: " + token);
            }
            params.compute(keyValue[0], (key, value) -> {
                if (value == null) {
                    value = new ArrayList<>();
                }
                value.add(keyValue[1]);
                return value;
            });
        }
        return params;
    }

    private void extractVerb(String group) {
        this.verb = Verb.valueOf(group.toUpperCase(Locale.ROOT));
    }

    public enum Verb {
        GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE, CONNECT, PATCH
    }

    public Verb getVerb() {
        return verb;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public String getRawValue() {
        return rawValue;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public Map<String, List<String>> getUrlParams() {
        return urlParams;
    }
}
