<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="loginMemberId" value="${sessionScope.memberId}" />
<c:set var="loginNickname" value="${sessionScope.nickname}" />
<c:set var="loginEmail" value="${sessionScope.email}" />
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>마이페이지 - 에어비앤짐</title>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg" />
    <link rel="stylesheet" href="<c:url value='/css/mypage.css'/>" />
</head>
<body data-logged-in="${not empty sessionScope.memberId}"
      data-user-name="${sessionScope.nickname}"
      data-user-email="${sessionScope.email}"
      data-member-id="${sessionScope.memberId}">
    <div class="container">
        <!-- 헤더 -->
        <c:set var="headerTitle" value="마이페이지"/>
        <c:set var="showBackButton" value="false"/>
        <%@ include file="common/header.jsp" %>


        <!-- 메인 컨텐츠 -->
        <main class="main-content">
            <!-- 로그인 상태에 따른 조건부 렌더링 -->
            <div id="loggedOutSection">
                <!-- 로그인 안된 상태 -->
                <c:set var="welcomeTitle" value="환영합니다!"/>
                <c:set var="welcomeSubtitle" value="로그인하여 더 많은 서비스를 이용해보세요."/>
                <%@ include file="/WEB-INF/views/common/welcom.jsp" %>

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
                            <h2 class="username">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.nickname}">
                                        ${sessionScope.nickname}님
                                    </c:when>
                                    <c:otherwise>
                                        사용자님
                                    </c:otherwise>
                                </c:choose>
                            </h2>
                        </div>
                    </div>
                </div>

                <!-- 로그인 후 전체 메뉴 (후기 메뉴 제거) -->
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

    <!-- 모달 영역 -->
    <div id="success-modal" class="modal-overlay hidden">
        <div class="modal">
            <div class="modal-content">
                <div class="modal-title">로그인 성공</div>
            </div>
            <div class="modal-buttons">
                <button class="modal-btn" onclick="confirmLoginSuccess()" style="width: 100%; border-right: none;">확인</button>
            </div>
        </div>
    </div>

    <%@ include file="navbar.jsp" %>

    <script>
        // 서버에서 렌더링된 세션 데이터를 JavaScript 전역 변수로 설정
        const sessionData = {
            memberId: '${loginMemberId}',
            nickname: '${loginNickname}',
            email: '${loginEmail}',
            isLoggedIn: ${not empty sessionScope.memberId}
        };

        // 컨텍스트 패스 설정
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
    <script src="<c:url value='/js/mypage.js'/>"></script>

</body>
</html>