<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
            <%@ page import="java.util.List,java.util.Map" %>
                <%@ page import="ma.ac.esi.gameverseacademy.model.*" %>
                <%@ page import="ma.ac.esi.gameverseacademy.security.HtmlEncoder" %>
                    <% Mod mod=(Mod) request.getAttribute("mod"); Review userReview=(Review)
                        request.getAttribute("userReview"); List<Review> reviews = (mod != null) ? mod.getReviews() :
                        null;
                        double averageRating = (mod != null) ? mod.getAverageRating() : 0.0;
                        List<ModImage> modImages = (mod != null) ? mod.getImages() : null;
                            Map<Integer,Integer> ratingDist = (mod != null) ? mod.getRatingDistribution() : null;
                                Game game = (mod != null) ? mod.getGame() : null;
                                List<Tag> tags = (mod != null) ? mod.getTags() : null;

                                    User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
                                    String ctx = request.getContextPath();
                                    int totalReviews = (reviews != null) ? reviews.size() : 0;
                                    int filledPct = (int) Math.round((averageRating / 5.0) * 100);
                                    %>
                                    <!DOCTYPE html>
                                    <html lang="en">

                                    <head>
                                        <meta charset="UTF-8">
                                        <meta name="viewport" content="width=device-width,initial-scale=1">
                                        <title>
                                            <%= HtmlEncoder.encode(mod!=null?mod.getTitle():"Mod") %> — GameVerse Academy
                                        </title>

                                        <!-- Fonts -->
                                        <link rel="preconnect" href="https://fonts.googleapis.com">
                                        <link
                                            href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600&family=Manrope:wght@400;500;600&display=swap"
                                            rel="stylesheet">

                                        <!-- Global CSS -->
                                        <link rel="stylesheet" href="<%=ctx%>/assets/css/generic.css">
                                        <link rel="stylesheet" href="<%=ctx%>/css/modDetails.css">
                                    </head>

                                    <body>
                                        <jsp:include page="/components/navbar.jsp" />

                                        <% if(mod==null){ %>
                                            <div class="details-container" style="text-align:center; padding: 100px 0;">
                                                <h1 class="mod-title-large">Entry Not Found</h1>
                                                <a href="<%=ctx%>/ModController" class="btn-futuristic"
                                                    style="margin-top:20px;">Return to Archive</a>
                                            </div>
                                            <% } else { %>

                                                <main class="details-container">

                                                    <!-- HERO SECTION -->
                                                    <section class="hero-section">

                                                        <!-- LEFT: GALLERY AREA -->
                                                        <div class="mod-gallery-area">
                                                            <!-- THUMBNAIL SWITCHER (ON THE SIDE) -->
                                                            <% if(modImages !=null && modImages.size()> 1) { %>
                                                                <div class="thumb-switch-container">
                                                                    <% int idx=0; for(ModImage img : modImages) { %>
                                                                        <div class="thumb-switch-item <%= idx == 0 ? "active" : "" %>"
                                                                            onclick="goToSlide(<%= idx %>)"
                                                                                data-index="<%= idx %>">
                                                                                    <img src="<%=ctx%>/assets/images/mods/<%= img.getImageName() %>"
                                                                                        alt="Thumbnail">
                                                                        </div>
                                                                        <% idx++; } %>
                                                                </div>
                                                                <% } %>

                                                                    <div class="cinematic-slider-wrap" id="mainSlider">
                                                                        <div class="slides-track" id="slidesTrack">
                                                                            <% if(modImages !=null &&
                                                                                !modImages.isEmpty()){ for(ModImage img
                                                                                : modImages){ %>
                                                                                <div class="slide-item">
                                                                                    <img src="<%=ctx%>/assets/images/mods/<%= img.getImageName() %>"
                                                                                        alt="Screenshot">
                                                                                    <div class="slider-overlay"></div>
                                                                                </div>
                                                                                <% } } else { %>
                                                                                    <div class="slide-item">
                                                                                        <img src="<%=ctx%>/assets/images/mods/<%= mod.getId() %>_0.jpg"
                                                                                            alt="Thumbnail">
                                                                                        <div class="slider-overlay">
                                                                                        </div>
                                                                                    </div>
                                                                                    <% } %>
                                                                        </div>

                                                                        <% if(modImages !=null && modImages.size()> 1){
                                                                            %>
                                                                            <div class="slide-nav nav-prev"
                                                                                onclick="moveSlide(-1)">&#8249;
                                                                            </div>
                                                                            <div class="slide-nav nav-next"
                                                                                onclick="moveSlide(1)">&#8250;
                                                                            </div>
                                                                            <% } %>
                                                                    </div>
                                                        </div>

                                                        <!-- RIGHT: MOD ARCHIVE INFO -->
                                                        <div class="mod-details-panel">
                                                            <header class="mod-header">
                                                                <!-- TAGS UNDER TITLE -->
                                                                <div class="futuristic-tags-row">
                                                                    <% if(tags !=null) { for(Tag t : tags) { String
                                                                        hex=t.getColor() !=null ?
                                                                        t.getColor().replace("#","") : "5cffb0" ; int
                                                                        r=92, g=255, b=176; try {
                                                                        r=Integer.parseInt(hex.substring(0,2), 16);
                                                                        g=Integer.parseInt(hex.substring(2,4), 16);
                                                                        b=Integer.parseInt(hex.substring(4,6), 16); }
                                                                        catch(Exception e) {} %>
                                                                        <a href="<%=ctx%>/ModController?tags=<%=t.getId()%>"
                                                                            style="text-decoration: none;">
                                                                            <span class="mod-tag-ticket"
                                                                                style="--tag-rgb: <%=r%>,<%=g%>,<%=b%>;">
                                                                                <%= HtmlEncoder.encode(t.getName()) %>
                                                                            </span>
                                                                        </a>
                                                                        <% } } %>
                                                                </div>

                                                                <h1 class="mod-title-large">
                                                                    <%= HtmlEncoder.encode(mod.getTitle()) %>
                                                                </h1>

                                                                <div class="author-archive-strip">
                                                                    <% request.setAttribute("modObj", mod); %>
                                                                        <c:choose>
                                                                            <c:when test="${empty modObj.authorAvatar}">
                                                                                <c:set var="authorAvatarUrl"
                                                                                    value="${pageContext.request.contextPath}/assets/images/avatars/default-avatar.png" />
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${fn:startsWith(modObj.authorAvatar, 'http') || fn:startsWith(modObj.authorAvatar, '/')}">
                                                                                <c:set var="authorAvatarUrl"
                                                                                    value="${modObj.authorAvatar}" />
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <c:set var="authorAvatarUrl"
                                                                                    value="${pageContext.request.contextPath}/${modObj.authorAvatar}" />
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                        <img src="${authorAvatarUrl}"
                                                                            class="mini-av-sharp"
                                                                            onerror="this.src='${pageContext.request.contextPath}/assets/images/avatars/default-avatar.png'">
                                                                        <div class="archive-info">
                                                                            <span
                                                                                class="archive-label">Contributor</span>
                                                                            <span class="archive-value">@<%=
                                                                                    HtmlEncoder.encode(mod.getAuthorName()) %></span>
                                                                        </div>
                                                                        <div class="archive-info"
                                                                            style="margin-left: 20px;">
                                                                            <span
                                                                                class="archive-label">Catalogued</span>
                                                                            <span class="archive-value">
                                                                                <%= mod.getCreatedAt() !=null ? new
                                                                                    java.text.SimpleDateFormat("dd.MM.yyyy").format(mod.getCreatedAt())
                                                                                    : "Unknown" %>
                                                                            </span>
                                                                        </div>
                                                                </div>
                                                            </header>

                                                            <div class="mod-description-text">
                                                                <%= mod.getDescription() !=null ? HtmlEncoder.encode(mod.getDescription())
                                                                    : "Data missing." %>
                                                            </div>

                                                            <div class="action-meta-grid">
                                                                <div class="meta-archive-item">
                                                                    <span class="archive-label">Base Game</span>
                                                                    <div class="archive-value">
                                                                        <%= game !=null ? HtmlEncoder.encode(game.getTitle()) : "N/A" %>
                                                                    </div>
                                                                </div>
                                                                <div class="meta-archive-item">
                                                                    <span class="archive-label">Platform</span>
                                                                    <div class="archive-value">
                                                                        <%= game !=null ? game.getPlatform() : "PC" %>
                                                                    </div>
                                                                </div>
                                                                <div class="meta-archive-item">
                                                                    <span class="archive-label">Users</span>
                                                                    <div class="archive-value">
                                                                        <%= String.format("%,d", mod.getDownloads()) %>
                                                                            Accesses
                                                                    </div>
                                                                </div>
                                                            </div>

                                                            <a href="<%=ctx%>/DownloadModController?id=<%= mod.getId() %>"
                                                                class="btn-download-mega">
                                                                <svg width="24" height="24" viewBox="0 0 24 24"
                                                                    fill="none" stroke="currentColor" stroke-width="2">
                                                                    <path
                                                                        d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M7 10l5 5 5-5M12 15V3" />
                                                                </svg>
                                                                INITIATE DOWNLOAD
                                                            </a>
                                                        </div>
                                                    </section>

                                                    <!-- VIDEO SECTION -->
                                                    <% if(mod.getYoutubeVideoId() !=null &&
                                                        !mod.getYoutubeVideoId().isEmpty()){ %>
                                                        <section class="content-section">
                                                            <h2 class="section-heading-futuristic">Visual Protocol</h2>
                                                            <div class="yt-frame-container">
                                                                <iframe
                                                                    src="https://www.youtube.com/embed/<%= mod.getYoutubeVideoId() %>"
                                                                    allowfullscreen></iframe>
                                                            </div>
                                                        </section>
                                                        <% } %>

                                                            <!-- COMMUNITY RATINGS -->
                                                            <section class="content-section">
                                                                <h2 class="section-heading-futuristic">User Evaluation
                                                                </h2>
                                                                <div class="ratings-futuristic-panel">

                                                                    <div class="bars-container">
                                                                        <% int[] starVals={5,4,3,2,1}; for(int s :
                                                                            starVals){ int
                                                                            cnt=(ratingDist!=null&&ratingDist.containsKey(s))?ratingDist.get(s):0;
                                                                            int pct=totalReviews>
                                                                            0?(int)Math.round((cnt*100.0)/totalReviews):0;
                                                                            %>
                                                                            <div class="bar-archive-row">
                                                                                <span class="bar-label-sharp">
                                                                                    <%= s %> ★
                                                                                </span>
                                                                                <div class="bar-track-sharp">
                                                                                    <div class="bar-fill-neon"
                                                                                        style="width: <%=pct%>%;"></div>
                                                                                </div>
                                                                                <span class="archive-label"
                                                                                    style="width: 30px;">
                                                                                    <%= cnt %>
                                                                                </span>
                                                                            </div>
                                                                            <% } %>
                                                                    </div>

                                                                    <div class="star-archive-focus">
                                                                        <div class="avg-star-container large"
                                                                            style="--star-fill-pct: <%=filledPct%>%;">
                                                                            <div class="star-layer empty"></div>
                                                                            <div class="star-layer filled"></div>
                                                                        </div>
                                                                        <div class="avg-rating-value-large">
                                                                            <%= String.format("%.1f", averageRating) %>
                                                                        </div>
                                                                        <div class="avg-rating-label-large">Consensus
                                                                            Score</div>
                                                                    </div>

                                                                </div>
                                                            </section>

                                                            <!-- USER REVIEW INTERACTION -->
                                                            <section class="content-section">
                                                                <h2 class="section-heading-futuristic">Review Submission
                                                                </h2>

                                                                <% if(currentUser !=null) { %>
                                                                    <div class="review-write-panel">
                                                                        <form action="<%= (userReview != null) ? ctx + "/UpdateReviewController" : ctx + "/ReviewController" %>" method="post">
                                                                            <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>">
                                                                            <% if(userReview !=null){ %><input
                                                                                    type="hidden" name="reviewId"
                                                                                    value="<%= userReview.getId() %>">
                                                                                <% } %>
                                                                                    <input type="hidden" name="modId"
                                                                                        value="<%= mod.getId() %>">
                                                                                    <input type="hidden" name="rating"
                                                                                        id="ratingInput"
                                                                                        value="<%= (userReview!=null?userReview.getRating():0) %>">

                                                                                    <div style="display:flex; justify-content:center; gap: 15px; margin-bottom: 30px;"
                                                                                        id="starsContainer">
                                                                                        <% for(int i=1; i<=5; i++){ %>
                                                                                            <button type="button"
                                                                                                class="star-trigger"
                                                                                                data-value="<%=i%>">
                                                                                                <div class="selection-star <%= (userReview!=null && i<=userReview.getRating()) ? "active" : "inactive" %>"
                                                                                                    id="selStar-<%=i%>">
                                                                                                </div>
                                                                                            </button>
                                                                                            <% } %>
                                                                                    </div>

                                                                                    <textarea name="comment"
                                                                                        class="futuristic-textarea"
                                                                                        placeholder="Input evaluation notes..."><%= (userReview!=null?userReview.getComment():"") %></textarea>

                                                                                    <div
                                                                                        style="margin-top: 20px; text-align:center;">
                                                                                        <button type="submit"
                                                                                            class="btn-download-mega"
                                                                                            style="height: 50px; width: auto; padding: 0 40px;">
                                                                                            <%= (userReview !=null)
                                                                                                ? "UPDATE LOG"
                                                                                                : "SUBMIT LOG" %>
                                                                                        </button>
                                                                                    </div>
                                                                        </form>
                                                                    </div>
                                                                    <% } else { %>
                                                                        <div
                                                                            style="text-align:center; color: var(--text-muted);">
                                                                            Authentication required to submit
                                                                            evaluation. <a href="<%=ctx%>/index.jsp"
                                                                                style="color:var(--accent-primary);">Sign
                                                                                In</a>
                                                                        </div>
                                                                        <% } %>

                                                                            <!-- REVIEWS LIST -->
                                                                            <div style="margin-top: 60px;">
                                                                                <% if(reviews !=null &&
                                                                                    !reviews.isEmpty()){ for(Review r :
                                                                                    reviews){ String
                                                                                    reviewAvatar=(r.getUserAvatar()
                                                                                    !=null &&
                                                                                    !r.getUserAvatar().isEmpty()) ?
                                                                                    r.getUserAvatar()
                                                                                    : "assets/images/avatars/default-avatar.png"
                                                                                    ;
                                                                                    if(!reviewAvatar.startsWith("http")
                                                                                    && !reviewAvatar.startsWith("/")) {
                                                                                    reviewAvatar=ctx + "/" +
                                                                                    reviewAvatar; } %>
                                                                                    <div class="review-archive-item">
                                                                                        <div
                                                                                            style="display:flex; align-items:flex-start; gap: 15px;">
                                                                                            <img src="<%= reviewAvatar %>"
                                                                                                class="mini-av-sharp"
                                                                                                style="width:45px; height:45px;"
                                                                                                onerror="this.src='<%=ctx%>/assets/images/avatars/default-avatar.png'">

                                                                                            <div class="archive-info"
                                                                                                style="flex:1;">
                                                                                                <span
                                                                                                    class="archive-value">@
                                                                                                    <%= HtmlEncoder.encode(r.getUsername()) %></span>
                                                                                                <div class="review-meta"
                                                                                                    style="display:flex; align-items:center; gap:10px; margin-top:5px;">
                                                                                                    <div class="avg-star-container"
                                                                                                        style="--star-fill-pct: <%= r.getRating() * 20 %>%; width: 80px; height: 16px;">
                                                                                                        <div
                                                                                                            class="star-layer empty">
                                                                                                        </div>
                                                                                                        <div
                                                                                                            class="star-layer filled">
                                                                                                        </div>
                                                                                                    </div>
                                                                                                    <span
                                                                                                        class="review-date"
                                                                                                        style="font-size:0.75rem; color:var(--text-muted);">
                                                                                                        <%= r.getCreatedAt()
                                                                                                            !=null ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(r.getCreatedAt())
                                                                                                            : "" %>
                                                                                                    </span>
                                                                                                </div>

                                                                                                <p
                                                                                                    style="color: var(--text-secondary); font-size: 0.95rem; margin-top: 15px; line-height: 1.6;">
                                                                                                    <%= HtmlEncoder.encode(r.getComment()) %>
                                                                                                </p>
                                                                                            </div>

                                                                                            <% if(currentUser !=null &&
                                                                                                r.getUserId()==currentUser.getId()){
                                                                                                %>
                                                                                                <form
                                                                                                    action="<%=ctx%>/DeleteReviewController"
                                                                                                    method="post"
                                                                                                    style="margin-left:auto;">
                                                                                                    <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>">
                                                                                                    <input type="hidden"
                                                                                                        name="reviewId"
                                                                                                        value="<%= r.getId() %>">
                                                                                                    <input type="hidden"
                                                                                                        name="modId"
                                                                                                        value="<%= mod.getId() %>">
                                                                                                    <button
                                                                                                        type="submit"
                                                                                                        class="archive-label"
                                                                                                        style="background:none; border:none; cursor:pointer; color:#ff6b6b; font-weight:700; font-size:0.6rem; letter-spacing:1px;">PURGE</button>
                                                                                                </form>
                                                                                                <% } %>
                                                                                        </div>
                                                                                    </div>
                                                                                    <% } } else { %>
                                                                                        <div
                                                                                            style="text-align:center; padding: 60px; border: 1px dashed var(--border-subtle); border-radius: 4px; color: var(--text-muted);">
                                                                                            <p>No evaluations archived
                                                                                                for this entry yet.</p>
                                                                                        </div>
                                                                                        <% } %>
                                                                            </div>
                                                            </section>

                                                </main>

                                                <footer class="footer-futuristic">
                                                    <div class="details-container"
                                                        style="display:flex; justify-content: space-between; align-items:center;">
                                                        <span
                                                            style="font-family: var(--font-heading); color: var(--accent-primary);">GameVerse
                                                            Academy Vault</span>
                                                        <div style="display:flex; gap: 40px;">
                                                            <a href="#" class="archive-label">Archive Protocol</a>
                                                            <a href="#" class="archive-label">Security</a>
                                                        </div>
                                                    </div>
                                                </footer>

                                                <% } %>

                                                    <script src="<%=ctx%>/assets/js/modDetails.js"></script>

                                    </html>