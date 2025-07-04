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

//document.addEventListener('DOMContentLoaded', function () {
//    const urlParams = new URLSearchParams(window.location.search);
//    const address = urlParams.get('address');
//    if (address) {
//        const input = document.getElementById('searchInput'); // input의 id가 searchInput일 경우
//        if (input) input.value = decodeURIComponent(address);
//    }
//
//    // 바텀 시트 드래그 기능
//    const sheet = document.getElementById('bottomSheet');
//    const header = document.getElementById('sheetHeader');
//
//    let startY = 0;
//    let isDragging = false;
//
//    const mouseDownHandler = function (e) {
//        startY = e.clientY;
//        isDragging = true;
//        sheet.classList.add("dragging");
//
//        document.addEventListener('mousemove', mouseMoveHandler);
//        document.addEventListener('mouseup', mouseUpHandler);
//    };
//
//    const mouseMoveHandler = function (e) {
//        const debug = document.getElementById("debugOutput");
//        if (debug) {
//            debug.textContent = "현재 Y: " + e.clientY;
//        }
//    };
//
//    const mouseUpHandler = function (e) {
//        if (!isDragging) return;
//
//        const currentY = e.clientY;
//        const deltaY = currentY - startY;
//
//        if (deltaY > 20) {
//            // 조금이라도 내렸으면 바텀시트 내려감
//            sheet.style.transform = "translateX(-50%) translateY(70%)";
//        } else {
//            // 그렇지 않으면 올라감
//            sheet.style.transform = "translateX(-50%) translateY(0%)";
//        }
//
//        isDragging = false;
//        sheet.classList.remove("dragging");
//
//        document.removeEventListener('mousemove', mouseMoveHandler);
//        document.removeEventListener('mouseup', mouseUpHandler);
//    };
//
//    header.addEventListener('mousedown', mouseDownHandler);
//});
//
//// 보관소 리스트 불러오기 (기존 코드 하단에 추가)
//document.addEventListener("DOMContentLoaded", function () {
//    const urlParams = new URLSearchParams(window.location.search);
//    const address = urlParams.get("address");
//
//    if (!address) return;
//
//    fetch(`${contextPath}/lockers?address=${encodeURIComponent(address)}`)
//        .then(response => response.json())
//        .then(data => {
//            console.log("보관소 정보:", data);
//            const container = document.getElementById("lockerList");
//            if (!container) return;
//
//            const lockers = data.result.lockers;
//            if (!lockers || lockers.length === 0) {
//                container.innerHTML = "<p>검색 결과가 없습니다.</p>";
//                return;
//            }
//
//            container.innerHTML = "";
//
//            lockers.forEach(locker => {
//                const div = document.createElement("div");
//                div.className = `storage-item ${locker.isAvailable === 'NO' ? 'disabled' : ''}`;
//                const imageUrl = locker.url != null ? locker.url : contextPath + "/images/default.jpg";
//
//                div.innerHTML = `
//                    <div class="storage-image"
//                         style="background-image: url('${imageUrl}')">
//                    </div>
//                    <div class="storage-info">
//                        <div class="storage-name">${locker.lockerName}</div>
//                        <div class="storage-address">${locker.address}</div>
//                    </div>
//                    <button class="storage-button"
//                            data-available="${locker.isAvailable}"
//                            data-id="${locker.lockerId}">
//                        ${locker.isAvailable === 'YES' ? '보관가능' : '보관대기'}
//                    </button>
//                `;
//                container.appendChild(div);
//            });
//
//            // 클릭 시 선택 스타일과 텍스트 전환
//            container.addEventListener("click", function (e) {
//                const clickedItem = e.target.closest(".storage-item");
//                if (!clickedItem) return;
//
//                if (clickedItem.classList.contains("selected")) return;
//
//                // 기존 선택된 항목 초기화
//                document.querySelectorAll(".storage-item").forEach(item => {
//                    item.classList.remove("selected");
//                    const btn = item.querySelector(".storage-button");
//                    const isAvailable = btn.dataset.available === "YES";
//                    btn.textContent = isAvailable ? "보관가능" : "보관대기";
//                });
//
//                // 클릭된 항목 스타일 변경
//                clickedItem.classList.add("selected");
//                const clickedButton = clickedItem.querySelector(".storage-button");
//                clickedButton.textContent = "상세보기";
//
//                // 상세보기 버튼 클릭 시 페이지 이동
//                clickedButton.onclick = function (e) {
//
//                    if (clickedButton.textContent !== "상세보기") return;
//
//                    const lockerId = clickedButton.dataset.id;
//                    const url = `${contextPath}/page/lockerDetails?lockerId=${encodeURIComponent(lockerId)}`;
//                    window.location.href = url;
//                };
//            });
//
//        })
//        .catch(error => {
//            console.error("보관소 정보를 불러오는 데 실패했습니다:", error);
//        });
//
//});


