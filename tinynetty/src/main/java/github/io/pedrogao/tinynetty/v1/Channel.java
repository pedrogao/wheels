package github.io.pedrogao.tinynetty.v1;

// Channel接口表示网络通道
public interface Channel {
    void write(Object message);

    void flush();

    void close();

    ChannelPipeline pipeline();
}
