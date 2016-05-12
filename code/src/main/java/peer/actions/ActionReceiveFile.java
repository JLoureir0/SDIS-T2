package peer.actions;

import peer.Connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ActionReceiveFile extends Action {

    final private AsynchronousFileChannel fileChannel;

    public ActionReceiveFile(Connection connection, String filePath) throws IOException {
        super(connection);
        this.fileChannel = AsynchronousFileChannel.open(Paths.get(filePath), StandardOpenOption.WRITE);
    }

    @Override
    public boolean execute(ByteBuffer byteBuffer) {
        return false;
    }
}