document.addEventListener("DOMContentLoaded", function () {
    // 👉 주소 입력값 세팅
    const urlParams = new URLSearchParams(window.location.search);
    const address = urlParams.get("address");
    const jimTypeId = urlParams.get("jimTypeId");

    if (address) {
        const input = document.getElementById('searchInput');
        if (input) input.value = decodeURIComponent(address);
    }

    // 👉 바텀시트 드래그 기능
    const sheet = document.getElementById('bottomSheet');
    const header = document.getElementById('sheetHeader');
    let startY = 0;
    let isDragging = false;

    header.addEventListener('mousedown', function (e) {
        startY = e.clientY;
        isDragging = true;
        sheet.classList.add("dragging");

        document.addEventListener('mousemove', mouseMoveHandler);
        document.addEventListener('mouseup', mouseUpHandler);
    });

    function mouseMoveHandler(e) {
        const debug = document.getElementById("debugOutput");
        if (debug) {
            debug.textContent = "현재 Y: " + e.clientY;
        }
    }

    function mouseUpHandler(e) {
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
    }

    // 👉 보관소 리스트 불러오기
    if (address ) {
        let queryString = `address=${encodeURIComponent(address)}`;
        if (jimTypeId) {
            queryString += `&jimTypeId=${encodeURIComponent(jimTypeId)}`;
        }

        fetch(`${contextPath}/lockers?${queryString}`)
            .then(response => response.json())
            .then(data => {
                const container = document.getElementById("lockerList");
                if (!container) return;

                let lockers = data.result.lockers;

                // 👉 jimTypeId가 있으면 필터링
                if (jimTypeId) {
                    lockers = lockers.filter(locker => {
                        console.log("locker", locker);
                        return locker.jimTypeResults?.some(jtr => String(jtr.jimTypeId) === String(jimTypeId));
                    });
                }

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

                // 클릭 시 상세보기로 이동
                container.addEventListener("click", function (e) {
                    const clickedItem = e.target.closest(".storage-item");
                    if (!clickedItem) return;

                    if (clickedItem.classList.contains("selected")) return;

                    document.querySelectorAll(".storage-item").forEach(item => {
                        item.classList.remove("selected");
                        const btn = item.querySelector(".storage-button");
                        const isAvailable = btn.dataset.available === "YES";
                        btn.textContent = isAvailable ? "보관가능" : "보관대기";
                    });

                    clickedItem.classList.add("selected");
                    const clickedButton = clickedItem.querySelector(".storage-button");
                    clickedButton.textContent = "상세보기";

                    clickedButton.onclick = function () {
                        if (clickedButton.textContent !== "상세보기") return;
                        const lockerId = clickedButton.dataset.id;
                        const url = `${contextPath}/page/lockerDetails?lockerId=${encodeURIComponent(lockerId)}`;
                        window.location.href = url;
                    };
                });

            })
            .catch(error => {
                console.error("보관소 정보를 불러오는 데 실패했습니다:", error);
            });
    }
});

function dropdown() {
    const dropdownMenu = document.getElementById("bag-dropdown");
        if (dropdownMenu) {
            dropdownMenu.classList.toggle("hidden");
        }
}

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

    // 주소 파라미터 갱신
    const url = new URL(window.location.href);
    if (jimTypeId === 0) {
        url.searchParams.delete("jimTypeId");
    } else {
        url.searchParams.set("jimTypeId", jimTypeId);
    }

    window.location.href = url.toString();
}
