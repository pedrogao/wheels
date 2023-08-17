package github.io.pedrogao.tinyrpc.core.server;

import com.alibaba.fastjson.JSON;
import github.io.pedrogao.tinyrpc.core.common.RpcInvocation;
import github.io.pedrogao.tinyrpc.core.common.RpcProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;

class ServerHandler extends ChannelInboundHandlerAdapter {
    final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("ServerHandler.channelRead: " + msg);

        RpcProtocol rpcProtocol = (RpcProtocol) msg;
        String json = new String(rpcProtocol.getContent(), 0, rpcProtocol.getContentLength());
        RpcInvocation invocation = JSON.parseObject(json, RpcInvocation.class);

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
        RpcProtocol response = new RpcProtocol(JSON.toJSONString(invocation).getBytes());
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ServerHandler.exceptionCaught: ", cause);
        Channel channel = ctx.channel();
        if (channel.isActive())
            channel.close();
    }
}
