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
    const confirmDoneBtn = document.getElementById('confirmDoneBtn');

    if (approveBtn) {
        approveBtn.addEventListener('click', () => handleApproveReject('yes'));
    }

    if (rejectBtn) {
        rejectBtn.addEventListener('click', () => handleApproveReject('no'));
    }

    if (confirmDoneBtn) {
        confirmDoneBtn.addEventListener('click', () => {
            // history.back(); // 또는 location.href = '...' 로 특정 페이지 이동
            location.href= document.referrer || '/AirBnG/page/reservations/list';
        });
    }
}

// 예약 데이터 로드
function loadReservationData() {
    const loadingState = document.getElementById('loadingState');
    const reservationContainer = document.getElementById('reservationContainer');

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
    // 보관소 정보 (세션 스토리지에서 가져오기)
    const lockerData = JSON.parse(sessionStorage.getItem(`reservationData_${reservationId}`) || '{}');

    document.getElementById('keeperNickname').textContent = lockerData.address || '정보 없음';
    document.getElementById('lockerAddress').textContent = lockerData.addressDetail || '주소 정보 없음';

    // 보관소 이미지 설정
    const lockerImage = document.getElementById('lockerImage');
    if (lockerData.lockerImage) {
        lockerImage.src = lockerData.lockerImage;
    } else {
        lockerImage.src = '/AirBnG/images/user.svg';
    }

    // 예약 날짜
    const startDate = new Date(data.startTime);
    const endDate = new Date(data.endTime);
    document.getElementById('reservationDate').textContent = formatDateRange(startDate, endDate);

    // 예약 시간
    document.getElementById('reservationTime').textContent = formatTimeRange(startDate, endDate);

    // 짐 정보
    displayJimTypes(data.reservationJimTypes);

    // 결제 정보
    displayPriceDetails(data.reservationJimTypes);

    // 안내 문구 표시 여부 결정
    const notice = document.getElementById('autoApproveNotice');
    if (data.state === 'PENDING') {
        notice.classList.remove('hidden');
    } else {
        notice.classList.add('hidden');
    }
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
        jimItem.className = 'flex items-center';
        jimItem.innerHTML = `
            <img src="${contextPath}/images/box_ic.svg" alt="짐 아이콘" class="w-5 h-5 mr-2">
            <span class="text-gray-900">${jim.typeName} ${jim.count}개</span>
        `;
        container.appendChild(jimItem);
    });
}

// 가격 상세 정보 표시
function displayPriceDetails(jimTypes) {
    const container = document.getElementById('priceDetailsList');
    container.innerHTML = '';

    if (!jimTypes || jimTypes.length === 0) return;

    let totalAmount = 0;
    const serviceFee = 400;

    // 시간 계산 (공통)
    const startTime = new Date(reservationData.startTime);
    const endTime = new Date(reservationData.endTime);
    const diffMinutes = (endTime - startTime) / (1000 * 60);
    const hours = diffMinutes / 60;

    jimTypes.forEach(item => {
        const { typeName, count, pricePerHour } = item;

        // 단일 짐 총액 계산
        const itemTotal = pricePerHour * count * hours;
        totalAmount += itemTotal;

        const priceItem = document.createElement('div');
        priceItem.className = 'flex justify-between text-sm text-gray-600';
        priceItem.innerHTML = `
            <span>${typeName} × ${count}개 × ${formatHours(hours)}</span>
            <span>${Math.round(itemTotal).toLocaleString()}원</span>
        `;
        container.appendChild(priceItem);
    });

    // 총 결제 금액 (서비스 수수료 포함)
    const finalTotal = Math.round(totalAmount + serviceFee);
    document.getElementById('totalPrice').textContent = finalTotal.toLocaleString() + '원';
}

// 시간 포맷팅 함수
function formatHours(hours) {
    if (hours < 1) {
        return `${Math.round(hours * 60)}분`;
    } else if (hours === parseInt(hours)) {
        return `${hours}시간`;
    } else {
        const wholeHours = Math.floor(hours);
        const minutes = Math.round((hours - wholeHours) * 60);
        return `${wholeHours}시간${minutes}분`;
    }
}

// 상태에 따라 액션 버튼 표시
function hideActionButtons() {
    const bottomButtons = document.getElementById('bottomButtons');
    bottomButtons.classList.add('hidden');
}
//
function showConfirmedButton() {
    const confirmedBtnContainer = document.getElementById('confirmedButton');
    confirmedBtnContainer.classList.remove('hidden');
}

function handleApproveReject(approve) {
    if (isProcessing) return;

    const action = approve === 'yes' ? '승인' : '거절';
    const message = `이 예약을 ${action}하시겠습니까?`;

    ModalUtils.showConfirm(
        message,                   // 본문
        "예약 확인",                // 제목
        () => {
            // 확인 버튼 클릭 시 실행될 콜백
            isProcessing = true;
            setButtonsDisabled(true);

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
                            updateReservationState(newState);
                            hideActionButtons();
                            showConfirmedButton();
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
        },
        () => {
            // 취소 버튼 클릭 시 아무 동작 안 함
        }
    );
}
// 예약 상태 업데이트
function updateReservationState(newState) {
    // 예약 데이터 업데이트
    if (reservationData) {
        reservationData.state = newState;
    }
}
function showActionButtonsIfNeeded() {
    const bottomButtons = document.getElementById('bottomButtons');
    const confirmedBtnContainer = document.getElementById('confirmedButton');

    if (reservationData) {
        if (reservationData.state === 'PENDING') {
            bottomButtons.classList.remove('hidden');
            confirmedBtnContainer.classList.add('hidden');
        } else {
            bottomButtons.classList.add('hidden');
            confirmedBtnContainer.classList.remove('hidden');
        }
    }
}
// 액션 버튼 숨기기
function hideActionButtons() {
    // const actionButtons = document.getElementById('actionButtons');
    const bottomButtons = document.getElementById('bottomButtons');

    // actionButtons.classList.add('hidden');
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
        month: '2-digit',
        day: '2-digit'
    }).replace(/\./g, '.').replace(/\s/g, '');

    const endDateStr = endDate.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    }).replace(/\./g, '.').replace(/\s/g, '');

    if (startDate.toDateString() === endDate.toDateString()) {
        return startDateStr.slice(0, -1); // 마지막 점 제거
    } else {
        return `${startDateStr.slice(0, -1)} ~ ${endDateStr.slice(0, -1)}`;
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
        'PENDING': '대기중',
        'CONFIRMED': '확정',
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