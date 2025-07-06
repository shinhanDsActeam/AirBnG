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

    // 알림 표시 로직
    function showDotIndicator() {

        const bellWrapper = document.querySelector('.bell-wrapper');
        if (!bellWrapper) {
            console.error("bell-wrapper 요소를 찾을 수 없습니다.");
            return;
        }

        let dotIndicator = document.getElementById('dotIndicator');

        if (dotIndicator) {
            dotIndicator.style.animation = 'none';
            dotIndicator.offsetHeight; // 강제 리플로우
            dotIndicator.style.animation = 'dot-appear 0.4s ease-out, dot-pulse 2s ease-in-out infinite 0.8s';
            dotIndicator.style.display = 'block';
            dotIndicator.style.visibility = 'visible';
            dotIndicator.style.opacity = '1';

            dotIndicator.classList.remove('hide');
            dotIndicator.classList.add('show');
        } else {
            console.log("새 dot 생성");
            const newDot = document.createElement('img');
            newDot.src = `${contextPath}/images/dot.svg`;
            newDot.alt = '새 알림 표시';
            newDot.className = 'dot-indicator show';
            newDot.id = 'dotIndicator';
            newDot.style.display = 'block';
            newDot.style.visibility = 'visible';
            newDot.style.opacity = '1';
            newDot.style.animation = 'dot-appear 0.4s ease-out, dot-pulse 2s ease-in-out infinite 0.8s';

            bellWrapper.insertBefore(newDot, bellWrapper.firstChild);
        }
    }

    function hideDotIndicator() {

        const dotIndicator = document.getElementById('dotIndicator');
        if (dotIndicator) {
            dotIndicator.classList.remove('show');
            dotIndicator.classList.add('hide');

            dotIndicator.style.opacity = '0';
            dotIndicator.style.transition = 'opacity 0.3s ease-out';

            setTimeout(() => {
                dotIndicator.style.display = 'none';
                dotIndicator.style.visibility = 'hidden';
                dotIndicator.classList.remove('hide');
            }, 300);
        }
    }

    // ======= SSE 매니저를 통한 알림 처리 =======
    const memberId = document.body.dataset.memberId;

    if (memberId && memberId !== 'null' && memberId !== '') {
      const sseManager = getSSEManager();

      // 중복 등록 방지용으로 같은 콜백을 변수에 할당하거나 외부에서 관리하면 좋지만
      // 간단히 이렇게 등록해도 무방함
      sseManager.addEventListener('alarm', (alarmData) => {
          showDotIndicator();

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