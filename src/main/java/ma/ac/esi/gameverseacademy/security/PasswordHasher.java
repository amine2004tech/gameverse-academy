package ma.ac.esi.gameverseacademy.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {

    private static final int SALT_LENGTH = 32;
    private static final int ITERATIONS = 310000;
    private static final int KEY_LENGTH = 256; // bits
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String HASH_PREFIX = "pbkdf2_sha256$" + ITERATIONS + "$";

    public static String hashPassword(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);
        
        return HASH_PREFIX + saltBase64 + "$" + hashBase64;
    }

    public static boolean verifyPassword(String password, String storedValue) {
        if (storedValue == null || password == null) {
            return false;
        }

        // Check if it's the new format
        if (storedValue.startsWith("pbkdf2_sha256$")) {
            String[] parts = storedValue.split("\\$");
            if (parts.length != 4) return false;
            
            try {
                int iterations = Integer.parseInt(parts[1]);
                byte[] salt = Base64.getDecoder().decode(parts[2]);
                String storedHashBase64 = parts[3];
                
                byte[] computedHash = pbkdf2(password.toCharArray(), salt, iterations, KEY_LENGTH);
                String computedHashBase64 = Base64.getEncoder().encodeToString(computedHash);
                
                return constantTimeEquals(storedHashBase64, computedHashBase64);
            } catch (Exception e) {
                return false;
            }
        }
        
        // Check if it's the old SHA-256 format (salt:hash)
        if (storedValue.contains(":")) {
            String[] parts = storedValue.split(":", 2);
            if (parts.length != 2) return false;
            
            String salt = parts[0];
            String storedHash = parts[1];
            String computedHash = sha256Legacy(salt + password);
            return constantTimeEquals(storedHash, computedHash);
        }

        // Support legacy plaintext passwords (migration path)
        return storedValue.equals(password);
    }
    
    public static boolean isHashedPassword(String storedValue) {
        if (storedValue == null) return false;
        return storedValue.startsWith("pbkdf2_sha256$");
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private static String sha256Legacy(String input) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
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
