package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.model.ModImage;
import ma.ac.esi.gameverseacademy.model.Game;
import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.service.ModService;
import ma.ac.esi.gameverseacademy.service.GameService;
import ma.ac.esi.gameverseacademy.service.TagService;
import ma.ac.esi.gameverseacademy.service.ModImageService;
import ma.ac.esi.gameverseacademy.security.SecureUploadService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/ModSubmitController")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 50,       // 50MB
    maxRequestSize = 1024 * 1024 * 100    // 100MB
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

        // 1. Extract Parameters (Basic Validation)
        String modIdParam = request.getParameter("modId");
        String title = request.getParameter("title");
        String gameIdParam = request.getParameter("gameId");
        String description = request.getParameter("description");
        String youtubeUrl = request.getParameter("youtubeUrl");
        String selectedTagsStr = request.getParameter("selectedTags");

        int gameId = 0;
        try { gameId = Integer.parseInt(gameIdParam); } catch (Exception e) {}

        int modId = 0;
        if (modIdParam != null && !modIdParam.isEmpty()) {
            try { modId = Integer.parseInt(modIdParam); } catch (Exception e) {}
        }

        // 2. Prepare Data for Service
        Mod mod = new Mod();
        mod.setTitle(title);
        mod.setGameId(gameId);
        mod.setUserId(currentUser.getId());
        mod.setDescription(description);
        mod.setYoutubeVideoId(youtubeUrl); // Service will extract ID

        List<Integer> tagIds = ModService.parseTagIds(selectedTagsStr);

        // 3. Secure File Extraction (Offloaded to Service Layer)
        SecureUploadService uploadService = new SecureUploadService();
        SecureUploadService.UploadPackage uploadPkg = uploadService.processSecureUploads(request.getParts(), mod);

        List<ModService.FileUploadEntry> imageParts = uploadPkg.getImageParts();
        ModService.FileUploadEntry zipPart = uploadPkg.getZipPart();

        String baseUploadPath = request.getServletContext().getRealPath("/assets");

        // 3. Delegate to Service
        boolean success;
        if (modId > 0) {
            mod.setId(modId);
            Mod existing = modService.getModById(modId);
            if (existing != null && existing.getUserId() == currentUser.getId()) {
                success = modService.updateMod(mod, tagIds, imageParts, zipPart, baseUploadPath);
            } else {
                success = false;
            }
        } else {
            success = modService.submitMod(mod, tagIds, imageParts, zipPart, baseUploadPath) > 0;
        }

        // 4. Orchestrate Response
        if (success) {
            response.sendRedirect(request.getContextPath() + "/ModSubmitController?success=1");
        } else {
            response.sendRedirect(request.getContextPath() + "/ModSubmitController?error=1");
        }
    }
}