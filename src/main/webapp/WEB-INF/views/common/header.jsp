<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="<c:url value='/css/common/header.css' />"/>

<div class="common-header">
    <c:choose>
        <c:when test="${not empty showBackButton and showBackButton}">
            <c:choose>
                <c:when test="${not empty backUrl}">
                    <img class="back-icon" src="${pageContext.request.contextPath}/images/arrow-left.svg" alt="뒤로가기"
                         onclick="location.href='${backUrl}'">
                </c:when>
                <c:otherwise>
                    <img class="back-icon" src="${pageContext.request.contextPath}/images/arrow-left.svg" alt="뒤로가기"
                         onclick="history.back()">
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <div class="back-spacer"></div>
        </c:otherwise>
    </c:choose>

    <div class="header-title">${headerTitle}</div>

    <c:choose>
        <c:when test="${not empty showHomeButton and showHomeButton}">
            <c:choose>
                <c:when test="${not empty homeUrl}">
                    <img class="home-icon" src="${pageContext.request.contextPath}/images/home.svg" alt="홈"
                         onclick="location.href='${homeUrl}'">
                </c:when>
                <c:otherwise>
                    <img class="home-icon" src="${pageContext.request.contextPath}/images/home.svg" alt="홈"
                         onclick="window.location.href='${pageContext.request.contextPath}/page/home'">
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <div class="header-spacer"></div>
        </c:otherwise>
    </c:choose>
</div>