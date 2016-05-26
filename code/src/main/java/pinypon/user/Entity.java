package pinypon.user;

import org.abstractj.kalium.keys.PublicKey;

public abstract class Entity implements Comparable<Entity> {
    protected String username;
    protected PublicKey publicKey;

    public Entity(String username) {
        if (username.isEmpty()) {
            throw new IllegalArgumentException("Fields cannot be empty.");
        }
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PublicKey getPublicKey() {
        return publicKey;
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
