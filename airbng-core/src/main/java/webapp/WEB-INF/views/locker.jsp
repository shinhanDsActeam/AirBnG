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
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg" />
</head>

<body class="airbng-locker">
<div class="container">
    <!-- 헤더 -->
    <c:set var="headerTitle" value="보관소"/>
    <c:set var="showBackButton" value="false"/>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <main class="main-content">
        <c:choose>
            <c:when test="${isLoggedIn}">
                <c:choose>
                    <c:when test="${isExistLocker}">
                        <div class="menu-section">
                            <div class="menu-item-card">
                                <div class="menu-item-content">
                                    <!-- 썸네일 -->
                                    <div class="locker-thumbnail">
                                        <img src="${lockerDetail.images[0]}" alt="보관소 이미지" />
                                    </div>

                                    <!-- 텍스트 영역 -->
                                    <div class="locker-info">
                                        <div class="locker-title-row">
                                            <h3 class="locker-title">${lockerDetail.lockerName}</h3>
                                            <c:choose>
                                                <c:when test="${lockerDetail.isAvailable eq 'YES'}">
                                                    <span class="status-badge active">운영중</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge inactive">중지됨</span>
                                                </c:otherwise>
                                            </c:choose>

                                            <button class="edit-locker-btn"
                                                    data-locker-id="${lockerDetail.lockerId}"
                                                    onclick="goToLockerManage(this)">
                                                <img src="<c:url value='/images/settings.svg'/>" alt="편집" width="25" height="25">
                                            </button>

                                        </div>
                                        <div class="locker-jim-types">
                                            <c:forEach var="type" items="${lockerDetail.jimTypeResults}">
                                                <span class="jim-type-badge">
                                                    ${type.typeName} : ₩${type.pricePerHour}
                                                </span>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>

                                <!-- 버튼 영역 -->
                                <div class="locker-action-buttons">
                                    <button class="manage-locker-btn"
                                            data-locker-id="${lockerDetail.lockerId}"
                                            onclick="goToLockerDetails(this)">
                                        보관소 상세보기
                                    </button>
<%--                                    <button type="button"--%>
<%--                                            class="delete-locker-btn"--%>
<%--                                            data-locker-id="${lockerDetail.lockerId}"--%>
<%--                                            onclick="deleteLocker(this)">--%>
<%--                                        보관소 삭제하기--%>
<%--                                    </button>--%>

                                    <button type="button"
                                            class="toggle-btn ${lockerDetail.isAvailable eq 'YES' ? 'btn-stop' : 'btn-restart'}"
                                            data-locker-id="${lockerDetail.lockerId}"
                                            onclick="toggleLockerAvailability(this)">
                                        ${lockerDetail.isAvailable eq 'YES' ? '보관소 중지' : '보관소 재개'}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- 보관소가 없는 경우 -->
                        <div class="empty-locker-section">
                            <div class="empty-locker-icon">
                                <img src="<c:url value='/images/locker_empty_ic.svg'/>" alt="보관소 아이콘" />
                            </div>
                            <h2 class="empty-locker-title">보관소 등록</h2>
                            <p class="empty-locker-subtext">
                                보관소가 아직 등록되지 않았습니다!<br>
                                보관소 등록하러 가시겠습니까?
                            </p>
                            <button class="register-locker-btn" onclick="goToRegisterLocker()">등록하기</button>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:when>

            <c:otherwise>
                <c:set var="welcomeTitle" value="보관소 서비스 이용 안내"/>
                <c:set var="welcomeSubtitle" value="로그인 후 보관소 등록/관리 기능을 사용할 수 있습니다."/>
                <%@ include file="/WEB-INF/views/common/welcom.jsp" %>
            </c:otherwise>
        </c:choose>
    </main>
</div>

<%@ include file="navbar.jsp" %>
<%@ include file="common/modal.jsp" %>

<script>
    const contextPath = '${pageContext.request.contextPath}';
</script>

<c:if test="${not empty sessionScope.memberId}">
     <script>
         window.memberId = "${sessionScope.memberId}";
     </script>
</c:if>

<script src="${pageContext.request.contextPath}/js/sse.js"></script>
<script src="${pageContext.request.contextPath}/js/notification.js"></script>

<script src="<c:url value='/js/locker.js'/>"></script>
</body>
</html>
