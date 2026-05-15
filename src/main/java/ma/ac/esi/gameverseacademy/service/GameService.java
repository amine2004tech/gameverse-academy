package ma.ac.esi.gameverseacademy.service;

import ma.ac.esi.gameverseacademy.model.Game;
import ma.ac.esi.gameverseacademy.repository.GameRepository;

import java.util.List;

public class GameService {

    private GameRepository gameRepository;

    public GameService() {
        this.gameRepository = new GameRepository();
    }

    public List<Game> getAllGames() {
        return gameRepository.getAllGames();
    }

    public Game getGameById(int id) {
        if (id <= 0) {
            return null;
        }
        return gameRepository.getGameById(id);
    }
}
