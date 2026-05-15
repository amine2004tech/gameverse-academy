package ma.ac.esi.gameverseacademy.repository;

import ma.ac.esi.gameverseacademy.model.Review;
import ma.ac.esi.gameverseacademy.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {

    // CREATE
    public boolean addReview(Review review) {

        String sql =
                "INSERT INTO reviews (mod_id, user_id, rating, comment) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, review.getModId());
            stmt.setInt(2, review.getUserId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Review> getReviewsByModId(int modId) {

        List<Review> reviews = new ArrayList<>();

        String sql =
                "SELECT r.id, r.mod_id, r.user_id, r.rating, r.comment, r.created_at, u.username, u.avatar AS user_avatar " +
                "FROM reviews r " +
                "JOIN users u ON r.user_id = u.id " +
                "WHERE r.mod_id = ? " +
                "ORDER BY r.id DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, modId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Review review = new Review();

                    review.setId(rs.getInt("id"));
                    review.setModId(rs.getInt("mod_id"));
                    review.setUserId(rs.getInt("user_id"));

                    // ✅ correct field now
                    review.setUsername(rs.getString("username"));
                    review.setUserAvatar(rs.getString("user_avatar"));

                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    review.setCreatedAt(rs.getTimestamp("created_at"));

                    reviews.add(review);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }
    public boolean hasUserReviewed(int userId, int modId) {

        String sql =
                "SELECT id FROM reviews WHERE user_id = ? AND mod_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, modId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    // AVERAGE RATING
    public double getAverageRating(int modId) {

        String sql =
                "SELECT AVG(rating) AS avg_rating FROM reviews WHERE mod_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, modId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    // UPDATE (SECURE: only owner should update — enforced in service)
    public boolean updateReview(Review review) {

        String sql =
                "UPDATE reviews SET rating = ?, comment = ? " +
                "WHERE id = ? AND user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getComment());
            stmt.setInt(3, review.getId());
            stmt.setInt(4, review.getUserId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE (SECURE ownership check)
    public boolean deleteReview(int reviewId, int userId) {

        String sql =
                "DELETE FROM reviews WHERE id = ? AND user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reviewId);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}