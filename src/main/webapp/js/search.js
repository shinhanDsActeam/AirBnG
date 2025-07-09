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

let map; // 전역으로 선언하여 다른 함수에서도 접근 가능
let markers = []; // 마커 배열 추가 (기존 마커 정리용)
let infoWindows = []; // 인포윈도우 배열 추가 (토글 기능용)
let lockersData = []; // 락커 데이터 저장 (리스트 클릭 시 지도 이동용)

function initMapAndResults() {
    const mapContainer = document.getElementById('map');
    const mapOption = {
        center: new kakao.maps.LatLng(37.55935630141197, 126.92263348592226),
        level: 4
    };
    map = new kakao.maps.Map(mapContainer, mapOption);

    setupSearchBar();
    setupBottomSheet();

    const urlParams = new URLSearchParams(window.location.search);
    const address = urlParams.get("address");
    const jimTypeId = urlParams.get("jimTypeId");
    const reservationDate = urlParams.get("reservationDate");

    if (address) {
        fetchAndRenderLockers(address, jimTypeId, reservationDate);
    }
}

// 기존 마커들을 지도에서 제거하는 함수
function clearMarkers() {
    markers.forEach(marker => marker.setMap(null));
    infoWindows.forEach(infoWindow => infoWindow.close());
    markers = [];
    infoWindows = [];
}

// 마커를 지도에 표시하는 함수
function renderLockerMarkers(lockers) {
    if (!map || !lockers || lockers.length === 0) {
        console.log("지도 또는 락커 데이터가 없습니다.");
        return;
    }

    // 기존 마커들 제거
    clearMarkers();

    console.log("마커 렌더링 시작, 락커 개수:", lockers.length);

    lockers.forEach((locker, index) => {
        const { latitude, longitude, lockerName, lockerId } = locker;

        console.log(`락커 ${index + 1}: ${lockerName}, 위도: ${latitude}, 경도: ${longitude}`);

        if (latitude && longitude) {
            const position = new kakao.maps.LatLng(parseFloat(latitude), parseFloat(longitude));

            const marker = new kakao.maps.Marker({
                map: map,
                position: position,
                title: lockerName
            });

            // 마커 배열에 추가
            markers.push(marker);

            const infowindow = new kakao.maps.InfoWindow({
                content: `<div style="padding:5px; font-size:12px;">${lockerName}</div>`
            });

            // 인포윈도우 배열에 추가
            infoWindows.push(infowindow);

            // 마커 클릭 시 토글 기능
            kakao.maps.event.addListener(marker, 'click', () => {
                // 현재 인포윈도우가 열려있는지 확인
                const isOpen = infowindow.getMap();

                if (isOpen) {
                    // 열려있으면 닫기
                    infowindow.close();
                } else {
                    // 다른 인포윈도우들은 모두 닫기
                    infoWindows.forEach(iw => iw.close());
                    // 현재 인포윈도우 열기
                    infowindow.open(map, marker);
                }
            });

            console.log(`마커 생성 완료: ${lockerName}`);
        } else {
            console.warn(`락커 ${lockerName}의 위도/경도 정보가 없습니다.`);
        }
    });

    // 지도 중심을 첫 번째 락커로 이동 (선택)
    if (lockers[0]?.latitude && lockers[0]?.longitude) {
        const firstPosition = new kakao.maps.LatLng(
            parseFloat(lockers[0].latitude),
            parseFloat(lockers[0].longitude)
        );
        map.setCenter(firstPosition);
        console.log("지도 중심 이동 완료");
    }
}

function setupSearchBar() {
    const input = document.getElementById('searchInput');
    const urlParams = new URLSearchParams(window.location.search);
    const address = urlParams.get('address');

    if (input && address) {
        input.value = decodeURIComponent(address);
        input.addEventListener("focus", function () {
            window.location.href = `${contextPath}/page/lockerSearch?jimTypeId=0`;
        });
    }
}

