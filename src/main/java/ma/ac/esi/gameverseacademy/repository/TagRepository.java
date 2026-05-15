package ma.ac.esi.gameverseacademy.repository;

import ma.ac.esi.gameverseacademy.model.Tag;
import ma.ac.esi.gameverseacademy.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TagRepository {

    public List<Tag> getTagsByModId(int modId) {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT t.id, t.name, t.color FROM TAGS t " +
                     "JOIN MOD_TAGS mt ON t.id = mt.tag_id " +
                     "WHERE mt.mod_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, modId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(new Tag(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("color")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }

    public List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT id, name, color FROM TAGS";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tags.add(new Tag(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("color")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }

    public void addTagsToMod(int modId, List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return;
        String sql = "INSERT INTO MOD_TAGS (mod_id, tag_id) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Integer tagId : tagIds) {
                stmt.setInt(1, modId);
                stmt.setInt(2, tagId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTagsByModId(int modId) {
        String sql = "DELETE FROM MOD_TAGS WHERE mod_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, modId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
