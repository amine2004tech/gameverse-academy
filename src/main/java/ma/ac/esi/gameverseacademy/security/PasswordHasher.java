package ma.ac.esi.gameverseacademy.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * SEC-FIX: Password hashing utility using SHA-256 with per-user salts.
 * 
 * Note: For production, consider bcrypt or Argon2 via a dedicated library.
 * SHA-256 + salt is a significant improvement over plaintext storage and
 * requires no additional dependencies.
 */
public class PasswordHasher {

    private static final int SALT_LENGTH = 16;

    /**
     * Hashes a password with a random salt.
     * @return "salt:hash" format string
     */
    public static String hashPassword(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hash = sha256(saltBase64 + password);
        return saltBase64 + ":" + hash;
    }

    /**
     * Verifies a password against a stored "salt:hash" string.
     * Also supports legacy plaintext comparison for migration.
     */
    public static boolean verifyPassword(String password, String storedValue) {
        if (storedValue == null || password == null) {
            return false;
        }
        // Support legacy plaintext passwords (migration path)
        if (!storedValue.contains(":")) {
            return storedValue.equals(password);
        }
        String[] parts = storedValue.split(":", 2);
        if (parts.length != 2) {
            return false;
        }
        String salt = parts[0];
        String storedHash = parts[1];
        String computedHash = sha256(salt + password);
        return constantTimeEquals(storedHash, computedHash);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Constant-time comparison to prevent timing attacks.
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
