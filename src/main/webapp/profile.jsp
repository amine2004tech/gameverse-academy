<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>GameVerse Academy — User Profile</title>
    
    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    
    <!-- Profile Styling -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css">
</head>

<body data-context-path="${pageContext.request.contextPath}">
    <!-- Premium Navbar -->
    <jsp:include page="/components/navbar.jsp" />

    <div class="profile-bg-mesh"></div>

    <div class="profile-container">
        <!-- Sidebar: User Info -->
        <aside class="profile-sidebar">
            <div class="sidebar-card">
                <div class="avatar-frame">
                    <c:set var="avatarUrl" value="${pageContext.request.contextPath}/assets/images/avatars/default-avatar.png" />
                    <c:if test="${not empty user.avatar}">
                        <c:choose>
                            <c:when test="${fn:startsWith(user.avatar, 'http') || fn:startsWith(user.avatar, '/')}">
                                <c:set var="avatarUrl" value="${user.avatar}" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="avatarUrl" value="${pageContext.request.contextPath}/${user.avatar}" />
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                    <img src="${avatarUrl}" alt="Avatar" class="profile-avatar" id="avatarSelectorTrigger" 
                         onclick="openModal('avatarModal')" style="cursor: pointer;"
                         onerror="this.src='${pageContext.request.contextPath}/assets/images/avatars/default-avatar.png'">
                    <div class="avatar-edit-hint" title="Change Avatar" onclick="openModal('avatarModal')">✎</div>
                </div>

                <h2 class="username-title">${not empty user.username ? user.username : user.login}</h2>

                <div class="stats-grid">
                    <div class="stat-item">
                        <span class="stat-label">Identifier</span>
                        <span class="stat-value">${user.login}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">Member Role</span>
                        <span class="stat-value" style="text-transform: capitalize;">${user.role}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">Contributions</span>
                        <span class="stat-value">${not empty mods ? fn:length(mods) : '0'} Mods</span>
                    </div>
                </div>

                <div style="display: flex; flex-direction: column; gap: 15px; width: 100%;">
                    <button class="profile-btn" onclick="openModal('editUsernameModal')">Edit Identity</button>
                    <button class="profile-btn" onclick="openModal('passwordModal')">Security Access</button>
                    <a href="${pageContext.request.contextPath}/LogoutController" class="profile-btn danger">Terminate Session</a>
                </div>
            </div>
        </aside>

        <!-- Main Content: Mods List -->
        <main class="profile-content">
            <div class="content-header">
                <div>
                    <h1 class="section-title">REPOSITORY ENTRIES</h1>
                    <p style="color: var(--text-muted); font-size: 0.9rem;">Manage your published modifications within the academy.</p>
                </div>
                <a href="${pageContext.request.contextPath}/ModSubmitController" class="profile-btn">+ NEW SUBMISSION</a>
            </div>

            <c:choose>
                <c:when test="${empty mods}">
                    <div class="sidebar-card" style="padding: 100px 0; width: 100%;">
                        <h3 style="font-size: 1.5rem; color: var(--text-muted);">No entries found in your repository.</h3>
                        <a href="${pageContext.request.contextPath}/ModSubmitController" class="profile-btn" style="margin-top: 20px;">Begin First Submission</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="mod-grid">
                        <c:forEach var="m" items="${mods}">
                            <div class="mod-card">
                                <c:set var="status" value="${not empty m.status ? m.status : 'APPROVED'}" />
                                <span class="mod-status-tag status-${fn:toLowerCase(status)}">${status}</span>
                                
                                <div class="mod-thumbnail-wrapper">
                                    <c:set var="thumb" value="${not empty m.thumbnail ? m.thumbnail : m.id.toString() + '_0.jpg'}" />
                                    <img src="${pageContext.request.contextPath}/assets/images/mods/${thumb}" alt="${m.title}" class="mod-thumbnail" onerror="this.src='${pageContext.request.contextPath}/assets/avatar.jpg'">
                                </div>

                                <div class="mod-card-content">
                                    <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 15px; margin-bottom: 10px;">
                                        <div style="flex: 1;">
                                            <h3 class="mod-card-title">${m.title}</h3>
                                            <div class="mod-card-game">${not empty m.gameTitle ? m.gameTitle : 'Archives'}</div>
                                        </div>
                                        
                                        <div class="mod-card-big-star">
                                            <div class="avg-star-container large" style="--star-fill-pct: ${m.averageRating * 20}%;">
                                                <div class="star-layer empty"></div>
                                                <div class="star-layer filled"></div>
                                            </div>
                                            <div class="mod-card-rating-text">${m.averageRating > 0 ? m.averageRating : '0.0'}</div>
                                        </div>
                                    </div>
                                    
                                    <div class="mod-card-footer">
                                        <div class="mod-stat">
                                            <span>⬇</span> ${not empty m.downloads ? m.downloads : 0}
                                        </div>
                                        <div style="display: flex; gap: 10px;">
                                            <a href="${pageContext.request.contextPath}/ModDetailsController?id=${m.id}" class="profile-btn" style="padding: 6px 12px; font-size: 0.65rem;">VIEW</a>
                                            <a href="${pageContext.request.contextPath}/DeleteModController?id=${m.id}" class="profile-btn danger" style="padding: 6px 12px; font-size: 0.65rem;" onclick="return confirm('Confirm deletion of this entry?');">DELETE</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </div>

    <!-- Modals -->
    <jsp:include page="/components/profile_modals.jsp" />

    <!-- Scripts -->
    <script src="${pageContext.request.contextPath}/assets/js/generic.js"></script>
</body>
</html>
