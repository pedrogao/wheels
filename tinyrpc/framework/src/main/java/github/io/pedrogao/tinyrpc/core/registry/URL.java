package github.io.pedrogao.tinyrpc.core.registry;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class URL {
    private String applicationName;

    private String serviceName;

    private Map<String, String> parameters = new HashMap<>();

    public URL() {
    }

    public URL(String applicationName, String serviceName) {
        this.applicationName = applicationName;
        this.serviceName = serviceName;
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

    public static String buildProviderUrlStr(URL url) {
        String host = url.getParameters().get("host");
        String port = url.getParameters().get("port");
        return new String((url.getApplicationName() + ";" + url.getServiceName() + ";" + host + ":" + port + ";"
                + System.currentTimeMillis()).getBytes(), StandardCharsets.UTF_8);
    }

    public static String buildConsumerUrlStr(URL url) {
        String host = url.getParameters().get("host");
        return new String((url.getApplicationName() + ";" + url.getServiceName() + ";" + host + ";" +
                System.currentTimeMillis()).getBytes(), StandardCharsets.UTF_8);
    }
}
