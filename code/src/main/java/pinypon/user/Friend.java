package pinypon.user;

public class Friend extends Entity {

    private String alias;

    public Friend(String username, String publicKey) {
        super(username, publicKey);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
