package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.service.ModService;
import ma.ac.esi.gameverseacademy.service.GameService;
import ma.ac.esi.gameverseacademy.service.TagService;
import ma.ac.esi.gameverseacademy.security.SecureUploadService;
import ma.ac.esi.gameverseacademy.security.InputSanitizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/ModSubmitController")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 5,   // 5MB
    maxFileSize = 1024 * 1024 * 550L,      // 550MB (Allows for the 500MB Zip)
    maxRequestSize = 1024 * 1024 * 600L    // 600MB (Zip + Images overhead)
)
public class ModSubmitController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = null;

        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }

        GameService gameService = new GameService();
        request.setAttribute("games", gameService.getAllGames());
        
        TagService tagService = new TagService();
        request.setAttribute("tags", tagService.getAllTags());

        if (currentUser != null) {
            ModService modService = new ModService();
            request.setAttribute("mods", modService.getModsByUserId(currentUser.getId()));
        }

        request.getRequestDispatcher("/submit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginController");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        ModService modService = new ModService();
        // 1. Extract Parameters
        String modIdParam = request.getParameter("modId");
        String title = InputSanitizer.sanitize(request.getParameter("title"));
        String gameIdParam = request.getParameter("gameId");
        String description = InputSanitizer.sanitize(request.getParameter("description"));
        String youtubeUrl = InputSanitizer.sanitize(request.getParameter("youtubeUrl"));
        String selectedTagsStr = request.getParameter("selectedTags");
        String imageOrderStr = request.getParameter("imageOrder");
        String baseUploadPath = request.getServletContext().getRealPath("/assets");

        // 2. Extract File Uploads
        SecureUploadService uploadService = new SecureUploadService();
        SecureUploadService.UploadResult uploadResult = uploadService.processSecureUploads(request.getParts());

        // 3. Delegate ALL business logic to the Service Layer
        List<String> errors = modService.processModSubmission(
                currentUser, title, gameIdParam, modIdParam, description, youtubeUrl, 
                selectedTagsStr, imageOrderStr, 
                uploadResult.getImageParts(), uploadResult.getZipPart(), 
                uploadResult.getErrors(), baseUploadPath
        );

        // 4. Orchestrate Response
        if (!errors.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder();
            for (int i = 0; i < errors.size(); i++) {
                if (i > 0) errorMsg.append("|");
                errorMsg.append(errors.get(i));
            }
            response.sendRedirect(request.getContextPath() + "/ModSubmitController?error=" 
                + java.net.URLEncoder.encode(errorMsg.toString(), "UTF-8"));
        } else {
            response.sendRedirect(request.getContextPath() + "/ModSubmitController?success=1");
        }
    }
}
