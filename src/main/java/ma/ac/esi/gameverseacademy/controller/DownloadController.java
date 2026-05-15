package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.service.ModService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/DownloadModController")
public class DownloadController extends HttpServlet {

    // Dynamically resolved in doGet


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) {

        try {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.sendError(400, "Missing ID");
                return;
            }

            int modId = Integer.parseInt(idParam);
            ModService modService = new ModService();
            Mod mod = modService.getModById(modId);

            if (mod == null || mod.getFileName() == null) {
                response.sendError(404, "Mod not found");
                return;
            }

            String baseUploadPath = getServletContext().getRealPath("/assets");
            File file = modService.getPhysicalModFile(modId, baseUploadPath);

            if (!file.exists()) {
                response.sendError(404, "Physical archive not found");
                return;
            }

            // Register download (Business logic)
            modService.registerDownload(modId);

            // Set response headers
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + mod.getFileName() + "\"");

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
            try { response.sendError(500, e.getMessage()); } catch (IOException ignored) {}
        }
    }
}