<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="ma.ac.esi.gameverseacademy.model.*" %>
<%
    if (request.getAttribute("games") == null) {
        response.sendRedirect(request.getContextPath() + "/ModSubmitController");
        return;
    }
%>

<c:set var="pendingCount" value="0" />
<c:if test="${not empty mods}">
    <c:forEach var="m" items="${mods}">
        <c:if test="${m.status eq 'PENDING'}">
            <c:set var="pendingCount" value="${pendingCount + 1}" />
        </c:if>
    </c:forEach>
</c:if>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Submit Mod — GameVerse Academy</title>
    
    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Manrope:wght@400;500;600&display=swap" rel="stylesheet">
    
    <!-- Styling -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/generic.css?v=4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/submit.css?v=4">
</head>

<body data-context-path="${pageContext.request.contextPath}" data-pending-count="${pendingCount}">
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

                <%-- Validation Error Messages (Converted to Cinematic Toasts) --%>
                <c:if test="${not empty param.error}">
                    <script>
                        document.addEventListener('DOMContentLoaded', () => {
                            const errStr = "${fn:escapeXml(param.error)}".replace(/\+/g, ' ');
                            const errLines = errStr.includes('|') 
                                ? errStr.split('|').map(s => `• ${s}`).join('<br>') 
                                : errStr;
                            showToast("Submission Error", errLines, "error");
                        });
                    </script>
                </c:if>
                <c:if test="${not empty param.success}">
                    <script>
                        document.addEventListener('DOMContentLoaded', () => {
                            showToast("Success", "Mod archived successfully! It will appear after admin approval.", "success");
                        });
                    </script>
                </c:if>

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
                                    <div class="mod-card" id="mod-card-${m.id}">
                                        <c:set var="status" value="${not empty m.status ? m.status : 'APPROVED'}" />
                                        <span class="mod-status-tag status-${fn:toLowerCase(status)}">${status}</span>
                                        
                                        <div class="mod-thumbnail-wrapper">
                                            <c:set var="thumb" value="${not empty m.thumbnail ? m.thumbnail : m.id.toString().concat('_0.jpg')}" />
                                            <img src="${pageContext.request.contextPath}/assets/images/mods/${thumb}" alt="${fn:escapeXml(m.title)}" class="mod-thumbnail" onerror="this.src='${pageContext.request.contextPath}/assets/images/mods/default_mod.png'">
                                        </div>

                                        <div class="mod-card-content">
                                            <div class="mod-card-header-flex">
                                                <div style="flex: 1;">
                                                    <h3 class="mod-card-title">${fn:escapeXml(m.title)}</h3>
                                                    <div class="mod-card-game">${not empty m.gameTitle ? m.gameTitle : 'Archives'}</div>
                                                </div>
                                                
                                                <div class="mod-card-big-star">
                                                    <div class="avg-star-container large" style="--star-fill-pct: ${m.averageRating * 20}%;">
                                                        <div class="star-layer empty"></div>
                                                        <div class="star-layer filled"></div>
                                                    </div>
                                                    <div class="mod-card-rating-text"><fmt:formatNumber value="${m.averageRating}" pattern="0.0" /></div>
                                                </div>
                                            </div>
                                            
                                            <%-- Tags --%>
                                            <div class="mod-tags-row">
                                                <c:if test="${not empty m.tags}">
                                                    <c:forEach var="t" items="${m.tags}" end="2">
                                                        <%
                                                            Tag tagObj = (Tag)pageContext.getAttribute("t");
                                                            String hex = tagObj.getColor() != null ? tagObj.getColor().replace("#", "") : "5cffb0";
                                                            int r = 92, g = 255, b = 176;
                                                            try {
                                                                if(hex.length() == 6) {
                                                                    r = Integer.parseInt(hex.substring(0, 2), 16);
                                                                    g = Integer.parseInt(hex.substring(2, 4), 16);
                                                                    b = Integer.parseInt(hex.substring(4, 6), 16);
                                                                }
                                                            } catch(Exception e) {}
                                                        %>
                                                        <span class="mod-mini-tag" style="--tag-rgb: <%=r%>, <%=g%>, <%=b%>">
                                                            ${t.name}
                                                        </span>
                                                    </c:forEach>
                                                    <c:if test="${fn:length(m.tags) > 3}">
                                                        <span class="mod-mini-tag" style="--tag-rgb: 136, 136, 136">+${fn:length(m.tags) - 3}</span>
                                                    </c:if>
                                                </c:if>
                                            </div>

                                            <div class="mod-card-footer">
                                                <div class="mod-stat">
                                                    <span>⬇</span> ${not empty m.downloads ? m.downloads : 0}
                                                </div>
                                                <div class="mod-card-actions">
                                                    <a href="${pageContext.request.contextPath}/ModDetailsController?id=${m.id}" class="card-btn btn-view">VIEW</a>
                                                    <button class="card-btn btn-edit"
                                                            data-id="${m.id}"
                                                            data-title="${fn:escapeXml(m.title)}"
                                                            data-desc="${fn:escapeXml(m.description)}"
                                                            data-game="${m.gameId}"
                                                            data-yt="${m.youtubeVideoId}"
                                                            data-zip="${not empty m.fileName ? fn:escapeXml(m.fileName) : ''}"
                                                            data-images='[<c:forEach var="img" items="${m.images}" varStatus="loop">"${img.imageName}"${!loop.last ? "," : ""}</c:forEach>]'
                                                            data-tags='[<c:forEach var="t" items="${m.tags}" varStatus="loop">${t.id}${!loop.last ? "," : ""}</c:forEach>]'
                                                            onclick="openEditModalFromBtn(this)">EDIT</button>
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
                
                <form id="submitModForm" action="${pageContext.request.contextPath}/ModSubmitController" method="post" enctype="multipart/form-data" novalidate>
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
                                    <option value="${g.id}">${fn:escapeXml(g.title)}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Classification Tags</label>
                        <div class="tags-selector-grid">
                            <c:forEach var="t" items="${tags}">
                                <%
                                    Tag tagObjSel = (Tag)pageContext.getAttribute("t");
                                    String hexSel = tagObjSel.getColor() != null ? tagObjSel.getColor().replace("#", "") : "5cffb0";
                                    int rSel = 92, gSel = 255, bSel = 176;
                                    try {
                                        if(hexSel.length() == 6) {
                                            rSel = Integer.parseInt(hexSel.substring(0, 2), 16);
                                            gSel = Integer.parseInt(hexSel.substring(2, 4), 16);
                                            bSel = Integer.parseInt(hexSel.substring(4, 6), 16);
                                        }
                                    } catch(Exception e) {}
                                %>
                                <div class="tag-selector-item" data-tag-id="${t.id}" style="--tag-rgb: <%=rSel%>, <%=gSel%>, <%=bSel%>;" onclick="toggleTag(this)">
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
                    
                    <div class="form-row-split" style="max-width: 100%;">
                        <div class="form-group" style="flex: 1; min-width: 0; max-width: 100%;">
                            <label class="form-label" for="modImages">Images (JPG, PNG, WEBP) *</label>
                            <div class="file-upload-wrapper">
                                <input type="file" id="modImages" name="modImages" class="form-file" accept=".jpg,.jpeg,.png,.webp" multiple>
                                <div class="file-upload-hint">SELECT IMAGES</div>
                            </div>
                            <!-- Cinematic Image Slider Container -->
                            <div id="imagePreviewContainer" class="gv-image-slider"></div>
                            <input type="hidden" id="imageOrder" name="imageOrder" value="">
                        </div>

                        <div class="form-group" style="flex: 1; min-width: 0; max-width: 100%;">
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
    <script src="${pageContext.request.contextPath}/assets/js/generic.js?v=4"></script>
    <script src="${pageContext.request.contextPath}/js/submit.js?v=4"></script>
</body>
</html>
