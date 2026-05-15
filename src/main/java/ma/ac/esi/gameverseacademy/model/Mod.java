package ma.ac.esi.gameverseacademy.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class Mod {

    private int id;
    private int gameId;
    private int userId;
    private String title;
    private String description;
    private String status;
    private String fileName;
    private String youtubeVideoId;
    private int downloads;
    private Timestamp createdAt;

    // =========================
    // UI / TRANSIENT FIELDS
    // =========================
    private String authorName;
    private String authorAvatar;
    private List<Tag> tags;
    private String thumbnail;
    private String gameTitle; // lightweight display only
    private double averageRating;
    private List<Review> reviews;
    private Map<Integer, Integer> ratingDistribution;
    private List<ModImage> images;
    private Game game;

    public Mod() {
    }

    public Mod(int id,
            int gameId,
            int userId,
            String title,
            String description,
            String status,
            String fileName,
            String youtubeVideoId,
            int downloads,
            Timestamp createdAt) {

        this.id = id;
        this.gameId = gameId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.fileName = fileName;
        this.youtubeVideoId = youtubeVideoId;
        this.downloads = downloads;
        this.createdAt = createdAt;
    }

    // =========================
    // GETTERS / SETTERS
    // =========================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getYoutubeVideoId() {
        return youtubeVideoId;
    }

    public void setYoutubeVideoId(String youtubeVideoId) {
        this.youtubeVideoId = youtubeVideoId;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Map<Integer, Integer> getRatingDistribution() {
        return ratingDistribution;
    }

    public void setRatingDistribution(Map<Integer, Integer> ratingDistribution) {
        this.ratingDistribution = ratingDistribution;
    }

    public List<ModImage> getImages() {
        return images;
    }

    public void setImages(List<ModImage> images) {
        this.images = images;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    // =========================
    // TO STRING
    // =========================

    @Override
    public String toString() {
        return "Mod{" +
                "id=" + id +
                ", gameId=" + gameId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", downloads=" + downloads +
                ", fileName='" + fileName + '\'' +
                ", youtubeVideoId='" + youtubeVideoId + '\'' +

                '}';
    }
}
