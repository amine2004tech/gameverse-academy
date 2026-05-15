package ma.ac.esi.gameverseacademy.security;

import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.service.ModService.FileUploadEntry;

import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Security Layer component for handling multi-part file uploads safely.
 * This moves infrastructure-heavy extraction logic out of the Controller.
 */
public class SecureUploadService {

    public static class UploadPackage {
        private final List<FileUploadEntry> imageParts = new ArrayList<>();
        private FileUploadEntry zipPart = null;

        public List<FileUploadEntry> getImageParts() { return imageParts; }
        public FileUploadEntry getZipPart() { return zipPart; }
    }

    /**
     * Extracts and sanitizes file uploads from request parts.
     * 
     * @param parts The collection of parts from the HttpServletRequest
     * @param mod   The Mod model to be enriched with metadata (e.g., filename)
     * @return An UploadPackage containing processed FileUploadEntry objects
     * @throws IOException If streaming fail
     */
    public UploadPackage processSecureUploads(Collection<Part> parts, Mod mod) throws IOException {
        UploadPackage pkg = new UploadPackage();

        for (Part part : parts) {
            String fieldName = part.getName();
            
            if ("modImages".equals(fieldName) && part.getSize() > 0) {
                // Sanitize filename to prevent directory traversal or shell injection
                String safeName = sanitizeFileName(part.getSubmittedFileName());
                pkg.imageParts.add(new FileUploadEntry(part.getInputStream(), safeName));
                
            } else if ("modFile".equals(fieldName) && part.getSize() > 0) {
                String safeName = sanitizeFileName(part.getSubmittedFileName());
                pkg.zipPart = new FileUploadEntry(part.getInputStream(), safeName);
                
                if (mod != null) {
                    mod.setFileName(safeName); // Enriched model with sanitized name
                }
            }
        }
        return pkg;
    }

    /**
     * Strips path components and restricts filename to a safe character set.
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "unknown_binary";
        }
        // Take only the basename (prevents ../../ traversal)
        String baseName = new java.io.File(fileName).getName();
        // Replace all non-alphanumeric (except . _ -) with underscore
        return baseName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
