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




//document.addEventListener("DOMContentLoaded", function () {
//    // 👉 주소 입력값 세팅
//    const urlParams = new URLSearchParams(window.location.search);
//    const address = urlParams.get("address");
//    const jimTypeId = urlParams.get("jimTypeId");
//
//    if (address) {
//        const input = document.getElementById('searchInput');
//        if (input) input.value = decodeURIComponent(address);
//
//        fetchAndRenderLockers(address, jimTypeId);
//    }
//
//    // 👉 바텀시트 드래그 기능
//    const sheet = document.getElementById('bottomSheet');
//    const header = document.getElementById('sheetHeader');
//    let startY = 0;
//    let isDragging = false;
//
//    header.addEventListener('mousedown', function (e) {
//        startY = e.clientY;
//        isDragging = true;
//        sheet.classList.add("dragging");
//
//        document.addEventListener('mousemove', mouseMoveHandler);
//        document.addEventListener('mouseup', mouseUpHandler);
//    });
//
//    function mouseMoveHandler(e) {
//        const debug = document.getElementById("debugOutput");
//        if (debug) {
//            debug.textContent = "현재 Y: " + e.clientY;
//        }
//    }
//
//    function mouseUpHandler(e) {
//        if (!isDragging) return;
//        const currentY = e.clientY;
//        const deltaY = currentY - startY;
//
//        if (deltaY > 20) {
//            sheet.style.transform = "translateX(-50%) translateY(70%)";
//            sheet.classList.remove("fixed");
//        } else {
//            sheet.style.transform = "translateX(-50%) translateY(0%)";
//            sheet.classList.add("fixed");
//        }
//
//        isDragging = false;
//        sheet.classList.remove("dragging");
//
//        document.removeEventListener('mousemove', mouseMoveHandler);
//        document.removeEventListener('mouseup', mouseUpHandler);
//    }
//
//    // 👉 보관소 리스트 불러오기
//    if (address ) {
//        let queryString = `address=${encodeURIComponent(address)}`;
//
//        if (jimTypeId && jimTypeId !== "0") {
//                    queryString += `&jimTypeId=${jimTypeId}`;
//        }
//
//        fetch(`${contextPath}/lockers?${queryString}`)
//            .then(response => {
//                const contentType = response.headers.get("content-type");
//
//                if (contentType && contentType.includes("application/json")) {
//                    return response.json();
//                    } else {
//                         // JSON이 아닌 경우 텍스트로 받아서 에러 처리
//                         return response.text().then(text => {
//                            console.error("JSON이 아닌 응답:", text);
//                            throw new Error("서버에서 잘못된 응답을 반환했습니다.");
//                         });
//                    }
//               })
//
//            .then(data => {
//                const container = document.getElementById("lockerList");
//                if (!container) return;
//
//
//                if (data.code === 3001) {
//                    container.innerHTML = `
//                        <div class="no-result-wrapper">
//                            <img class="search-warning" src="${contextPath}/images/danger.svg" alt="검색결과없음">
//                            <p class="no-result-main">검색 결과가 없습니다</p>
//                            <p class="no-result-sub">다른 위치나 키워드로 다시 시도해보세요!</p>
//                        </div>
//                    `;
//
//                    const countSpan = document.querySelector(".sheet-count");
//                    if (countSpan) countSpan.textContent = "0";
//
//                    // 바텀시트 강제 열기
//                    const sheet = document.getElementById("bottomSheet");
//                    if (sheet) {
//                        sheet.style.transform = "translateX(-50%) translateY(0%)";
//                    }
//                    return;
//                }
//
//                let lockers = data.result?.lockers || [];
//
//                // 👉 jimTypeId가 있으면 필터링
//                if (jimTypeId !== null && jimTypeId !== undefined && jimTypeId !== "0") {
//                    lockers = lockers.filter(locker => {
//                        console.log("locker", locker);
//                        return locker.jimTypeResults?.some(jtr => String(jtr.jimTypeId) === String(jimTypeId));
//                    });
//
//                    const countSpan = document.querySelector(".sheet-count");
//                    if (countSpan) countSpan.textContent = "0";
//
//                    const sheet = document.getElementById("bottomSheet");
//                    if (sheet) {
//                        sheet.style.transform = "translateX(-50%) translateY(0%)";
//                    }
//                }
//
//                if (!lockers || lockers.length === 0) {
//                    container.innerHTML = `
//                        <div class="no-result-wrapper">
//                            🔍 <span class="no-result-text">검색 결과가 없습니다.</span>
//                        </div>
//                    `;
//                    // 검색 결과 수를 0으로 업데이트
//                    const countSpan = document.querySelector(".sheet-count");
//                    if (countSpan) countSpan.textContent = "0";
//
//                    const sheet = document.getElementById("bottomSheet");
//                        if (sheet) {
//                            sheet.style.transform = "translateX(-50%) translateY(0%)";
//                        }
//
//                        return;
//                    }
//
//                const countSpan = document.querySelector(".sheet-count");
//                if (countSpan) countSpan.textContent = lockers.length.toString();
//
//
//                container.innerHTML = "";
//
//                lockers.forEach(locker => {
//                    const div = document.createElement("div");
//                    div.className = `storage-item ${locker.isAvailable === 'NO' ? 'disabled' : ''}`;
//                    const imageUrl = locker.url != null ? locker.url : contextPath + "/images/default.jpg";
//
//                    div.innerHTML = `
//                        <div class="storage-image"
//                             style="background-image: url('${imageUrl}')">
//                        </div>
//                        <div class="storage-info">
//                            <div class="storage-name">${locker.lockerName}</div>
//                            <div class="storage-address">${locker.address}</div>
//                        </div>
//                        <button class="storage-button"
//                                data-available="${locker.isAvailable}"
//                                data-id="${locker.lockerId}">
//                            ${locker.isAvailable === 'YES' ? '보관가능' : '보관대기'}
//                        </button>
//                    `;
//                    container.appendChild(div);
//                });
//
//                // 클릭 시 상세보기로 이동
//                container.addEventListener("click", function (e) {
//                    const clickedItem = e.target.closest(".storage-item");
//                    if (!clickedItem || clickedItem.classList.contains("disabled")) return;
//
//                    //if (clickedItem.classList.contains("selected") || clickedItem.classList.contains("disabled")) return;
//
//                    const clickedButton = clickedItem.querySelector(".storage-button");
//                        const isAvailable = clickedButton.dataset.available === "YES";
//
//                        if (clickedItem.classList.contains("selected")) {
//                            // 👉 이미 선택되어 있으면 선택 해제
//                            clickedItem.classList.remove("selected");
//                            clickedButton.textContent = isAvailable ? "보관가능" : "보관대기";
//                            clickedButton.onclick = null; // 상세보기로 가는 이벤트 제거
//                            return;
//                        }
//
//                    document.querySelectorAll(".storage-item").forEach(item => {
//                        item.classList.remove("selected");
//                        const btn = item.querySelector(".storage-button");
//                        const isAvailable = btn.dataset.available === "YES";
//                        btn.textContent = isAvailable ? "보관가능" : "보관대기";
//                        btn.onclick = null; // 기존 이벤트 제거
//                    });
//
//                    clickedItem.classList.add("selected");
//                    //const clickedButton = clickedItem.querySelector(".storage-button");
//                    clickedButton.textContent = "상세보기";
//
//                    clickedButton.onclick = function () {
//                        if (clickedButton.textContent !== "상세보기") return;
//                        const lockerId = clickedButton.dataset.id;
//                        const url = `${contextPath}/page/lockerDetails?lockerId=${encodeURIComponent(lockerId)}`;
//                        window.location.href = url;
//                    };
//                });
//
//            })
//            .catch(error => {
//                console.error("보관소 정보를 불러오는 데 실패했습니다:", error);
//                const container = document.getElementById("lockerList");
//                                if (container) {
//                                    container.innerHTML = `
//                                        <div class="no-result-wrapper">
//                                            <img class="search-warning" src="${pageContext.request.contextPath}/images/danger.svg" alt="검색결과없음">
//                                            <p class="no-result-main">검색 중 오류가 발생했습니다</p>
//                                            <p class="no-result-sub">잠시 후 다시 시도해주세요!</p>
//                                        </div>
//                                    `;
//                                }
//
//                                const countSpan = document.querySelector(".sheet-count");
//                                if (countSpan) countSpan.textContent = "0";
//                            });
//    }
//});
//
//function dropdown() {
//    const dropdownMenu = document.getElementById("bag-dropdown");
//        if (dropdownMenu) {
//            dropdownMenu.classList.toggle("hidden");
//        }
//}
//
//function selectBagType(jimTypeId) {
//    const typeMap = {
//        0: '모든 짐',
//        1: '백팩/가방',
//        2: '캐리어',
//        3: '박스/큰 짐',
//        4: '유모차'
//    };
//
//    document.getElementById("selectedBagType").textContent = typeMap[jimTypeId];
//    document.getElementById("bag-dropdown").classList.add("hidden");
//
//    // 주소 파라미터 갱신
//    const url = new URL(window.location.href);
//    const address = url.searchParams.get("address");
//
//    if (!address) {
//        alert("주소 정보가 없습니다. 다시 검색해주세요.");
//        return;
//    }
//
//        // ✅ reservationDate 없거나 비어 있으면 오늘 날짜로 대체
//        let reservationDate = url.searchParams.get("reservationDate");
//        if (!reservationDate || reservationDate.trim() === "") {
//            const today = new Date().toISOString().split("T")[0];
//            url.searchParams.set("reservationDate", today);
//        }
//
//    if (jimTypeId === 0) {
//        //url.searchParams.delete("jimTypeId");
//        url.searchParams.set("jimTypeId", 0);
//    } else {
//        url.searchParams.set("jimTypeId", jimTypeId);
//    }
//
//    window.location.href = url.toString();
//}


