package peer.actions;

import peer.Connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.SelectionKey;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ActionOnFileReceive extends Action {

    final private AsynchronousFileChannel fileChannel;

    public ActionOnFileReceive(Connection connection, String filePath) throws IOException {
        super(connection);
        this.operation = SelectionKey.OP_READ;
        this.fileChannel = AsynchronousFileChannel.open(Paths.get(filePath), StandardOpenOption.WRITE);
    }

    @Override
    public boolean execute(ByteBuffer byteBuffer) {
        return false;
    }
}
