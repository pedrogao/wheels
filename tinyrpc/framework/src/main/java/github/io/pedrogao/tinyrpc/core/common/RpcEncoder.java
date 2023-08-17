package github.io.pedrogao.tinyrpc.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<RpcProtocol> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          RpcProtocol rpcProtocol, ByteBuf out) throws Exception {
        out.writeShort(rpcProtocol.getMagicNumber());
        out.writeInt(rpcProtocol.getContentLength());
        out.writeBytes(rpcProtocol.getContent());
    }
}