// ✅ 공통: 락커 목록 가져와서 렌더링하는 함수
function fetchAndRenderLockers(address, jimTypeId, reservationDate) {
    let queryString = `address=${encodeURIComponent(address)}`;

    if (jimTypeId && jimTypeId !== "0") {
        queryString += `&jimTypeId=${jimTypeId}`;
    }

    if (reservationDate) {
        queryString += `&reservationDate=${reservationDate}`;
    }

    fetch(`${contextPath}/lockers?${queryString}`)
        .then(response => {
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
                return response.json();
            } else {
                return response.text().then(text => {
                    console.error("JSON이 아닌 응답:", text);
                    throw new Error("서버에서 잘못된 응답을 반환했습니다.");
                });
            }
        })
        .then(data => {
            const container = document.getElementById("lockerList");
            if (!container) return;

            if (data.code === 3001) {
                container.innerHTML = `
                    <div class="no-result-wrapper">
                        <img class="search-warning" src="${contextPath}/images/danger.svg" alt="검색결과없음">
                        <p class="no-result-main">검색 결과가 없습니다</p>
                        <p class="no-result-sub">다른 위치나 키워드로 다시 시도해보세요!</p>
                    </div>
                `;

                document.querySelector(".sheet-count").textContent = "0";
                const sheet = document.getElementById("bottomSheet");
                sheet.style.transform = "translateX(-50%) translateY(0%)";
                sheet.classList.add("fixed");
                return;
            }

            let lockers = data.result?.lockers || [];

            if (jimTypeId && jimTypeId !== "0") {
                lockers = lockers.filter(locker =>
                    locker.jimTypeResults?.some(jtr => String(jtr.jimTypeId) === String(jimTypeId))
                );
            }

            if (!lockers.length) {
                container.innerHTML = `
                    <div class="no-result-wrapper">
                        🔍 <span class="no-result-text">검색 결과가 없습니다.</span>
                    </div>
                `;
                document.querySelector(".sheet-count").textContent = "0";
                const sheet = document.getElementById("bottomSheet");
                sheet.style.transform = "translateX(-50%) translateY(0%)";
                sheet.classList.add("fixed");
                return;
            }

            document.querySelector(".sheet-count").textContent = lockers.length;
            container.innerHTML = "";

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

            const sheet = document.getElementById("bottomSheet");
            sheet.style.transform = "translateX(-50%) translateY(0%)";
            sheet.classList.add("fixed");
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

// ✅ 상세보기 버튼 로직 묶기
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
            const isAvailable = btn.dataset.available === "YES";
            btn.textContent = isAvailable ? "보관가능" : "보관대기";
            btn.onclick = null;
        });

        clickedItem.classList.add("selected");
        clickedButton.textContent = "상세보기";

        clickedButton.onclick = function () {
            if (clickedButton.textContent !== "상세보기") return;
            const lockerId = clickedButton.dataset.id;
            window.location.href = `${contextPath}/page/lockerDetails?lockerId=${encodeURIComponent(lockerId)}`;
        };
    });
}

