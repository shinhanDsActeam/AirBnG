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
  <script>
    tailwind.config = {
      theme: {
        extend: {
          colors: {
            primary: '#4561DB',
            'primary-hover': '#3A4FCF'
          }
        }
      }
    }
  </script>
  <link rel="stylesheet" href="<c:url value='/css/dot.css' />" />
  <link rel="stylesheet" href="<c:url value='/css/reservation/reservationDetail.css' />"/>
  <link rel="stylesheet" href="<c:url value='/css/common/modal.css' />"/>
  <style>
    .btn-cancel {
      background-color: #4561DB !important;
      border-color: #4561DB !important;
    }
    .btn-cancel:hover {
      background-color: #4561DB !important;
      border-color: #4561DB !important;
    }
    .btn-primary {
      background-color: #4561DB !important;
      border-color: #4561DB !important;
    }
    .btn-primary:hover {
      background-color: #4561DB !important;
      border-color: #4561DB !important;
    }
  </style>
</head>
<body class="bg-gray-100 mx-auto">
<!-- 스크립트 로드 순서 중요 -->
<script src="<c:url value='/js/common/modal.js' />"></script>

<div class="max-w-md mx-auto bg-white min-h-screen">
    <%-- 전역변수 --%>
    <script>
        const reservationId = ${reservationId};
        const memberId = ${memberId};
        const contextPath = '${pageContext.request.contextPath}';
    </script>

    <%-- 헤더 설정 --%>
    <c:set var="headerTitle" value="예약 상세"/>
    <c:set var="showBackButton" value="true"/>  <%-- 뒤로가기 있는 버전 --%>
    <c:set var="showMenuButton" value="true"/>  <%-- 메뉴 있는 버전 --%>
    <c:set var="menuUrl" value="${pageContext.request.contextPath}/page/reservations/list"/> <%-- 메뉴 URL 설정 --%>
    <%@ include file="../common/header.jsp" %>

    <div class="main-wrapper">
        <div class="space-y-6 pb-32">
            <!-- 예약 정보 로딩 상태 -->
            <div id="loadingState" class="flex justify-center items-center py-12">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
            </div>

            <!-- 예약 정보 컨테이너 -->
            <div id="reservationContainer" class="hidden">

                <!-- 보관소 정보 (최상단) -->
                <div class="bg-white rounded-lg p-2 mb-4 mt-8">
                    <div class="flex items-center">
                        <img id="lockerImage" src="" alt="보관소 이미지" class="w-20 h-20 rounded-lg mr-4 object-cover">
                        <div>
                            <h3 id="keeperNickname" class="text-lg font-semibold text-gray-900 mb-1"></h3>
                            <p id="lockerAddress" class="text-sm text-gray-600"></p>
                        </div>
                    </div>
                </div>

                <div class="w-[372px] h-[1px] bg-[#E5E5E5] my-4"></div>

                <!-- 보관 날짜 -->
                <div class="mb-6">
                    <h4 class="text-base font-semibold text-gray-900 mb-3">보관 날짜</h4>
                    <div class="flex items-center">
                        <img src="<c:url value='/images/calendar_ic.svg'/>" alt="캘린더" class="w-5 h-5 mr-2" />
                        <span id="reservationDate" class="text-gray-900"></span>
                    </div>
                </div>

                <!-- 보관 시간 -->
                <div class="mb-6">
                    <h4 class="text-base font-semibold text-gray-900 mb-3">보관 시간</h4>
                    <div class="flex items-center">
                        <img src="<c:url value='/images/clock_ic.svg'/>" alt="시간" class="w-5 h-5 mr-2" />
                        <span id="reservationTime" class="text-gray-900"></span>
                    </div>
                </div>

                <!-- 짐 종류 -->
                <div class="mb-6">
                    <h4 class="text-base font-semibold text-gray-900 mb-3">짐 종류</h4>
                    <div id="jimTypesList" class="space-y-3">
                        <!-- 짐 타입들이 여기에 동적으로 추가됩니다 -->
                    </div>
                </div>

                <!-- 픽업 방식 -->
                <div class="mb-6">
                    <h4 class="text-base font-semibold text-gray-900 mb-3">픽업 방식</h4>
                    <div class="flex items-center">
                        <img src="<c:url value='/images/pickup_ic.svg'/>" alt="픽업 방법" class="w-5 h-5 mr-2" />
                        <span class="text-gray-900">직접 짐 건네주기</span>
                    </div>
                </div>

                <!-- 결제 정보 -->
                <div class="bg-gray-50 p-4 rounded-lg mb-6">
                    <div class="space-y-3">
                        <div id="priceDetailsList">
                            <!-- 가격 상세 정보가 여기에 동적으로 추가됩니다 -->
                        </div>
                        <div class="flex justify-between text-sm text-gray-600">
                            <span>서비스 수수료</span>
                            <span id="serviceFee">400원</span>
                        </div>
                        <div class="border-t border-gray-300 pt-3">
                            <div class="flex justify-between font-semibold text-lg">
                                <span>총 결제 금액</span>
                                <span id="totalPrice" class="text-primary"></span>
                            </div>
                        </div>
                    </div>
                </div>

                <p id="autoApproveNotice" class="text-sm text-primary hidden">
                </p>
            </div>
        </div>

        <!-- 하단 고정 버튼 -->
        <div id="bottomButtons" class="main-wrapper fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 p-4 z-50 hidden">
            <button id="cancelBtn" type="button"
                    class="btn-cancel w-full text-white py-4 rounded-lg font-medium text-lg transition-colors">
                취소
            </button>
        </div>
    </div>

    <!-- 승인 완료 후 나오는 단일 확인 버튼 -->
    <div id="confirmedButton" class="main-wrapper fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 p-4 z-50 hidden">
        <button id="confirmDoneBtn"
                type="button"
                class="btn-primary w-full text-white py-4 rounded-lg font-medium text-lg transition-colors">
            확인
        </button>
    </div>
    <div id="cancelledButton" class="main-wrapper fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 p-4 z-50 hidden">
        <button id="cancelledConfirmBtn"
                type="button"
                class="w-full bg-gray-400 text-white py-4 rounded-lg font-medium text-lg transition-colors hover:bg-gray-500">
            취소 완료
        </button>
    </div>

    <%@ include file="../common/modal.jsp" %>
</div>

<c:if test="${not empty sessionScope.memberId}">
    <script>
        window.memberId = "${sessionScope.memberId}";
    </script>
</c:if>

<script src="<c:url value='/js/sse.js'/>"></script>
<script src="<c:url value='/js/dot.js'/>"></script>
<!-- 메인 스크립트를 마지막에 로드 -->
<script src="<c:url value='/js/reservation/reservationDetail.js' />"></script>
<script src="${pageContext.request.contextPath}/js/notification.js"></script>

</body>
</html>