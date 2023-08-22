package github.io.pedrogao.tinyrpc.core.registry.zookeeper;

import com.google.common.annotations.VisibleForTesting;
import github.io.pedrogao.tinyrpc.core.registry.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZookeeperRegister extends AbstractRegister {

    private final Logger log = LoggerFactory.getLogger(ZookeeperRegister.class);

    private final AbstractZookeeperClient zkClient;

    public final static String ROOT = "/tinyrpc";

    public ZookeeperRegister(String address) {
        this.zkClient = new CuratorZookeeperClient(address);
    }

    private String getProviderPath(URL url) {
        if (url.getPort().isEmpty())
            throw new IllegalArgumentException("url port is empty");

        // /tinyrpc/{serviceName}/provider/${host}:${port}
        return ROOT + "/" +
                url.getServiceName() +
                "/provider/" +
                url.getHost() +
                ":" +
                url.getPort().get();
    }

    private String getConsumerPath(URL url) {

        // /tinyrpc/{serviceName}/consumer/${host}
        return ROOT + "/" +
                url.getServiceName() +
                "/consumer/" +
                url.getHost();
    }

    @Override
    public List<URL> getProviderList(String serviceName) {
        if (serviceName == null || serviceName.isEmpty())
            return Collections.emptyList();

        List<String> providerPathList = zkClient.getChildrenData(ROOT + "/" + serviceName + "/provider");
        if (providerPathList == null || providerPathList.isEmpty())
            return Collections.emptyList();

        List<URL> providerList = new ArrayList<>();

        for (String providerPath : providerPathList) {
            String providerData = zkClient.getNodeData(ROOT + "/" + serviceName + "/provider/" + providerPath);
            if (providerData == null || providerData.isEmpty())
                continue;

            URL url = URL.parseProviderUrlValue(providerData);
            if (url == null)
                continue;

            providerList.add(url);
        }
        return providerList;
    }

    @Override
    public void register(URL url) {
        if (!zkClient.existNode(ROOT)) {
            zkClient.createPersistentData(ROOT, "");
        }

        String value = URL.buildProviderUrlValue(url);
        String providerPath = getProviderPath(url);

        if (!zkClient.existNode(providerPath)) {
            zkClient.createTemporaryData(providerPath, value);
        } else {
            zkClient.deleteNode(providerPath);
            zkClient.createTemporaryData(providerPath, value);
        }
        super.register(url);
    }

    @Override
    public void unRegister(URL url) {
        String providerPath = getProviderPath(url);
        zkClient.deleteNode(providerPath);

        super.unRegister(url);
    }

    @Override
    public void subscribe(URL url) {
        if (!zkClient.existNode(ROOT)) {
            zkClient.createPersistentData(ROOT, "");
        }

        String value = URL.buildConsumerUrlValue(url);
        String consumerPath = getConsumerPath(url);

        if (!zkClient.existNode(consumerPath)) {
            zkClient.createTemporaryData(consumerPath, value);
        } else {
            zkClient.deleteNode(consumerPath);
            zkClient.createTemporaryData(consumerPath, value);
        }
        super.subscribe(url);
    }

    @Override
    public void doBeforeSubscribe(URL url) {
        // TODO
    }

    @Override
    public void doAfterSubscribe(URL url) {
        // TODO

        // String newServerNodePath = ROOT + "/" + url.getServiceName() + "/provider";
        // watchChildNodeData(newServerNodePath);
    }

    @Override
    public void unSubscribe(URL url) {
        String consumerPath = getConsumerPath(url);

        this.zkClient.deleteNode(consumerPath);
        super.unSubscribe(url);
    }

    public void watchChildNodeData(String newServerNodePath) {
        zkClient.watchChildNodeData(newServerNodePath, watchedEvent -> {
            log.info("watch child node data change, path:{}", watchedEvent.getPath());

            String path = watchedEvent.getPath();
            // 收到回调之后在注册一次监听，这样能保证一直都收到消息
            watchChildNodeData(path);
        });
    }

    @Override
    public void close() {
        zkClient.destroy();
    }

    @VisibleForTesting
    protected AbstractZookeeperClient getZkClient() {
        return zkClient;
    }
}
