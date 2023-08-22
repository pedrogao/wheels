package github.io.pedrogao.tinyrpc.core.registry.zookeeper;

import github.io.pedrogao.tinyrpc.core.registry.URL;
import junit.framework.TestCase;

import java.util.List;

public class ZookeeperRegisterTest extends TestCase {

    private ZookeeperRegister zookeeperRegister;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        zookeeperRegister = new ZookeeperRegister("127.0.0.1:2181");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        zookeeperRegister.close();
    }

    public void testGetProviderList() {
        URL url = new URL("testApplication", "com.test.service", "192.168.0.11", 8080);
        zookeeperRegister.register(url);

        List<String> children = zookeeperRegister.getZkClient().getChildrenData(ZookeeperRegister.ROOT + "/com.test.service/provider");
        assertEquals(children.size(), 1);

        List<URL> providerList = zookeeperRegister.getProviderList("com.test.service");
        assertEquals(providerList.size(), 1);
        assertEquals(providerList.get(0).getHost(), "192.168.0.11");
        assertEquals(providerList.get(0).getPort().get().intValue(), 8080);
    }

    public void testRegister() {
        boolean exception = false;
        try {
            URL url = new URL("testApplication", "com.test.service", "192.168.0.11");
            zookeeperRegister.register(url);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        assertTrue(exception);

        URL url = new URL("testApplication", "com.test.service", "192.168.0.11", 8080);
        zookeeperRegister.register(url);

        List<String> children = zookeeperRegister.getZkClient().getChildrenData(ZookeeperRegister.ROOT + "/com.test.service/provider");
        assertEquals(children.size(), 1);
    }

    public void testUnRegister() {
        URL url = new URL("testApplication", "com.test.service", "192.168.0.11", 8080);
        zookeeperRegister.register(url);
        List<String> children = zookeeperRegister.getZkClient().getChildrenData(ZookeeperRegister.ROOT + "/com.test.service/provider");
        assertEquals(children.size(), 1);

        zookeeperRegister.unRegister(url);
        children = zookeeperRegister.getZkClient().getChildrenData(ZookeeperRegister.ROOT + "/com.test.service/provider");
        assertEquals(children.size(), 0);
    }

    public void testSubscribe() {
        URL url = new URL("testApplication", "com.test.service", "192.168.0.33");
        zookeeperRegister.subscribe(url);
        List<String> children = zookeeperRegister.getZkClient().getChildrenData(ZookeeperRegister.ROOT + "/com.test.service/consumer");
        assertEquals(children.size(), 1);
    }

    public void testUnSubscribe() {
        URL url = new URL("testApplication", "com.test.service", "192.168.0.33");
        zookeeperRegister.subscribe(url);
        List<String> children = zookeeperRegister.getZkClient().getChildrenData(ZookeeperRegister.ROOT + "/com.test.service/consumer");
        assertEquals(children.size(), 1);

        zookeeperRegister.unSubscribe(url);
        children = zookeeperRegister.getZkClient().getChildrenData(ZookeeperRegister.ROOT + "/com.test.service/consumer");
        assertEquals(children.size(), 0);
    }

    public void testDoBeforeSubscribe() {
    }

    public void testDoAfterSubscribe() {
    }
}