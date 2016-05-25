package pinypon.user;

public abstract class Entity implements Comparable<Entity> {
    private String username;
    private String publicKey;

    public Entity(String username, String publicKey) {
        this.username = username;
        this.publicKey = publicKey;
    }

    public String getUsername() {
        return username;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public int compareTo(Entity entity) {
        return this.getPublicKey().compareTo(entity.getPublicKey());
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
