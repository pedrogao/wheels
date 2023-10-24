package github.io.pedrogao.tinynetty.v1;

// ChannelPipeline接口用于管理数据处理链
public interface ChannelPipeline {
    ChannelPipeline addLast(String name, ChannelHandler handler);

    ChannelPipeline fireChannelRead(Object msg);
}
