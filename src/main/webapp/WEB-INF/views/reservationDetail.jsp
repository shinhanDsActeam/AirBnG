<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>예약 상세</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: #f9fafb;
            color: #111827;
            max-width: 412px;
            margin: 0 auto;
        }

        .header {
            background: white;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            padding: 12px 16px;
            display: flex;
            align-items: center;
            max-width: 412px;
            margin: 0 auto;
            position: relative;
        }

        .back-btn {
            background: none;
            border: none;
            font-size: 18px;
            cursor: pointer;
            color: #6b7280;
        }

        .header h1 {
            margin-left: 16px;
            font-size: 18px;
            font-weight: 600;
        }

        .container {
            padding: 16px;
            max-width: 412px;
            margin: 0 auto;
        }

        .card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            margin-bottom: 16px;
            overflow: hidden;
        }

        .card-content {
            padding: 16px;
        }

        .reservation-status {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
            margin-bottom: 12px;
        }

        .status-confirmed {
            background: #dcfce7;
            color: #166534;
        }

        .status-cancelled {
            background: #fee2e2;
            color: #dc2626;
        }

        .status-completed {
            background: #dbeafe;
            color: #1d4ed8;
        }

        .reservation-info {
            border-bottom: 1px solid #e5e7eb;
            padding-bottom: 16px;
            margin-bottom: 16px;
        }

        .date-time {
            display: flex;
            align-items: center;
            margin-bottom: 8px;
        }

        .date-main {
            font-weight: 600;
            font-size: 16px;
        }

        .time-range {
            font-size: 14px;
            color: #6b7280;
        }

        .section-title {
            font-size: 16px;
            font-weight: 600;
            margin-bottom: 12px;
        }

        .user-info {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 12px 0;
        }

        .user-details {
            display: flex;
            align-items: center;
        }

        .user-avatar {
            width: 40px;
            height: 40px;
            background: #f3f4f6;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 12px;
            font-size: 18px;
            color: #6b7280;
        }

        .user-text {
            display: flex;
            flex-direction: column;
        }

        .user-name {
            font-weight: 500;
            font-size: 14px;
        }

        .user-role {
            font-size: 12px;
            color: #6b7280;
            margin-top: 2px;
        }

        .jim-list {
            space-y: 8px;
        }

        .jim-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px 0;
            border-bottom: 1px solid #f3f4f6;
        }

        .jim-item:last-child {
            border-bottom: none;
        }

        .jim-name {
            font-weight: 500;
            font-size: 14px;
        }

        .jim-count {
            background: #f3f4f6;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
        }

        .loading {
            display: flex;
            align-items: center;
            justify-content: center;
            height: 200px;
        }

        .spinner {
            border: 2px solid #f3f4f6;
            border-top: 2px solid #3181E2;
            border-radius: 50%;
            width: 20px;
            height: 20px;
            animation: spin 1s linear infinite;
            margin-right: 8px;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        .error {
            background: #fee2e2;
            color: #dc2626;
            padding: 16px;
            border-radius: 8px;
            text-align: center;
        }

        .bottom-buttons {
            position: fixed;
            bottom: 0;
            left: 50%;
            transform: translateX(-50%);
            max-width: 412px;
            width: 100%;
            background: white;
            border-top: 1px solid #e5e7eb;
            padding: 16px;
            display: flex;
            gap: 12px;
            box-sizing: border-box;
        }

        .btn {
            flex: 1;
            padding: 12px;
            border-radius: 8px;
            font-weight: 500;
            border: none;
            cursor: pointer;
            transition: opacity 0.2s;
        }

        .btn-secondary {
            background: #f3f4f6;
            color: #374151;
        }

        .btn-secondary:disabled {
            background: #e5e7eb;
            color: #9ca3af;
            cursor: not-allowed;
            opacity: 0.6;
        }

        .spacer {
            height: 80px;
        }

        /* 모달 스타일 */
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 1000;
            padding: 20px;
        }

        .modal {
            background: white;
            border-radius: 16px;
            padding: 0;
            max-width: 320px;
            width: 100%;
            text-align: center;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
        }

        .modal-content {
            padding: 40px 24px 24px;
        }

        .modal-icon {
            width: 64px;
            height: 64px;
            background: #3181E2;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 20px;
            color: white;
            font-size: 24px;
        }

        .modal-title {
            font-size: 18px;
            font-weight: 600;
            color: #111827;
            margin-bottom: 8px;
        }

        .modal-message {
            font-size: 14px;
            color: #6b7280;
            line-height: 1.5;
            margin-bottom: 24px;
        }

        .modal-buttons {
            display: flex;
            border-top: 1px solid #e5e7eb;
        }

        .modal-btn {
            flex: 1;
            padding: 16px;
            border: none;
            background: none;
            cursor: pointer;
            font-size: 16px;
            font-weight: 500;
        }

        .modal-btn:first-child {
            border-right: 1px solid #e5e7eb;
            color: #6b7280;
        }

        .modal-btn:last-child {
            color: #3181E2;
        }

        .modal-btn:hover {
            background: #f9fafb;
        }

        .modal-btn:last-child:hover {
            border-bottom-left-radius: 16px;
            border-bottom-right-radius: 16px;
        }

        .modal-btn:first-child:hover {
            border-bottom-left-radius: 16px;
        }

        .hidden {
            display: none;
        }
    </style>
