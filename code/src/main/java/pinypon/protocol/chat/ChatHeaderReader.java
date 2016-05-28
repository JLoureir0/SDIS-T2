package pinypon.protocol.chat;

import java.nio.ByteBuffer;

public class ChatHeaderReader {

    public enum Type {
        ADD_FRIEND,
        MESSAGE_RECEIVE,
    }

    public HeaderAndLeftOvers parse(ByteBuffer fragment) {
        return null;
    }

    public static class HeaderAndLeftOvers {
        public final ByteBuffer header;
        public final ByteBuffer leftOvers;
        public HeaderAndLeftOvers(ByteBuffer header, ByteBuffer leftOvers) {
            this.header = header;
            this.leftOvers = leftOvers;
        }
    }
}
