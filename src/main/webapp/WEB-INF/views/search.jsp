<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>이미지 마커와 커스텀 오버레이</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/search.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bottom-sheet.css">
    <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=4bf15ccba3d2bdbeffb6776b0c82521d&autoload=false"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/search.js"></script>
</head>
<body>
    <div class="page-container">
        <div id="map"></div>
        <div class="top-bar">
            <img class="back-icon" src="${pageContext.request.contextPath}/images/arrow-left.svg" alt="뒤로가기" onclick="history.back()">
            <div class="search-container">
                <form class="search-form" action="${pageContext.request.contextPath}/search" method="get">
                    <input class="search-input" type="text" name="query" placeholder="검색어를 입력하세요" required>
                    <img class="search-button" src="${pageContext.request.contextPath}/images/Group 2.svg" alt="검색">
                </form>
            </div>
        </div>

        <!-- 바텀시트 -->
        <div class="bottom-sheet" id="bottomSheet">
            <div class="sheet-header" id="sheetHeader">
                <div class="sheet-drag-handle"></div>
            </div>
            <div class="sheet-content">
                <div class="storage-item">
                    <div class="storage-image"></div>
                    <div class="storage-info">
                        <div class="storage-name">강남역 보관소</div>
                        <div class="storage-address">주소주소주소주소주소주소</div>
                    </div>
                    <button class="storage-button">보관가능</button>
                </div>
                <div class="storage-item">
                    <div class="storage-image"></div>
                    <div class="storage-info">
                        <div class="storage-name">강남역 보관소</div>
                        <div class="storage-address">주소주소주소주소주소주소</div>
                    </div>
                    <button class="storage-button">보관가능</button>
                </div>
                <div class="storage-item disabled">
                    <div class="storage-image"></div>
                    <div class="storage-info">
                        <div class="storage-name">강남역 보관소</div>
                        <div class="storage-address">주소주소주소주소주소주소</div>
                    </div>
                    <button class="storage-button">보관대기</button>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
