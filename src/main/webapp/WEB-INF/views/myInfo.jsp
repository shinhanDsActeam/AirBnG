<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="loginMemberId" value="${sessionScope.memberId}" />
<c:set var="loginNickname" value="${sessionScope.nickname}" />
<c:set var="loginEmail" value="${sessionScope.email}" />

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>내 정보 수정 - AirBnG</title>
  <link rel="stylesheet" href="<c:url value='/css/myInfo.css' />" />
</head>
<body>
<div class="page-container">
  <button class="back-button" onclick="goBack()">
    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
    </svg>
  </button>

  <div class="edit-profile-container">
    <div class="edit-profile-header">
      <h1 class="edit-profile-title">내 정보 수정</h1>
      <p class="edit-profile-subtitle">개인정보를 안전하게 수정하세요</p>
    </div>

    <div class="profile-section">
      <div class="profile-image-container">
        <img id="profile-preview"
             src=""
             alt="프로필 이미지"
             class="profile-image"
             onclick="document.getElementById('profile-input').click()">
        <div class="profile-upload-btn" onclick="document.getElementById('profile-input').click()">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"></path>
          </svg>
        </div>
      </div>
      <input type="file" id="profile-input" accept="image/*" onchange="handleProfileImageChange(event)">
      <p class="profile-text">프로필 사진을 변경하려면 클릭하세요</p>
    </div>

    <div id="error-container"></div>
    <div id="loading-container" class="loading-container hidden">
      <div class="loading-spinner"></div>
      <p>정보를 불러오는 중...</p>
    </div>

    <form class="edit-profile-form" onsubmit="handleProfileUpdate(event)">
      <div class="input-group">
        <label class="input-label">이메일</label>
        <div class="input-row">
          <input type="email"
                 class="form-input"
                 id="email"
                 readonly
                 disabled>
        </div>
        <div class="input-help">이메일은 변경할 수 없습니다</div>
      </div>

      <div class="input-group">
        <label class="input-label">이름</label>
        <div class="input-row">
          <input type="text"
                 class="form-input"
                 id="name"
                 readonly
                 disabled>
        </div>
        <div class="input-help">이름은 변경할 수 없습니다</div>
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
                 oninput="resetNicknameValidation()">
          <button type="button"
                  class="check-button"
                  id="nickname-check-btn"
                  onclick="checkNicknameDuplicate()">중복확인</button>
        </div>
        <div id="nickname-message" class="validation-message"></div>
      </div>

      <button type="submit" class="save-button" id="save-btn">수정 완료</button>
    </form>
  </div>
</div>

<!-- 성공 모달 -->
<div id="success-modal" class="modal-overlay hidden">
  <div class="modal">
    <div class="modal-content">
      <div class="modal-icon success-rotate">✓</div>
      <h3 class="modal-title">수정 완료!</h3>
      <p class="modal-message">정보가 성공적으로 수정되었습니다.</p>
    </div>
    <div class="modal-buttons">
      <button class="modal-btn" onclick="goToMyPage()">확인</button>
    </div>
  </div>
</div>

<script>
  // 서버에서 렌더링된 세션 데이터를 JavaScript 전역 변수로 설정
  const sessionData = {
    memberId: '${loginMemberId}',
    nickname: '${loginNickname}',
    email: '${loginEmail}',
    isLoggedIn: ${not empty sessionScope.memberId}
  };

  const contextPath = '${pageContext.request.contextPath}';

  // 디버깅용 로그
  console.log('=== 세션 디버깅 ===');
  console.log('memberId:', sessionData.memberId);
  console.log('nickname:', sessionData.nickname);
  console.log('email:', sessionData.email);
  console.log('isLoggedIn:', sessionData.isLoggedIn);
  console.log('contextPath:', contextPath);
  console.log('==================');
</script>
<script src="<c:url value='/js/myInfo.js' />"></script>
</body>
</html>