</head>
<body>
    <div class="header">
        <button class="back-btn" onclick="history.back()">←</button>
        <h1>예약 상세</h1>
    </div>

    <div class="container">
        <div id="loading" class="loading">
            <div class="spinner"></div>
            <span>로딩중...</span>
        </div>

        <div id="error" class="error" style="display: none;">
            <p id="error-message"></p>
        </div>

        <div id="content" style="display: none;">
            <!-- 예약 정보 카드 -->
            <div class="card">
                <div class="card-content">
                    <div class="reservation-info">
                        <span id="reservation-status" class="reservation-status"></span>
                        <div class="date-time">
                            <span id="reservation-date" class="date-main"></span>
                        </div>
                        <div id="reservation-time" class="time-range"></div>
                    </div>
                </div>
            </div>

            <!-- 맡기는 사람 -->
            <div class="card">
                <div class="card-content">
                    <h3 class="section-title">맡기는 사람</h3>
                    <div class="user-info">
                        <div class="user-details">
                            <div class="user-avatar">D</div>
                            <div class="user-text">
                                <div id="dropper-name" class="user-name"></div>
                                <div class="user-role">짐 보관 요청자</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 보관해주는 사람 -->
            <div class="card">
                <div class="card-content">
                    <h3 class="section-title">보관해주는 사람</h3>
                    <div class="user-info">
                        <div class="user-details">
                            <div class="user-avatar">K</div>
                            <div class="user-text">
                                <div id="keeper-name" class="user-name"></div>
                                <div class="user-role">보관소 관리자</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 맡긴 짐 정보 -->
            <div class="card">
                <div class="card-content">
                    <h3 class="section-title">맡긴 짐</h3>
                    <div id="jim-list" class="jim-list"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="bottom-buttons">
        <button id="cancel-btn" class="btn btn-secondary">예약 취소</button>
    </div>

    <!-- 확인 모달 -->
    <div id="confirm-modal" class="modal-overlay hidden">
        <div class="modal">
            <div class="modal-content">
                <div class="modal-icon" style="background: #f59e0b; font-size: 20px;">?</div>
                <div class="modal-title">예약을 취소하시겠습니까?</div>
                <div class="modal-message">
                    취소하시면 환불 처리됩니다
                </div>
            </div>
            <div class="modal-buttons">
                <button class="modal-btn" onclick="closeConfirmModal()">아니요</button>
                <button class="modal-btn" onclick="proceedCancel()">확인</button>
            </div>
        </div>
    </div>

    <!-- 성공 모달 -->
    <div id="success-modal" class="modal-overlay hidden">
        <div class="modal">
            <div class="modal-content">
                <div class="modal-icon">✓</div>
                <div class="modal-title">예약을 취소했어요!</div>
                <div class="modal-message">
                    <span id="refund-amount">10,000원</span>의 수수료가 발생했어요!
                </div>
            </div>
            <div class="modal-buttons">
                <button class="modal-btn" onclick="confirmCancel()" style="width: 100%; border-right: none;">확인</button>
            </div>
        </div>
    </div>

    <!-- 실패 모달 -->
    <div id="error-modal" class="modal-overlay hidden">
        <div class="modal">
            <div class="modal-content">
                <div class="modal-icon" style="background: #dc2626;">✗</div>
                <div class="modal-title">예약 취소 실패</div>
                <div class="modal-message" id="error-modal-message">
                    예약 상태 변경에 실패하였습니다.
                </div>
            </div>
            <div class="modal-buttons">
                <button class="modal-btn" onclick="closeErrorModal()" style="width: 100%; border-right: none;">확인</button>
            </div>
        </div>
    </div>

    <div class="spacer"></div>

    <script>
        // 예시로 reservationId=1, memberId=3 사용
        const reservationId = 1;
        const memberId = 3;
        let currentReservationState = null;

        async function fetchReservationDetail() {
            try {
                const response = await fetch('http://localhost:8080/AirBnG/reservations/' + reservationId + '/members/' + memberId + '/detail');
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
                const response = await fetch('http://localhost:8080/AirBnG/reservations/' + reservationId + '/members/' + memberId + '/cancel', {
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
    </script>
</body>
</html>