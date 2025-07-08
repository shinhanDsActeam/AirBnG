<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="<c:url value='/css/common/welcome.css' />"/>
<script src="<c:url value='/js/common/welcome.js' />"></script>

<div class="welcome-section">
    <h2 class="welcome-title">
        ${empty welcomeTitle ? '환영합니다!' : welcomeTitle}
    </h2>
    <p class="welcome-subtitle">
        ${empty welcomeSubtitle ? '로그인하여 더 많은 서비스를 이용해보세요.' : welcomeSubtitle}
    </p>

    <div class="auth-buttons">
        <button class="login-btn" onclick="goToLogin()">로그인</button>
        <button class="signup-btn" onclick="goToSignup()">회원가입</button>
    </div>
</div>
