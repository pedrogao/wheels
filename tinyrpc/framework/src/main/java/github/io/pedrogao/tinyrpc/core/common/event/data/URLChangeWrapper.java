package github.io.pedrogao.tinyrpc.core.common.event.data;

import java.util.List;

public class URLChangeWrapper {

    private String serviceName;

    private List<String> providerUrl;

    public String getServiceName() {
        return serviceName;
    }

    public List<String> getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(List<String> childrenDataList) {
        this.providerUrl = childrenDataList;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
