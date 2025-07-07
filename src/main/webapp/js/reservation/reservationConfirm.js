let reservationData = null;
let isProcessing = false;

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function () {
    loadReservationData();
    setupEventListeners();
});

// 이벤트 리스너 설정
function setupEventListeners() {
    const approveBtn = document.getElementById('approveBtn');
    const rejectBtn = document.getElementById('rejectBtn');

    if (approveBtn) {
        approveBtn.addEventListener('click', () => handleApproveReject('yes'));
    }

    if (rejectBtn) {
        rejectBtn.addEventListener('click', () => handleApproveReject('no'));
    }
}

// 예약 데이터 로드
function loadReservationData() {
    const loadingState = document.getElementById('loadingState');
    const reservationContainer = document.getElementById('reservationContainer');

    // TODO: 실제 API 엔드포인트로 변경 필요
    fetch(`/AirBnG/reservations/${reservationId}/members/${memberId}/detail`)
        .then(response => response.json())
        .then(data => {
            if (data.code === 1000) {
                reservationData = data.result;
                displayReservationData(reservationData);
                console.log(reservationData);
                // 로딩 상태 숨기고 컨테이너 표시
                loadingState.classList.add('hidden');
                reservationContainer.classList.remove('hidden');

                // 상태에 따라 버튼 표시 여부 결정
                showActionButtonsIfNeeded();
            } else {
                handleError(data.message || '예약 정보를 불러올 수 없습니다.');
            }
        })
        .catch(error => {
            console.error('예약 정보 로드 실패:', error);
            handleError('서버 오류가 발생했습니다.');
        });
}

// 예약 데이터 표시
function displayReservationData(data) {
    // 예약자 정보
    document.getElementById('userName').textContent = data.userName || '정보 없음';
    document.getElementById('userPhone').textContent = data.userPhone || '정보 없음';

    // 보관소 정보
    document.getElementById('lockerName').textContent = data.lockerName || '정보 없음';
    document.getElementById('lockerAddress').textContent = data.lockerAddress || '정보 없음';

    // 예약 날짜
    const startDate = new Date(data.startTime);
    const endDate = new Date(data.endTime);
    document.getElementById('reservationDate').textContent = formatDateRange(startDate, endDate);

    // 예약 시간
    document.getElementById('reservationTime').textContent = formatTimeRange(startDate, endDate);

    // 예약 상태
    const statusElement = document.getElementById('reservationStatus');
    statusElement.textContent = getStatusText(data.state);
    statusElement.className = `px-2 py-1 rounded-full text-xs font-medium ${getStatusClass(data.state)}`;

    // 짐 정보
    displayJimTypes(data.jimTypes);

    // 결제 정보
    displayPriceDetails(data.priceDetails);
}

// 짐 타입 정보 표시
function displayJimTypes(jimTypes) {
    const container = document.getElementById('jimTypesList');
    container.innerHTML = '';

    if (!jimTypes || jimTypes.length === 0) {
        container.innerHTML = '<p class="text-gray-500 text-sm">짐 정보가 없습니다.</p>';
        return;
    }

    jimTypes.forEach(jim => {
        const jimItem = document.createElement('div');
        jimItem.className = 'jim-item';
        jimItem.innerHTML = `
            <div>
                <div class="jim-item-name">${jim.typeName}</div>
                <div class="jim-item-count">${jim.count}개</div>
            </div>
            <div class="jim-item-price">${jim.totalPrice.toLocaleString()}원</div>
        `;
        container.appendChild(jimItem);
    });
}

// 가격 상세 정보 표시
function displayPriceDetails(priceDetails) {
    const container = document.getElementById('priceDetailsList');
    container.innerHTML = '';

    if (!priceDetails) return;

    // 각 짐 타입별 가격 표시
    if (priceDetails.itemPrices) {
        priceDetails.itemPrices.forEach(item => {
            const priceItem = document.createElement('div');
            priceItem.className = 'price-item';
            priceItem.innerHTML = `
                <span>${item.typeName} × ${item.count}개 × ${item.hours}시간</span>
                <span>${item.totalPrice.toLocaleString()}원</span>
            `;
            container.appendChild(priceItem);
        });
    }

    // 서비스 수수료
    document.getElementById('serviceFee').textContent =
        priceDetails.serviceFee ? priceDetails.serviceFee.toLocaleString() + '원' : '0원';

    // 총 결제 금액
    document.getElementById('totalPrice').textContent =
        priceDetails.totalPrice ? priceDetails.totalPrice.toLocaleString() + '원' : '0원';
}

