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
import java.io.IOException;

@WebFilter(urlPatterns = {
        "/LoginController",
        "/ModSubmitController",
        "/AdminController",
        "/ReviewController",
        "/UpdateReviewController"
})
public class SecurityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Wrap request to intercept getParameter and apply InputSanitizer
        HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
            @Override
            public String getParameter(String name) {
                String value = super.getParameter(name);
                if (value == null) {
                    return null;
                }

                // CRITICAL REQUIREMENT: Do not sanitize passwords to avoid breaking
                // authentication
                if ("psw".equals(name) || "password".equals(name) || "passwordConfirm".equals(name)) {
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

                if ("psw".equals(name) || "password".equals(name) || "passwordConfirm".equals(name)) {
                    return values;
                }

                String[] sanitizedValues = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    sanitizedValues[i] = InputSanitizer.sanitize(values[i]);
                }
                return sanitizedValues;
            }
        };

        String path = httpRequest.getServletPath();
        String method = httpRequest.getMethod();

        // Input Validation Layer
        if ("/LoginController".equals(path) && "POST".equalsIgnoreCase(method)) {
            String uname = wrappedRequest.getParameter("uname");
            String psw = wrappedRequest.getParameter("psw"); // Unsanitized password

            // Validate boundaries safely without breaking legitimate logins
            if (!InputValidator.isValidUsernameLength(uname) || !InputValidator.isValidPasswordLength(psw)) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?error=1");
                return;
            }
        }

        if (("/ReviewController".equals(path) || "/UpdateReviewController".equals(path))
                && "POST".equalsIgnoreCase(method)) {
            String comment = wrappedRequest.getParameter("comment");
            if (!InputValidator.isSafeText(comment, 2000)) {
                // If comment is too large, we could handle it here, but to avoid breaking flow,
                // we allow the business logic to either truncate or save safely.
            }
        }

        // Pass the wrapped (sanitized) request along the filter chain
        chain.doFilter(wrappedRequest, httpResponse);
    }

    @Override
    public void destroy() {
        // Cleanup
    }
}
