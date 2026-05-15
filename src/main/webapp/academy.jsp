<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Academy Courses — GameVerse Academy</title>
    
    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Manrope:wght@400;500;600&display=swap" rel="stylesheet">
    
    <!-- Reusing submit.css for the hero design layout -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/submit.css">
    
    <style>
        .academy-hero-content {
            border-color: rgba(92, 255, 176, 0.4) !important;
            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.8), inset 0 0 20px rgba(92, 255, 176, 0.05);
        }
        
        .academy-hero-title {
            color: #5cffb0;
            text-shadow: 0 0 20px rgba(92, 255, 176, 0.5);
            margin-bottom: 5px;
        }

        .btn-waitlist {
            background: rgba(92, 255, 176, 0.05);
            backdrop-filter: blur(5px);
        }
        
        .btn-waitlist:hover {
            background: #5cffb0 !important;
            color: #0f0609 !important;
            transform: translateY(-5px);
            box-shadow: 0 0 30px rgba(92, 255, 176, 0.5) !important;
        }

        body.light-mode .academy-hero-content {
            background: rgba(255, 255, 255, 0.9);
            border-color: rgba(14, 43, 35, 0.3) !important;
            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.1);
        }
        body.light-mode .academy-hero-title {
            color: #0e2b23;
            text-shadow: none;
        }
        body.light-mode .hero-immersive-subtitle {
            color: #1a5646;
        }
        body.light-mode .hero-elegant-paragraph {
            color: #333;
        }
        body.light-mode .btn-waitlist {
            color: #0e2b23;
            border-color: #0e2b23;
            background: transparent;
        }
        body.light-mode .btn-waitlist:hover {
            background: #0e2b23 !important;
            color: #ffffff !important;
        }
    </style>
</head>

<body data-context-path="${pageContext.request.contextPath}">
    <!-- Premium Navbar -->
    <jsp:include page="/components/navbar.jsp" />

    <div class="submit-bg-mesh"></div>

    <div class="hero-unauth-container">
        <div class="hero-unauth-content academy-hero-content">
            <h1 class="hero-giant-title academy-hero-title">ACADEMY COURSES</h1>
            <h3 class="hero-immersive-subtitle">Master Mod Creation. Coming Soon.</h3>
            <p class="hero-elegant-paragraph">
                Ready to forge your own legendary artifacts? We are preparing exclusive masterclasses designed to teach you the intricate arts of modding. From advanced 3D asset integration to complex scripting mastery, your journey as a creator will begin shortly.
            </p>
            <a href="#" class="btn-join-community btn-waitlist" onclick="alert('You have been added to the waitlist! We will notify you when courses are live.'); return false;">
                JOIN THE WAITLIST
            </a>
        </div>
    </div>
</body>
</html>
