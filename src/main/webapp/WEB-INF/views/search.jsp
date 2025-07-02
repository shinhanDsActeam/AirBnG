<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>이미지 마커와 커스텀 오버레이</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/search.css">
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
    <script>
        var mapContainer = document.getElementById('map'), // 지도를 표시할 div
            mapOption = {
                center: new kakao.maps.LatLng(37.55935630141197, 126.92263348592226), // 지도의 중심좌표
                level: 4 // 지도의 확대 레벨
            };

        var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다

        var position =  new kakao.maps.LatLng(37.55935630141197, 126.92263348592226);

        var marker = new kakao.maps.Marker({
          position: position,
          clickable: true // 마커를 클릭했을 때 지도의 클릭 이벤트가 발생하지 않도록 설정합니다
        });


        var iwContent = '<div style="padding:5px;">Hello World! <br></div>', // 인포윈도우에 표출될 내용으로 HTML 문자열이나 document element가 가능합니다
            iwPosition = new kakao.maps.LatLng(37.55935630141197, 126.92263348592226);

        var infowindow = new kakao.maps.InfoWindow({
            position : iwPosition,
            content : iwContent
        });

        // 마커가 지도 위에 표시되도록 설정합니다
        marker.setMap(map);

        var iwContent = '<div style="padding:5px;">Hello World!</div>', // 인포윈도우에 표출될 내용으로 HTML 문자열이나 document element가 가능합니다
            iwRemoveable = true;

        // 인포윈도우를 생성합니다
        var infowindow = new kakao.maps.InfoWindow({
            content : iwContent,
            removable : iwRemoveable
        });

        // 마커에 클릭이벤트를 등록합니다
        kakao.maps.event.addListener(marker, 'click', function() {
              // 마커 위에 인포윈도우를 표시합니다
              infowindow.open(map, marker);
        });
    </script>
</body>
</html>
