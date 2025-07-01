<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>마이페이지 - 에어비앤짐</title>
    <link rel="stylesheet" href="<c:url value='/css/mypage.css'/>" />
</head>
<body data-logged-in="${not empty sessionScope.user}"
      data-user-name="${sessionScope.user.name}"
      data-user-email="${sessionScope.user.email}">
    <div class="container">
        <!-- 헤더 -->
        <header class="header">
            <div class="header-content">
                <div class="logo">
                    <span class="logo-text">마이페이지</span>
                </div>
            </div>
        </header>

        <!-- 메인 컨텐츠 -->
        <main class="main-content">
            <!-- 로그인 상태에 따른 조건부 렌더링 -->
            <div id="loggedOutSection">
                <!-- 로그인 안된 상태 -->
                <div class="welcome-section">
                    <h2 class="welcome-title">환영합니다!</h2>
                    <p class="welcome-subtitle">로그인하여 더 많은 서비스를 이용해보세요.</p>

                    <div class="auth-buttons">
                        <button class="login-btn" onclick="goToLogin()">로그인</button>
                        <button class="signup-btn" onclick="goToSignup()">회원가입</button>
                    </div>
                </div>

                <!-- 로그인 전 제한된 메뉴 -->
                <div class="limited-menu">
                    <div class="menu-item disabled">
                        <div class="menu-icon user-icon"></div>
                        <div class="menu-content">
                            <h3>내 정보</h3>
                            <p>로그인이 필요한 서비스입니다</p>
                        </div>
                        <div class="menu-arrow lock-icon"></div>
                    </div>

                    <div class="menu-item disabled">
                        <div class="menu-icon calendar-icon"></div>
                        <div class="menu-content">
                            <h3>예약 내역</h3>
                            <p>로그인이 필요한 서비스입니다</p>
                        </div>
                        <div class="menu-arrow lock-icon"></div>
                    </div>

                    <div class="menu-item disabled">
                        <div class="menu-icon review-icon"></div>
                        <div class="menu-content">
                            <h3>작성한 후기</h3>
                            <p>로그인이 필요한 서비스입니다</p>
                        </div>
                        <div class="menu-arrow lock-icon"></div>
                    </div>
                </div>
            </div>

            <div id="loggedInSection" style="display: none;">
                <!-- 로그인된 상태 -->
                <div class="user-info-section">
                    <div class="user-profile">
                        <div class="profile-image">
                            <div class="profile-avatar"></div>
                        </div>
                        <div class="user-details">
                            <h2 class="username">${sessionScope.user.name}님</h2>
                            <p class="user-email">${sessionScope.user.email}</p>
                        </div>
                    </div>
                </div>

                <!-- 로그인 후 전체 메뉴 -->
                <div class="menu-section">
                    <div class="menu-item active" onclick="goToMyInfo()">
                        <div class="menu-icon user-icon"></div>
                        <div class="menu-content">
                            <h3>내 정보 보기/수정</h3>
                            <p>개인정보를 확인하고 수정하세요</p>
                        </div>
                        <div class="menu-arrow right-arrow"></div>
                    </div>

                    <div class="menu-item active" onclick="goToReservations()">
                        <div class="menu-icon calendar-icon"></div>
                        <div class="menu-content">
                            <h3>예약 내역 보기</h3>
                            <p>나의 예약 현황을 확인하세요</p>
                        </div>
                        <div class="menu-arrow right-arrow"></div>
                    </div>

                    <div class="menu-item active" onclick="goToReviews()">
                        <div class="menu-icon review-icon"></div>
                        <div class="menu-content">
                            <h3>작성한 후기 보기</h3>
                            <p>내가 작성한 후기를 관리하세요</p>
                        </div>
                        <div class="menu-arrow right-arrow"></div>
                    </div>

                    <div class="menu-item logout" onclick="logout()">
                        <div class="menu-icon logout-icon"></div>
                        <div class="menu-content">
                            <h3>로그아웃</h3>
                            <p>안전하게 로그아웃하세요</p>
                        </div>
                        <div class="menu-arrow right-arrow"></div>
                    </div>
                </div>
            </div>
        </main>
    </div>
    <%@ include file="navbar.jsp" %>

    <script src="<c:url value='/js/mypage.js'/>"></script>
</body>
</html>