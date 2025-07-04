<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<link rel="stylesheet" href="<c:url value='/css/navigation.css'/>" />
<!-- navbar.jsp는 네비게이션 바만 포함 -->
<nav class="bottom-nav">
    <a href="shopping-cart.jsp" class="nav-item" data-page="shopping-cart">
        <img src="<c:url value='/images/shopping-cart.svg'/>" alt="보관소" class="nav-icon">
        <span class="nav-text">보관소</span>
    </a>

    <a href="${pageContext.request.contextPath}/page/chatList" class="nav-item" data-page="messages">
        <img src="<c:url value='/images/messages.svg'/>" alt="채팅" class="nav-icon">
        <span class="nav-text">채팅</span>
    </a>

    <a href="${pageContext.request.contextPath}/page/home" class="nav-item" data-page="home">
        <img src="<c:url value='/images/home.svg'/>" alt="홈" class="nav-icon">
        <span class="nav-text">홈</span>
    </a>

    <a href="${pageContext.request.contextPath}/page/reservation/list" class="nav-item" data-page="calendar">
        <img src="<c:url value='/images/calendar.svg'/>" alt="예약" class="nav-icon">
        <span class="nav-text">예약</span>
    </a>

    <a href="${pageContext.request.contextPath}/page/mypage" class="nav-item" data-page="mypage">
        <img src="<c:url value='/images/user.svg'/>" alt="마이" class="nav-icon">
        <span class="nav-text">마이</span>
    </a>
</nav>

<script src="<c:url value='/js/navigation.js'/>"></script>