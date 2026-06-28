package ma.ac.esi.gameverseacademy.security;

import ma.ac.esi.gameverseacademy.service.ModService.FileUploadEntry;

import javax.servlet.http.Part;
import java.io.IOException;
import java.util.*;

/**
 * Security Layer component for handling multi-part file uploads safely.
 * Returns structured validation errors instead of silently dropping files.
 */
public class SecureUploadService {

    // Strict extension allowlists
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_MOD_EXTENSIONS = Set.of("zip", "rar", "7z");
    private static final long MAX_IMAGE_SIZE_BYTES = 10L * 1024 * 1024;  // 10MB per image
    private static final long MAX_MOD_PACKAGE_SIZE_BYTES = 500L * 1024 * 1024;   // 500MB per mod file

    /**
     * Result object containing processed uploads AND any validation errors.
     */
    public static class UploadResult {
        private final List<FileUploadEntry> imageParts = new ArrayList<>();
        private FileUploadEntry zipPart = null;
        private final List<String> errors = new ArrayList<>();

        public List<FileUploadEntry> getImageParts() { return imageParts; }
        public FileUploadEntry getZipPart() { return zipPart; }
        public List<String> getErrors() { return errors; }
        public boolean hasErrors() { return !errors.isEmpty(); }
    }

    /**
     * Extracts and validates file uploads from request parts.
     * Invalid files produce user-readable errors instead of being silently dropped.
     */
    public UploadResult processSecureUploads(Collection<Part> parts) throws IOException {
        UploadResult result = new UploadResult();

        for (Part part : parts) {
            String fieldName = part.getName();

            if ("modImages".equals(fieldName)) {
                if (part.getSubmittedFileName() == null || part.getSubmittedFileName().trim().isEmpty()) {
                    continue; // Skip empty file inputs (user didn't select a file)
                }

                String originalName = part.getSubmittedFileName();
                
                if (part.getSize() == 0) {
                    result.errors.add(sanitizeForDisplay(originalName) + " was rejected: file is empty.");
                    continue;
                }

                String extension = getFileExtension(originalName);

                // Validate extension
                if (extension.isEmpty() || !ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
                    result.errors.add(sanitizeForDisplay(originalName) + " was rejected: images must be JPG, JPEG, PNG, or WEBP.");
                    continue;
                }

                // Validate MIME type
                String contentType = part.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    result.errors.add(sanitizeForDisplay(originalName) + " was rejected: file content is not a valid image.");
                    continue;
                }

                // Reject SVG (even though extension wouldn't match, belt-and-suspenders)
                if (contentType.contains("svg")) {
                    result.errors.add(sanitizeForDisplay(originalName) + " was rejected: SVG images are not supported.");
                    continue;
                }

                // Validate size
                if (part.getSize() > MAX_IMAGE_SIZE_BYTES) {
                    result.errors.add(sanitizeForDisplay(originalName) + " was rejected: image size exceeds 10 MB.");
                    continue;
                }

                String safeName = sanitizeFileName(originalName);
                result.imageParts.add(new FileUploadEntry(part.getInputStream(), safeName));

            } else if ("modFile".equals(fieldName)) {
                if (part.getSubmittedFileName() == null || part.getSubmittedFileName().trim().isEmpty()) {
                    continue;
                }

                String originalName = part.getSubmittedFileName();

                if (part.getSize() == 0) {
                    result.errors.add(sanitizeForDisplay(originalName) + " was rejected: archive file is empty.");
                    continue;
                }

                String extension = getFileExtension(originalName);

                // Validate extension
                if (extension.isEmpty() || !ALLOWED_MOD_EXTENSIONS.contains(extension.toLowerCase())) {
                    result.errors.add(sanitizeForDisplay(originalName) + " was rejected: mod package must be ZIP, RAR, or 7Z.");
                    continue;
                }

                // Reject dangerous double extensions
                String secondExtension = getSecondToLastExtension(originalName);
                if (isDangerousExtension(secondExtension)) {
                    result.errors.add(sanitizeForDisplay(originalName) + " was rejected: mod package must be ZIP, RAR, or 7Z.");
                    continue;
                }

                // Validate size
                if (part.getSize() > MAX_MOD_PACKAGE_SIZE_BYTES) {
                    result.errors.add(sanitizeForDisplay(originalName) + " was rejected: archive size exceeds 500 MB.");
                    continue;
                }

                String safeName = sanitizeFileName(originalName);
                result.zipPart = new FileUploadEntry(part.getInputStream(), safeName);
            }
        }
        return result;
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
     * Sanitizes a filename for safe display in error messages (prevent XSS in error text).
     */
    private String sanitizeForDisplay(String fileName) {
        if (fileName == null) return "unknown";
        // Strip to basename, limit length, remove HTML-dangerous chars
        String baseName = new java.io.File(fileName).getName();
        if (baseName.length() > 50) baseName = baseName.substring(0, 50) + "...";
        return baseName.replaceAll("[<>\"'&]", "_");
    }

    /**
     * Extracts the last file extension from a filename.
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * Extracts the second-to-last extension (e.g., "exe" from "setup.exe.zip").
     */
    private String getSecondToLastExtension(String fileName) {
        if (fileName == null) return "";
        String withoutLast = fileName.substring(0, fileName.lastIndexOf('.'));
        if (!withoutLast.contains(".")) return "";
        return withoutLast.substring(withoutLast.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * Checks if an extension is a known dangerous/executable type.
     */
    private boolean isDangerousExtension(String ext) {
        if (ext == null || ext.isEmpty()) return false;
        Set<String> dangerous = Set.of(
            "exe", "bat", "cmd", "sh", "ps1", "vbs", "vbe", "js", "jse",
            "wsf", "wsh", "msi", "scr", "pif", "com", "hta",
            "jar", "war", "jsp", "php", "asp", "aspx", "cgi", "py", "rb", "pl"
        );
        return dangerous.contains(ext.toLowerCase());
    }
}
