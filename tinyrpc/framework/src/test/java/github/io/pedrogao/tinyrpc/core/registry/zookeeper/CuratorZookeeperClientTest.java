package github.io.pedrogao.tinyrpc.core.registry.zookeeper;

import junit.framework.TestCase;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CuratorZookeeperClientTest extends TestCase {

    private CuratorZookeeperClient client;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        client = new CuratorZookeeperClient("127.0.0.1:2181");
    }

    public void testCreatePersistentData() {
        client.deleteNode("/test/1");
        client.deleteNode("/test");

        client.createPersistentData("/test", "test");
        String nodeData = client.getNodeData("/test");
        assertEquals(nodeData, "test");

        client.createPersistentData("/test/1", "test");
        nodeData = client.getNodeData("/test/1");
        assertEquals(nodeData, "test");
    }

    public void testUpdateNodeData() {
        client.deleteNode("/test/1");
        client.deleteNode("/test");

        client.createPersistentData("/test", "test");
        String nodeData = client.getNodeData("/test");
        assertEquals(nodeData, "test");

        client.updateNodeData("/test", "test1");
        nodeData = client.getNodeData("/test");
        assertEquals(nodeData, "test1");
    }

    public void testGetNodeData() {
        client.deleteNode("/test/1");
        client.deleteNode("/test");

        client.createPersistentData("/test", "test");
        String nodeData = client.getNodeData("/test");
        assertEquals(nodeData, "test");
    }

    public void testGetChildrenData() {
        client.deleteNode("/test/1");
        client.deleteNode("/test");

        client.createPersistentData("/test", "test");
        List<String> childrenData = client.getChildrenData("/test");
        assertEquals(childrenData.size(), 0);

        client.createPersistentData("/test/1", "test");
        childrenData = client.getChildrenData("/test");
        assertEquals(childrenData.size(), 1);
    }

    public void testDeleteNode() {
        client.deleteNode("/test/1");
        client.deleteNode("/test");

        client.createPersistentData("/test", "test");
        String nodeData = client.getNodeData("/test");
        assertEquals(nodeData, "test");

        boolean result = client.deleteNode("/test");
        assertTrue(result);
        nodeData = client.getNodeData("/test");
        assertNull(nodeData);
    }

    public void testExistNode() {
        client.deleteNode("/test/1");
        client.deleteNode("/test");

        boolean ok = client.existNode("/test");
        assertFalse(ok);

        client.createPersistentData("/test", "test");
        ok = client.existNode("/test");
        assertTrue(ok);
    }

    public void testWatchNodeData() throws InterruptedException {
        client.deleteNode("/test/1");
        client.deleteNode("/test");

        client.createPersistentData("/test", "test");

        AtomicBoolean triggered = new AtomicBoolean(false);
        client.watchNodeData("/test", (event) -> {
            System.out.println("event: " + event);
            triggered.set(true);
        });

        client.updateNodeData("/test", "test1");
        TimeUnit.SECONDS.sleep(1);
        assertTrue(triggered.get());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        client.destroy();
    }
}