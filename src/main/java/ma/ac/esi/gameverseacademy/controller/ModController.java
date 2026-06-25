package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.model.Game;
import ma.ac.esi.gameverseacademy.model.Tag;
import ma.ac.esi.gameverseacademy.service.ModService;
import ma.ac.esi.gameverseacademy.service.GameService;
import ma.ac.esi.gameverseacademy.service.TagService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/ModController")
public class ModController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String gameIdParam = request.getParameter("game");
        String tagsParam = request.getParameter("tags");

        Integer selectedGame = null;
        if (gameIdParam != null && !gameIdParam.isEmpty()) {
            try { selectedGame = Integer.parseInt(gameIdParam); } catch (NumberFormatException e) { /* ignore */ }
        }
        
        List<Integer> selectedTags = ModService.parseTagIds(tagsParam);

        ModService modService = new ModService();
        GameService gameService = new GameService();
        TagService tagService = new TagService();

        // Retrieve filtered mods
        List<Mod> mods = modService.getFilteredMods(selectedGame, selectedTags);
        
        // Retrieve all games and tags for the sidebar
        List<Game> games = gameService.getAllGames();
        List<Tag> tags = tagService.getAllTags();

        request.setAttribute("mods", mods);
        request.setAttribute("games", games);
        request.setAttribute("tags", tags);
        request.setAttribute("selectedGame", selectedGame);
        request.setAttribute("selectedTags", selectedTags);

        request.getRequestDispatcher("/home.jsp").forward(request, response);
    }
}