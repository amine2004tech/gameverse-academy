package ma.ac.esi.gameverseacademy.security;

import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidUsernameLength(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        // Allows reasonable usernames without breaking existing short/long ones
        return username.length() >= 3 && username.length() <= 100;
    }

    public static boolean isValidPasswordLength(String password) {
        if (password == null || password.isEmpty()) return false;
        // Password length check to prevent empty or excessively huge inputs (DoS)
        return password.length() >= 1 && password.length() <= 255;
    }
    
    public static boolean isSafeText(String text, int maxLength) {
        if (text == null) return true;
        return text.length() <= maxLength;
    }
    
    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
