package github.io.pedrogao.tinyrpc.core.registry.zookeeper;

import github.io.pedrogao.tinyrpc.core.common.event.Event;
import github.io.pedrogao.tinyrpc.core.common.event.ListenerLoader;
import github.io.pedrogao.tinyrpc.core.common.event.UpdateEvent;
import github.io.pedrogao.tinyrpc.core.common.event.data.URLChangeWrapper;
import github.io.pedrogao.tinyrpc.core.registry.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ZookeeperRegister extends AbstractRegister {

    private final Logger log = LoggerFactory.getLogger(ZookeeperRegister.class);

    private final AbstractZookeeperClient zkClient;

    private final static String ROOT = "/tinyrpc";

    public ZookeeperRegister(String address) {
        this.zkClient = new CuratorZookeeperClient(address);
    }

    private String getProviderPath(URL url) {
        return ROOT + "/" + url.getServiceName() + "/provider/" +
                url.getParameters().get("host") + ":" + url.getParameters().get("port");
    }

    private String getConsumerPath(URL url) {
        return ROOT + "/" + url.getServiceName() + "/consumer/" +
                url.getApplicationName() + ":" + url.getParameters().get("host") + ":";
    }

    @Override
    public List<String> getProviderIpList(String serviceName) {
        return this.zkClient.getChildrenData(ROOT + "/" + serviceName + "/provider");
    }

    @Override
    public void register(URL url) {
        if (!this.zkClient.existNode(ROOT)) {
            zkClient.createPersistentData(ROOT, "");
        }
        String urlStr = URL.buildProviderUrlStr(url);
        if (!zkClient.existNode(getProviderPath(url))) {
            zkClient.createTemporaryData(getProviderPath(url), urlStr);
        } else {
            zkClient.deleteNode(getProviderPath(url));
            zkClient.createTemporaryData(getProviderPath(url), urlStr);
        }
        super.register(url);
    }

    @Override
    public void unRegister(URL url) {
        zkClient.deleteNode(getProviderPath(url));
        super.unRegister(url);
    }

    @Override
    public void subscribe(URL url) {
        if (!this.zkClient.existNode(ROOT)) {
            zkClient.createPersistentData(ROOT, "");
        }
        String urlStr = URL.buildConsumerUrlStr(url);
        if (!zkClient.existNode(getConsumerPath(url))) {
            zkClient.createTemporarySeqData(getConsumerPath(url), urlStr);
        } else {
            zkClient.deleteNode(getConsumerPath(url));
            zkClient.createTemporarySeqData(getConsumerPath(url), urlStr);
        }
        super.subscribe(url);
    }

    @Override
    public void doAfterSubscribe(URL url) {
        // 监听是否有新的服务注册
        String newServerNodePath = ROOT + "/" + url.getServiceName() + "/provider";
        watchChildNodeData(newServerNodePath);
    }

    public void watchChildNodeData(String newServerNodePath) {
        zkClient.watchChildNodeData(newServerNodePath, watchedEvent -> {
            log.info("watch child node data change, path:{}", watchedEvent.getPath());

            String path = watchedEvent.getPath();
            List<String> childrenDataList = zkClient.getChildrenData(path);
            URLChangeWrapper urlChangeWrapper = new URLChangeWrapper();
            urlChangeWrapper.setProviderUrl(childrenDataList);
            urlChangeWrapper.setServiceName(path.split("/")[2]);

            // 自定义的一套事件监听组件
            Event iEvent = new UpdateEvent(urlChangeWrapper);
            ListenerLoader.sendEvent(iEvent);

            // 收到回调之后在注册一次监听，这样能保证一直都收到消息
            watchChildNodeData(path);
        });
    }

    @Override
    public void doBeforeSubscribe(URL url) {
    }

    @Override
    public void unSubscribe(URL url) {
        this.zkClient.deleteNode(getConsumerPath(url));
        super.unSubscribe(url);
    }
}
