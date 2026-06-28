<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.util.List, ma.ac.esi.gameverseacademy.model.*" %>

<%
    String ctx = request.getContextPath();
    User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
    if (request.getAttribute("mods") == null) {
        response.sendRedirect(ctx + "/ModController");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GameVerse Academy — Premium Mod Archive</title>
    
    <!-- Meta Tags for SEO -->
    <meta name="description" content="Discover premium mods, luxury game enhancements, and immersive community creations at GameVerse Academy. The ultimate dark fantasy mod archive.">
    
    <!-- Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Manrope:wght@400;500;600;700&display=swap" rel="stylesheet">
    
    <!-- Custom CSS -->
    <link rel="stylesheet" href="<%=ctx%>/css/home.css">
    
</head>
<body>
    <div class="home-bg-mesh"></div>
    
    <!-- Navbar Component -->
    <jsp:include page="/components/navbar.jsp" />

    <main class="main-layout">
        
        <!-- LEFT: FILTER PANEL -->
        <aside class="filter-panel">
            <div class="filter-panel-header">
                <h2 class="sidebar-title">Security Filter</h2>
                <a href="<%=ctx%>/ModController" class="btn-reset-filters">
                    Reset Protocol
                </a>
            </div>
            
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
                            <img src="<%=ctx%>/assets/images/games/${game.id}.jpg" alt="${fn:escapeXml(game.title)}" onerror="this.src='<%=ctx%>/assets/images/games/default.jpg'">
                            <div class="game-card-overlay">
                                <span class="game-title">${fn:escapeXml(game.title)}</span>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </section>
        </aside>

        <!-- RIGHT: MOD ARCHIVE FEED -->
        <section class="mod-archive-feed">
            <header class="feed-header">
                <h1 class="feed-title">Vault Entries</h1>
                <div class="feed-controls">
                    <!-- Future: Sort controls -->
                </div>
            </header>

            <c:choose>
                <c:when test="${not empty mods}">
                    <div class="mod-grid">
                        <c:forEach var="item" items="${mods}">
                            <a href="<%=ctx%>/ModDetailsController?id=${item.id}" class="mod-card" id="mod-${item.id}">
                                <div class="mod-thumbnail-box">
                                    <img src="<%=ctx%>/assets/images/mods/${item.id}_0.jpg" alt="${fn:escapeXml(item.title)}" onerror="this.src='${pageContext.request.contextPath}/assets/images/mods/default_mod.png'">
                                    <div class="mod-thumbnail-overlay"></div>
                                </div>
                                <div class="mod-info-box">
                                    <h3 class="mod-card-title">${fn:escapeXml(item.title)}</h3>
                                    
                                    <div class="creator-block">
                                        <c:set var="authorAv" value="${item.authorAvatar}" />
                                        <c:choose>
                                            <c:when test="${empty authorAv}">
                                                <c:set var="authorAv" value="${pageContext.request.contextPath}/assets/images/avatars/default-avatar.png" />
                                            </c:when>
                                            <c:when test="${fn:startsWith(authorAv, 'http') || fn:startsWith(authorAv, '/')}">
                                                <%-- Already absolute or has context path --%>
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

                                    <div class="mod-card-meta">
                                        <div class="meta-item">
                                            <span class="download-icon-clean"></span>
                                            <span>${item.downloads} downloads</span>
                                        </div>
                                        <div class="big-star-box">
                                            <div class="avg-star-container" style="--star-fill-pct: ${(item.averageRating / 5.0) * 100}%">
                                                <div class="star-layer empty"></div>
                                                <div class="star-layer filled"></div>
                                            </div>
                                            <span class="rating-double"><fmt:formatNumber value="${item.averageRating}" pattern="0.0" /></span>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-archive">
                        <div class="archive-vault-icon"></div>
                        <h2 class="empty-title">Archive Empty</h2>
                        <p class="empty-text">No entries found matching your filter criteria. Try adjusting your parameters to reveal hidden items in the vault.</p>
                        <a href="<%=ctx%>/ModController" style="margin-top: 20px; text-decoration: none;">
                            <span class="mod-tag-ticket" style="--tag-rgb: 92, 255, 176">Clear All Filters</span>
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

    </main>

    <!-- Footer Component -->
    <jsp:include page="/components/footer.jsp" />

    <!-- Scripts -->
    <script src="<%=ctx%>/js/home.js"></script>
</body>
</html>
