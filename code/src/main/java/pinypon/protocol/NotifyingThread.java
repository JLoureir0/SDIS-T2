package pinypon.protocol;

public abstract class NotifyingThread extends Thread {
    protected ListeningThread listeningThread;

    public final void addListener(final ListeningThread listener) {
        this.listeningThread = listener;
    }

    protected abstract void notifyListener();

    @Override
    public final void run() {
        try {
            doRun();
        } finally {
            notifyListener();
        }
    }

    public abstract void doRun();
}
