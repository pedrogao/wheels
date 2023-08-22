package github.io.pedrogao.tinyrpc.core.client;

import com.alibaba.fastjson.JSON;
import github.io.pedrogao.tinyrpc.core.common.Invocation;
import github.io.pedrogao.tinyrpc.core.common.TinyProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.RESP_MAP;

public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(RpcClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TinyProtocol tinyProtocol = (TinyProtocol) msg;
        byte[] content = tinyProtocol.getContent();

        Invocation invocation = JSON.parseObject(content, Invocation.class);
        if (!RESP_MAP.containsKey(invocation.getUuid())) {
            log.error("ClientHandler.channelRead: no such uuid in RESP_MAP");
            return;
        }

        RESP_MAP.put(invocation.getUuid(), invocation);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ClientHandler err", cause);
        Channel channel = ctx.channel();
        if (channel.isActive())
            channel.close();
    }
}
