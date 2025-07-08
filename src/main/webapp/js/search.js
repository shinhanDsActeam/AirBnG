function loadKakaoScript(callback) {
    const existingScript = document.querySelector("script[src*='dapi.kakao.com']");

    if (!existingScript) {
        const script = document.createElement("script");
        script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${window.KAKAO_APP_KEY}&autoload=false&libraries=services`;
        script.async = true;
        script.onload = () => kakao.maps.load(callback);
        document.head.appendChild(script);
    } else {
        kakao.maps.load(callback);
    }
}

function initMapAndResults() {
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

    setupSearchBar();
    setupBottomSheet();
    loadLockerList();
}

function setupSearchBar() {
    const urlParams = new URLSearchParams(window.location.search);
    const address = urlParams.get('address');
    if (address) {
        const input = document.getElementById('searchInput');
        if (input) input.value = decodeURIComponent(address);
    }
}

function setupBottomSheet() {
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

        const currentY = e.clientY;
        const deltaY = currentY - startY;

        if (deltaY > 20) {
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
}

function loadLockerList() {
    const urlParams = new URLSearchParams(window.location.search);
    const address = urlParams.get("address");

    if (!address) return;

    fetch(`${contextPath}/lockers?address=${encodeURIComponent(address)}`)
        .then(response => response.json())
        .then(data => {
            console.log("보관소 정보:", data);
            const container = document.getElementById("lockerList");
            if (!container) return;

            const lockers = data.result.lockers;
            if (!lockers || lockers.length === 0) {
                container.innerHTML = "<p>검색 결과가 없습니다.</p>";
                return;
            }

            container.innerHTML = "";

            lockers.forEach(locker => {
                const div = document.createElement("div");
                div.className = `storage-item ${locker.isAvailable === 'NO' ? 'disabled' : ''}`;
                const imageUrl = locker.url != null ? locker.url : contextPath + "/images/default.jpg";

                div.innerHTML = `
                    <div class="storage-image"
                         style="background-image: url('${imageUrl}')">
                    </div>
                    <div class="storage-info">
                        <div class="storage-name">${locker.lockerName}</div>
                        <div class="storage-address">${locker.address}</div>
                    </div>
                    <button class="storage-button"
                            data-available="${locker.isAvailable}"
                            data-id="${locker.lockerId}">
                        ${locker.isAvailable === 'YES' ? '보관가능' : '보관대기'}
                    </button>
                `;
                container.appendChild(div);
            });

            container.addEventListener("click", function (e) {
                const clickedItem = e.target.closest(".storage-item");
                if (!clickedItem || clickedItem.classList.contains("disabled")) return;

                document.querySelectorAll(".storage-item").forEach(item => {
                    item.classList.remove("selected");
                    const btn = item.querySelector(".storage-button");
                    const isAvailable = btn.dataset.available === "YES";
                    btn.textContent = isAvailable ? "보관가능" : "보관대기";
                });

                clickedItem.classList.add("selected");
                const clickedButton = clickedItem.querySelector(".storage-button");
                clickedButton.textContent = "상세보기";

                clickedButton.addEventListener('click', function (e) {
                    e.stopPropagation();
                    const lockerId = clickedButton.dataset.id;
                    const url = `${contextPath}/page/lockerDetails?lockerId=${encodeURIComponent(lockerId)}`;
                    window.location.href = url;
                });
            });

        })
        .catch(error => {
            console.error("보관소 정보를 불러오는 데 실패했습니다:", error);
        });
}

// 진입점
loadKakaoScript(initMapAndResults);