function setupBottomSheet() {
    const sheet = document.getElementById('bottomSheet');
    const header = document.getElementById('sheetHeader');

    let startY = 0;
    let isDragging = false;

    sheet.style.transform = "translateX(-50%) translateY(70%)";
    sheet.classList.remove("fixed");

    header.addEventListener('mousedown', function (e) {
        startY = e.clientY;
        isDragging = true;
        sheet.classList.add("dragging");

        document.addEventListener('mousemove', mouseMoveHandler);
        document.addEventListener('mouseup', mouseUpHandler);
    });

    function mouseMoveHandler(e) {
        const debug = document.getElementById("debugOutput");
        if (debug) debug.textContent = "현재 Y: " + e.clientY;
    }

    function mouseUpHandler(e) {
        if (!isDragging) return;

        const deltaY = e.clientY - startY;
        if (deltaY > 20) {
            sheet.style.transform = "translateX(-50%) translateY(70%)";
            sheet.classList.remove("fixed");
        } else {
            sheet.style.transform = "translateX(-50%) translateY(0%)";
            sheet.classList.add("fixed");
        }

        isDragging = false;
        sheet.classList.remove("dragging");
        document.removeEventListener('mousemove', mouseMoveHandler);
        document.removeEventListener('mouseup', mouseUpHandler);
    }
}

