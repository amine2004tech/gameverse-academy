<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Submit Mod — GameVerse Academy</title>
    
    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Manrope:wght@400;500;600&display=swap" rel="stylesheet">
    
    <!-- Styling -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/submit.css">
</head>

<body data-context-path="${pageContext.request.contextPath}">
    <!-- Premium Navbar -->
    <jsp:include page="/components/navbar.jsp" />

    <div class="submit-bg-mesh"></div>

    <c:choose>
        <%-- NON-LOGGED IN USER VIEW --%>
        <c:when test="${empty user}">
            <div class="hero-unauth-container">
                <div class="hero-unauth-content">
                    <h1 class="hero-giant-title">THE ARCHIVES AWAIT</h1>
                    <h3 class="hero-immersive-subtitle">Your creations. Immortalized.</h3>
                    <p class="hero-elegant-paragraph">
                        Join the elite circle of modders and creators. Showcase your finest works, 
                        share your vision with the academy, and become a legend in the archives.
                    </p>
                    <a href="${pageContext.request.contextPath}/LoginController" class="btn-join-community">
                        JOIN THE COMMUNITY
                    </a>
                </div>
            </div>
        </c:when>
        
        <%-- LOGGED IN USER VIEW --%>
        <c:otherwise>
            <div class="submit-container">
                <%-- Upload Section --%>
                <section class="upload-section">
                    <div class="upload-header-content">
                        <h1 class="section-title">MODIFICATION ARCHIVE</h1>
                        <p class="section-subtitle">Deposit your latest creations into the secure vault.</p>
                    </div>
                    <button class="btn-upload-new" onclick="openSubmitModal()">
                        UPLOAD NEW MOD
                    </button>
                </section>

                <%-- User Published Mods Section --%>
                <section class="published-mods-section">
                    <h2 class="published-title">YOUR ARCHIVED ENTRIES</h2>
                    <c:choose>
                        <c:when test="${empty mods}">
                            <div class="empty-mods-state">
                                <p>You have not archived any modifications yet.</p>
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
                                            <div class="mod-card-header-flex">
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
                                            
                                            <%-- Tags --%>
                                            <div class="mod-tags-row">
                                                <c:if test="${not empty m.tags}">
                                                    <c:forEach var="t" items="${m.tags}" end="2">
                                                        <span class="mod-mini-tag" style="--tag-color: ${not empty t.color ? t.color : '#5cffb0'};">
                                                            ${t.name}
                                                        </span>
                                                    </c:forEach>
                                                    <c:if test="${fn:length(m.tags) > 3}">
                                                        <span class="mod-mini-tag" style="--tag-color: #888;">+${fn:length(m.tags) - 3}</span>
                                                    </c:if>
                                                </c:if>
                                            </div>

                                            <div class="mod-card-footer">
                                                <div class="mod-stat">
                                                    <span>⬇</span> ${not empty m.downloads ? m.downloads : 0}
                                                </div>
                                                <div class="mod-card-actions">
                                                    <a href="${pageContext.request.contextPath}/ModDetailsController?id=${m.id}" class="card-btn btn-view">VIEW</a>
                                                    <button onclick='openEditModal(${m.id}, "${fn:escapeXml(m.title)}", "${fn:escapeXml(m.description)}", "${m.gameId}", "${m.youtubeVideoId}")' class="card-btn btn-edit">EDIT</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </section>
            </div>
        </c:otherwise>
    </c:choose>

    <%-- Modals --%>
    <c:if test="${not empty user}">
        <div class="submit-modal-overlay" id="submitModModal">
            <div class="submit-modal-content">
                <button class="modal-close-btn" onclick="closeSubmitModal()">×</button>
                <h2 class="modal-title" id="modalTitle">ARCHIVE NEW ENTRY</h2>
                
                <form id="submitModForm" action="${pageContext.request.contextPath}/ModSubmitController" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" id="modId" name="modId" value="">
                    
                    <div class="form-group">
                        <label class="form-label" for="title">Title *</label>
                        <input type="text" id="title" name="title" class="form-input" required placeholder="Enter mod title...">
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="gameId">Game Universe *</label>
                        <div class="custom-select-wrapper">
                            <select id="gameId" name="gameId" class="form-select" required>
                                <option value="" disabled selected>Select an archive...</option>
                                <c:forEach var="g" items="${games}">
                                    <option value="${g.id}">${g.title}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Classification Tags</label>
                        <div class="tags-selector-grid">
                            <c:forEach var="t" items="${tags}">
                                <div class="tag-selector-item" data-tag-id="${t.id}" style="--tag-color: ${not empty t.color ? t.color : '#5cffb0'};" onclick="toggleTag(this)">
                                    ${t.name}
                                </div>
                            </c:forEach>
                        </div>
                        <input type="hidden" id="selectedTags" name="selectedTags" value="">
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="description">Detailed Description</label>
                        <textarea id="description" name="description" class="form-textarea" placeholder="Describe the contents of this archive..."></textarea>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="youtubeUrl">YouTube Showcase Link</label>
                        <input type="url" id="youtubeUrl" name="youtubeUrl" class="form-input" placeholder="https://youtube.com/watch?v=...">
                    </div>
                    
                    <div class="form-row-split">
                        <div class="form-group" style="flex: 1;">
                            <label class="form-label" for="modImages">Images (JPG) - First is Thumbnail</label>
                            <div class="file-upload-wrapper">
                                <input type="file" id="modImages" name="modImages" class="form-file" accept=".jpg,.jpeg" multiple>
                                <div class="file-upload-hint">SELECT IMAGES</div>
                            </div>
                        </div>

                        <div class="form-group" style="flex: 1;">
                            <label class="form-label" for="modFile">Mod Archive (ZIP)</label>
                            <div class="file-upload-wrapper">
                                <input type="file" id="modFile" name="modFile" class="form-file" accept=".zip">
                                <div class="file-upload-hint">SELECT ZIP</div>
                            </div>
                        </div>
                    </div>

                    <button type="submit" class="btn-submit-archive">ARCHIVE MOD</button>
                </form>
            </div>
        </div>
    </c:if>

    <!-- Scripts -->
    <script src="${pageContext.request.contextPath}/assets/js/generic.js"></script>
    <script src="${pageContext.request.contextPath}/js/submit.js"></script>
</body>
</html>