// 상태에 따라 액션 버튼 표시
function showActionButtonsIfNeeded() {
    const actionButtons = document.getElementById('actionButtons');
    const bottomButtons = document.getElementById('bottomButtons');

    if (reservationData && reservationData.state === 'PENDING') {
        actionButtons.classList.remove('hidden');
        bottomButtons.classList.remove('hidden');
    }
}

// 승인/거절 처리
function handleApproveReject(approve) {
    if (isProcessing) return;

    const action = approve === 'yes' ? '승인' : '거절';
    const message = `이 예약을 ${action}하시겠습니까?`;

    if (!confirm(message)) return;

    isProcessing = true;
    setButtonsDisabled(true);

    const requestData = {
        approve: approve
    };

    fetch(`/AirBnG/reservations/${reservationId}/members/${memberId}/confirm?approve=${approve}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.code === 1000) {
                const newState = data.result.state;
                const successMessage = approve === 'yes' ? '예약이 승인되었습니다.' : '예약이 거절되었습니다.';

                ModalUtils.showSuccess(successMessage, "", () => {
                    // 상태 업데이트
                    updateReservationState(newState);
                    // 버튼 숨기기
                    hideActionButtons();
                });
            } else {
                handleError(data.message || `예약 ${action}에 실패했습니다.`);
            }
        })
        .catch(error => {
            console.error(`예약 ${action} 요청 실패:`, error);
            handleError('네트워크 오류가 발생했습니다.');
        })
        .finally(() => {
            isProcessing = false;
            setButtonsDisabled(false);
        });
}

// 예약 상태 업데이트
function updateReservationState(newState) {
    const statusElement = document.getElementById('reservationStatus');
    statusElement.textContent = getStatusText(newState);
    statusElement.className = `px-2 py-1 rounded-full text-xs font-medium ${getStatusClass(newState)}`;

    // 예약 데이터 업데이트
    if (reservationData) {
        reservationData.state = newState;
    }
}

// 액션 버튼 숨기기
function hideActionButtons() {
    const actionButtons = document.getElementById('actionButtons');
    const bottomButtons = document.getElementById('bottomButtons');

    actionButtons.classList.add('hidden');
    bottomButtons.classList.add('hidden');
}

// 버튼 비활성화/활성화
function setButtonsDisabled(disabled) {
    const approveBtn = document.getElementById('approveBtn');
    const rejectBtn = document.getElementById('rejectBtn');

    if (approveBtn) {
        approveBtn.disabled = disabled;
        approveBtn.classList.toggle('btn-disabled', disabled);
    }

    if (rejectBtn) {
        rejectBtn.disabled = disabled;
        rejectBtn.classList.toggle('btn-disabled', disabled);
    }
}

// 에러 처리
function handleError(message) {
    const loadingState = document.getElementById('loadingState');
    const reservationContainer = document.getElementById('reservationContainer');

    loadingState.classList.add('hidden');
    reservationContainer.classList.add('hidden');

    ModalUtils.showError(message, "오류", () => {
        history.back();
    });
}

// 날짜 범위 포맷팅
function formatDateRange(startDate, endDate) {
    const startDateStr = startDate.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'short'
    });

    const endDateStr = endDate.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'short'
    });

    if (startDate.toDateString() === endDate.toDateString()) {
        return startDateStr;
    } else {
        return `${startDateStr} ~ ${endDateStr}`;
    }
}

// 시간 범위 포맷팅
function formatTimeRange(startDate, endDate) {
    const startTime = startDate.toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
    });

    const endTime = endDate.toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
    });

    return `${startTime} ~ ${endTime}`;
}

// 상태 텍스트 변환
function getStatusText(state) {
    const statusMap = {
        'PENDING': '승인 대기',
        'CONFIRMED': '승인됨',
        'CANCELLED': '취소됨',
        'COMPLETED': '완료됨'
    };

    return statusMap[state] || '알 수 없음';
}

// 상태 CSS 클래스 반환
function getStatusClass(state) {
    const classMap = {
        'PENDING': 'status-pending',
        'CONFIRMED': 'status-confirmed',
        'CANCELLED': 'status-cancelled',
        'COMPLETED': 'status-completed'
    };

    return classMap[state] || 'status-pending';
}