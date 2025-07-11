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
            ModalUtils.showWarning('장소와 날짜를 모두 입력해주세요!', '검색 실패');
            return;
        }

        try {
            // 검색 결과 페이지로 GET 파라미터를 붙여서 이동
            const targetUrl = `${contextPath}/page/lockerSearchDetails?address=${encodeURIComponent(address)}&reservationDate=${encodeURIComponent(reservationDate)}&jimTypeId=0`;
            window.location.href = targetUrl;
        } catch (error) {
            console.error("페이지 이동 실패:", error);
            ModalUtils.showError("검색 조건 처리 중 오류가 발생했습니다.", "이동 실패");
        }
    });

    const categoryCards = document.querySelectorAll('.category-card');
    categoryCards.forEach(function (card, index) {
        card.addEventListener('click', function () {
            if (!isLoggedIn) {
                ModalUtils.showConfirm(
                    '로그인 필요!',
                    '짐 타입을 선택하려면 로그인이 필요합니다.\n로그인 페이지로 이동하시겠습니까?',
                    () => {
                        window.location.href = `${contextPath}/page/login`;
                    },
                    () => {}
                );
                return;
            }

            // index 기준: 0=백팩, 1=캐리어, 2=박스, 3=유모차
            if (index === 1) {
                // 캐리어 클릭 시 뒤로가기 히스토리 추가
                history.pushState({ modal: 'carrier' }, '', '#carrier');

                // 캐리어 클릭 시 소형/대형 선택 모달 표시
                ModalUtils.createCustomModal({
                    id: 'carrier-size-modal',
                    type: 'confirm-modal',
                    title: '캐리어 크기 선택',
                    message: '소형 또는 대형을 선택하세요.',
                    confirmText: '대형', // 오른쪽
                    cancelText: '소형',  // 왼쪽
                    showCancel: true,
                    onConfirm: () => {
                        ModalUtils.hideModal('carrier-size-modal');
                        setTimeout(() => {
                            window.location.href = `${contextPath}/page/lockerSearch?jimTypeId=3`; // 대형
                        }, 100);
                    },
                    onCancel: () => {
                        ModalUtils.hideModal('carrier-size-modal');
                        setTimeout(() => {
                            window.location.href = `${contextPath}/page/lockerSearch?jimTypeId=2`; // 소형
                        }, 100);
                    }
                });
            } else {
                const jimTypeIdMap = {
                    0: 1,  // 백팩
                    2: 4,  // 박스
                    3: 5   // 유모차
                };
                const jimTypeId = jimTypeIdMap[index];
                if (jimTypeId) {
                    window.location.href = `${contextPath}/page/lockerSearch?jimTypeId=${jimTypeId}`;
                }
            }
        });
    });

    // popstate 외에: 모달 바깥 클릭 시도 -> 뒤로가기
    document.addEventListener('click', function (e) {
        const modal = document.getElementById('carrier-size-modal');
        if (modal && !modal.classList.contains('hidden')) {
            const isOutside = e.target.classList.contains('modal-overlay');
            if (isOutside) {
                history.back(); // 뒤로가기
            }
        }
    });

    // 뒤로가기 누르면 캐리어 모달 닫기
    window.addEventListener('popstate', function (event) {
        if (ModalUtils.currentModal === 'carrier-size-modal') {
            ModalUtils.hideModal('carrier-size-modal');
            history.replaceState(null, '', location.pathname); // URL #carrier 제거
        }
    });

    fetch(`${contextPath}/lockers/popular`)
        .then(function (response) {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(function (data) {
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

//          sseManager.showBrowserNotification(
//              '새로운 알림',
//              alarmData.message || '새로운 예약 알림이 도착했습니다.',
//              `${contextPath}/images/dot.svg`
//          );
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