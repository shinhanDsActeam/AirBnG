<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>로그인 - AirBnG</title>
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
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    }

    .page-container {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
      max-width: 412px;
      width: 100%;
      background-color: #ffffff;
    }

    .login-container {
      flex: 1;
      padding: 40px 20px 20px;
      display: flex;
      flex-direction: column;
      justify-content: flex-start;
      margin-top: 30px;
    }

    .login-header {
      text-align: center;
      margin-bottom: 50px;
    }

    .login-title {
      font-size: 24px;
      font-weight: 700;
      color: #333;
      margin-bottom: 8px;
    }

    .login-subtitle {
      font-size: 14px;
      color: #666;
    }

    .login-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .input-group {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .input-label {
      font-size: 14px;
      color: #333;
      font-weight: 500;
    }

    .form-input {
      width: 100%;
      height: 50px;
      padding: 0 16px;
      border: 1px solid #e0e0e0;
      border-radius: 4px;
      font-size: 16px;
      background-color: #ffffff;
      transition: border-color 0.2s ease;
    }

    .form-input:focus {
      outline: none;
      border-color: #3181E2;
    }

    .form-input::placeholder {
      color: #999;
    }

    .login-options {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 10px 0;
    }

    .checkbox-wrapper {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .checkbox-wrapper input[type="checkbox"] {
      width: 16px;
      height: 16px;
      accent-color: #3181E2;
    }

    .checkbox-label {
      font-size: 14px;
      color: #666;
    }

    .forgot-password {
      font-size: 14px;
      color: #666;
      text-decoration: none;
    }

    .forgot-password:hover {
      color: #3181E2;
      text-decoration: underline;
    }

    .login-button {
      width: 100%;
      height: 50px;
      background-color: #3181E2;
      color: #ffffff;
      border: none;
      border-radius: 4px;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;
      transition: background-color 0.2s ease;
      margin-top: 10px;
    }

    .login-button:disabled {
      background-color: #cccccc;
      cursor: not-allowed;
    }

    .divider {
      display: flex;
      align-items: center;
      margin: 30px 0;
      gap: 16px;
    }

    .divider-line {
      flex: 1;
      height: 1px;
      background-color: #e0e0e0;
    }

    .divider-text {
      font-size: 14px;
      color: #999;
    }

    .signup-section {
      text-align: center;
      margin-top: 20px;
    }

    .signup-text {
      font-size: 14px;
      color: #666;
    }

    .signup-link {
      color: #3181E2;
      text-decoration: none;
      font-weight: 600;
    }

    .signup-link:hover {
      text-decoration: underline;
    }

    .error-message {
      background-color: #fee;
      color: #c33;
      padding: 12px 16px;
      border-radius: 4px;
      font-size: 14px;
      margin-bottom: 16px;
      border: 1px solid #fcc;
    }

    .back-button {
      position: absolute;
      top: 20px;
      left: 20px;
      width: 40px;
      height: 40px;
      background: none;
      border: none;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 8px;
      transition: background-color 0.2s ease;
    }

    .back-button:hover {
      background-color: #f5f5f5;
    }

    .back-button svg {
      width: 24px;
      height: 24px;
      stroke: #333;
    }

    /* 반응형 디자인 */
    @media (max-width: 412px) {
      .login-container {
        padding: 20px 16px;
      }
    }
  </style>
</head>
<body>
<div class="page-container">
  <div class="login-container">
    <div class="login-header">
      <h1 class="login-title">로그인/회원가입</h1>
      <p class="login-subtitle">AirBnG에 오신 것을 환영합니다</p>
    </div>

    <form class="login-form" action="${pageContext.request.contextPath}/auth/login" method="post">
      <c:if test="${not empty errorMessage}">
        <div class="error-message">
            ${errorMessage}
        </div>
      </c:if>

      <div class="input-group">
        <label class="input-label" for="username">아이디</label>
        <input
                type="text"
                id="username"
                name="username"
                class="form-input"
                placeholder="아이디를 입력하세요"
                value="${param.username}"
                required
        >
      </div>

      <div class="input-group">
        <label class="input-label" for="password">비밀번호</label>
        <input
                type="password"
                id="password"
                name="password"
                class="form-input"
                placeholder="비밀번호를 입력하세요"
                required
        >
      </div>

      <div class="login-options">
        <div class="checkbox-wrapper">
          <input type="checkbox" id="autoLogin" name="autoLogin">
          <label class="checkbox-label" for="autoLogin">자동 로그인</label>
        </div>
        <a href="${pageContext.request.contextPath}/auth/forgot-password" class="forgot-password">
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
        <a href="${pageContext.request.contextPath}/auth/signup" class="signup-link">회원가입</a>
      </p>
    </div>
  </div>
</div>

<script>
  document.querySelector('.login-form').addEventListener('submit', function(e) {
    e.preventDefault();

    const email = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();

    if (!email || !password) {
      alert('아이디와 비밀번호를 모두 입력해주세요.');
      return;
    }

    const requestData = {
      email: email,
      password: password
    };

    fetch('${pageContext.request.contextPath}/members/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(requestData)
    })
            .then(response => response.json())
            .then(data => {
              if (data.code === 2000) {
                // alert('로그인 성공!');
                location.href = '/AirBnG/page/home'; // 로그인 후 이동
              } else {
                alert(data.message || '로그인 실패');
              }
            })
            .catch(err => {
              console.error(err);
              alert('서버 오류');
            });
  });
</script>
</body>
</html>