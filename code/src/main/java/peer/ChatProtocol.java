import java.net.Socket;

public class ChatProtocol {
	
	/*
	 * Message Protocol
	 * - Message Header
	 * <message type> <sender id> <receiver id> <size>
	 * - Message Body
	 * <body>
	 * ---- 
	 */
	
	public enum MessageType {
		message
	}

	private MessageType msg_type;
	private String sender_id;
	private String receiver_id;
	private int msg_size;
	private String msg;
	
	ChatProtocol(MessageType msg_type, String sender_id, String receiver_id, int msg_size, String msg) {
		this.msg_type = msg_type;
		this.sender_id = sender_id;
		this.receiver_id = receiver_id;
		this.msg_size = msg_size;
		this.msg = msg;
		
	}
	
	private byte[] buildFrame() {
		StringBuilder frame = new StringBuilder();
		
		switch (msg_type) {
		case message: frame.append("MESSAGE ");
			break;

		default:
			break;
		}
		
		frame.append(sender_id);
		frame.append(" ");
		frame.append(receiver_id);
		
		if (msg_size != 0) {
			frame.append(" ");
			frame.append(msg_size);
			frame.append("\r\n");
			frame.append(msg);
		}
		
		return frame.toString().getBytes();
	}
	
	public void sendMsg(Socket socket) {
		
	}
	
	



	public static void main(String[] args) {
		System.out.println("hello");

	}

}
