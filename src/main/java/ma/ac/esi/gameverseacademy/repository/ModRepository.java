package ma.ac.esi.gameverseacademy.repository;

import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModRepository {

    private static final String BASE_SELECT =
        "SELECT m.id, m.game_id, m.user_id, m.title, m.description, " +
        "m.status, m.file_name, m.youtube_video_id, m.downloads, m.created_at, " +
        "u.username AS author_name, u.avatar AS author_avatar, " +
        "g.title AS game_title " +
        "FROM MODS m " +
        "JOIN USERS u ON m.user_id = u.id " +
        "JOIN GAMES g ON m.game_id = g.id ";

    // =========================
    // GET ALL
    // =========================
    public List<Mod> getAllMods() {

        List<Mod> mods = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     BASE_SELECT + " ORDER BY m.id ASC");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                mods.add(mapRowToMod(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mods;
    }

    // =========================
    // GET BY ID
    // =========================
    public Mod getModById(int id) {

        String sql = BASE_SELECT + " WHERE m.id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return mapRowToMod(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // GET BY USER
    // =========================
    public List<Mod> getModsByUserId(int userId) {

        List<Mod> mods = new ArrayList<>();

        String sql = BASE_SELECT +
                " WHERE m.user_id = ? " +
                " ORDER BY m.created_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    mods.add(mapRowToMod(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mods;
    }

    // =========================
    // INSERT
    // =========================
    public int insertMod(Mod mod) {

        String sql =
            "INSERT INTO MODS (" +
            "game_id, user_id, title, description, status, file_name, youtube_video_id" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, mod.getGameId());
            stmt.setInt(2, mod.getUserId());
            stmt.setString(3, mod.getTitle());
            stmt.setString(4, mod.getDescription());
            stmt.setString(5, mod.getStatus());
            stmt.setString(6, mod.getFileName());
            stmt.setString(7, mod.getYoutubeVideoId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        mod.setId(generatedId);
                        return generatedId;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // =========================
    // STATUS
    // =========================
    public List<Mod> getModsByStatus(String status) {

        List<Mod> mods = new ArrayList<>();

        String sql = BASE_SELECT +
                " WHERE m.status = ? ORDER BY m.id ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    mods.add(mapRowToMod(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mods;
    }

    public List<Mod> getApprovedMods() {
        return getModsByStatus("APPROVED");
    }

    // =========================
    // UPDATE STATUS
    // =========================
    public boolean updateModStatus(int modId, String status) {

        String sql =
            "UPDATE MODS SET status = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, modId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // UPDATE
    // =========================
    public boolean updateMod(Mod mod) {

        String sql =
            "UPDATE MODS SET game_id = ?, title = ?, description = ?, " +
            "youtube_video_id = ?, file_name = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mod.getGameId());
            stmt.setString(2, mod.getTitle());
            stmt.setString(3, mod.getDescription());
            stmt.setString(4, mod.getYoutubeVideoId());
            stmt.setString(5, mod.getFileName());
            stmt.setInt(6, mod.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // DELETE
    // =========================
    public boolean deleteMod(int modId) {

        String sql = "DELETE FROM MODS WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, modId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // DOWNLOADS
    // =========================
    public void increaseDownload(int modId) {

        String sql =
            "UPDATE MODS SET downloads = downloads + 1 WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, modId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // MAPPER (FIXED)
    // =========================
    private Mod mapRowToMod(ResultSet rs) throws SQLException {

        Mod mod = new Mod();

        mod.setId(rs.getInt("id"));
        mod.setGameId(rs.getInt("game_id"));   // ONLY ID, NO GAME OBJECT
        mod.setUserId(rs.getInt("user_id"));

        mod.setTitle(rs.getString("title"));
        mod.setDescription(rs.getString("description"));

        mod.setStatus(rs.getString("status"));
        mod.setFileName(rs.getString("file_name"));
        mod.setYoutubeVideoId(rs.getString("youtube_video_id"));

        mod.setDownloads(rs.getInt("downloads"));
        mod.setCreatedAt(rs.getTimestamp("created_at"));

        mod.setAuthorName(rs.getString("author_name"));
        mod.setAuthorAvatar(rs.getString("author_avatar"));
        mod.setGameTitle(rs.getString("game_title")); // only for display

        return mod;
    }
}