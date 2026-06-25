package ma.ac.esi.gameverseacademy.repository;

import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.security.PasswordHasher;
import ma.ac.esi.gameverseacademy.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    // =========================
    // LOGIN
    // =========================

    public User getUserByCredentials(String login,
            String password) {

        // SEC-FIX: Fetch user by login only, verify password with hasher
        String sql = "SELECT id, login, password, role, username, avatar " +
                "FROM users " +
                "WHERE login = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    User user = mapRowToUser(rs);
                    // Verify password (supports both hashed and legacy plaintext)
                    if (PasswordHasher.verifyPassword(password, user.getPassword())) {
                        return user;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("[UserRepository] Login query error: " + e.getMessage());
        }

        return null;
    }

    // =========================
    // GET USER BY ID
    // =========================

    public User getUserById(int userId) {

        String sql = "SELECT id, login, password, role, username, avatar " +
                "FROM users " +
                "WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // CHECK USERNAME EXISTS
    // =========================

    public boolean usernameExists(String username) {

        String sql = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // UPDATE USERNAME
    // =========================

    public boolean updateUsername(int userId,
            String username) {

        String sql = "UPDATE users SET username = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // UPDATE LOGIN
    // =========================

    public boolean updateLogin(int userId,
            String login) {

        String sql = "UPDATE users SET login = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // UPDATE PASSWORD
    // =========================

    public boolean updatePassword(int userId,
            String newPassword) {

        // SEC-FIX: Hash password before storing
        String hashedPassword = PasswordHasher.hashPassword(newPassword);
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserRepository] Password update error: " + e.getMessage());
        }

        return false;
    }

    // =========================
    // UPDATE AVATAR
    // =========================

    public boolean updateAvatar(int userId,
            String avatar) {

        String sql = "UPDATE users SET avatar = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, avatar);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // MAPPER
    // =========================

    private User mapRowToUser(ResultSet rs)
            throws SQLException {

        User user = new User();

        user.setId(rs.getInt("id"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setUsername(rs.getString("username"));
        user.setAvatar(rs.getString("avatar"));

        return user;
    }
}