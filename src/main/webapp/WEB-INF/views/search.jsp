<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>AirBnG | ${address}&nbsp;검색 결과</title>

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/search.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bottom-sheet.css">

    <!-- Kakao Maps -->
    <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=f3a12e6a3faad91c83bf4e365fca3d15&autoload=false"></script>

    <!-- JS에서 contextPath 사용 -->
    <script>
        const contextPath = '${pageContext.request.contextPath}';
    </script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/search.js"></script>
</head>

<body>
    <div class="page-container">
        <div id="map"></div>

        <!-- Top 검색 바 -->
        <div class="top-bar">
            <img class="back-icon" src="${pageContext.request.contextPath}/images/arrow-left.svg" alt="뒤로가기" onclick="history.back()">
            <div class="search-container">
                <form class="search-form" action="${pageContext.request.contextPath}/search" method="get">
                    <input class="search-input" type="text" name="query" id="searchInput" value="${address} | ${reservationDate}" required>
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
                <c:if test="${not empty jimTypeId}">
                    <div class="sheet-subtitle" onclick="dropdown()">
                        <span id="selectedBagType">
                            <c:choose>
                                <c:when test="${jimTypeId == 1}">백팩/가방</c:when>
                                <c:when test="${jimTypeId == 2}">캐리어</c:when>
                                <c:when test="${jimTypeId == 3}">박스/큰 짐</c:when>
                                <c:when test="${jimTypeId == 4}">유모차</c:when>
                                <c:otherwise>알 수 없음</c:otherwise>
                            </c:choose>
                        </span>
                        <img class="dropdown-down" src="${pageContext.request.contextPath}/images/arrow-down.svg" alt="드롭다운">
                    </div>

                    <ul id="bag-dropdown" class="dropdown-menu hidden">
                        <li onclick="selectBagType(0)">모든 짐</li>
                        <li onclick="selectBagType(1)">백팩/가방</li>
                        <li onclick="selectBagType(2)">캐리어</li>
                        <li onclick="selectBagType(3)">박스/큰 짐</li>
                        <li onclick="selectBagType(4)">유모차</li>
                    </ul>
                </c:if>
            </div>


            <div class="sheet-content" id="lockerList">
                <%-- 여기 안에는 JS가 검색 결과를 동적으로 삽입함 --%>
            </div>
        </div>
    </div>
</body>
</html>
