package github.io.pedrogao.tinyrpc.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static github.io.pedrogao.tinyrpc.core.common.constants.RpcConstants.MAGIC_NUMBER;

public class RpcDecoder extends ByteToMessageDecoder {

    public final int BASE_LENGTH = 2 + 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        if (byteBuf.readableBytes() < BASE_LENGTH) {
            return;
        }
        // skip if too large
        if (byteBuf.readableBytes() > 1000) {
            byteBuf.skipBytes(byteBuf.readableBytes());
        }

        int beginReader;
        while (true) {
            beginReader = byteBuf.readerIndex();
            byteBuf.markReaderIndex();
            if (byteBuf.readShort() == MAGIC_NUMBER) {
                break;
            } else {
                // invalid message, skip
                ctx.close();
                return;
            }
        }
        int length = byteBuf.readInt();
        // invalid message, skip
        if (byteBuf.readableBytes() < length) {
            byteBuf.readerIndex(beginReader);
            return;
        }

        byte[] content = new byte[length];
        byteBuf.readBytes(content);
        RpcProtocol rpcProtocol = new RpcProtocol(content);
        out.add(rpcProtocol);
    }
}
