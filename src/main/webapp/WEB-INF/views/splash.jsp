<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>에어비앤집</title>
    <link rel="stylesheet" href="<c:url value='/css/splash.css' />" />
</head>
<body>
<div class="container">
    <div class="main-content">
        <div class="splash-content">
            <div class="logo-section">
                <div class="logo-container">
                    <img src="<c:url value='/images/logo_name_ic.svg' />" alt="에어비앤집 로고" />
                </div>
            </div>
        </div>
    </div>
</div>

<!-- context path 설정 + 외부 JS 로딩 -->
<script>
    const contextPath = '${pageContext.request.contextPath}';  // 결과: /AirBnG
</script>
<script src="<c:url value='/js/splash.js' />"></script>
</body>
</html>