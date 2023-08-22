package github.io.pedrogao.tinyrpc.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TinyEncoder extends MessageToByteEncoder<TinyProtocol> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          TinyProtocol tinyProtocol, ByteBuf out) throws Exception {
        out.writeShort(tinyProtocol.getMagicNumber());
        out.writeInt(tinyProtocol.getContentLength());
        out.writeBytes(tinyProtocol.getContent());
    }
}
