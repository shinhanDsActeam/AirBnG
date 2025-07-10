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

// 마커 이미지 생성 함수
function createMarkerImage(isAvailable) {
    const imageSrc = isAvailable === 'YES'
        ? `${contextPath}/images/marker-available.png`
        : `${contextPath}/images/marker-unavailable.png`;

    const imageSize = new kakao.maps.Size(28, 32);
    const imageOption = { offset: new kakao.maps.Point(12, 35) };

    return new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);
}

// 바텀시트 높이를 고려한 지도 중심 오프셋 계산
function getVisibleMapBounds() {
    const bottomSheet = document.getElementById('bottomSheet');
    const mapContainer = document.getElementById('map');

    if (!bottomSheet || !mapContainer || !map) return map.getBounds();

    const isBottomSheetOpen = bottomSheet.classList.contains('fixed');

    if (isBottomSheetOpen) {
        const bottomSheetHeight = bottomSheet.offsetHeight;
        const mapHeight = mapContainer.offsetHeight;

        // 바텀시트가 차지하는 비율 계산
        const visibleRatio = (mapHeight - bottomSheetHeight) / mapHeight;

        // 현재 지도 영역
        const bounds = map.getBounds();
        const center = map.getCenter();

        // 위쪽 영역만 사용하도록 조정
        const latRange = bounds.getNorthEast().getLat() - bounds.getSouthWest().getLat();
        const adjustedLatRange = latRange * visibleRatio;

        const northEast = new kakao.maps.LatLng(
            center.getLat() + adjustedLatRange / 2,
            bounds.getNorthEast().getLng()
        );
        const southWest = new kakao.maps.LatLng(
            center.getLat() - adjustedLatRange / 2,
            bounds.getSouthWest().getLng()
        );

        const adjustedBounds = new kakao.maps.LatLngBounds(southWest, northEast);
        return adjustedBounds;
    }

    return map.getBounds();
}

function createInfoWindowTemplate(locker, contextPath) {
    const { lockerName, lockerId, isAvailable, address, url } = locker;

    const imageUrl = url || `${contextPath}/images/default.jpg`;
    const availabilityText = isAvailable === 'YES' ? '보관가능' : '보관대기';
    const availabilityColor = isAvailable === 'YES' ? '#4CAF50' : '#ff9800';

    return `
        <div class="info-window">
            <div class="info-window-image" style="background-image: url('${imageUrl}');">
                <div class="info-window-availability" style="background: ${availabilityColor};">
                    ${availabilityText}
                </div>
            </div>
            <div class="info-window-content">
                <div class="info-window-title">
                    ${lockerName}
                </div>
                <div class="info-window-address">
                    ${address || '주소 정보 없음'}
                </div>
                <button
                    class="info-window-button"
                    onclick="window.location.href='${contextPath}/page/lockerDetails?lockerId=${encodeURIComponent(lockerId)}'"
                >
                    상세보기
                </button>
            </div>
        </div>
    `;
}

