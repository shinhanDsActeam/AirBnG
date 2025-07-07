<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>예약 승인</title>
  <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg"/>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="<c:url value='/css/reservation/reservationConfirm.css' />"/>
  <link rel="stylesheet" href="<c:url value='/css/common/modal.css' />"/>
</head>
<body class="bg-gray-100 mx-auto">
<script src="<c:url value='/js/common/modal.js' />"></script>
<script src="<c:url value='/js/reservation/reservationConfirm.js' />"></script>
<div class="max-w-md mx-auto bg-white min-h-screen">
  <%-- 전역변수 --%>
  <script>
    const reservationId = ${reservationId};
    const memberId = ${memberId};
    const contextPath = '${pageContext.request.contextPath}';
  </script>

  <%-- 헤더 설정 --%>
  <c:set var="headerTitle" value="예약 승인"/>
  <c:set var="showBackButton" value="true"/>  <%-- 뒤로가기 있는 버전 --%>
  <%@ include file="../common/header.jsp" %>

  <div class="main-wrapper">
    <div class="space-y-6 pb-32">
      <!-- 예약 정보 로딩 상태 -->
      <div id="loadingState" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
      </div>

      <!-- 예약 정보 컨테이너 -->
      <div id="reservationContainer" class="hidden">
        <!-- 예약자 정보 -->
        <div class="bg-white border border-gray-200 rounded-lg p-4 mb-4">
          <h3 class="font-semibold text-gray-900 mb-3">예약자 정보</h3>
          <div class="space-y-2">
            <div class="flex items-center">
              <span class="text-gray-600 w-16">이름</span>
              <span id="dropperNickname" class="font-medium"></span>
            </div>
            <div class="flex items-center">
              <span class="text-gray-600 w-16">연락처</span>
              <span id="dropperPhone" class="font-medium">정보 없음</span>
            </div>
          </div>
        </div>

        <!-- 보관소 정보 -->
        <div class="bg-gray-50 p-4 rounded-lg mb-4">
          <h3 class="font-semibold text-gray-900 mb-2">보관소 정보</h3>
          <div class="flex items-center">
            <img id="lockerImage" src="" alt="보관소 이미지" class="w-12 h-12 rounded-lg mr-3 object-cover">
            <div>
              <h4 id="keeperNickname" class="font-medium text-gray-900 mb-1"></h4>
              <p id="lockerAddress" class="text-sm text-gray-600">서울 강남구 강남대로 396</p>
            </div>
          </div>
        </div>

        <!-- 예약 정보 -->
        <div class="bg-white border border-gray-200 rounded-lg p-4 mb-4">
          <h3 class="font-semibold text-gray-900 mb-3">예약 정보</h3>

          <!-- 예약 날짜 -->
          <div class="mb-3">
            <span class="text-gray-600 text-sm">보관 날짜</span>
            <div class="flex items-center mt-1">
              <svg class="w-4 h-4 text-gray-500 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
              </svg>
              <span id="reservationDate" class="font-medium"></span>
            </div>
          </div>

          <!-- 예약 시간 -->
          <div class="mb-3">
            <span class="text-gray-600 text-sm">보관 시간</span>
            <div class="flex items-center mt-1">
              <svg class="w-4 h-4 text-gray-500 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
              <span id="reservationTime" class="font-medium"></span>
            </div>
          </div>

          <!-- 예약 상태 -->
          <div class="mb-3">
            <span class="text-gray-600 text-sm">예약 상태</span>
            <div class="flex items-center mt-1">
              <span id="reservationStatus" class="px-2 py-1 rounded-full text-xs font-medium"></span>
            </div>
          </div>
        </div>

        <!-- 짐 정보 -->
        <div class="bg-white border border-gray-200 rounded-lg p-4 mb-4">
          <h3 class="font-semibold text-gray-900 mb-3">짐 정보</h3>
          <div id="jimTypesList" class="space-y-2">
            <!-- 짐 타입들이 여기에 동적으로 추가됩니다 -->
          </div>
        </div>

        <!-- 픽업 방식 -->
        <div class="bg-white border border-gray-200 rounded-lg p-4 mb-4">
          <h3 class="font-semibold text-gray-900 mb-3">픽업 방식</h3>
          <div class="flex items-center">
            <svg class="w-4 h-4 text-gray-500 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
            <span class="font-medium">직접 짐 전달받기</span>
          </div>
        </div>

        <!-- 결제 정보 -->
        <div class="bg-gray-50 p-4 rounded-lg mb-4">
          <h3 class="font-semibold text-gray-900 mb-3">결제 정보</h3>
          <div class="space-y-2">
            <div id="priceDetailsList">
              <!-- 가격 상세 정보가 여기에 동적으로 추가됩니다 -->
            </div>
            <div class="flex justify-between text-sm">
              <span>서비스 수수료</span>
              <span id="serviceFee">400원</span>
            </div>
            <hr class="border-gray-300">
            <div class="flex justify-between font-semibold text-lg">
              <span>총 결제 금액</span>
              <span id="totalPrice"></span>
            </div>
          </div>
        </div>

        <!-- 안내 문구 -->
        <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
          <p class="text-sm text-blue-700">* 상대방이 거절하면 일정이 자동으로 취소됩니다.</p>
        </div>

        <!-- 승인/거절 버튼 섹션 -->
        <div id="actionButtons" class="hidden">
          <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-4">
            <div class="flex items-center mb-2">
              <svg class="w-5 h-5 text-yellow-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L4.082 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
              </svg>
              <span class="font-medium text-yellow-800">예약 승인 요청</span>
            </div>
            <p class="text-sm text-yellow-700">이 예약을 승인하시겠습니까? 승인 후에는 취소할 수 없습니다.</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 하단 고정 버튼 -->
    <div id="bottomButtons" class="main-wrapper fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 p-4 z-50 hidden">
      <div class="flex space-x-3">
        <button id="rejectBtn" type="button"
                class="flex-1 bg-white border border-gray-300 text-gray-700 py-4 rounded-lg font-medium text-lg transition-colors hover:bg-gray-50">
          거절
        </button>
        <button id="approveBtn" type="button"
                class="flex-1 bg-blue-600 text-white py-4 rounded-lg font-medium text-lg transition-colors hover:bg-blue-700">
          승인
        </button>
      </div>
    </div>
  </div>

  <%@ include file="../common/modal.jsp" %>
</div>

</body>
</html>