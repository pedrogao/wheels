package github.io.pedrogao.tinyrpc.examples;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast(new ClientHandler());
            }
        });

        ChannelFuture channelFuture = bootstrap.connect("localhost", 1009).sync();
        int i = 0;
        while (true) {
            if (i >= 10) break;
            Thread.sleep(1000);
            channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("msg from client".getBytes()));
            i++;
        }
    }

    static class ClientHandler extends ChannelInboundHandlerAdapter {
        final Logger log = LoggerFactory.getLogger(ClientHandler.class);

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            String message = byteBuf.toString();
            log.info("channelRead: " + message);
            ReferenceCountUtil.release(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("ClientHandler.exceptionCaught: ", cause);
            Channel channel = ctx.channel();
            if (channel.isActive())
                channel.close();
        }
    }
}
