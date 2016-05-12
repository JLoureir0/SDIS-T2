package Peer;

final public class Action {

    private long connection_id;
    private boolean done = false;

    Action(long connection_id) {
        this.connection_id = connection_id;
    }

    public boolean execute() {
        if (this.done) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDone() {
        return this.done;
    }

    static final public String ERROR = "";
    static final public String RETRY = "";
    static final public String ACKNOWLEDGE = "";
    static final public String INVITE = "";
    static final public String WHISPER = "";
}
