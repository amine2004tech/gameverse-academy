<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="java.util.List, ma.ac.esi.gameverseacademy.model.*" %>

<%
    String ctx = request.getContextPath();
    User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GameVerse Academy — Moderation Vault</title>
    
    <!-- Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Manrope:wght@400;500;600;700&display=swap" rel="stylesheet">
    
    <!-- Reusing Home CSS for Layout + Filter Panel -->
    <link rel="stylesheet" href="<%=ctx%>/css/home.css">
    
<link rel="stylesheet" href="<%=ctx%>/css/admin.css">
</head>
<body>
    <div class="home-bg-mesh"></div>
    
    <jsp:include page="/components/navbar.jsp" />

    <main class="main-layout">
        
        <!-- LEFT: FILTER PANEL (Identical to Home) -->
        <aside class="filter-panel">
            
            <!-- TAGS SECTION -->
            <section class="filter-section" id="tags-section">
                <h2 class="section-title">Archive Tags</h2>
                <div class="tags-container">
                    <c:forEach var="tag" items="${tags}">
                        <%
                            Tag t = (Tag)pageContext.getAttribute("tag");
                            String hex = t.getColor() != null ? t.getColor().replace("#", "") : "5cffb0";
                            int r = 92, g = 255, b = 176;
                            try {
                                if(hex.length() == 6) {
                                    r = Integer.parseInt(hex.substring(0, 2), 16);
                                    g = Integer.parseInt(hex.substring(2, 4), 16);
                                    b = Integer.parseInt(hex.substring(4, 6), 16);
                                }
                            } catch(Exception e) {}
                        %>
                        <c:set var="isTagActive" value="false" />
                        <c:forEach var="selId" items="${selectedTags}">
                            <c:if test="${selId == tag.id}">
                                <c:set var="isTagActive" value="true" />
                            </c:if>
                        </c:forEach>
                        <a href="#" class="tag-filter-link" data-tag-id="${tag.id}">
                            <span class="mod-tag-ticket ${isTagActive ? 'active' : ''}" 
                                  style="--tag-rgb: <%=r%>, <%=g%>, <%=b%>">
                                ${tag.name}
                            </span>
                        </a>
                    </c:forEach>
                </div>
            </section>

            <!-- GAMES SECTION -->
            <section class="filter-section" id="games-section">
                <h2 class="section-title">Games Archive</h2>
                <div class="games-list">
                    <c:forEach var="game" items="${games}">
                        <a href="#" class="game-card ${selectedGame == game.id ? 'active' : ''}" 
                           data-game-id="${game.id}" 
                           id="game-${game.id}">
                            <img src="<%=ctx%>/assets/images/games/${game.id}.jpg" alt="${game.title}" onerror="this.src='<%=ctx%>/assets/images/games/default.jpg'">
                            <div class="game-card-overlay">
                                <span class="game-title">${game.title}</span>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </section>
        </aside>

        <!-- RIGHT: MOD ARCHIVE FEED -->
        <section class="mod-archive-feed">
            
            <!-- SECTION 1: PENDING MODS -->
            <div class="admin-feed-section">
                <h1 class="admin-section-title">Pending Clearance</h1>
                
                <c:choose>
                    <c:when test="${not empty pendingMods}">
                        <div class="mod-grid">
                            <c:forEach var="item" items="${pendingMods}">
                                <div class="mod-card mod-card-pending" id="mod-${item.id}">
                                    <div class="mod-thumbnail-box">
                                        <img src="<%=ctx%>/assets/images/mods/${item.id}_0.jpg" alt="${item.title}" onerror="this.src='<%=ctx%>/assets/images/mods/default.jpg'">
                                        <div class="mod-thumbnail-overlay"></div>
                                    </div>
                                    <div class="mod-info-box">
                                        <h3 class="mod-card-title">${item.title}</h3>
                                        
                                        <div class="creator-block">
                                            <c:set var="authorAv" value="${item.authorAvatar}" />
                                            <c:choose>
                                                <c:when test="${empty authorAv}">
                                                    <c:set var="authorAv" value="${pageContext.request.contextPath}/assets/images/avatars/default-avatar.png" />
                                                </c:when>
                                                <c:when test="${fn:startsWith(authorAv, 'http') || fn:startsWith(authorAv, '/')}">
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="authorAv" value="${pageContext.request.contextPath}/${authorAv}" />
                                                </c:otherwise>
                                            </c:choose>
                                            <img src="${authorAv}" class="creator-avatar" alt="${item.authorName}" onerror="this.src='${pageContext.request.contextPath}/assets/images/avatars/default-avatar.png'">
                                            <span class="creator-name">by ${item.authorName}</span>
                                        </div>

                                        <div class="mod-card-tags">
                                            <c:forEach var="tag" items="${item.tags}" end="2">
                                                <%
                                                    Tag t = (Tag)pageContext.getAttribute("tag");
                                                    String hex = t.getColor() != null ? t.getColor().replace("#", "") : "5cffb0";
                                                    int r = 92, g = 255, b = 176;
                                                    try {
                                                        if(hex.length() == 6) {
                                                            r = Integer.parseInt(hex.substring(0, 2), 16);
                                                            g = Integer.parseInt(hex.substring(2, 4), 16);
                                                            b = Integer.parseInt(hex.substring(4, 6), 16);
                                                        }
                                                    } catch(Exception e) {}
                                                %>
                                                <span class="card-tag-mini" style="--tag-rgb: <%=r%>, <%=g%>, <%=b%>">
                                                    ${tag.name}
                                                </span>
                                            </c:forEach>
                                        </div>

                                        <div class="admin-action-row">
                                            <a href="<%=ctx%>/ModDetailsController?id=${item.id}" class="btn-admin btn-view">VIEW</a>
                                            <form action="<%=ctx%>/AdminController" method="post" style="flex:1; margin:0; display:flex;">
                                                <input type="hidden" name="action" value="approve">
                                                <input type="hidden" name="modId" value="${item.id}">
                                                <button type="submit" class="btn-admin btn-approve" style="width:100%; border:none;" onclick="return confirm('Authorize this mod?');">AUTHORIZE</button>
                                            </form>
                                            <a href="<%=ctx%>/DeleteModController?id=${item.id}" class="btn-admin btn-danger" onclick="return confirm('Disapprove and delete this mod?');">DISAPPROVE</a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="empty-section-msg">No pending entries in the queue.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- SECTION 2: APPROVED MODS -->
            <div class="admin-feed-section">
                <h1 class="admin-section-title approved-section-title">Approved Vault</h1>
                
                <c:choose>
                    <c:when test="${not empty approvedMods}">
                        <div class="mod-grid">
                            <c:forEach var="item" items="${approvedMods}">
                                <div class="mod-card mod-card-approved" id="mod-${item.id}">
                                    <div class="mod-thumbnail-box">
                                        <img src="<%=ctx%>/assets/images/mods/${item.id}_0.jpg" alt="${item.title}" onerror="this.src='<%=ctx%>/assets/images/mods/default.jpg'">
                                        <div class="mod-thumbnail-overlay"></div>
                                    </div>
                                    <div class="mod-info-box">
                                        <h3 class="mod-card-title">${item.title}</h3>
                                        <div style="color: var(--text-muted); font-size: 0.8rem; margin-bottom: 10px;">${not empty item.gameTitle ? item.gameTitle : 'Unknown'}</div>
                                        
                                        <div class="mod-card-tags">
                                            <c:forEach var="tag" items="${item.tags}" end="2">
                                                <%
                                                    Tag t = (Tag)pageContext.getAttribute("tag");
                                                    String hex = t.getColor() != null ? t.getColor().replace("#", "") : "5cffb0";
                                                    int r = 92, g = 255, b = 176;
                                                    try {
                                                        if(hex.length() == 6) {
                                                            r = Integer.parseInt(hex.substring(0, 2), 16);
                                                            g = Integer.parseInt(hex.substring(2, 4), 16);
                                                            b = Integer.parseInt(hex.substring(4, 6), 16);
                                                        }
                                                    } catch(Exception e) {}
                                                %>
                                                <span class="card-tag-mini" style="--tag-rgb: <%=r%>, <%=g%>, <%=b%>">
                                                    ${tag.name}
                                                </span>
                                            </c:forEach>
                                        </div>

                                        <div class="mod-card-meta" style="margin-top: 15px; padding-bottom: 15px; border-bottom: 1px solid rgba(255,255,255,0.05);">
                                            <div class="meta-item">
                                                <span class="download-icon-clean"></span>
                                                <span>${item.downloads} DLs</span>
                                            </div>
                                            <div class="big-star-box">
                                                <div class="avg-star-container" style="--star-fill-pct: ${(item.averageRating / 5.0) * 100}%">
                                                    <div class="star-layer empty"></div>
                                                    <div class="star-layer filled"></div>
                                                </div>
                                                <span class="rating-double">${item.averageRating}</span>
                                            </div>
                                        </div>

                                        <div class="admin-action-row" style="border-top: none; padding-top: 0;">
                                            <a href="<%=ctx%>/ModDetailsController?id=${item.id}" class="btn-admin btn-view">VIEW</a>
                                            <a href="<%=ctx%>/DeleteModController?id=${item.id}" class="btn-admin btn-danger" onclick="return confirm('Permanently delete this approved mod?');">DELETE</a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="empty-section-msg">No approved entries found matching your criteria.</p>
                    </c:otherwise>
                </c:choose>
            </div>

        </section>
    </main>

    <jsp:include page="/components/footer.jsp" />

    <!-- Inline script for admin filtering to target AdminController instead of ModController -->
<script src="<%=ctx%>/assets/js/admin.js"></script>
</html>
