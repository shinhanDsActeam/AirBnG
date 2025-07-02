<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>이미지 마커와 커스텀 오버레이</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/search.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bottom-sheet.css">
    <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=4bf15ccba3d2bdbeffb6776b0c82521d"></script>
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

        <script>
            // 카카오 맵 초기화
            var mapContainer = document.getElementById('map');
            var mapOption = {
                center: new kakao.maps.LatLng(37.55935630141197, 126.92263348592226),
                level: 4
            };

            var map = new kakao.maps.Map(mapContainer, mapOption);

            var position = new kakao.maps.LatLng(37.55935630141197, 126.92263348592226);

            var marker = new kakao.maps.Marker({
                position: position,
                clickable: true
            });

            marker.setMap(map);

            var iwContent = '<div style="padding:5px;">Hello World!</div>';
            var iwRemoveable = true;

            var infowindow = new kakao.maps.InfoWindow({
                content: iwContent,
                removable: iwRemoveable
            });

            kakao.maps.event.addListener(marker, 'click', function() {
                infowindow.open(map, marker);
            });


document.addEventListener('DOMContentLoaded', function () {
  const sheet = document.getElementById('bottomSheet');
  const header = document.getElementById('sheetHeader');

  let startY = 0;
  let isDragging = false;

  const mouseDownHandler = function (e) {
    startY = e.clientY;
    isDragging = true;
    sheet.classList.add("dragging");

    document.addEventListener('mousemove', mouseMoveHandler);
    document.addEventListener('mouseup', mouseUpHandler);
  };

  const mouseMoveHandler = function (e) {
    // 선택적으로 디버그 텍스트 업데이트
    const debug = document.getElementById("debugOutput");
    if (debug) {
      debug.textContent = "현재 Y: " + e.clientY;
    }
  };

  const mouseUpHandler = function (e) {
    if (!isDragging) return;

    const middleY = window.innerHeight / 2;
    const currentY = e.clientY;

    // 조건에 따라 translateY 조정
    if (currentY > middleY) {
      // 아래면 70% 보이기
      sheet.style.transform = "translateX(-50%) translateY(70%)";
    } else {
      // 위면 30% 보이기
      sheet.style.transform = "translateX(-50%) translateY(0%)";
    }

    isDragging = false;
    sheet.classList.remove("dragging");

    document.removeEventListener('mousemove', mouseMoveHandler);
    document.removeEventListener('mouseup', mouseUpHandler);
  };

  header.addEventListener('mousedown', mouseDownHandler);
});

</script>
</body>
</html>
