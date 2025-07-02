<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>회원가입</title>
  <link rel="stylesheet" href="<c:url value='/css/signup.css' />" />
</head>
<body>
<div class="page-container">
  <button class="back-button" onclick="goBack()">
    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
    </svg>
  </button>

  <div class="signup-container">
    <div class="signup-header">
      <h1 class="signup-title">회원가입</h1>
      <p class="signup-subtitle">AirBnG에 오신 것을 환영합니다</p>
    </div>

    <div class="profile-section">
      <div class="profile-image-container">
        <img id="profile-preview"
             src="https://airbngbucket.s3.ap-northeast-2.amazonaws.com/profiles/8e99db50-0a6c-413e-a42c-c5213dc9d64a_default.jpg"
             alt="기본 프로필 이미지"
             class="profile-image"
             onclick="document.getElementById('profile-input').click()">
        <div class="profile-upload-btn" onclick="document.getElementById('profile-input').click()">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"></path>
          </svg>
        </div>
      </div>
      <input type="file" id="profile-input" accept="image/*" onchange="handleProfileImageChange(event)">
      <p class="profile-text">프로필 사진을 설정해주세요 (선택사항)</p>
    </div>

    <div id="error-container"></div>

    <form class="signup-form" onsubmit="handleSignup(event)">
      <div class="input-group">
        <label class="input-label">이메일</label>
        <div class="input-row">
          <input type="email"
                 class="form-input"
                 id="email"
                 placeholder="이메일을 입력하세요"
                 required
                 oninput="resetValidation('email')">
          <button type="button"
                  class="check-button"
                  id="email-check-btn"
                  onclick="checkEmailDuplicate()">중복확인</button>
        </div>
        <div id="email-message" class="validation-message"></div>
      </div>

      <div class="input-group">
        <label class="input-label">이름</label>
        <div class="input-row">
        <input type="text"
               class="form-input"
               id="name"
               placeholder="이름을 입력하세요"
               required>
        </div>
      </div>

      <div class="input-group">
        <label class="input-label">전화번호</label>
        <div class="input-row">
        <input type="tel"
               class="form-input"
               id="phone"
               placeholder="휴대폰 번호 입력('-'제외 11자리 입력)"
               required>
        </div>
      </div>

      <div class="input-group">
        <label class="input-label">닉네임</label>
        <div class="input-row">
          <input type="text"
                 class="form-input"
                 id="nickname"
                 placeholder="닉네임을 입력하세요"
                 required
                 oninput="resetValidation('nickname')">
          <button type="button"
                  class="check-button"
                  id="nickname-check-btn"
                  onclick="checkNicknameDuplicate()">중복확인</button>
        </div>
        <div id="nickname-message" class="validation-message"></div>
      </div>

      <div class="input-group">
        <label class="input-label">비밀번호</label>
        <div class="input-row">
        <input type="password"
               class="form-input"
               id="password"
               placeholder="대/소문자/숫자 하나 이상을 포함하여 8자 이상"
               required>
        </div>
        <div id="password-message" class="validation-message"></div>
      </div>

      <div class="input-group">
        <label class="input-label">비밀번호 확인</label>
        <div class="input-row">
        <input type="password"
               class="form-input"
               id="password-confirm"
               placeholder="비밀번호를 다시 입력하세요"
               required>
        </div>
        <div id="password-confirm-message" class="validation-message"></div>
      </div>

      <button type="submit" class="signup-button" id="signup-btn">회원가입</button>
    </form>

    <div class="login-section">
      <span class="login-text">이미 계정이 있으신가요? </span>
      <a href="#" class="login-link" onclick="goToLogin()">로그인</a>
    </div>
  </div>
</div>

<!-- 성공 모달 -->
<div id="success-modal" class="modal-overlay hidden">
  <div class="modal">
    <div class="modal-content">
      <div class="modal-icon">✓</div>
      <h3 class="modal-title">회원가입 완료!</h3>
      <p>환영합니다! 로그인 페이지로 이동합니다.</p>
    </div>
    <div class="modal-buttons">
      <button class="modal-btn" onclick="goToLogin()">확인</button>
    </div>
  </div>
</div>
<script>
  const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="<c:url value='/js/signup.js' />"></script>
</body>
</html>