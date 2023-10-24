package github.io.pedrogao.tinynetty.v1;

// ChannelHandler接口表示数据处理器
public interface ChannelHandler {
    void channelRead(ChannelHandlerContext ctx, Object msg);

    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);
}