// 마커를 지도에 표시하는 함수
function renderLockerMarkers(lockers) {
    if (!map || !lockers || lockers.length === 0) {
        console.log("지도 또는 락커 데이터가 없습니다.");
        return;
    }
    clearMarkers();

    lockers.forEach((locker, index) => {
        const { latitude, longitude, lockerName, lockerId, isAvailable } = locker;

        if (latitude && longitude) {
            const position = new kakao.maps.LatLng(parseFloat(latitude), parseFloat(longitude));

            // 보관 가능 여부에 따라 마커 이미지 설정
            const markerImage = createMarkerImage(isAvailable);

            const marker = new kakao.maps.Marker({
                map: map,
                position: position,
                title: lockerName,
                image: markerImage
            });

            // 마커 배열에 추가
            markers.push(marker);

            // 향상된 InfoWindow 내용 생성
            const imageUrl = locker.url || `${contextPath}/images/default.jpg`; // 이미지 URL이 없으면 기본 이미지 사용
            const availabilityText = isAvailable === 'YES' ? '보관가능' : '보관대기';
            const availabilityColor = isAvailable === 'YES' ? '#4CAF50' : '#ff9800';
            const priceText = '시간당 2,000원'; // 가격 정보가 있다면 locker.price 등으로 변경

            const infoWindowContent = createInfoWindowTemplate(locker, contextPath);

            const infowindow = new kakao.maps.InfoWindow({
                content: infoWindowContent,
                removable: true,
            });

            // 인포윈도우 배열에 추가
            infoWindows.push(infowindow);

            // 마커 클릭 시 토글 기능
//            kakao.maps.event.addListener(marker, 'click', () => {
//                const isOpen = infowindow.getMap();
//
//                if (isOpen) {
//                    infowindow.close();
//                } else {
//                    // 다른 인포윈도우 닫기
//                    infoWindows.forEach(iw => iw.close());
//
//                    // 현재 인포윈도우 열기
//                    infowindow.open(map, marker);
//
//                    // 마커 위치로 지도 이동
//                    map.panTo(marker.getPosition());
//
//                    // 줌 레벨 조정 (더 가까이 보기 위해 레벨 숫자 낮춤)
//                    const currentLevel = map.getLevel();
//                    const targetLevel = Math.max(currentLevel - 2, 4); // 최소 레벨 1
//                    map.setLevel(targetLevel, { animate: {
//                        duration: 300
//                    }});
//                }
//            });


            kakao.maps.event.addListener(marker, 'click', () => {
                const isOpen = infowindow.getMap();

                if (isOpen) {
                    infowindow.close();
                } else {
                    infoWindows.forEach(iw => iw.close());

                    infowindow.open(map, marker);

                    const position = marker.getPosition();

                    // 바텀시트 가림 방지용 오프셋 계산 (리스트 클릭과 동일한 로직)
                    const mapContainer = document.getElementById('map');
                    const bottomSheet = document.getElementById('bottomSheet');

                    let offsetLat = 0.0001; // 기본값 (작게 설정)

                    if (mapContainer && bottomSheet && bottomSheet.classList.contains('fixed')) {
                        const mapHeight = mapContainer.offsetHeight;
                        const sheetHeight = bottomSheet.offsetHeight;

                        // 바텀시트가 차지하는 비율 계산
                        const sheetRatio = sheetHeight / mapHeight;

                        // 현재 지도 범위 계산
                        const bounds = map.getBounds();
                        const latRange = bounds.getNorthEast().getLat() - bounds.getSouthWest().getLat();

                        // 오프셋을 바텀시트 높이에 비례하여 계산 (리스트 클릭과 동일)
                        offsetLat = latRange * sheetRatio * -0.2;

                        console.log('마커 클릭 오프셋 계산:', { mapHeight, sheetHeight, sheetRatio, latRange, offsetLat });
                    }

                    const adjustedPosition = new kakao.maps.LatLng(
                        position.getLat() + offsetLat,
                        position.getLng()
                    );

                    map.panTo(adjustedPosition);

                    // 확대 레벨 설정
                    setTimeout(() => {
                        map.setLevel(3);
                    }, 300);
                }
            });

        } else {
            console.warn(`락커 ${lockerName}의 위도/경도 정보가 없습니다.`);
        }
    });

    if (lockers.length > 0) {
        const bounds = new kakao.maps.LatLngBounds();

        lockers.forEach(locker => {
            if (locker.latitude && locker.longitude) {
                const position = new kakao.maps.LatLng(parseFloat(locker.latitude), parseFloat(locker.longitude));
                bounds.extend(position);
            }
        });

        // 지도에 마커가 모두 보이도록 영역 설정
        map.setBounds(bounds);
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

    // 지도 초기화 대기 (Promise 방식)
    waitForMapInitialization().then(() => {
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
                    clearMarkers();
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

                renderLockerMarkers(lockers);
                lockersData = lockers;

                container.innerHTML = "";
                document.querySelector(".sheet-count").textContent = lockers.length;

                lockers.forEach(locker => {
                    const div = document.createElement("div");
                    div.className = `storage-item ${locker.isAvailable === 'NO' ? 'disabled' : ''}`;
                    div.dataset.lockerId = locker.lockerId;
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
                clearMarkers();
            });
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

        // 이미 선택된 아이템을 다시 클릭한 경우
        if (clickedItem.classList.contains("selected")) {
            clickedItem.classList.remove("selected");
            clickedButton.textContent = isAvailable ? "보관가능" : "보관대기";
            clickedButton.onclick = null;

            // 인포윈도우 닫기
            closeInfoWindowForLocker(lockerId);
            return;
        }

        // 다른 아이템들 선택 해제
        document.querySelectorAll(".storage-item").forEach(item => {
            item.classList.remove("selected");
            const btn = item.querySelector(".storage-button");
            const available = btn.dataset.available === "YES";
            btn.textContent = available ? "보관가능" : "보관대기";
            btn.onclick = null;
        });

        // 현재 아이템 선택
        clickedItem.classList.add("selected");
        clickedButton.textContent = "상세보기";

        clickedButton.onclick = function () {
            const lockerId = clickedButton.dataset.id;
            window.location.href = `${contextPath}/page/lockerDetails?lockerId=${encodeURIComponent(lockerId)}`;
        };

        // 리스트 클릭 시 지도 이동 및 인포윈도우 열기
        moveMapToLocker(lockerId);
    });
}

function closeInfoWindowForLocker(lockerId) {
    const markerIndex = lockersData.findIndex(locker => String(locker.lockerId) === String(lockerId));

    if (markerIndex !== -1 && infoWindows[markerIndex]) {
        infoWindows[markerIndex].close();
        console.log(`락커 ID ${lockerId}의 인포윈도우가 닫혔습니다.`);
    }
}


// 특정 락커 위치로 지도 이동하는 함수 (바텀시트 고려)
function moveMapToLocker(lockerId) {
    console.log('moveMapToLocker 호출됨:', lockerId);
    console.log('lockersData:', lockersData);
    console.log('markers:', markers);
    console.log('map:', map);

    // 지도가 초기화되지 않았다면 잠시 대기
    if (!map) {
        console.warn('지도가 초기화되지 않았습니다.');
        setTimeout(() => moveMapToLocker(lockerId), 100);
        return;
    }

    const targetLocker = lockersData.find(locker => String(locker.lockerId) === String(lockerId));
    if (!targetLocker || !targetLocker.latitude || !targetLocker.longitude) {
        console.warn(`락커 ID ${lockerId}의 위치 정보를 찾을 수 없습니다.`);
        return;
    }

    console.log('타겟 락커 찾음:', targetLocker);

    // 마커 인덱스를 찾기 (lockerId 기준)
    const markerIndex = lockersData.findIndex(locker => String(locker.lockerId) === String(lockerId));
    if (markerIndex === -1) {
        console.warn('해당 락커의 마커를 찾을 수 없습니다.');
        return;
    }

    console.log('마커 인덱스:', markerIndex);

    const lat = parseFloat(targetLocker.latitude);
    const lng = parseFloat(targetLocker.longitude);

    // 바텀시트 높이를 고려한 오프셋 계산 (더 정확하게)
    const mapContainer = document.getElementById('map');
    const bottomSheet = document.getElementById('bottomSheet');

    let offsetLat = 0.0001; // 기본값 (작게 설정)

    if (mapContainer && bottomSheet && bottomSheet.classList.contains('fixed')) {
        const mapHeight = mapContainer.offsetHeight;
        const sheetHeight = bottomSheet.offsetHeight;

        // 바텀시트가 차지하는 비율 계산
        const sheetRatio = sheetHeight / mapHeight;

        // 현재 지도 범위 계산
        const bounds = map.getBounds();
        const latRange = bounds.getNorthEast().getLat() - bounds.getSouthWest().getLat();

        // 오프셋을 바텀시트 높이에 비례하여 계산 (더 보수적으로)
        offsetLat = latRange * sheetRatio * -0.2; // 0.35 대신 0.3으로 줄임

        console.log('오프셋 계산:', { mapHeight, sheetHeight, sheetRatio, latRange, offsetLat });
    }

    const adjustedPosition = new kakao.maps.LatLng(lat + offsetLat, lng);

    // 지도 이동 및 줌 레벨 조정
    map.setLevel(3);
    map.panTo(adjustedPosition);

    // 다른 인포윈도우 닫고, 해당 인포윈도우 열기
    infoWindows.forEach(iw => iw.close());

    // 마커와 인포윈도우가 존재하는지 확인 후 열기
    if (markers[markerIndex] && infoWindows[markerIndex]) {
        setTimeout(() => {
            infoWindows[markerIndex].open(map, markers[markerIndex]);
            console.log('인포윈도우 열림');
        }, 300);
    } else {
        console.warn('마커 또는 인포윈도우가 존재하지 않습니다.');
    }
}

function waitForMapInitialization() {
    return new Promise((resolve) => {
        if (map) {
            resolve();
        } else {
            const checkInterval = setInterval(() => {
                if (map) {
                    clearInterval(checkInterval);
                    resolve();
                }
            }, 50);
        }
    });
}

/*
function moveMapToLocker(lockerId) {
    const targetLocker = lockersData.find(locker => String(locker.lockerId) === String(lockerId));

    if (targetLocker && targetLocker.latitude && targetLocker.longitude) {
        const position = new kakao.maps.LatLng(
            parseFloat(targetLocker.latitude),
            parseFloat(targetLocker.longitude)
        );

        // 마커와 인포윈도우 인덱스를 찾기
        const markerIndex = lockersData.findIndex(locker => String(locker.lockerId) === String(lockerId));

        if (markerIndex !== -1 && markers[markerIndex]) {
            // 지도 중심 이동 (바텀시트 고려해서 조금 위로 올리기)
            const offsetLat = 0.002; // 바텀시트에 가려지지 않도록 위로 보정
            const adjustedPosition = new kakao.maps.LatLng(
                position.getLat() + offsetLat,
                position.getLng()
            );

            map.panTo(adjustedPosition);
            map.setLevel(Math.max(map.getLevel() - 2, 1)); // 확대 (줌인)

            // 다른 인포윈도우 닫고 현재 인포윈도우 열기
            infoWindows.forEach(iw => iw.close());
            infoWindows[markerIndex].open(map, markers[markerIndex]);
        }

        console.log(`지도가 ${targetLocker.lockerName} 위치로 이동했습니다.`);
    } else {
        console.warn(`락커 ID ${lockerId}의 위치 정보를 찾을 수 없습니다.`);
    }
}

*/

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

document.addEventListener("DOMContentLoaded", () => {
    loadKakaoScript(initMapAndResults);
});