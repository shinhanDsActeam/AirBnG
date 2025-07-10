<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>AirBnG | ${address}&nbsp;검색 결과</title>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg" />
    <link rel="stylesheet" href="<c:url value='/css/dot.css' />" />
    <link rel="stylesheet" href="<c:url value='/css/search.css' />" />
    <link rel="stylesheet" href="<c:url value='/css/bottom-sheet.css' />" />
</head>

<body>
    <div class="page-container">
        <div id="map"></div>

        <!-- Top 검색 바 -->
        <div class="top-bar">
            <img class="back-icon" src="${pageContext.request.contextPath}/images/arrow-left.svg" alt="뒤로가기" onclick="window.location.href='${pageContext.request.contextPath}/page/home';">
            <div class="search-container">
                <form class="search-form" action="${pageContext.request.contextPath}/search" method="get">
                    <input class="search-input" type="text" name="query" id="searchInput" placeholder="${address}" required>
                    <img class="search-button" src="${pageContext.request.contextPath}/images/Group 2.svg" alt="검색">
                </form>
            </div>
        </div>

        <!-- 바텀시트 -->
        <div class="bottom-sheet" id="bottomSheet">
            <div class="sheet-header" id="sheetHeader">
                <div class="sheet-drag-handle"></div>
            </div>
            <div class="sheet-title">
                <div class="sheet-left">
                    <span>검색 결과&nbsp;</span>
                    <span class="sheet-count">${count}</span>
                </div>
                    <div class="sheet-subtitle" onclick="dropdown()">
                        <span id="selectedBagType">
                            <c:choose>
                                <c:when test="${empty jimTypeId || jimTypeId == 0}">모든 짐</c:when>
                                <c:when test="${jimTypeId == 1}">백팩/가방</c:when>
                                <c:when test="${jimTypeId == 2}">캐리어 소형</c:when>
                                <c:when test="${jimTypeId == 3}">캐리어 대형</c:when>
                                <c:when test="${jimTypeId == 4}">박스/큰 짐</c:when>
                                <c:when test="${jimTypeId == 5}">유모차</c:when>
                            </c:choose>
                        </span>
                        <img class="dropdown-down" src="${pageContext.request.contextPath}/images/arrow-down.svg" alt="드롭다운">
                    </div>

                    <ul id="bag-dropdown" class="dropdown-menu hidden">
                        <li onclick="selectBagType(0)">모든 짐</li>
                        <li onclick="selectBagType(1)">백팩/가방</li>
                        <li onclick="selectBagType(2)">캐리어 소형</li>
                        <li onclick="selectBagType(3)">캐리어 대형</li>
                        <li onclick="selectBagType(4)">박스/큰 짐</li>
                        <li onclick="selectBagType(5)">유모차</li>
                    </ul>
            </div>
            <div class="sheet-content" id="lockerList"></div>
        </div>
    </div>

    <script type="text/javascript" src="${pageContext.request.contextPath}/js/config/kakao.config.js"></script>
    <!-- JS에서 contextPath 사용 -->
    <script>
        const contextPath = '${pageContext.request.contextPath}';
    </script>

    <c:if test="${not empty sessionScope.memberId}">
        <script>
            window.memberId = "${sessionScope.memberId}";
        </script>
    </c:if>
    <script src="<c:url value='/js/sse.js'/>"></script>
    <script src="<c:url value='/js/dot.js'/>"></script>
    <script src="<c:url value='/js/config/kakao.config.js'/>"></script>
    <script src="<c:url value='/js/search.js'/>"></script>
    <script src="${pageContext.request.contextPath}/js/notification.js"></script>

</body>
</html>