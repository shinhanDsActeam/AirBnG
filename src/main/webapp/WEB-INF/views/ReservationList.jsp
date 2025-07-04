<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="loginMemberId" value="${sessionScope.memberId}" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>예약 내역</title>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg" />
    <link rel="stylesheet" href="<c:url value='/css/ReservationList.css'/>">
</head>
<body class="page-container">
 <!-- 헤더 -->
    <div class="header">
        <div class="header-content">
            <button class="back-btn" onclick="history.back()">←</button>
            <h1 class="header-title">예약 내역</h1>
        </div>
    </div>

    <!-- 탭 메뉴 -->
    <div class="tab-container">
        <div class="tabs">
            <a class="tab active" id="tab-before" data-states="CONFIRMED,PENDING">이용전</a>
            <a class="tab" id="tab-after" data-states="COMPLETED">이용후</a>
            <a class="tab" id="tab-cancelled" data-states="CANCELLED">취소됨</a>
        </div>
    </div>

    <!-- 필터 섹션 (탭 메뉴 아래) -->
    <div class="filter-section" id="filter-section">
        <div class="period-dropdown">
            <button class="dropdown-btn" onclick="toggleDropdown()">
                <span id="selected-period">전체</span>
                <svg class="dropdown-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="6,9 12,15 18,9"></polyline>
                </svg>
            </button>
            <div class="dropdown-menu" id="period-dropdown">
                <div class="dropdown-item" onclick="selectPeriod('ALL', '전체')">전체</div>
                <div class="dropdown-item" onclick="selectPeriod('1W', '최근 1주일')">최근 1주일</div>
                <div class="dropdown-item" onclick="selectPeriod('3M', '최근 3개월')">최근 3개월</div>
                <div class="dropdown-item" onclick="selectPeriod('6M', '최근 6개월')">최근 6개월</div>
                <div class="dropdown-item" onclick="selectPeriod('1Y', '최근 1년')">최근 1년</div>
                <div class="dropdown-item" onclick="selectPeriod('2Y', '최근 2년')">최근 2년</div>
            </div>
        </div>
    </div>

    <!-- 예약 내역 리스트 -->
    <div class="reservation-list" id="reservation-list"></div>

    <!-- 빈 상태 -->
    <div class="empty-state" id="empty-state" style="display:none;">
        <p>예약 내역이 없습니다.</p>
    </div>

    <!-- 로딩 상태 -->
    <div class="loading" id="loading" style="display:none;">
        <p>로딩 중...</p>
    </div>

    <!-- 삭제 확인 모달 -->
    <div id="confirm-modal" class="modal-overlay hidden">
        <div class="modal confirm-modal">
            <div class="modal-content">
                <div class="modal-icon confirm-bounce">?</div>
                <div class="modal-title">예약을 삭제하시겠어요?</div>
                <div class="modal-message">
                    삭제하면 되돌릴 수 없어요.<br>
                    정말 삭제하시겠어요?
                </div>
            </div>
            <div class="modal-buttons two-buttons">
                <button class="modal-btn cancel" onclick="closeConfirmModal()">취소</button>
                <button class="modal-btn confirm" onclick="proceedDelete()">삭제</button>
            </div>
        </div>
    </div>

    <!-- 삭제 성공 모달 -->
    <div id="success-modal" class="modal-overlay hidden">
        <div class="modal success-modal">
            <div class="modal-content">
                <div class="modal-icon success-rotate">✓</div>
                <div class="modal-title">예약 내역을 삭제했어요!</div>
                <div class="modal-message">
                    <span id="refund-amount">10,000원</span>의 수수료가 발생했어요!
                </div>
            </div>
            <div class="modal-buttons">
                <button class="modal-btn" onclick="confirmDelete()" style="width: 100%; border-right: none;">확인</button>
            </div>
        </div>
    </div>

    <!-- 삭제 실패 모달 -->
    <div id="error-modal" class="modal-overlay hidden">
        <div class="modal error-modal">
            <div class="modal-content">
                <div class="modal-icon error-shake">✗</div>
                <div class="modal-title">예약 삭제 실패</div>
                <div class="modal-message">
                    삭제 중 오류가 발생했습니다.<br>
                    잠시 후 다시 시도해주세요.
                </div>
            </div>
            <div class="modal-buttons">
                <button class="modal-btn" onclick="closeErrorModal()" style="width: 100%; border-right: none;">확인</button>
            </div>
        </div>
    </div>

    <div id="svg-paths"
         data-spot-url="<c:url value='/images/spot.svg' />"
         data-blodspot-url="<c:url value='/images/blodspot.svg' />"
         style="display:none;">
    </div>

    <%@ include file="navbar.jsp" %>

    <script>
            const memberId = '${loginMemberId}';
            console.log('로그인한 회원 ID:', memberId);
     </script>
    <script src="<c:url value='/js/ReservationList.js'/>"></script>

</body>
</html>