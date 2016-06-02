package pinypon.utils;

import com.google.gson.Gson;

public class Defaults {

    public static final String APP_NAME = "Pinypon";
    public static final int NOUNCE_SIZE = 24;
    public static final int UDP_BUFFER_SIZE = 1024;
    public static final String ENCODING = "ISO-8859-1";
    public static final String WHITESPACE_REGEX = "\\s";

    public static final String PROTOCOL_CHAT_MESSAGE = "MESSAGE";
    public static final String PROTOCOL_CHAT_FILE = "FILE";

    public static final Gson gson = new Gson();

    public static final int PORT = 54321;
    public static final String USER_JSON_PATH = "user.secret";
    public static final String TRACKER_IP = "127.0.0.1";
    public static final int TRACKER_PORT = 12345;
}