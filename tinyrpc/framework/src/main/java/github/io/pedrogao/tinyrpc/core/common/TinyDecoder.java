package github.io.pedrogao.tinyrpc.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static github.io.pedrogao.tinyrpc.core.common.constants.ProtocolConstant.MAGIC_NUMBER;

public class TinyDecoder extends ByteToMessageDecoder {

    public final int BASE_LENGTH = 2 + 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        // magicNumber + contentLength
        if (byteBuf.readableBytes() < BASE_LENGTH) {
            return;
        }

        short magicNumber = byteBuf.readShort();
        if (magicNumber != MAGIC_NUMBER) {
            byteBuf.resetReaderIndex(); // magic number invalid, reset reader index
            return;
        }

        int length = byteBuf.readInt();
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex(); // content length invalid, reset reader index
            return;
        }

        byte[] content = new byte[length];
        byteBuf.readBytes(content);
        TinyProtocol tinyProtocol = new TinyProtocol(content);
        out.add(tinyProtocol);
    }
}
