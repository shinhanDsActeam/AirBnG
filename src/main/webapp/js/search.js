kakao.maps.load(function () {
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
        const debug = document.getElementById("debugOutput");
        if (debug) {
            debug.textContent = "현재 Y: " + e.clientY;
        }
    };

    const mouseUpHandler = function (e) {
        if (!isDragging) return;

        const middleY = window.innerHeight / 2;
        const currentY = e.clientY;

        if (currentY > middleY) {
            sheet.style.transform = "translateX(-50%) translateY(70%)";
        } else {
            sheet.style.transform = "translateX(-50%) translateY(0%)";
        }

        isDragging = false;
        sheet.classList.remove("dragging");

        document.removeEventListener('mousemove', mouseMoveHandler);
        document.removeEventListener('mouseup', mouseUpHandler);
    };

    header.addEventListener('mousedown', mouseDownHandler);
});
