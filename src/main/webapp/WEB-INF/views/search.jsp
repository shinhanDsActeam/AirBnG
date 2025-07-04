<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>AirBnG | ${address}&nbsp;검색 결과</title>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/search.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bottom-sheet.css">

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
                <span>검색 결과&nbsp;</span>
                <span class="sheet-count">${count}</span>
            </div>
            <div class="sheet-content" id="lockerList">
                <%-- 여기 안에는 JS가 검색 결과를 동적으로 삽입함 --%>
            </div>
        </div>
    </div>

    <script type="text/javascript" src="${pageContext.request.contextPath}/js/config/kakao.config.js"></script>
    <!-- JS에서 contextPath 사용 -->
    <script>
        const contextPath = '${pageContext.request.contextPath}';
    </script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/search.js"></script>
</body>
</html>
