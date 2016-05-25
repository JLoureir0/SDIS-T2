package pinypon.user;

import org.abstractj.kalium.keys.PublicKey;

public abstract class Entity implements Comparable<Entity> {
    private String username;
    private PublicKey publicKey;

    public Entity(String username, PublicKey publicKey) {
        if (username.isEmpty() || publicKey == null) {
            throw new IllegalArgumentException("Fields cannot be empty.");
        }
        this.username = username;
        this.publicKey = publicKey;
    }

    public String getUsername() {
        return username;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public int compareTo(Entity entity) {
        return this.getPublicKey().toString().compareTo(entity.getPublicKey().toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Entity otherEntity = (Entity) obj;
        return this.getPublicKey().equals(otherEntity.getPublicKey());
    }

    @Override
    public int hashCode() {
        return this.getPublicKey().hashCode();
    }
}
