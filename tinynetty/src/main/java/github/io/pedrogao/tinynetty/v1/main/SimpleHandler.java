package github.io.pedrogao.tinynetty.v1.main;

import github.io.pedrogao.tinynetty.v1.ChannelHandler;
import github.io.pedrogao.tinynetty.v1.ChannelHandlerContext;

class SimpleHandler implements ChannelHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 处理接收到的数据
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 处理异常
    }
}
