class HomeManager {
    constructor() {
        this.sseManager = null;
        this.memberId = document.body.dataset.memberId;
    }

    // 초기화
    init() {
        this.initDatePicker();
        this.initSearchButton();
        this.initCategoryCards();
        this.initPopularLockers();
        this.initNotificationSystem();
    }

    // 날짜 선택기 초기화
    initDatePicker() {
        const realDateInput = document.getElementById("date");
        const dateDisplay = document.getElementById("dateDisplay");

        if (!realDateInput || !dateDisplay) return;

        // 날짜 표시 영역 클릭 시 날짜 선택창 열기
        dateDisplay.addEventListener("click", () => {
            if (realDateInput.showPicker) {
                realDateInput.showPicker();
            } else {
                realDateInput.focus();
                realDateInput.click();
            }
        });

        // 초기 값 설정
        if (realDateInput.value) {
            dateDisplay.textContent = realDateInput.value;
        }

        // 날짜 선택 시 표시 업데이트
        realDateInput.addEventListener("change", () => {
            dateDisplay.textContent = realDateInput.value;
        });
    }

    // 검색 버튼 초기화
    initSearchButton() {
        const findButton = document.querySelector('.find-button');
        if (!findButton) return;

        findButton.addEventListener('click', () => {
            const address = document.getElementById('location').value;
            const reservationDate = document.getElementById('date').value;

            if (!address || !reservationDate) {
                alert('장소와 날짜를 모두 입력해주세요!');
                return;
            }

            try {
                const targetUrl = `${contextPath}/page/lockerSearchDetails?address=${encodeURIComponent(address)}&reservationDate=${encodeURIComponent(reservationDate)}`;
                window.location.href = targetUrl;
            } catch (error) {
                console.error("페이지 이동 실패:", error);
                alert("검색 조건 처리 중 오류가 발생했습니다.");
            }
        });
    }

    // 카테고리 카드 초기화
    initCategoryCards() {
        const categoryCards = document.querySelectorAll('.category-card');
        console.log("카테고리 카드들 로딩됨:", categoryCards);

        categoryCards.forEach((card, index) => {
            card.addEventListener('click', () => {
                const jimTypeId = index + 1;
                window.location.href = `${contextPath}/page/lockerSearch?jimTypeId=${jimTypeId}`;
            });
        });
    }

    // 인기 보관소 초기화
    initPopularLockers() {
        fetch(`${contextPath}/lockers/popular`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("인기 보관소 데이터:", data);
                this.renderPopularLockers(data);
            })
            .catch(error => {
                console.error("인기 보관소 가져오기 실패:", error);
            });
    }

    // 인기 보관소 렌더링
    renderPopularLockers(data) {
        if (data.code === 1000 && data.result && data.result.lockers) {
            const list = data.result.lockers;
            const container = document.getElementById('popularList');
            if (!container) return;

            container.innerHTML = '';

            list.forEach(locker => {
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

                item.addEventListener('click', () => {
                    const targetUrl = `${contextPath}/page/lockerDetails?lockerId=${locker.lockerId}`;
                    window.location.href = targetUrl;
                });

                container.appendChild(item);
            });
        } else {
            console.warn("데이터 형식이 올바르지 않습니다.");
        }
    }

    // 알림 시스템 초기화
    initNotificationSystem() {
        if (!this.memberId || this.memberId === 'null' || this.memberId === '') {
            console.log('로그인하지 않은 사용자 - 알림 시스템 비활성화');
            return;
        }

        console.log("알림 시스템 초기화 시작");

        // 초기 안읽은 알림 상태 확인
        const hasInitialUnreadAlarm = document.getElementById('dotIndicator') !== null;
        console.log("페이지 로드 시 안읽은 알림 상태:", hasInitialUnreadAlarm);

        // SSE 매니저 초기화
        this.sseManager = getSSEManager();

        // 알림 이벤트 리스너 등록
        this.sseManager.addEventListener('alarm', (alarmData) => {
            console.log("📥 알림 수신:", alarmData);
            this.handleAlarmReceived(alarmData);
        });

        // 알림 링크 클릭 이벤트
        this.initNotificationLinkClick();

        // SSE 연결 시작
        this.sseManager.init();
    }

    // 알림 수신 처리
    handleAlarmReceived(alarmData) {
        // dot 표시
        this.showDotIndicator();

        console.log('알림 메시지:', alarmData.message);

        // 브라우저 알림 표시
        this.sseManager.showBrowserNotification(
            '새로운 알림',
            alarmData.message || '새로운 예약 알림이 도착했습니다.',
            `${contextPath}/images/dot.svg`
        );
    }

    // 알림 링크 클릭 이벤트 초기화
    initNotificationLinkClick() {
        const notificationLink = document.querySelector('.notification-link');
        if (notificationLink) {
            notificationLink.addEventListener('click', () => {
                console.log("알림 링크 클릭됨");
                this.hideDotIndicator();
            });
        }
    }

    // dot 표시
    showDotIndicator() {
        console.log("showDotIndicator 호출됨");

        const bellWrapper = document.querySelector('.bell-wrapper');
        if (!bellWrapper) {
            console.error("bell-wrapper 요소를 찾을 수 없습니다.");
            return;
        }

        let dotIndicator = document.getElementById('dotIndicator');

        if (dotIndicator) {
            console.log("기존 dot에 새 알림 효과 적용");
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
            console.log("새 dot 생성 완료");
        }
    }

    // dot 숨기기
    hideDotIndicator() {
        console.log("hideDotIndicator 호출됨");

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

            console.log("dot 숨김 애니메이션 시작");
        }
    }
}

// 전역 홈 매니저 인스턴스
let homeManager = null;

// DOM 로딩 완료 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    homeManager = new HomeManager();
    homeManager.init();
});