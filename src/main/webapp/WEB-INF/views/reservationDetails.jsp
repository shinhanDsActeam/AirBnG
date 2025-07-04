<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>예약 상세</title>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg" />
    <link rel="stylesheet" href="<c:url value='/css/reservationDetails.css' />" />
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
            <div class="modal-icon confirm-bounce">?</div>
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
                <div class="modal-icon success-rotate">✓</div>
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
    <div class="spacer"></div>
        <script>
            // 서버에서 전달받은 데이터를 JavaScript 변수로 설정
            window.serverData = {
                reservationId: ${reservationId != null ? reservationId : (param.id != null ? param.id : 1)},
                memberId: '${memberId != null ? memberId : sessionScope.memberId}',
                contextPath: '${contextPath}'
            };

            console.log('Reservation ID:', window.serverData.reservationId);
            console.log('Member ID:', window.serverData.memberId);
        </script>
        <script src="<c:url value='/js/reservationDetails.js'/>"></script>
    <script src="<c:url value='/js/reservationDetails.js'/>"></script>
</body>
</html>