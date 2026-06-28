<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>GameVerse Academy — Login</title>

        <!-- Link to Generic Styling -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/generic.css">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">

    <body>
        <jsp:include page="/components/navbar.jsp" />

        <!-- Uses existing wallpaper but integrates perfectly into the dark overlay -->
        <img class="login-bg" src="${pageContext.request.contextPath}/login_wallpaper.jpg" alt="Background">

        <div class="futuristic-card login-card">

            <h1 class="login-logo">GameVerse <span>Academy</span></h1>
            <p class="login-subtitle">ACCESS YOUR MODDING DASHBOARD</p>

            <c:if test="${not empty param.error}">
                <script>
                    document.addEventListener('DOMContentLoaded', () => {
                        showToast("Authentication Failed", "Invalid identifier or access code. Please try again.", "error");
                    });
                </script>
            </c:if>
            <c:if test="${not empty param.success}">
                <script>
                    document.addEventListener('DOMContentLoaded', () => {
                        showToast("Success", "${fn:escapeXml(param.success)}", "success");
                    });
                </script>
            </c:if>

                    <form action="${pageContext.request.contextPath}/LoginController" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                        <div class="input-group">
                            <label class="input-label" for="uname">Username</label>
                            <input type="text" id="uname" name="uname" class="futuristic-input"
                                placeholder="Enter your identifier" required>
                        </div>

                        <div class="input-group">
                            <label class="input-label" for="psw">Password</label>
                            <input type="password" id="psw" name="psw" class="futuristic-input"
                                placeholder="Enter your access code" required>
                        </div>

                        <div class="options-row">
                            <label class="checkbox-label">
                                <input type="checkbox" name="remember" checked>
                                Remember Session
                            </label>
                            <a href="#" class="forgot-link">Recover Access</a>
                        </div>

                        <button type="submit" class="btn-futuristic login-btn">INITIALIZE LOGIN</button>

                        <div class="register-link">
                            New to the academy? <a href="${pageContext.request.contextPath}/register">Create a profile</a>
                        </div>

                    </form>

        </div>

    <script src="${pageContext.request.contextPath}/assets/js/generic.js"></script>
    </body>

    </html>
