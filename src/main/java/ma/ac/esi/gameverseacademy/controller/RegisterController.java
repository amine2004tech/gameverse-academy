package ma.ac.esi.gameverseacademy.controller;

import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/register")
public class RegisterController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/ModController");
            return;
        }

        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/ModController");
            return;
        }

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        UserService userService = new UserService();
        
        // Validation
        if (username == null || username.trim().isEmpty() || username.trim().length() < 3 || username.trim().length() > 30 || !username.matches("^[a-zA-Z0-9_-]+$")) {
            redirectWithError(request, response, username, email, "Username must be between 3 and 30 characters and contain only letters, numbers, underscores, or dashes.");
            return;
        }
        username = username.trim();

        if (email == null || email.trim().isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            redirectWithError(request, response, username, email, "Email address is invalid.");
            return;
        }
        email = email.trim().toLowerCase();

        if (password == null || password.length() < 8 || password.length() > 128) {
            redirectWithError(request, response, username, email, "Password must be between 8 and 128 characters.");
            return;
        }
        
        if (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*\\d.*") || !password.matches(".*[^a-zA-Z0-9].*")) {
            redirectWithError(request, response, username, email, "Password must contain uppercase, lowercase, number, and special character.");
            return;
        }

        if (password.equalsIgnoreCase(username)) {
            redirectWithError(request, response, username, email, "Password cannot equal your username.");
            return;
        }

        if (password.equalsIgnoreCase(email)) {
            redirectWithError(request, response, username, email, "Password cannot equal your email.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            redirectWithError(request, response, username, email, "Passwords do not match.");
            return;
        }

        if (userService.usernameExists(username)) {
            redirectWithError(request, response, username, email, "This username is already taken.");
            return;
        }

        if (userService.emailExists(email)) {
            redirectWithError(request, response, username, email, "This email is already used.");
            return;
        }

        String avatar = request.getParameter("avatar");
        if (avatar != null && !avatar.trim().isEmpty()) {
            avatar = avatar.trim();
            if (avatar.contains("..") || !avatar.startsWith("assets/images/avatars_default/")) {
                avatar = "assets/images/avatars_default/blue/1.png";
            }
        } else {
            avatar = "assets/images/avatars_default/blue/1.png";
        }

        // Register user
        User user = new User();
        user.setUsername(username);
        user.setLogin(email);
        user.setPassword(password); // Will be hashed in UserRepository.registerUser
        user.setRole("USER");
        user.setAvatar(avatar);

        if (userService.registerUser(user)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?success=" + URLEncoder.encode("Account created successfully. You can now log in.", "UTF-8"));
        } else {
            redirectWithError(request, response, username, email, "Failed to create account. Please try again later.");
        }
    }
    
    private void redirectWithError(HttpServletRequest request, HttpServletResponse response, String username, String email, String error) throws IOException {
        String url = request.getContextPath() + "/register?error=" + URLEncoder.encode(error, "UTF-8");
        if (username != null) url += "&uname=" + URLEncoder.encode(username, "UTF-8");
        if (email != null) url += "&email=" + URLEncoder.encode(email, "UTF-8");
        response.sendRedirect(url);
    }
}
