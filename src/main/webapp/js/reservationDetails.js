// 서버에서 전달받은 데이터 사용
const reservationId = window.serverData.reservationId;
const memberId = window.serverData.memberId;
const contextPath = window.serverData.contextPath;
let currentReservationState = null;

async function fetchReservationDetail() {
    try {
        const response = await fetch(contextPath + '/reservations/' + reservationId + '/members/' + memberId + '/detail');
        const data = await response.json();

        if (data.code === 1000) {
            displayReservationData(data.result);
        } else {
            showError(data.message);
        }
    } catch (err) {
        showError('네트워크 오류가 발생했습니다.');
    }
}

function displayReservationData(data) {
    document.getElementById('loading').style.display = 'none';
    document.getElementById('content').style.display = 'block';

    currentReservationState = data.state;

    // 예약 상태
    const statusElement = document.getElementById('reservation-status');
    statusElement.textContent = getStateText(data.state);
    statusElement.className = 'reservation-status ' + getStateClass(data.state);

    // 날짜 시간
    const startDateTime = new Date(data.startTime);
    const endDateTime = new Date(data.endTime);

    document.getElementById('reservation-date').textContent =
        startDateTime.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            weekday: 'short'
        });

    document.getElementById('reservation-time').textContent =
        startDateTime.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit', hour12: false }) +
        ' - ' +
        endDateTime.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit', hour12: false });

    // 사용자 정보
    document.getElementById('dropper-name').textContent = data.dropperNickname;
    document.getElementById('keeper-name').textContent = data.keeperNickname;

    // 짐 정보
    const jimListElement = document.getElementById('jim-list');
    jimListElement.innerHTML = '';
    data.reservationJimTypes.forEach(function(jim) {
        const jimItem = document.createElement('div');
        jimItem.className = 'jim-item';
        jimItem.innerHTML =
            '<span class="jim-name">' + jim.typeName + '</span>' +
            '<span class="jim-count">' + jim.count + '개</span>';
        jimListElement.appendChild(jimItem);
    });

    // 취소 버튼 상태 업데이트
    updateCancelButton(data.state);
}

function updateCancelButton(state) {
    const cancelBtn = document.getElementById('cancel-btn');
    if (state === 'CANCELLED' || state === 'COMPLETED') {
        cancelBtn.disabled = true;
        cancelBtn.textContent = state === 'CANCELLED' ? '취소 완료' : '보관 완료';
    } else {
        cancelBtn.disabled = false;
        cancelBtn.textContent = '예약 취소';
    }
}

function showError(message) {
    document.getElementById('loading').style.display = 'none';
    document.getElementById('error').style.display = 'block';
    document.getElementById('error-message').textContent = message;
}

function getStateText(state) {
    switch(state) {
        case 'CONFIRMED': return '예약 확정';
        case 'CANCELLED': return '예약 취소';
        case 'COMPLETED': return '보관 완료';
        case 'PENDING': return '대기 상태';
        default: return state;
    }
}

function getStateClass(state) {
    switch(state) {
        case 'CONFIRMED': return 'status-confirmed';
        case 'CANCELLED': return 'status-cancelled';
        case 'COMPLETED': return 'status-completed';
        default: return 'status-confirmed';
    }
}

// 예약 취소 API 호출
async function cancelReservation() {
    try {
        const response = await fetch(contextPath + '/reservations/' + reservationId + '/members/' + memberId + '/cancel', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const data = await response.json();

        if (data.code === 1000) {
            // 성공 시 모달 표시
            document.getElementById('refund-amount').textContent = data.result.charge.toLocaleString() + '원';
            document.getElementById('success-modal').classList.remove('hidden');
        } else {
            // 실패 시 에러 모달 표시
            document.getElementById('error-modal-message').textContent = data.message;
            document.getElementById('error-modal').classList.remove('hidden');
        }
    } catch (err) {
        document.getElementById('error-modal-message').textContent = '네트워크 오류가 발생했습니다.';
        document.getElementById('error-modal').classList.remove('hidden');
    }
}

function closeConfirmModal() {
    document.getElementById('confirm-modal').classList.add('hidden');
}

function proceedCancel() {
    document.getElementById('confirm-modal').classList.add('hidden');
    cancelReservation();
}

function confirmCancel() {
    document.getElementById('success-modal').classList.add('hidden');
    // 페이지 새로고침하여 업데이트된 상태 표시
    fetchReservationDetail();
}

function closeErrorModal() {
    document.getElementById('error-modal').classList.add('hidden');
}

// 취소 버튼 클릭 이벤트
document.getElementById('cancel-btn').addEventListener('click', function() {
    if (currentReservationState !== 'CANCELLED' && currentReservationState !== 'COMPLETED') {
        document.getElementById('confirm-modal').classList.remove('hidden');
    }
});

// 페이지 로드 시 데이터 가져오기
document.addEventListener('DOMContentLoaded', fetchReservationDetail);