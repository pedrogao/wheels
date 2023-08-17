package github.io.pedrogao.tinyrpc.core.client;

import com.alibaba.fastjson.JSON;
import github.io.pedrogao.tinyrpc.core.common.RpcInvocation;
import github.io.pedrogao.tinyrpc.core.common.RpcProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.RESP_MAP;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcProtocol rpcProtocol = (RpcProtocol) msg;
        byte[] content = rpcProtocol.getContent();
        RpcInvocation invocation = JSON.parseObject(new String(content, 0, content.length), RpcInvocation.class);
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
