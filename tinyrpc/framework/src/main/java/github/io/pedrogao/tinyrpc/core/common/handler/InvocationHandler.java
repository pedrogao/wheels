package github.io.pedrogao.tinyrpc.core.common.handler;

import github.io.pedrogao.tinyrpc.core.common.Invocation;
import github.io.pedrogao.tinyrpc.core.common.protocol.TinyProtocol;
import github.io.pedrogao.tinyrpc.core.common.serialization.Serializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;

public class InvocationHandler extends ChannelInboundHandlerAdapter {
    final Logger log = LoggerFactory.getLogger(InvocationHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("InvocationHandler.channelRead: " + msg);

        TinyProtocol protocol = (TinyProtocol) msg;
        Serializer serializer = Serializer.getSerializer(protocol.getSerialization());
        Invocation invocation = serializer.deserialize(protocol.getContent(), Invocation.class);

        Object targetObject = PROVIDER_CLASS_MAP.get(invocation.getTargetServiceName());
        Method[] methods = targetObject.getClass().getDeclaredMethods();
        Object result = null;
        for (Method method : methods) {
            if (method.getName().equals(invocation.getTargetMethod())) {
                if (method.getReturnType().equals(Void.TYPE)) {
                    method.invoke(targetObject, invocation.getArgs());
                } else {
                    result = method.invoke(targetObject, invocation.getArgs());
                }
                break;
            }
        }
        invocation.setResponse(result);

        TinyProtocol response = new TinyProtocol(serializer.serialize(invocation));
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("InvocationHandler.exceptionCaught: ", cause);
        Channel channel = ctx.channel();
        if (channel.isActive())
            channel.close();
    }
}
