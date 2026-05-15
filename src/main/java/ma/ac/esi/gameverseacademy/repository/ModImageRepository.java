package ma.ac.esi.gameverseacademy.repository;

import ma.ac.esi.gameverseacademy.model.ModImage;
import ma.ac.esi.gameverseacademy.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModImageRepository {

    // GET ALL IMAGES FOR MOD
    public List<ModImage> getImagesByModId(int modId) {

        List<ModImage> images = new ArrayList<>();

        String sql =
                "SELECT * FROM mod_images " +
                "WHERE mod_id = ? " +
                "ORDER BY position ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, modId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    ModImage image = mapRowToImage(rs);

                    images.add(image);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return images;
    }

    // GET THUMBNAIL
    public ModImage getThumbnailByModId(int modId) {

        String sql =
                "SELECT * FROM mod_images " +
                "WHERE mod_id = ? AND position = 0 " +
                "LIMIT 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, modId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return mapRowToImage(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // INSERT IMAGE
    public boolean addImage(ModImage image) {

        String sql =
                "INSERT INTO mod_images " +
                "(mod_id, image_name, position) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, image.getModId());
            stmt.setString(2, image.getImageName());
            stmt.setInt(3, image.getPosition());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // MAPPING
    private ModImage mapRowToImage(ResultSet rs)
            throws SQLException {

        return new ModImage(
                rs.getInt("id"),
                rs.getInt("mod_id"),
                rs.getString("image_name"),
                rs.getInt("position")
        );
    }
}