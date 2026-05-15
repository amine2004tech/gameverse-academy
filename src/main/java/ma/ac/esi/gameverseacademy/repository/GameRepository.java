package ma.ac.esi.gameverseacademy.repository;

import ma.ac.esi.gameverseacademy.model.Game;
import ma.ac.esi.gameverseacademy.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameRepository {

    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM GAMES";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                games.add(mapRowToGame(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    public Game getGameById(int id) {
        String sql = "SELECT * FROM GAMES WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToGame(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Game mapRowToGame(ResultSet rs) throws SQLException {
        return new Game(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("publisher"),
                rs.getString("developer"),
                rs.getString("platform"),
                rs.getString("release_date"),
                rs.getInt("metacritic")
        );
    }
}
