package ma.ac.esi.gameverseacademy.repository;

import ma.ac.esi.gameverseacademy.model.User;
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

        String sql = "SELECT id, login, password, role, username, avatar " +
                "FROM users " +
                "WHERE login = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, password);

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

        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
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