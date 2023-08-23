package github.io.pedrogao.tinyrpc.core.common.handler;

import github.io.pedrogao.tinyrpc.core.common.Invocation;
import github.io.pedrogao.tinyrpc.core.common.protocol.TinyProtocol;
import github.io.pedrogao.tinyrpc.core.common.serialization.Serializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.RESP_MAP;

public class InvocationSender extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(InvocationSender.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TinyProtocol protocol = (TinyProtocol) msg;
        byte[] content = protocol.getContent();

        Serializer serializer = Serializer.getSerializer(protocol.getSerialization());
        Invocation invocation = serializer.deserialize(content, Invocation.class);
        if (!RESP_MAP.containsKey(invocation.getUuid())) {
            log.error("InvocationSender.channelRead: no such uuid in RESP_MAP");
            return;
        }

        RESP_MAP.put(invocation.getUuid(), invocation);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("InvocationSender err", cause);
        Channel channel = ctx.channel();
        if (channel.isActive())
            channel.close();
    }
}
