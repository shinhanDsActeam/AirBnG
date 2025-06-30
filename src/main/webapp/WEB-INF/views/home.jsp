<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>홈</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/navigation.css">    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: #f8f9fa;
            width: 412px;
            height: 892px;
            margin: 0 auto;
            overflow: hidden;
        }

        .page-container {
            width: 412px;
            height: 892px;
            background-color: #ffffff;
            position: relative;
        }

        .page-content {
            width: 100%;
            height: calc(892px - 80px);
            padding: 20px;
            overflow-y: auto;
        }

        .welcome-section {
            text-align: center;
            padding: 40px 0;
        }

        .welcome-title {
            font-size: 28px;
            font-weight: 700;
            color: #333;
            margin-bottom: 10px;
        }

        .welcome-subtitle {
            font-size: 16px;
            color: #666;
            margin-bottom: 30px;
        }

        .feature-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
            margin-top: 30px;
        }

        .feature-card {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 12px;
            text-align: center;
        }

        .feature-card h3 {
            font-size: 16px;
            color: #333;
            margin-bottom: 8px;
        }

        .feature-card p {
            font-size: 14px;
            color: #666;
        }
    </style>
</head>
<body>
    <div class="page-container">
        <!-- 네비게이션 바 include -->
        <%@ include file="navbar.jsp" %>
    </div>
</body>
</html>