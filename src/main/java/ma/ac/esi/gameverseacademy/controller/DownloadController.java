package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.service.ModService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/DownloadModController")
public class DownloadController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) {

        try {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.sendError(400, "Missing ID");
                return;
            }

            int modId;
            try {
                modId = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                response.sendError(400, "Invalid ID");
                return;
            }

            ModService modService = new ModService();
            Mod mod = modService.getModById(modId);

            if (mod == null || mod.getFileName() == null) {
                response.sendError(404, "Mod not found");
                return;
            }

            String baseUploadPath = getServletContext().getRealPath("/assets");
            File file = modService.getPhysicalModFile(modId, baseUploadPath);

            // SEC-FIX: Canonical path check to prevent path traversal
            String canonicalBase = new File(baseUploadPath).getCanonicalPath();
            String canonicalFile = file.getCanonicalPath();
            if (!canonicalFile.startsWith(canonicalBase)) {
                response.sendError(403, "Access denied");
                return;
            }

            if (!file.exists()) {
                response.sendError(404, "Archive not found");
                return;
            }

            // Register download (Business logic)
            modService.registerDownload(modId);

            // SEC-FIX: Sanitize filename for Content-Disposition to prevent header injection
            String safeFileName = mod.getFileName().replaceAll("[^a-zA-Z0-9._-]", "_");
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + safeFileName + "\"");
            response.setContentLengthLong(file.length());

            // Stream file
            try (FileInputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytes;
                while ((bytes = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes);
                }
            }

        } catch (Exception e) {
            // SEC-FIX: Don't leak exception message to client
            System.err.println("[DownloadController] Error: " + e.getMessage());
            try { response.sendError(500, "Download failed"); } catch (IOException ignored) {}
        }
    }
}