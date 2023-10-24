package github.io.pedrogao.tinynetty.v1;

// ChannelPipelineImpl是ChannelPipeline的具体实现
class ChannelPipelineImpl implements ChannelPipeline {
    @Override
    public ChannelPipeline addLast(String name, ChannelHandler handler) {
        // 实现添加处理器到链中
        return this;
    }

    @Override
    public ChannelPipeline fireChannelRead(Object msg) {
        // 实现数据传递和处理
        return this;
    }
}
