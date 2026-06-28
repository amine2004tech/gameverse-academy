package ma.ac.esi.gameverseacademy.service;

import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.repository.UserRepository;
import ma.ac.esi.gameverseacademy.security.PasswordHasher;

public class UserService {

    private UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    // =========================
    // LOGIN
    // =========================

    public User getUserByCredentials(String login,
            String password) {

        User user = userRepository.getUserByCredentials(
                login,
                password);
                
        // Lazy migration to new PBKDF2 hash
        if (user != null && !PasswordHasher.isHashedPassword(user.getPassword())) {
            System.out.println("[Migration] Upgrading password for user: " + user.getLogin());
            userRepository.updatePassword(user.getId(), password);
            user.setPassword(PasswordHasher.hashPassword(password));
        }
        
        return user;
    }

    public boolean findUserByCredentials(String login,
            String password) {

        return getUserByCredentials(
                login,
                password) != null;
    }

    // =========================
    // REGISTRATION / VALIDATION
    // =========================
    
    public boolean registerUser(User user) {
        if (user == null) return false;
        return userRepository.registerUser(user);
    }
    
    public boolean usernameExists(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        return userRepository.usernameExists(username.trim());
    }
    
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return userRepository.emailExists(email.trim().toLowerCase());
    }

    // =========================
    // GET USER
    // =========================

    public User getUserById(int userId) {

        if (userId <= 0) {
            return null;
        }

        return userRepository.getUserById(userId);
    }

    // =========================
    // UPDATE USERNAME
    // =========================

    public boolean updateUsername(int userId,
            String username) {

        if (userId <= 0) {
            return false;
        }

        if (username == null ||
                username.trim().isEmpty()) {

            return false;
        }

        username = username.trim();

        if (username.length() < 3 ||
                username.length() > 30) {

            return false;
        }

        if (userRepository.usernameExists(username)) {
            return false;
        }

        return userRepository.updateUsername(
                userId,
                username);
    }

    // =========================
    // UPDATE LOGIN
    // =========================

    public boolean updateLogin(int userId,
            String login) {

        if (userId <= 0) {
            return false;
        }

        if (login == null ||
                login.trim().isEmpty()) {

            return false;
        }

        login = login.trim();

        return userRepository.updateLogin(
                userId,
                login);
    }

    // =========================
    // UPDATE PASSWORD
    // =========================

    public boolean updatePassword(int userId,
            String currentPassword,
            String newPassword) {

        if (userId <= 0) {
            return false;
        }

        User user = userRepository.getUserById(userId);

        if (user == null) {
            return false;
        }

        // SEC-FIX: Use PasswordHasher for verification instead of plaintext equals
        if (!PasswordHasher.verifyPassword(currentPassword, user.getPassword())) {
            return false;
        }

        if (newPassword == null ||
                newPassword.trim().length() < 4) {

            return false;
        }

        return userRepository.updatePassword(
                userId,
                newPassword);
    }

    // =========================
    // UPDATE AVATAR
    // =========================

    public boolean updateAvatar(int userId,
            String avatar) {

        if (userId <= 0) {
            return false;
        }

        if (avatar == null ||
                avatar.trim().isEmpty()) {

            return false;
        }

        // minimal protection
        if (avatar.contains("..")) {
            return false;
        }

        return userRepository.updateAvatar(
                userId,
                avatar);
    }
}