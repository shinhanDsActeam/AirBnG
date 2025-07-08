let reservationData = null;
let isProcessing = false;

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function () {
    loadReservationData();
    setupEventListeners();
});

// 이벤트 리스너 설정
function setupEventListeners() {
    // 이벤트 위임 방식으로 변경하여 동적으로 생성되는 요소에도 이벤트 적용
    document.addEventListener('click', function(event) {
        const target = event.target;

        if (target.id === 'cancelBtn') {
            event.preventDefault();
            handleCancel();
        } else if (target.id === 'confirmDoneBtn') {
            event.preventDefault();
            // 예약 목록으로 이동
            location.href = '/AirBnG/page/reservations/list';
        } else if (target.id === 'cancelledConfirmBtn') {
            event.preventDefault();
            // 취소 완료 후 예약 목록으로 이동
            location.href = '/AirBnG/page/reservations/list';
        }
    });
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
    const dateElements = document.querySelectorAll('#reservationDate');
    dateElements.forEach(element => {
        element.textContent = formatDateRange(startDate, endDate);
    });

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

// 예약 취소 처리
function handleCancel() {
    if (isProcessing) return;

    console.log('취소 버튼 클릭됨'); // 디버깅용

    // 취소 확인 모달 표시
    if (typeof ModalUtils !== 'undefined') {
        ModalUtils.showConfirm(
            '취소 시 수수료가 발생할 수 있어요!',
            '예약을 취소하시겠습니까?',
            () => {
                // 확인 버튼 클릭 시 실제 취소 처리
                isProcessing = true;
                setButtonsDisabled(true);
                cancelReservation();
            },
            () => {
                // 취소 버튼 클릭 시 아무것도 하지 않음
                console.log('취소 확인 모달에서 취소 선택됨');
            }
        );
    } else {
        // ModalUtils가 없는 경우 기본 confirm 사용
        if (confirm('취소 시 수수료가 발생할 수 있어요!')) {
            isProcessing = true;
            setButtonsDisabled(true);
            cancelReservation();
        }
    }
}

// 예약 취소 API 호출
async function cancelReservation() {
    try {
        const response = await fetch(`${contextPath}/reservations/${reservationId}/members/${memberId}/cancel`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        if (data.code === 1000) {
            const chargeAmount = data.result.charge || 0;
            const message = chargeAmount > 0
                ? `${chargeAmount.toLocaleString()}원의 수수료가 발생했어요!`
                : '예약이 취소되었습니다.';

            // 예약 상태를 취소됨으로 업데이트
            updateReservationState('CANCELLED');

            // 취소 완료 버튼 표시
            showCancelledButton();

            if (typeof ModalUtils !== 'undefined') {
                ModalUtils.showSuccess(message, '예약 취소 완료');
            } else {
                alert(message);
            }
        } else {
            const errorMsg = data.message || '예약 취소에 실패했습니다.';
            if (typeof ModalUtils !== 'undefined') {
                ModalUtils.showError(errorMsg, '예약 취소 실패');
            } else {
                alert(errorMsg);
            }
        }
    } catch (error) {
        console.error('예약 취소 실패:', error);
        const errorMsg = '네트워크 오류가 발생했습니다.';
        if (typeof ModalUtils !== 'undefined') {
            ModalUtils.showError(errorMsg, '예약 취소 실패');
        } else {
            alert(errorMsg);
        }
    } finally {
        isProcessing = false;
        setButtonsDisabled(false);
    }
}

// 예약 상태 업데이트
function updateReservationState(newState) {
    // 예약 데이터 업데이트
    if (reservationData) {
        reservationData.state = newState;
    }
}

// 취소 완료 버튼 표시
function showCancelledButton() {
    const bottomButtons = document.getElementById('bottomButtons');
    const confirmedBtnContainer = document.getElementById('confirmedButton');
    const cancelledBtnContainer = document.getElementById('cancelledButton');

    // DOM 요소가 존재하는지 확인
    if (!bottomButtons || !confirmedBtnContainer || !cancelledBtnContainer) {
        console.error('필요한 DOM 요소를 찾을 수 없습니다:', {
            bottomButtons: !!bottomButtons,
            confirmedBtnContainer: !!confirmedBtnContainer,
            cancelledBtnContainer: !!cancelledBtnContainer
        });
        return;
    }

    // 기존 버튼들 숨기기
    bottomButtons.classList.add('hidden');
    confirmedBtnContainer.classList.add('hidden');

    // 취소 완료 버튼 표시
    cancelledBtnContainer.classList.remove('hidden');
}

function showActionButtonsIfNeeded() {
    const bottomButtons = document.getElementById('bottomButtons');
    const confirmedBtnContainer = document.getElementById('confirmedButton');
    const cancelledBtnContainer = document.getElementById('cancelledButton');

    // DOM 요소가 존재하는지 확인
    if (!bottomButtons || !confirmedBtnContainer || !cancelledBtnContainer) {
        console.error('필요한 DOM 요소를 찾을 수 없습니다:', {
            bottomButtons: !!bottomButtons,
            confirmedBtnContainer: !!confirmedBtnContainer,
            cancelledBtnContainer: !!cancelledBtnContainer
        });
        return;
    }

    if (reservationData) {
        if (reservationData.state === 'PENDING') {
            bottomButtons.classList.remove('hidden');
            confirmedBtnContainer.classList.add('hidden');
            cancelledBtnContainer.classList.add('hidden');
        } else if (reservationData.state === 'CONFIRMED') {
            bottomButtons.classList.add('hidden');
            confirmedBtnContainer.classList.remove('hidden');
            cancelledBtnContainer.classList.add('hidden');
        } else if (reservationData.state === 'CANCELLED') {
            bottomButtons.classList.add('hidden');
            confirmedBtnContainer.classList.add('hidden');
            cancelledBtnContainer.classList.remove('hidden');
        } else {
            bottomButtons.classList.add('hidden');
            confirmedBtnContainer.classList.add('hidden');
            cancelledBtnContainer.classList.add('hidden');
        }
    }
}

// 버튼 비활성화/활성화
function setButtonsDisabled(disabled) {
    const cancelBtn = document.getElementById('cancelBtn');

    if (cancelBtn) {
        cancelBtn.disabled = disabled;
        cancelBtn.classList.toggle('btn-disabled', disabled);
        if (disabled) {
            cancelBtn.classList.add('opacity-50', 'cursor-not-allowed');
        } else {
            cancelBtn.classList.remove('opacity-50', 'cursor-not-allowed');
        }
    }
}

// 에러 처리
function handleError(message) {
    const loadingState = document.getElementById('loadingState');
    const reservationContainer = document.getElementById('reservationContainer');

    loadingState.classList.add('hidden');
    reservationContainer.classList.add('hidden');

    if (typeof ModalUtils !== 'undefined') {
        ModalUtils.showError(message, "오류", () => {
            history.back();
        });
    } else {
        history.back();
    }
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

// SSE 초기화
function initSSE() {
    if (!memberId || memberId === 'null' || memberId === '') {
        console.log('로그인된 회원이 아니어서 SSE 연결하지 않습니다.');
        return;
    }

    const sseManager = getSSEManager();

    sseManager.addEventListener('alarm', (alarmData) => {
        console.log('알림 메시지:', alarmData.message);
        showDotIndicator();

        if (typeof getNotificationSSE === 'function') {
                const notificationManager = getNotificationSSE();
                if (notificationManager) {
                      notificationManager.handleNotification(alarmData);
                }
            }

        // 예약 상세 페이지에서 상태 변경 알림 수신 시 상세 데이터 갱신
        if (alarmData.type === 'STATE_CHANGE' && alarmData.reservationId == reservationId) {
            console.log('예약 상태 변경 알림 감지 - 상세 데이터 갱신');
            fetchReservationDetail();
        }

    });

    sseManager.onConnectionStatusChange((connected) => {
        console.log('SSE 연결 상태:', connected ? '연결됨' : '연결 끊김');
    });

    sseManager.requestNotificationPermission().then((permission) => {
        console.log('브라우저 알림 권한:', permission);
    });
}

// 페이지 로드 시 데이터 가져오기
document.addEventListener('DOMContentLoaded', () => {
    initSSE();
});