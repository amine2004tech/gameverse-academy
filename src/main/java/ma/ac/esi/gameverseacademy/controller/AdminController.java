package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.service.ModService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/AdminController")
public class AdminController extends HttpServlet {

    private ModService modService = new ModService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/error403.jsp");
            return;
        }

        String gameIdParam = request.getParameter("game");
        String tagsParam = request.getParameter("tags");

        Integer selectedGame = (gameIdParam != null && !gameIdParam.isEmpty()) ? Integer.parseInt(gameIdParam) : null;

        java.util.List<Integer> selectedTags = ModService.parseTagIds(tagsParam);

        ma.ac.esi.gameverseacademy.service.GameService gameService = new ma.ac.esi.gameverseacademy.service.GameService();
        ma.ac.esi.gameverseacademy.service.TagService tagService = new ma.ac.esi.gameverseacademy.service.TagService();

        // Retrieve filtered mods
        java.util.List<ma.ac.esi.gameverseacademy.model.Mod> approvedMods = modService.getFilteredMods(selectedGame,
                selectedTags);
        java.util.List<ma.ac.esi.gameverseacademy.model.Mod> pendingMods = modService
                .getFilteredPendingMods(selectedGame, selectedTags);

        // Retrieve all games and tags for the sidebar
        java.util.List<ma.ac.esi.gameverseacademy.model.Game> games = gameService.getAllGames();
        java.util.List<ma.ac.esi.gameverseacademy.model.Tag> tags = tagService.getAllTags();

        request.setAttribute("approvedMods", approvedMods);
        request.setAttribute("pendingMods", pendingMods);
        request.setAttribute("games", games);
        request.setAttribute("tags", tags);
        request.setAttribute("selectedGame", selectedGame);
        request.setAttribute("selectedTags", selectedTags);

        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String modIdParam = request.getParameter("modId");

        if (action != null && modIdParam != null) {
            try {
                int modId = Integer.parseInt(modIdParam);
                String baseUploadPath = request.getServletContext().getRealPath("/assets");

                if ("approve".equalsIgnoreCase(action)) {
                    modService.approveMod(modId, baseUploadPath);
                } else if ("reject".equalsIgnoreCase(action)) {
                    modService.rejectMod(modId);
                }
            } catch (NumberFormatException e) {
                // Log or handle error
            }
        }

        response.sendRedirect(request.getContextPath() + "/AdminController");
    }
}