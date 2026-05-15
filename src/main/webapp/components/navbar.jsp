<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="ma.ac.esi.gameverseacademy.model.User" %>

<%
    // Determine if we should show the navbar
    String uri = request.getRequestURI();
    boolean hideNavbar = uri.endsWith("login.jsp") || uri.endsWith("register.jsp");
    request.setAttribute("hideNavbar", hideNavbar);

    // Get current user from session
    User currentUser = (User) session.getAttribute("user");
    request.setAttribute("currentUser", currentUser);
%>

<c:if test="${!hideNavbar}">
    <!-- Navbar Assets -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Vinque Font placeholder - assuming it's available in global CSS or provided here -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbar.css">
    
    <nav class="gv-navbar-wrapper">
        <div class="gv-navbar">
            <!-- LEFT: Logo -->
            <div class="gv-navbar-left">
                <a href="${pageContext.request.contextPath}/ModController" class="logo">GAMEVERSE ACADEMY</a>
            </div>

            <!-- CENTER: Navigation -->
            <div class="gv-navbar-center">
                <a href="${pageContext.request.contextPath}/ModController" class="nav-link">Home</a>
                <a href="${pageContext.request.contextPath}/ModSubmitController" class="nav-link">Submit Mod</a>
                <a href="${pageContext.request.contextPath}/academy.jsp" class="nav-link">Academy</a>
            </div>

            <!-- RIGHT: Actions -->
            <div class="gv-navbar-right">
                <!-- Theme Switcher -->
                <div class="theme-switch-container">
                    <div class="theme-switch" title="Toggle Light/Dark Mode"></div>
                </div>

                <c:choose>
                    <c:when test="${not empty currentUser}">
                        <!-- Admin Panel Button (Admin Only) -->
                        <c:if test="${fn:toLowerCase(currentUser.role) eq 'admin'}">
                            <a href="${pageContext.request.contextPath}/AdminController" class="gv-btn">ADMIN PANEL</a>
                        </c:if>

                        <!-- Logout Button -->
                        <a href="${pageContext.request.contextPath}/LogoutController" class="gv-btn">Logout</a>

                        <!-- Avatar -->
                        <a href="${pageContext.request.contextPath}/ProfileController" class="gv-avatar-container">
                            <c:set var="navAvatar" value="${pageContext.request.contextPath}/assets/images/avatars/default-avatar.png" />
                            <c:if test="${not empty currentUser.avatar}">
                                <c:choose>
                                    <c:when test="${fn:startsWith(currentUser.avatar, 'http') || fn:startsWith(currentUser.avatar, '/')}">
                                        <c:set var="navAvatar" value="${currentUser.avatar}" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="navAvatar" value="${pageContext.request.contextPath}/${currentUser.avatar}" />
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                            <img src="${navAvatar}" alt="Avatar" class="gv-avatar" onerror="this.src='${pageContext.request.contextPath}/assets/images/avatars/default-avatar.png'">
                        </a>
                    </c:when>
                    <c:otherwise>
                        <!-- Login Button (Not Connected) -->
                        <a href="${pageContext.request.contextPath}/LoginController" class="gv-btn">Login</a>
                    </c:otherwise>
                </c:choose>

                <!-- Mobile Menu Toggle -->
                <div class="gv-mobile-toggle">
                    <span></span>
                    <span></span>
                    <span></span>
                </div>
            </div>
        </div>
    </nav>

    <script src="${pageContext.request.contextPath}/js/navbar.js"></script>
</c:if>
