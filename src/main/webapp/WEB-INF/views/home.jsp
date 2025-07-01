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
            margin: 0 auto;
        }

        .page-container {
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            max-width: 412px;
            width: 100%;
            background-color: #ffffff;
            margin: 0 auto;
        }

        .header {
            background: white;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            padding: 12px 16px;
            display: flex;
            align-items: center;
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
            flex: 1;
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

        .icon {
            width: 20px;
            height: 20px;
            margin-right: 8px;
        }

        .date-main {
            font-weight: 600;
            font-size: 16px;
        }

        .time-range {
            font-size: 14px;
            color: #6b7280;
            margin-left: 28px;
        }

        .section-title {
            font-size: 16px;
            font-weight: 600;
            margin-bottom: 12px;
        }

        .user-info {
            display: flex;
            align-items: center;
            margin-bottom: 12px;
        }

        .user-avatar {
            width: 40px;
            height: 40px;
            background: #dbeafe;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 12px;
        }

        .user-name {
            font-weight: 500;
        }

        .user-role {
            font-size: 12px;
            color: #6b7280;
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
        }

        .jim-count {
            background: #f3f4f6;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
        }

        .detail-info {
            font-size: 12px;
            color: #6b7280;
        }

        .loading {
            display: flex;
            align-items: center;
            justify-content: center;
            height: 200px;
        }

        .spinner {
            border: 2px solid #f3f4f6;
            border-top: 2px solid #3b82f6;
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
            left: 0;
            right: 0;
            background: white;
            border-top: 1px solid #e5e7eb;
            padding: 16px;
            display: flex;
            gap: 12px;
        }

        .btn {
            flex: 1;
            padding: 12px;
            border-radius: 8px;
            font-weight: 500;
            border: none;
            cursor: pointer;
        }

        .btn-secondary {
            background: #f3f4f6;
            color: #374151;
        }

        .btn-primary {
            background: #3b82f6;
            color: white;
        }

        .spacer {
            height: 80px;
        }
    </style>
</head>
<body>
    <div class="page-container">
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
                            <span class="icon">&#128197;</span>
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
                        <div class="user-avatar">&#128100;</div>
                        <div>
                            <div id="dropper-name" class="user-name"></div>
                            <div class="user-role">짐 맡기는 사람</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 보관해주는 사람 -->
            <div class="card">
                <div class="card-content">
                    <h3 class="section-title">보관해주는 사람</h3>
                    <div class="user-info">
                        <div class="user-avatar">&#127978;</div>
                        <div>
                            <div id="keeper-name" class="user-name"></div>
                            <div class="user-role">보관소 관리자</div>
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

            <!-- 예약 정보 -->
            <div class="card">
                <div class="card-content">
                    <h3 class="section-title">예약 정보</h3>
                    <div class="detail-info">
                        <p>예약 ID: <span id="reservation-id"></span></p>
                    </div>
                </div>
            </div>
        </div>
        </div>

        <div class="bottom-buttons">
            <button class="btn btn-secondary">예약 취소</button>
            <button class="btn btn-primary">위치 보기</button>
        </div>
    </div>

    <script>
        // 예시로 reservationId=1, memberId=3 사용
        const reservationId = 1;
        const memberId = 3;

        async function fetchReservationDetail() {
            try {
                const response = await fetch(`http://localhost:8080/AirBnG/reservations/${reservationId}/members/${memberId}/detail`);
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

            // 예약 상태
            const statusElement = document.getElementById('reservation-status');
            statusElement.textContent = getStateText(data.state);
            statusElement.className = `reservation-status ${getStateClass(data.state)}`;

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
                startDateTime.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit', hour12: false }) + ' - ' + endDateTime.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit', hour12: false });

            // 사용자 정보
            document.getElementById('dropper-name').textContent = data.dropperNickname;
            document.getElementById('keeper-name').textContent = data.keeperNickname;

            // 예약 ID
            document.getElementById('reservation-id').textContent = data.reservationId;

            // 짐 정보
            const jimListElement = document.getElementById('jim-list');
            jimListElement.innerHTML = '';
            data.reservationJimTypes.forEach(jim => {
                const jimItem = document.createElement('div');
                jimItem.className = 'jim-item';
                jimItem.innerHTML =
                    '<span class="jim-name">&#128230; ' + jim.typeName + '</span>' +
                    '<span class="jim-count">' + jim.count + '개</span>';
                jimListElement.appendChild(jimItem);
            });
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

        // 페이지 로드 시 데이터 가져오기
        document.addEventListener('DOMContentLoaded', fetchReservationDetail);
    </script>
</body>
</html>