<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="loginMember" value="${sessionScope.memberId}" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>실시간 알림</title>
    <link rel="stylesheet" href="<c:url value='/css/notification.css'/>">
</head>

<body data-member-id="${loginMember}">
<c:choose>
    <c:when test="${empty loginMember}">
        <div class="notification-empty-message">
            로그인 후 사용해주세요.
        </div>
    </c:when>

    <c:otherwise>
        <!-- 연결 상태 표시 -->
        <div class="connection-indicator" id="connectionIndicator" data-status="연결 대기 중..."></div>

        <!-- 전체 화면을 감싸는 wrapper -->
        <div class="notification-wrapper">

            <!-- 통합된 알림 컨테이너 -->
            <div class="notification-container">
                <c:set var="headerTitle" value="알림"/>
                <c:set var="showBackButton" value="true"/> <%-- 뒤로가기 있는 버전 --%>
                <%@ include file="common/header.jsp" %>

                <!-- 모든 알림 지우기 버튼 (별도 영역) -->
                <div class="clear-all-section">
                    <button class="clear-all-btn" id="clearAllBtn" onclick="notificationSSE.clearAllNotifications()" disabled>
                        모든 알림 지우기
                    </button>
                </div>

                <!-- 알림 목록 영역 -->
                <div id="notifications" class="notification-list">
                    <div class="loading-indicator">알림이 없습니다.</div>
                </div>
            </div>

        </div>

        <!-- JS -->
        <c:if test="${not empty sessionScope.memberId}">
            <script>
                window.memberId = "${sessionScope.memberId}";
            </script>
        </c:if>
        <script src="${pageContext.request.contextPath}/js/sse.js"></script>
        <script src="<c:url value='/js/notification.js'/>"></script>
    </c:otherwise>
</c:choose>
</body>
</html>