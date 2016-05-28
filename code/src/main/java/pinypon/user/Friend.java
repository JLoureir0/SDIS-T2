package pinypon.user;

public final class Friend extends Entity {

    private String alias;

    public Friend(String username, String encodedPublicKey) {
        super(username);
        this.encodedPublicKey = encodedPublicKey;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        if (this.alias == null) {
            return this.username;
        } else {
            return this.alias;
        }
    }
}
