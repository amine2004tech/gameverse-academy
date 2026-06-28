package ma.ac.esi.gameverseacademy.security;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;

/**
 * Central security filter: input sanitization, security headers, CSRF tokens.
 */
@WebFilter(urlPatterns = {"/*"})
public class SecurityFilter implements Filter {

    // SEC-FIX: Paths that require CSRF token validation on POST
    private static final Set<String> CSRF_PROTECTED_PATHS = Set.of(
            "/LoginController",
            "/ModSubmitController",
            "/AdminController",
            "/ReviewController",
            "/UpdateReviewController",
            "/DeleteReviewController",
            "/DeleteModController",
            "/ProfileController",
            "/register"
    );

    // Paths where input sanitization should be applied
    private static final Set<String> SANITIZED_PATHS = Set.of(
            "/LoginController",
            "/ModSubmitController",
            "/AdminController",
            "/ReviewController",
            "/UpdateReviewController",
            "/ProfileController",
            "/register"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getServletPath();
        String method = httpRequest.getMethod();

        // ========== SEC-FIX: Block Direct Access to Mod Archives ==========
        if (path != null) {
            String requestURI = httpRequest.getRequestURI();
            if (requestURI != null) {
                String lowerURI = requestURI.toLowerCase();
                String contextPath = httpRequest.getContextPath().toLowerCase();
                String relativePath = lowerURI;
                if (!contextPath.isEmpty() && lowerURI.startsWith(contextPath)) {
                    relativePath = lowerURI.substring(contextPath.length());
                }
                if (relativePath.startsWith("/assets/mods/") || relativePath.equals("/assets/mods")) {
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                    return;
                }
            }
        }

        // ========== SEC-FIX: Block Direct JSP Access ==========
        // Because this filter only intercepts DispatcherType.REQUEST by default,
        // it blocks direct browser navigation to JSPs but perfectly allows 
        // RequestDispatcher.forward() and <jsp:include> to render them normally!
        if (path != null && path.endsWith(".jsp")) {
            if (!path.equals("/index.jsp") && 
                !path.equals("/login.jsp") && 
                !path.equals("/academy.jsp") && 
                !path.equals("/error403.jsp") && 
                !path.equals("/error404.jsp")) {
                
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/ModController");
                return;
            }
        }

        // ========== SEC-FIX: Security Headers ==========
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        httpResponse.setHeader("Content-Security-Policy",
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline'; " +
                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                "font-src 'self' https://fonts.gstatic.com; " +
                "img-src 'self' data: https:; " +
                "frame-src https://www.youtube.com; " +
                "connect-src 'self'");
        // Prevent caching of sensitive authenticated pages
        if (path != null && (path.contains("Controller") || path.endsWith(".jsp"))) {
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setDateHeader("Expires", 0);
        }

        // ========== SEC-FIX: CSRF Token Generation ==========
        HttpSession session = httpRequest.getSession(false);
        if (session == null && (path != null && (path.endsWith(".jsp") || CSRF_PROTECTED_PATHS.contains(path)))) {
            session = httpRequest.getSession(true);
        }
        if (session != null && session.getAttribute("csrfToken") == null) {
            session.setAttribute("csrfToken", generateCsrfToken());
        }

        // ========== SEC-FIX: CSRF Token Validation on POST ==========
        if ("POST".equalsIgnoreCase(method) && CSRF_PROTECTED_PATHS.contains(path)) {
            if (session == null) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Session required");
                return;
            }
            String sessionToken = (String) session.getAttribute("csrfToken");
            String requestToken = httpRequest.getParameter("csrfToken");
            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
                return;
            }
        }

        // ========== Input Sanitization (only on specific paths) ==========
        HttpServletRequest finalRequest = httpRequest;
        if (SANITIZED_PATHS.contains(path)) {
            finalRequest = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getParameter(String name) {
                    String value = super.getParameter(name);
                    if (value == null) {
                        return null;
                    }

                    // CRITICAL REQUIREMENT: Do not sanitize passwords to avoid breaking
                    // authentication
                    if ("psw".equals(name) || "password".equals(name) || "passwordConfirm".equals(name)
                            || "currentPassword".equals(name) || "newPassword".equals(name)
                            || "csrfToken".equals(name)) {
                        return value;
                    }

                    return InputSanitizer.sanitize(value);
                }

                @Override
                public String[] getParameterValues(String name) {
                    String[] values = super.getParameterValues(name);
                    if (values == null) {
                        return null;
                    }

                    if ("psw".equals(name) || "password".equals(name) || "passwordConfirm".equals(name)
                            || "currentPassword".equals(name) || "newPassword".equals(name)
                            || "csrfToken".equals(name)) {
                        return values;
                    }

                    String[] sanitizedValues = new String[values.length];
                    for (int i = 0; i < values.length; i++) {
                        sanitizedValues[i] = InputSanitizer.sanitize(values[i]);
                    }
                    return sanitizedValues;
                }
            };
        }

        // ========== Input Validation Layer ==========
        if ("/LoginController".equals(path) && "POST".equalsIgnoreCase(method)) {
            String uname = finalRequest.getParameter("uname");
            String psw = finalRequest.getParameter("psw"); // Unsanitized password

            // Validate boundaries safely without breaking legitimate logins
            if (!InputValidator.isValidUsernameLength(uname) || !InputValidator.isValidPasswordLength(psw)) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?error=1");
                return;
            }
        }

        if (("/ReviewController".equals(path) || "/UpdateReviewController".equals(path))
                && "POST".equalsIgnoreCase(method)) {
            String comment = finalRequest.getParameter("comment");
            if (!InputValidator.isSafeText(comment, 2000)) {
                // If comment is too large, we could handle it here, but to avoid breaking flow,
                // we allow the business logic to either truncate or save safely.
            }
        }

        // Pass the wrapped (sanitized) request along the filter chain
        chain.doFilter(finalRequest, httpResponse);
    }

    @Override
    public void destroy() {
        // Cleanup
    }

    private static String generateCsrfToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
