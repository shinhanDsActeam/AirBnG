<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>로그인 - AirBnG</title>
  <link rel="stylesheet" href="<c:url value='/css/login.css' />" />
</head>
<body>
<!-- 모달 영역 -->
<div id="success-modal" class="modal-overlay hidden">
  <div class="modal">
    <div class="modal-content">
      <div class="modal-icon">✓</div>
      <div class="modal-title">로그인 성공</div>
    </div>
    <div class="modal-buttons">
      <button class="modal-btn" onclick="confirmLoginSuccess()" style="width: 100%; border-right: none;">확인</button>
    </div>
  </div>
</div>

<div id="error-modal" class="modal-overlay hidden">
  <div class="modal">
    <div class="modal-content">
      <div class="modal-icon" style="background: #dc2626;">✗</div>
      <div class="modal-title">로그인 실패</div>
    </div>
    <div class="modal-buttons">
      <button class="modal-btn" onclick="closeErrorModal()" style="width: 100%; border-right: none;">확인</button>
    </div>
  </div>
</div>

<div class="page-container">
  <div class="login-container">
    <div class="login-header">
      <h1 class="login-title">로그인/회원가입</h1>
      <p class="login-subtitle">AirBnG에 오신 것을 환영합니다</p>
    </div>

    <form class="login-form">
      <div class="input-group">
        <label class="input-label" for="username">아이디</label>
        <input
                type="text"
                id="username"
                class="form-input"
                placeholder="아이디를 입력하세요"
                required
        >
      </div>

      <div class="input-group">
        <label class="input-label" for="password">비밀번호</label>
        <input
                type="password"
                id="password"
                class="form-input"
                placeholder="비밀번호를 입력하세요"
                required
        >
      </div>

      <div class="login-options">
        <div class="checkbox-wrapper">
          <input type="checkbox" id="autoLogin">
          <label class="checkbox-label" for="autoLogin">자동 로그인</label>
        </div>
        <a href="${pageContext.request.contextPath}/page/home" class="forgot-password">
          아이디/비밀번호 찾기
        </a>
      </div>

      <button type="submit" class="login-button">로그인</button>
    </form>

    <div class="divider">
      <div class="divider-line"></div>
      <span class="divider-text">또는</span>
      <div class="divider-line"></div>
    </div>
    <div class="signup-section">
      <p class="signup-text">
        아직 계정이 없으신가요?
        <a href="${pageContext.request.contextPath}/page/home" class="signup-link">회원가입</a>
      </p>
    </div>
  </div>
</div>
<script>
  const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="<c:url value='/js/login.js' />"></script>
</body>
</html>