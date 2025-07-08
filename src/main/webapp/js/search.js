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

function fetchAndRenderLockers(address, jimTypeId, reservationDate) {
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
                return;
            }

            let lockers = data.result.lockers;
            if (jimTypeId && jimTypeId !== "0") {
                lockers = lockers.filter(locker =>
                    locker.jimTypeResults?.some(jtr => String(jtr.jimTypeId) === String(jimTypeId))
                );
            }

            container.innerHTML = "";
            document.querySelector(".sheet-count").textContent = lockers.length;

            lockers.forEach(locker => {
                const div = document.createElement("div");
                div.className = `storage-item ${locker.isAvailable === 'NO' ? 'disabled' : ''}`;
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