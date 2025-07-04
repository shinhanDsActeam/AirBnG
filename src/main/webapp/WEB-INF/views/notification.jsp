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
    <%-- 로그인 안된 경우 --%>
    <c:when test="${empty loginMember}">
        <div class="notification-empty-message">
            로그인 후 사용해주세요.
        </div>
    </c:when>


    <%-- 로그인 된 경우 --%>
    <c:otherwise>
        <!-- 알림 연결 상태 -->
        <div class="connection-indicator" id="connectionIndicator" data-status="연결 대기 중..."></div>

        <!-- 알림 목록 -->
        <div class="notification-container">
            <button class="clear-all-btn" id="clearAllBtn" onclick="notificationSSE.clearAllNotifications()" disabled>
                모든 알림 지우기
            </button>
            <div id="notifications">
                <div class="loading-indicator">알림이 없습니다.</div>
            </div>
        </div>

        <!-- JS 알림 시스템 동작 -->
        <script>
            const memberId = "${loginMember}";
        </script>
        <script src="<c:url value='/js/notification.js'/>"></script>
    </c:otherwise>
</c:choose>

</body>
</html>