// 통합된 fetchAndRenderLockers 함수
function fetchAndRenderLockers(address, jimTypeId, reservationDate) {
    console.log("fetchAndRenderLockers 호출됨:", { address, jimTypeId, reservationDate });

    let queryString = `address=${encodeURIComponent(address)}`;
    if (jimTypeId && jimTypeId !== "0") queryString += `&jimTypeId=${jimTypeId}`;
    if (reservationDate) queryString += `&reservationDate=${reservationDate}`;

    fetch(`${contextPath}/lockers?${queryString}`)
        .then(response => {
            const contentType = response.headers.get("content-type");
            if (contentType?.includes("application/json")) return response.json();
            return response.text().then(text => {
                console.error("JSON이 아닌 응답:", text);
                throw new Error("잘못된 응답입니다.");
            });
        })
        .then(data => {
            console.log("서버 응답 데이터:", data);

            const container = document.getElementById("lockerList");
            if (!container) return;

            if (data.code === 3001 || !data.result?.lockers?.length) {
                container.innerHTML = `
                    <div class="no-result-wrapper">
                        <img class="search-warning" src="${contextPath}/images/danger.svg" alt="검색결과없음">
                        <p class="no-result-main">검색 결과가 없습니다</p>
                        <p class="no-result-sub">다른 위치나 키워드로 다시 시도해보세요!</p>
                    </div>
                `;
                document.querySelector(".sheet-count").textContent = "0";
                document.getElementById("bottomSheet").classList.add("fixed");
                clearMarkers(); // 검색 결과가 없으면 마커도 지우기
                return;
            }

            let lockers = data.result.lockers;
            console.log("락커 데이터:", lockers);

            if (jimTypeId && jimTypeId !== "0") {
                lockers = lockers.filter(locker =>
                    locker.jimTypeResults?.some(jtr => String(jtr.jimTypeId) === String(jimTypeId))
                );
                console.log("필터링된 락커 데이터:", lockers);
            }

            // 마커 렌더링 호출 (중요!)
            renderLockerMarkers(lockers);

            // 락커 데이터 저장 (리스트 클릭 시 지도 이동용)
            lockersData = lockers;

            // UI 업데이트
            container.innerHTML = "";
            document.querySelector(".sheet-count").textContent = lockers.length;

            lockers.forEach(locker => {
                const div = document.createElement("div");
                div.className = `storage-item ${locker.isAvailable === 'NO' ? 'disabled' : ''}`;
                div.dataset.lockerId = locker.lockerId; // 락커 ID 추가
                const imageUrl = locker.url || `${contextPath}/images/default.jpg`;

                div.innerHTML = `
                    <div class="storage-image" style="background-image: url('${imageUrl}')"></div>
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

            document.getElementById("bottomSheet").classList.add("fixed");
            document.getElementById("bottomSheet").style.transform = "translateX(-50%) translateY(0%)";
            addLockerItemClickEvents();
        })
        .catch(error => {
            console.error("보관소 정보를 불러오는 데 실패했습니다:", error);
            const container = document.getElementById("lockerList");
            if (container) {
                container.innerHTML = `
                    <div class="no-result-wrapper">
                        <img class="search-warning" src="${contextPath}/images/danger.svg" alt="검색결과없음">
                        <p class="no-result-main">검색 중 오류가 발생했습니다</p>
                        <p class="no-result-sub">잠시 후 다시 시도해주세요!</p>
                    </div>
                `;
            }
            document.querySelector(".sheet-count").textContent = "0";
            clearMarkers(); // 에러 시에도 마커 지우기
        });
}

function addLockerItemClickEvents() {
    const container = document.getElementById("lockerList");
    container.addEventListener("click", function (e) {
        const sheet = document.getElementById("bottomSheet");
        if (!sheet.classList.contains("fixed")) return;

        const clickedItem = e.target.closest(".storage-item");
        if (!clickedItem || clickedItem.classList.contains("disabled")) return;

        const clickedButton = clickedItem.querySelector(".storage-button");
        const isAvailable = clickedButton.dataset.available === "YES";
        const lockerId = clickedItem.dataset.lockerId;

        // 리스트 클릭 시 지도 이동
        moveMapToLocker(lockerId);

        if (clickedItem.classList.contains("selected")) {
            clickedItem.classList.remove("selected");
            clickedButton.textContent = isAvailable ? "보관가능" : "보관대기";
            clickedButton.onclick = null;
            return;
        }

        document.querySelectorAll(".storage-item").forEach(item => {
            item.classList.remove("selected");
            const btn = item.querySelector(".storage-button");
            const available = btn.dataset.available === "YES";
            btn.textContent = available ? "보관가능" : "보관대기";
            btn.onclick = null;
        });

        clickedItem.classList.add("selected");
        clickedButton.textContent = "상세보기";

        clickedButton.onclick = function () {
            const lockerId = clickedButton.dataset.id;
            window.location.href = `${contextPath}/page/lockerDetails?lockerId=${encodeURIComponent(lockerId)}`;
        };
    });
}

// 특정 락커 위치로 지도 이동하는 함수
function moveMapToLocker(lockerId) {
    const targetLocker = lockersData.find(locker => String(locker.lockerId) === String(lockerId));

    if (targetLocker && targetLocker.latitude && targetLocker.longitude) {
        const position = new kakao.maps.LatLng(
            parseFloat(targetLocker.latitude),
            parseFloat(targetLocker.longitude)
        );

        // 지도 중심 이동 (부드러운 애니메이션)
        map.panTo(position);

        // 해당 락커의 인포윈도우 열기
        const markerIndex = lockersData.findIndex(locker => String(locker.lockerId) === String(lockerId));
        if (markerIndex !== -1 && infoWindows[markerIndex]) {
            // 다른 인포윈도우들은 모두 닫기
            infoWindows.forEach(iw => iw.close());
            // 해당 인포윈도우 열기
            infoWindows[markerIndex].open(map, markers[markerIndex]);
        }

        console.log(`지도가 ${targetLocker.lockerName} 위치로 이동했습니다.`);
    } else {
        console.warn(`락커 ID ${lockerId}의 위치 정보를 찾을 수 없습니다.`);
    }
}

function selectBagType(jimTypeId) {
    const typeMap = {
        0: '모든 짐',
        1: '백팩/가방',
        2: '캐리어 소형',
        3: '캐리어 대형',
        4: '박스/큰 짐',
        5: '유모차'
    };

    document.getElementById("selectedBagType").textContent = typeMap[jimTypeId];
    document.getElementById("bag-dropdown").classList.add("hidden");

    const url = new URL(window.location.href);
    const address = url.searchParams.get("address");
    let reservationDate = url.searchParams.get("reservationDate") || new Date().toISOString().split("T")[0];

    url.searchParams.set("jimTypeId", jimTypeId);
    url.searchParams.set("reservationDate", reservationDate);
    window.history.replaceState({}, '', url.toString());

    fetchAndRenderLockers(address, jimTypeId, reservationDate);
}

function dropdown() {
    const dropdownMenu = document.getElementById("bag-dropdown");
    const sheet = document.getElementById("bottomSheet");
    if (!sheet.classList.contains("fixed")) return;
    if (dropdownMenu) {
        dropdownMenu.classList.toggle("hidden");
    }
}

// 진입점
document.addEventListener("DOMContentLoaded", () => {
    loadKakaoScript(initMapAndResults);
});