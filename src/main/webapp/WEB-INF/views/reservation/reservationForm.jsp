<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>예약하기</title>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg" />
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="<c:url value='/css/reservation/reservationForm.css' />"/>
    <link rel="stylesheet" href="<c:url value='/css/common/modal.css' />"/>
</head>
<body class="bg-gray-100 mx-auto">
<script src="<c:url value='/js/common/modal.js' />"></script>
<script src="<c:url value='/js/reservation/reservationForm.js' />"></script>
<div class="max-w-md mx-auto bg-white min-h-screen">
    <%-- 전역변수 --%>
    <script>
        const lockerId = ${lockerId};
        const contextPath = '${pageContext.request.contextPath}';
    </script>

    <div class="header">
        <button class="back-btn" onclick="history.back()">
            <svg class="w-6 h-6 text-gray-600 cursor-pointer" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
        </button>
        <div class="header-title">예약하기</div>
    </div>


    <form id="reservationForm" method="post" action="/AirBnG/reservations">
        <div class="form space-y-6">
            <!-- 보관소 정보 -->
            <div class="bg-gray-50 p-4 rounded-lg">
                <h2 id="lockerName" class="font-semibold text-gray-900 mb-1">
                </h2>
                <p id="lockerAddress" class="text-sm text-gray-600">
                </p>
            </div>

            <!-- 보관 날짜 -->
            <div>
                <h3 class="font-medium text-gray-900 mb-3">보관 날짜</h3>
                <div class="flex flex-col items-center" id="date-buttons"></div>
            </div>

            <!-- 보관 시간 -->
            <div>
                <h3 class="font-medium text-gray-900 mb-3">보관 시간</h3>
                <div class="flex items-center space-x-4">
                    <div class="flex-1">
                        <label class="block text-sm text-gray-600 mb-1">시작 시간</label>
                        <div class="custom-dropdown" id="startTimeDropdown">
                            <div class="dropdown-selected" onclick="toggleDropdown('startTime')">
                                <span id="startTimeDisplay">시간 선택</span>
                                <svg class="dropdown-arrow" width="12" height="8" viewBox="0 0 12 8" fill="none">
                                    <path d="M1 1.5L6 6.5L11 1.5" stroke="#6B7280" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                </svg>
                            </div>
                            <div class="dropdown-options" id="startTimeOptions">
                            </div>
                        </div>
                    </div>
                    <div class="flex-1">
                        <label class="block text-sm text-gray-600 mb-1">종료 시간</label>
                        <div class="custom-dropdown" id="endTimeDropdown">
                            <div class="dropdown-selected" onclick="toggleDropdown('endTime')">
                                <span id="endTimeDisplay">시간 선택</span>
                                <svg class="dropdown-arrow" width="12" height="8" viewBox="0 0 12 8" fill="none">
                                    <path d="M1 1.5L6 6.5L11 1.5" stroke="#6B7280" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                </svg>
                            </div>
                            <div class="dropdown-options" id="endTimeOptions">
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 짐 종류 -->
            <div id = "jimSection">
                <h3 class="font-medium text-gray-900 mb-3">짐 종류</h3>
                <div class="space-y-3" id="jimTypes">
                </div>
            </div>

            <!-- 가격 계산 -->
            <div class="bg-gray-50 p-4 rounded-lg space-y-2" id="priceCalculation">
                <div id="itemsList">
                </div>
                <div class="flex justify-between text-sm">
                    <span>서비스 수수료 (5%)</span>
                    <span id="serviceFee">0원</span>
                </div>
                <hr class="border-gray-300">
                <div class="flex justify-between font-semibold">
                    <span>총 결제 금액</span>
                    <span id="totalPrice">400원</span>
                </div>
            </div>

            <!-- 예약 버튼 -->
            <button type="submit"
                    class="submit-btn w-full text-white py-4 rounded-lg font-medium text-lg transition-colors">
                예약하기
            </button>
        </div>

        <!-- Hidden inputs -->
        <input type="hidden" name="lockerId" id="lockerId" value="" />
    </form>

    <%@ include file="../common/modal.jsp" %>

</div>

</body>
</html>