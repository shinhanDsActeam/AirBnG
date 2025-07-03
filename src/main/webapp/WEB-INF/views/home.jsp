<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>AirBnG 홈</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/gh/webfontworld/bmjua/BMJUA.css" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/home.css' />" />
</head>
<body class="airbng-home">

<div class="top-section">
    <div class="top-bar">
        <div class="logo-group">
            <img src="<c:url value='/images/logo_ic.svg' />" alt="로고" />
            <span class="brand-text">에어비앤짐</span>
        </div>
        <img src="<c:url value='/images/bell_ic.svg' />" alt="알림" />

        <div class="bell-ring"></div>
    </div>

    <div class="greeting">
        <c:choose>
            <c:when test="${not empty sessionScope.nickname}">
                반갑습니다<br><span class="nickname">${sessionScope.nickname}</span>님.
            </c:when>
            <c:otherwise>
                Welcome, <span>AirBnG!</span>
            </c:otherwise>
        </c:choose>

        <!-- outer 링 -->
        <div class="greeting-ring"></div>

        <!-- inner 링 추가 -->
        <div class="greeting-ring-inner"></div>
    </div>
</div>

<div class="info-card">
    <div class="info-row-group">
        <div class="info-row">
            <label for="location">장소</label>
            <input type="text" id="location" name="location" value="${locationName}" placeholder="예: 강남구" />
        </div>

        <div class="info-row">
            <label for="date">날짜</label>

            <div class="date-wrapper">
                <div class="custom-date-display" id="dateDisplay">연도-월-일</div>
                <input type="date" id="date" name="date" class="real-date" />
            </div>
        </div>

        <div class="info-row">
            <label for="time">시간</label>
            <select id="time" name="time">
                <option value="18:00~20:00">(18:00~20:00) 2시간</option>
                <option value="20:00~22:00">(20:00~22:00) 2시간</option>
                <option value="22:00~24:00">(22:00~24:00) 2시간</option>
            </select>
        </div>
    </div>
    <button class="find-button">보관소 찾기</button>
</div>

<!-- 어떤 짐을 보관하냐 -->
<section class="category-section">
    <h3>어떤 짐을 보관하시나요?</h3>
    <div class="category-grid">
        <div class="category-card">
            <img src="<c:url value='/images/backpack_img.svg' />" alt="백팩" />
            <p>백팩/가방<br><small>시간당 2,000원부터</small></p>
        </div>
        <div class="category-card">
            <img src="<c:url value='/images/carrier_img.svg' />" alt="캐리어" />
            <p>캐리어<br><small>시간당 3,000원부터</small></p>
        </div>
        <div class="category-card">
            <img src="<c:url value='/images/box_img.svg' />" alt="박스" />
            <p>박스/큰 짐<br><small>시간당 4,000원부터</small></p>
        </div>
        <div class="category-card">
            <img src="<c:url value='/images/stroller_img.svg' />" alt="유모차" />
            <p>유모차<br><small>시간당 5,000원부터</small></p>
        </div>
    </div>
</section>

<!-- 인기 보관 지역 -->
<section class="popular-section">
    <h3>인기 보관 지역</h3>
    <div class="popular-list" id="popularList">
        <!-- JS가 이 안에 동적으로 데이터를 채워 넣음 -->
    </div>
</section>

<!-- 네비게이션 바 include - page-container 밖에 위치 -->
<%@ include file="navbar.jsp" %>

<script>
    const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="<c:url value='/js/home.js' />"></script>

</body>
</html>