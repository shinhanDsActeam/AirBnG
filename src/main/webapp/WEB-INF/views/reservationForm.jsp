<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>예약하기</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="../../css/reservationForm.css"/>
</head>
<body class="bg-gray-100">
<script src="../../js/reservationForm.js"></script>
<div class="max-w-md mx-auto bg-white min-h-screen">
    <!-- Header -->
    <div class="sticky top-0 z-50 flex items-center p-4 border-b border-gray-200 bg-white" id="header">
        <svg class="w-6 h-6 text-gray-600 cursor-pointer" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
        </svg>
        <h1 class="ml-4 text-lg font-medium">예약하기</h1>
    </div>

    <form id="reservationForm" method="post" action="/AirBnG/reservations">
        <div class="p-4 space-y-6">
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
                        <select id="startTimeSelect" class="custom-select" onchange="updateTimeOptions(); calculateTotal();">
                        </select>
                    </div>
                    <div class="flex-1">
                        <label class="block text-sm text-gray-600 mb-1">종료 시간</label>
                        <select id="endTimeSelect" class="custom-select" onchange="calculateTotal()">
                        </select>
                    </div>
                </div>
            </div>

            <!-- 짐 종류 -->
            <div>
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
</div>

</body>
</html>