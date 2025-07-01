<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>홈 - AirBnG</title>
    <!-- navigation.css를 여기서 한번만 로드 -->
    <link rel="stylesheet" href="<c:url value='/css/navigation.css'/>" />
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            width: 100%;
            height: 100vh;
            margin: 0;
            background-color: #f8f9fa;
            overflow: hidden;
            display: flex;
            justify-content: center;
        }

        .page-container {
            display: flex;
            flex-direction: column;
            min-height: 100vh; /* 브라우저 높이에 맞게 유동 */
            max-width: 412px;
            width: 100%;
            background-color: #ffffff;
        }


        .page-content {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
            padding-bottom: 100px; /* 네비바 높이만큼 여유 공간 */
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
            border: 1px solid #e9ecef;
        }

        .feature-card h3 {
            font-size: 16px;
            color: #333;
            margin-bottom: 8px;
        }

        .feature-card p {
            font-size: 14px;
            color: #666;
            line-height: 1.4;
        }

        .content-section {
            margin: 30px 0;
        }

        .section-title {
            font-size: 20px;
            font-weight: 600;
            color: #333;
            margin-bottom: 15px;
        }

        .info-card {
            background: white;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 10px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        }

        .info-card h4 {
            font-size: 16px;
            color: #333;
            margin: 0 0 8px 0;
        }

        .info-card p {
            font-size: 14px;
            color: #666;
            margin: 0;
            line-height: 1.4;
        }

        /* 네비게이션 관련 스타일 제거 - navigation.css에서 처리 */

        /* 스크롤바 스타일링 */
        .page-content::-webkit-scrollbar {
            width: 4px;
        }

        .page-content::-webkit-scrollbar-track {
            background: #f1f1f1;
        }

        .page-content::-webkit-scrollbar-thumb {
            background: #c1c1c1;
            border-radius: 2px;
        }

        .page-content::-webkit-scrollbar-thumb:hover {
            background: #a8a8a8;
        }
    </style>
</head>
<body>
    <div class="page-container">
        <div class="page-content">
            <div class="welcome-section">
                <h1 class="welcome-title">환영합니다!</h1>
                <p class="welcome-subtitle">AirBnG에서 특별한 여행을 시작하세요</p>
            </div>
        </div>
    </div>

    <!-- 네비게이션 바 include - page-container 밖에 위치 -->
    <%@ include file="navbar.jsp" %>
</body>
</html>