// ✅ 짐 타입 선택 시 URL만 바꾸고 데이터 다시 불러오기
function selectBagType(jimTypeId) {
    const typeMap = {
        0: '모든 짐',
        1: '백팩/가방',
        2: '캐리어',
        3: '박스/큰 짐',
        4: '유모차'
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

// ✅ 페이지 초기화

document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("searchInput");

    if (searchInput) {
        searchInput.addEventListener("focus", function () {
            // input 클릭(포커스) 시 이동
            window.location.href = `${contextPath}/page/lockerSearch?jimTypeId=0`;
        });
    }

    const urlParams = new URLSearchParams(window.location.search);
    const address = urlParams.get("address");
    const jimTypeId = urlParams.get("jimTypeId");
    const reservationDate = urlParams.get("reservationDate");

    const sheet = document.getElementById('bottomSheet');
    const header = document.getElementById('sheetHeader');
    let startY = 0;
    let isDragging = false;

    // 페이지가 로딩될 때는 바텀시트 초기 상태로 내려가 있게 설정
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
        const currentY = e.clientY;
        const deltaY = currentY - startY;

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

    if (address) {
        const input = document.getElementById('searchInput');
        if (input) input.value = decodeURIComponent(address);
        fetchAndRenderLockers(address, jimTypeId, reservationDate);
    }
});

function dropdown() {
    const dropdownMenu = document.getElementById("bag-dropdown");
    const sheet = document.getElementById("bottomSheet");
    if (!sheet.classList.contains("fixed")) return; // 바텀시트가 올라와 있을 때만 허용
    if (dropdownMenu) {
        dropdownMenu.classList.toggle("hidden");
    }
}
