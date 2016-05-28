package pinypon.user;

public abstract class Entity implements Comparable<Entity> {
    protected String username;
    protected String encodedPublicKey;

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

    public String getEncodedPublicKey() {
        return this.encodedPublicKey;
    }

    @Override
    public int compareTo(Entity entity) {
        return this.encodedPublicKey.compareTo(entity.encodedPublicKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Entity otherEntity = (Entity) obj;
        return this.encodedPublicKey.equals(otherEntity.encodedPublicKey);
    }

    @Override
    public int hashCode() {
        return this.getEncodedPublicKey().hashCode();
    }

    @Override
    public String toString() {
        return this.username;
    }
}
