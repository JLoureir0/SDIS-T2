package pinypon.user;


import org.abstractj.kalium.keys.PublicKey;

public final class Friend extends Entity {

    private String alias;

    public Friend(String username, PublicKey publicKey) {
        super(username);
        this.publicKey = publicKey;
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
