package ma.ac.esi.gameverseacademy.security;

import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.service.ModService.FileUploadEntry;

import javax.servlet.http.Part;
import java.io.IOException;
import java.util.*;

/**
 * Security Layer component for handling multi-part file uploads safely.
 * This moves infrastructure-heavy extraction logic out of the Controller.
 */
public class SecureUploadService {

    // SEC-FIX: Strict extension allowlists
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> ALLOWED_MOD_EXTENSIONS = Set.of("zip", "rar", "7z");
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;  // 10MB per image
    private static final long MAX_MOD_SIZE = 50 * 1024 * 1024;    // 50MB per mod file

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
     * @throws IOException If streaming fails
     * @throws SecurityException If upload validation fails
     */
    public UploadPackage processSecureUploads(Collection<Part> parts, Mod mod) throws IOException {
        UploadPackage pkg = new UploadPackage();

        for (Part part : parts) {
            String fieldName = part.getName();
            
            if ("modImages".equals(fieldName) && part.getSize() > 0) {
                // SEC-FIX: Validate image uploads
                String originalName = part.getSubmittedFileName();
                String extension = getFileExtension(originalName);
                
                if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
                    System.err.println("[SecureUpload] Rejected image extension: " + extension);
                    continue; // Skip disallowed file types
                }
                if (part.getSize() > MAX_IMAGE_SIZE) {
                    System.err.println("[SecureUpload] Rejected oversized image: " + part.getSize());
                    continue;
                }
                // SEC-FIX: Validate MIME type
                String contentType = part.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    System.err.println("[SecureUpload] Rejected non-image MIME: " + contentType);
                    continue;
                }

                String safeName = sanitizeFileName(originalName);
                pkg.imageParts.add(new FileUploadEntry(part.getInputStream(), safeName));
                
            } else if ("modFile".equals(fieldName) && part.getSize() > 0) {
                String originalName = part.getSubmittedFileName();
                String extension = getFileExtension(originalName);
                
                // SEC-FIX: Validate mod file extension
                if (!ALLOWED_MOD_EXTENSIONS.contains(extension.toLowerCase())) {
                    System.err.println("[SecureUpload] Rejected mod extension: " + extension);
                    continue;
                }
                if (part.getSize() > MAX_MOD_SIZE) {
                    System.err.println("[SecureUpload] Rejected oversized mod: " + part.getSize());
                    continue;
                }

                String safeName = sanitizeFileName(originalName);
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

    /**
     * Extracts the file extension from a filename.
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        // SEC-FIX: Handle double extensions like file.php.jpg — get the LAST extension
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}
