<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>403 Forbidden — GameVerse Academy</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/generic.css">
    <style>
        body {
            background: #0f0609;
            color: #edf2c3;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
            margin: 0;
            font-family: 'Inter', sans-serif;
            text-align: center;
        }
        .error-card {
            background: rgba(14, 43, 35, 0.4);
            backdrop-filter: blur(20px);
            border: 1px solid rgba(92, 255, 176, 0.2);
            padding: 60px;
            border-radius: 4px;
            max-width: 500px;
        }
        h1 {
            font-size: 5rem;
            margin: 0;
            color: #5cffb0;
            letter-spacing: -2px;
        }
        p {
            color: rgba(237, 242, 195, 0.6);
            margin: 20px 0 40px;
        }
        .btn-back {
            display: inline-block;
            padding: 12px 30px;
            border: 1px solid #5cffb0;
            color: #5cffb0;
            text-decoration: none;
            font-size: 0.8rem;
            font-weight: 700;
            letter-spacing: 2px;
            transition: all 0.3s ease;
        }
        .btn-back:hover {
            background: #5cffb0;
            color: #0f0609;
        }
    </style>
</head>
<body>
    <div class="error-card">
        <h1>403</h1>
        <h3 style="letter-spacing: 4px;">ACCESS DENIED</h3>
        <p>Your clearance level is insufficient to access this sector of the vault.</p>
        <a href="${pageContext.request.contextPath}/ModController" class="btn-back">RETURN TO ARCHIVE</a>
    </div>
</body>
</html>
