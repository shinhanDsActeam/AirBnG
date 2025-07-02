<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="loginMemberId" value="${sessionScope.memberId}" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>예약 내역</title>
    <link rel="stylesheet" href="<c:url value='/css/ReservationList.css'/>">
</head>
 <!-- 헤더 -->
    <div class="header">
        <div class="header-content">
            <button class="back-btn" onclick="history.back()">←</button>
            <h1 class="header-title">예약 내역</h1>
            <span>${sessionScope.memberId}님.</span>
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

    <!-- 더보기 버튼 -->
    <div class="load-more" id="load-more" style="display:none;">
        <button class="load-more-btn" id="load-more-btn">더보기</button>
    </div>

    <!-- 로딩 상태 -->
    <div class="loading" id="loading" style="display:none;">
        <p>로딩 중...</p>
    </div>

    <script>
            const memberId = '${loginMemberId}';
            console.log('로그인한 회원 ID:', memberId);
     </script>
    <script src="<c:url value='/js/ReservationList.js'/>"></script>

</body>
</html>