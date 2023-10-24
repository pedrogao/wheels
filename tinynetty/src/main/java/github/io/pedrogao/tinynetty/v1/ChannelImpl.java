package github.io.pedrogao.tinynetty.v1;

// ChannelImpl是Channel的具体实现
public class ChannelImpl implements Channel {
    private int port;
    private EventLoop bossEventLoop;
    private EventLoop workerEventLoop;
    private ChannelInitializer channelInitializer;
    private ChannelPipeline pipeline;

    public ChannelImpl(int port, EventLoop bossEventLoop, EventLoop workerEventLoop, ChannelInitializer channelInitializer) {
        this.port = port;
        this.bossEventLoop = bossEventLoop;
        this.workerEventLoop = workerEventLoop;
        this.channelInitializer = channelInitializer;
        this.pipeline = new ChannelPipelineImpl();
        this.initialize();
    }

    private void initialize() {
        // 初始化Channel
        channelInitializer.initChannel(this);
    }

    @Override
    public void write(Object message) {
        // 实现数据写入操作
    }

    @Override
    public void flush() {
        // 实现数据刷新操作
    }

    @Override
    public void close() {
        // 实现关闭操作
    }

    @Override
    public ChannelPipeline pipeline() {
        return pipeline;
    }
}