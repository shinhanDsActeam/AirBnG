document.addEventListener('DOMContentLoaded', function () {

    const realDateInput = document.getElementById("date");
    const dateDisplay = document.getElementById("dateDisplay");

    // 날짜 표시 영역 클릭 시 → 날짜 선택창 열기
    dateDisplay.addEventListener("click", function () {
        if (realDateInput.showPicker) {
            realDateInput.showPicker();  // 최신 브라우저 (크롬 등)
        } else {
            realDateInput.focus();
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
            alert('장소와 날짜를 모두 입력해주세요!');
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

    // ======= SSE 매니저를 통한 알림 처리 =======
    const memberId = document.body.dataset.memberId;

    if (memberId && memberId !== 'null' && memberId !== '') {
      const sseManager = getSSEManager();

      sseManager.addEventListener('alarm', (alarmData) => {
          showDotIndicator(contextPath);

          console.log('알림 메시지:', alarmData.message);

          sseManager.showBrowserNotification(
              '새로운 알림',
              alarmData.message || '새로운 예약 알림이 도착했습니다.',
              `${contextPath}/images/dot.svg`
          );
      });

      sseManager.onConnectionStatusChange((connected) => {
          console.log('home.js - SSE 연결 상태 변경:', connected ? '연결됨' : '연결 끊김');
      });

      sseManager.requestNotificationPermission().then((permission) => {
          console.log('브라우저 알림 권한:', permission);
      });
    }

    // 알림 링크 클릭 시 dot 숨기기 (필요 시)
    const notificationLink = document.querySelector('.notification-link');
    if (notificationLink) {
      notificationLink.addEventListener('click', () => {
          console.log('알림 링크 클릭됨');
          hideDotIndicator();
      });
    }
});