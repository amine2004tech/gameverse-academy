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

@WebServlet("/DeleteModController")
public class DeleteModController extends HttpServlet {

    // SEC-FIX: Changed from GET to POST to prevent CSRF via link injection
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginController");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        String idParam = request.getParameter("id");

        if (idParam != null) {
            try {
                int modId = Integer.parseInt(idParam);
                ModService modService = new ModService();
                modService.deleteMod(modId, currentUser);
            } catch (NumberFormatException e) {
                /* ignore bad input */ }
        }

        // SEC-FIX: Removed open redirect via untrusted Referer header
        response.sendRedirect(request.getContextPath() + "/ProfileController");
    }

    // SEC-FIX: Reject GET requests for state-changing action
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Use POST to delete mods");
    }
}
