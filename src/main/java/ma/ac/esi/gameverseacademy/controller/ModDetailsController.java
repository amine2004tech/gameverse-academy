package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.*;
import ma.ac.esi.gameverseacademy.service.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/ModDetailsController")
public class ModDetailsController extends HttpServlet {

        private ModService modService;
        private ReviewService reviewService;
        private ModImageService imageService;
        private GameService gameService;
        private TagService tagService;

        @Override
        public void init() {
                modService = new ModService();
                reviewService = new ReviewService();
                imageService = new ModImageService();
                gameService = new GameService();
                tagService = new TagService();
        }

        @Override
        protected void doGet(HttpServletRequest request,
                        HttpServletResponse response)
                        throws ServletException, IOException {

                int modId;

                // =========================
                // VALIDATE ID
                // =========================
                try {
                        String idParam = request.getParameter("id");

                        if (idParam == null || idParam.trim().isEmpty()) {
                                redirectToMods(request, response);
                                return;
                        }

                        modId = Integer.parseInt(idParam);

                } catch (NumberFormatException e) {
                        redirectToMods(request, response);
                        return;
                }

                // =========================
                // LOAD MOD DATA (Enriched)
                // =========================
                Mod mod = modService.getModDetails(modId);

                if (mod == null) {
                        redirectToMods(request, response);
                        return;
                }

                HttpSession session = request.getSession(false);
                User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

                // =========================
                // ACCESS SECURITY (Service)
                // =========================
                if (!modService.canUserAccessMod(mod, currentUser)) {
                        response.sendRedirect(request.getContextPath() + "/ModController");
                        return;
                }

                // =========================
                // USER REVIEW (SESSION)
                // =========================
                Review userReview = null;
                if (currentUser != null) {
                        userReview = reviewService.getUserReview(modId, currentUser.getId());
                }

                // =========================
                // SEND TO VIEW
                // =========================
                request.setAttribute("mod", mod);
                request.setAttribute("userReview", userReview);

                request.getRequestDispatcher("/modDetails.jsp").forward(request, response);
        }

        // =========================
        // REDIRECT HELPER
        // =========================
        private void redirectToMods(HttpServletRequest request,
                        HttpServletResponse response)
                        throws IOException {

                response.sendRedirect(
                                request.getContextPath() + "/ModController");
        }
}