<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>GameVerse Academy — Register</title>

    <!-- Link to Generic Styling -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/generic.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile_modals.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/register.css">
</head>

<body data-context-path="${pageContext.request.contextPath}">
    <jsp:include page="/components/navbar.jsp" />

    <!-- Uses existing wallpaper but integrates perfectly into the dark overlay -->
    <img class="login-bg" src="${pageContext.request.contextPath}/login_wallpaper.jpg" alt="Background">

    <div class="futuristic-card login-card register-card">

        <h1 class="login-logo">GameVerse <span>Academy</span></h1>
        <p class="login-subtitle" style="margin-bottom: 15px;">ENLIST IN THE ACADEMY</p>

        <c:if test="${not empty param.error}">
            <script>
                document.addEventListener('DOMContentLoaded', () => {
                    showToast("Registration Failed", "${fn:escapeXml(param.error)}", "error");
                });
            </script>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <input type="hidden" name="avatar" id="selectedAvatarInput" value="assets/images/avatars_default/blue/1.png">

            <!-- Avatar Preview Frame -->
            <div class="avatar-frame">
                <img src="${pageContext.request.contextPath}/assets/images/avatars_default/blue/1.png" alt="Avatar" class="profile-avatar" id="avatarSelectorTrigger" 
                     onclick="openModal('avatarModal')" style="cursor: pointer;"
                     onerror="this.src='${pageContext.request.contextPath}/assets/images/avatars/default-avatar.png'">
                <div class="avatar-edit-hint" title="Choose Avatar" onclick="openModal('avatarModal')">✎</div>
            </div>

            <div class="form-row">
                <div class="input-group">
                    <label class="input-label" for="username">Identifier</label>
                    <input type="text" id="username" name="username" class="futuristic-input"
                        placeholder="Choose an identifier" value="${fn:escapeXml(param.uname)}" required>
                </div>
                <div class="input-group">
                    <label class="input-label" for="email">Commlink (Email)</label>
                    <input type="email" id="email" name="email" class="futuristic-input"
                        placeholder="Enter your email" value="${fn:escapeXml(param.email)}" required>
                </div>
            </div>

            <div class="input-group">
                <label class="input-label" for="password">Access Code</label>
                <input type="password" id="password" name="password" class="futuristic-input"
                    placeholder="Create an access code" required>
            </div>

            <div class="input-group">
                <label class="input-label" for="confirmPassword">Verify Access Code</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="futuristic-input"
                    placeholder="Verify your access code" required>
            </div>

            <button type="submit" class="btn-futuristic login-btn" style="margin-top: 15px;">INITIALIZE PROFILE</button>

            <div class="register-link">
                Already an agent? <a href="${pageContext.request.contextPath}/LoginController">Access Dashboard</a>
            </div>

        </form>

    </div>

    <!-- Include Modal Component -->
    <jsp:include page="/components/profile_modals.jsp" />

    <script src="${pageContext.request.contextPath}/assets/js/generic.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/register.js"></script>
</body>

</html>
