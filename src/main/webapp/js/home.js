document.addEventListener('DOMContentLoaded', function () {

    const realDateInput = document.getElementById("date");
    const dateDisplay = document.getElementById("dateDisplay");

    // 날짜 표시 영역 클릭 시 → 날짜 선택창 열기
    dateDisplay.addEventListener("click", function () {
        if (realDateInput.showPicker) {
            realDateInput.showPicker();  // 최신 브라우저 (크롬 등)
        } else {
            realDateInput.focus();       // fallback
            realDateInput.click();
        }
    });

    // 초기 값 설정 (페이지 로드 시)
    if (realDateInput.value) {
        dateDisplay.textContent = realDateInput.value;
    }

    // 날짜 선택 시 표시 업데이트
    realDateInput.addEventListener("change", function () {
        dateDisplay.textContent = realDateInput.value;
    });

    const findButton = document.querySelector('.find-button');
    findButton.addEventListener('click', function () {
        const address = document.getElementById('location').value;
        const reservationDate = document.getElementById('date').value;

        if (!address || !reservationDate) {
            ModalUtils.showWarning('장소와 날짜를 모두 입력해주세요!', '검색 실패');
            return;
        }

        try {
            // 검색 결과 페이지로 GET 파라미터를 붙여서 이동
            const targetUrl = `${contextPath}/page/lockerSearchDetails?address=${encodeURIComponent(address)}&reservationDate=${encodeURIComponent(reservationDate)}`;
            window.location.href = targetUrl;
        } catch (error) {
            console.error("페이지 이동 실패:", error);
            alert("검색 조건 처리 중 오류가 발생했습니다.");
        }
    });

    console.log("카테고리 카드들 로딩됨:", document.querySelectorAll('.category-card'));

    const categoryCards = document.querySelectorAll('.category-card');
    categoryCards.forEach(function (card, index) {
        card.addEventListener('click', function () {
            const jimTypeId = index + 1; // 1~4

            // reservation.jsp로 이동 (짐 타입 ID 전달)
            window.location.href = `${contextPath}/page/lockerSearch?jimTypeId=${jimTypeId}`;
        });
    });

    fetch(`${contextPath}/lockers/popular`)
        .then(function (response) {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(function (data) {
            console.log("인기 보관소 데이터:", data);

            if (data.code === 1000 && data.result && data.result.lockers) {
                const list = data.result.lockers;
                const container = document.getElementById('popularList');
                container.innerHTML = '';

                list.forEach(function (locker) {
                    const item = document.createElement('div');
                    item.className = 'popular-item';
                    item.innerHTML = `
                        <div class="thumb">
                            <img src="${locker.url}" alt="${locker.lockerName}" />
                        </div>
                        <div class="info">
                            <div class="locker-name">${locker.lockerName}</div>
                            <div class="locker-address">${locker.address}</div>
                        </div>
                    `;

                    //console.log(locker.lockerId);

                    item.addEventListener('click', function () {
                        const targetUrl = `${contextPath}/page/lockerDetails?lockerId=${locker.lockerId}`;
                        window.location.href = targetUrl;
                    });
                    container.appendChild(item);
                });
            } else {
                console.warn("데이터 형식이 올바르지 않습니다.");
            }
        })
        .catch(function (error) {
            console.error("인기 보관소 가져오기 실패:", error);
        });
});