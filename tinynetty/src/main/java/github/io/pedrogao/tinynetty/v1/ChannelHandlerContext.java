package github.io.pedrogao.tinynetty.v1;

// ChannelHandlerContext表示ChannelHandler的执行上下文
public interface ChannelHandlerContext {
    Channel channel();

    void fireChannelRead(Object msg);
}
