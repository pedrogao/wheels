package github.io.pedrogao.tinyrpc.core.registry;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class URL {
    private String applicationName;

    private String serviceName;

    private String host;

    // Consumer don't need to set this field, but Provider need to set this field
    private Optional<Integer> port = Optional.empty();

    private Map<String, String> parameters = new HashMap<>();

    public URL() {
    }

    public URL(String applicationName, String serviceName) {
        this.applicationName = applicationName;
        this.serviceName = serviceName;
    }

    public URL(String applicationName, String serviceName, String host) {
        this.applicationName = applicationName;
        this.serviceName = serviceName;
        this.host = host;
    }

    public URL(String applicationName, String serviceName, String host, Integer port) {
        this.applicationName = applicationName;
        this.serviceName = serviceName;
        this.host = host;
        this.port = Optional.of(port);
    }

    public URL(String applicationName, String serviceName, Map<String, String> parameters) {
        this.applicationName = applicationName;
        this.serviceName = serviceName;
        this.parameters = parameters;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Optional<Integer> getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = Optional.of(port);
    }

    public static String buildProviderUrlValue(URL url) {
        if (url.getPort().isEmpty()) {
            throw new IllegalArgumentException("provider port is empty");
        }

        return url.getApplicationName() + ";" + url.getServiceName() + ";" + url.getHost() + ";" + url.getPort().get() + ";" + new Date();
    }

    public static URL parseProviderUrlValue(String value) {
        if (value == null || value.isEmpty()) return null;

        String[] values = value.split(";");
        if (values.length != 5)
            return null;
        return new URL(values[0], values[1], values[2], Integer.parseInt(values[3]));
    }

    public static String buildConsumerUrlValue(URL url) {
        return url.getApplicationName() + ";" + url.getServiceName() + ";" + url.getHost() + ";" + new Date();
    }

    public static URL parseConsumerUrlValue(String value) {
        if (value == null || value.isEmpty()) return null;

        String[] values = value.split(";");
        if (values.length != 4)
            return null;
        return new URL(values[0], values[1], values[2]);
    }
}
