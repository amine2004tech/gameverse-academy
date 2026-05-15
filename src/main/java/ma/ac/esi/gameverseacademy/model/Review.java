package ma.ac.esi.gameverseacademy.model;

public class Review {

    private int id;
    private int modId;

    // NEW: real ownership (IMPORTANT)
    private int userId;

    

    private int rating;
    private String comment;
    private java.sql.Timestamp createdAt;
    
// kept for display convenience
    private String username;
    private String userAvatar; // Transient
    // DEFAULT
    public Review() {
    }

    public Review(int modId, int userId, String username, int rating, String comment, java.sql.Timestamp createdAt) {
        this.modId = modId;
        this.userId = userId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Review(int id, int modId, int userId, String username, int rating, String comment, java.sql.Timestamp createdAt) {
        this.id = id;
        this.modId = modId;
        this.userId = userId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // GETTERS / SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getModId() {
        return modId;
    }

    public void setModId(int modId) {
        this.modId = modId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", modId=" + modId +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}