package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.service.ModService;
import ma.ac.esi.gameverseacademy.service.ReviewService;
import ma.ac.esi.gameverseacademy.service.UserService;
import ma.ac.esi.gameverseacademy.security.InputSanitizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/ProfileController")
public class ProfileController extends HttpServlet {

    private UserService userService;
    private ModService modService;
    private ReviewService reviewService;

    @Override
    public void init() {
        userService = new UserService();
        modService = new ModService();
        reviewService = new ReviewService();
    }

    // =========================
    // LOAD PROFILE PAGE
    // =========================
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginController");
            return;
        }

        User user = (User) session.getAttribute("user");

        // =========================
        // USER MODS (Enriched in service with thumb and rating)
        // =========================
        List<Mod> userMods = modService.getModsByUserId(user.getId());

        // =========================
        // SEND DATA TO JSP
        // =========================
        request.setAttribute("user", user);
        request.setAttribute("mods", userMods);

        request.getRequestDispatcher("/profile.jsp")
                .forward(request, response);
    }

    // =========================
    // PROFILE ACTIONS
    // =========================
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginController");
            return;
        }

        User user = (User) session.getAttribute("user");

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("ProfileController");
            return;
        }

        switch (action) {

            // =========================
            // UPDATE USERNAME
            // =========================
            case "updateUsername": {

                String username = InputSanitizer.sanitize(request.getParameter("username"));

                userService.updateUsername(user.getId(), username);
                break;
            }

            // =========================
            // UPDATE LOGIN
            // =========================
            case "updateLogin": {

                String login = InputSanitizer.sanitize(request.getParameter("login"));

                userService.updateLogin(user.getId(), login);
                break;
            }

            // =========================
            // UPDATE PASSWORD
            // =========================
            case "updatePassword": {

                String current = request.getParameter("currentPassword");

                String newPass = request.getParameter("newPassword");

                userService.updatePassword(
                        user.getId(),
                        current,
                        newPass);

                break;
            }

            // =========================
            // UPDATE AVATAR
            // =========================
            case "updateAvatar": {

                String avatar = InputSanitizer.sanitize(request.getParameter("avatar"));

                userService.updateAvatar(user.getId(), avatar);

                break;
            }

            default:
                break;
        }

        // =========================
        // REFRESH SESSION USER
        // =========================
        User updatedUser = userService.getUserById(user.getId());

        session.setAttribute("user", updatedUser);

        response.sendRedirect("ProfileController");
    }
}