package github.io.pedrogao.tinyrpc.examples;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {
    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;

    public static void main(String[] args) throws InterruptedException {
        final Logger log = LoggerFactory.getLogger(NettyServer.class);

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_SNDBUF, 16 * 1024)
                .option(ChannelOption.SO_RCVBUF, 16 * 1024)
                .option(ChannelOption.SO_KEEPALIVE, true);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                log.info("initChannel ch: " + channel);

                channel.pipeline().addLast(new StringEncoder());
                channel.pipeline().addLast(new StringDecoder());
                channel.pipeline().addLast(new ServerHandler());
            }
        });
        bootstrap.bind(1009).sync();
        log.info("Server started!");
    }

    static class ServerHandler extends ChannelInboundHandlerAdapter {
        final Logger log = LoggerFactory.getLogger(ServerHandler.class);

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("ServerHandler.channelRead: " + msg);
            ctx.writeAndFlush(Unpooled.copiedBuffer("msg from server".getBytes()));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("ServerHandler.exceptionCaught: ", cause);
            Channel channel = ctx.channel();
            if (channel.isActive())
                channel.close();
        }
    }
}
