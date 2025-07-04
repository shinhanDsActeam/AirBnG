<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="isLoggedIn" value="${not empty sessionScope.memberId}" />
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>보관소</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/gh/webfontworld/bmjua/BMJUA.css" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/locker.css'/>" />
</head>

<body class="airbng-locker">
<div class="container">
    <header class="header">
        <div class="header-content">
            <span class="logo-text">보관소</span>
        </div>
    </header>

    <main class="main-content">
        <c:choose>
            <c:when test="${isLoggedIn}">
                <div class="menu-section">
                    <div class="menu-item active" onclick="goToRegisterLocker()">
                        <div class="menu-content">
                            <h3>보관소 등록하기</h3>
                            <p>새로운 보관소를 등록해보세요</p>
                        </div>
                        <div class="menu-arrow right-arrow"></div>
                    </div>

                    <div class="menu-item active" onclick="goToManageLocker()">
                        <div class="menu-content">
                            <h3>보관소 관리하기</h3>
                            <p>등록한 보관소를 확인하고 수정하세요</p>
                        </div>
                        <div class="menu-arrow right-arrow"></div>
                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <div class="welcome-section">
                    <h2 class="welcome-title">보관소 서비스 이용 안내</h2>
                    <p class="welcome-subtitle">로그인 후 보관소 등록/관리 기능을 사용할 수 있습니다.</p>
                    <div class="auth-buttons">
                        <button class="login-btn" onclick="goToLogin()">로그인</button>
                        <button class="signup-btn" onclick="goToSignup()">회원가입</button>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</div>

<%@ include file="navbar.jsp" %>

<script>
    const contextPath = '${pageContext.request.contextPath}';
</script>

<script src="<c:url value='/js/locker.js'/>"></script>
</body>
</html>
