<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>AirBnG | 검색</title>

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/searchFilter.css">

    <!-- JS에서 contextPath 사용 -->
    <script>
        const contextPath = '${pageContext.request.contextPath}';
    </script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/searchFilter.js"></script>
</head>

<body>
    <div class="page-container">
        <header class="header">
                    <button class="back-button" onclick="history.back()">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M19 12H5M12 19l-7-7 7-7"/>
                        </svg>
                    </button>
                    <h1 class="header-title">검색</h1>
                </header>

                <!-- 검색 필터 박스 -->
                <div class="search-filter-container">
                    <div class="search-input-wrapper">
                        <img class="search-icon" src="${pageContext.request.contextPath}/images/Group 2.svg" alt="검색">
                        <input type="text" class="search-input" placeholder="내 근처 짐 맡길 곳 검색하기" onclick="openLocationSearch()">
                    </div>
                    <hr class="filter-divider">
                    <div class="filter-row">
                        <div class="filter-item" onclick="openDatePicker()">
                            <img class="filter-icon" src="${pageContext.request.contextPath}/images/calendarC.svg" alt="캘린더">
                                <line x1="16" y1="2" x2="16" y2="6"/>
                                <line x1="8" y1="2" x2="8" y2="6"/>
                                <line x1="3" y1="10" x2="21" y2="10"/>
                            <span id="selected-date">2025.06.29 (일)</span>
                        </div>
                        <div class="filter-item bag-filter" onclick="openBagTypePicker()">
                            <img class="filter-icon" src="${pageContext.request.contextPath}/images/bag-2.svg" alt="짐">
                                    <path d="M6 6h12v12H6z" stroke-width="2"/>
                                    <path d="M9 6V4a3 3 0 0 1 6 0v2" stroke-width="2"/>
                                <span id="selected-bag">백팩/가방</span>
                            </div>
                    </div>
                    <hr class="filter-divider">
                        <div class="filter-item time-filter" onclick="openTimePicker()">
                            <img class="filter-icon" src="${pageContext.request.contextPath}/images/clock.svg" alt="시계">
                                <circle cx="12" cy="12" r="10"/>
                                <polyline points="12,6 12,12 16,14"/>
                            <span id="selected-time">18:00 - 20:00 (2시간)</span>
                        </div>
                </div>

                <!-- 검색 순위 -->
                <div class="search-ranking">
                    <h3 class="ranking-title">검색 순위</h3>
                    <div class="ranking-list">
                        <div class="ranking-item" data-location="강남">
                            <span class="ranking-number">1</span>
                            <span class="ranking-location">강남</span>
                        </div>
                        <div class="ranking-item" data-location="홍대">
                            <span class="ranking-number">2</span>
                            <span class="ranking-location">홍대</span>
                        </div>
                        <div class="ranking-item" data-location="이태원">
                            <span class="ranking-number">3</span>
                            <span class="ranking-location">이태원</span>
                        </div>
                        <div class="ranking-item" data-location="성수">
                            <span class="ranking-number">4</span>
                            <span class="ranking-location">성수</span>
                        </div>
                    </div>
                </div>

                <!-- 날짜 선택 모달 -->
                <div class="modal" id="dateModal">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3>날짜 선택</h3>
                            <button class="close-button" onclick="closeModal('dateModal')">×</button>
                        </div>
                        <div class="modal-body">
                            <input type="date" id="dateInput" class="date-input">
                            <button class="confirm-button" onclick="selectDate()">확인</button>
                        </div>
                    </div>
                </div>

                <!-- 시간 선택 모달 -->
                <div class="modal" id="timeModal">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3>시간 선택</h3>
                            <button class="close-button" onclick="closeModal('timeModal')">×</button>
                        </div>
                        <div class="modal-body">
                            <div class="time-selector">
                                <div class="dropdown time-group">
                                    <label>시작 시간</label>
                                    <div class="custom-dropdown" id="startDropdown">
                                          <div class="selected">18:00</div>
                                          <ul class="dropdown-options">
                                            <li>09:00</li>
                                            <li>10:00</li>
                                            <li>11:00</li>
                                            <li>12:00</li>
                                            <li>13:00</li>
                                            <li>14:00</li>
                                            <li>15:00</li>
                                            <li>16:00</li>
                                            <li>17:00</li>
                                            <li>18:00</li>
                                            <li>19:00</li>
                                            <li>20:00</li>
                                            <li>21:00</li>
                                            <li>22:00</li>
                                          </ul>
                                        </div>
                                      </div>

                                      <div class="dropdown time-group">
                                        <label>종료 시간</label>
                                        <div class="custom-dropdown" id="endDropdown">
                                          <div class="selected">20:00</div>
                                          <ul class="dropdown-options">
                                            <li>10:00</li>
                                            <li>11:00</li>
                                            <li>12:00</li>
                                            <li>13:00</li>
                                            <li>14:00</li>
                                            <li>15:00</li>
                                            <li>16:00</li>
                                            <li>17:00</li>
                                            <li>18:00</li>
                                            <li>19:00</li>
                                            <li>20:00</li>
                                            <li>21:00</li>
                                            <li>22:00</li>
                                            <li>23:00</li>
                                          </ul>
                                        </div>
                                      </div>
                                    </div>

                                    <button class="confirm-button" onclick="selectTime()">확인</button>
                        </div>
                    </div>
                </div>

                <!-- 검색 결과 표시 영역 -->
                <div class="search-results" id="searchResults" style="display: none;">
                    <h3>검색 결과</h3>
                    <div class="results-container">
                        <!-- 검색 결과가 여기에 동적으로 추가됩니다 -->
                    </div>
                </div>

                <!-- 숨겨진 폼 (검색 실행용) -->
                <form id="searchForm" method="get" action="${pageContext.request.contextPath}/lockerSearch" style="display: none;">
                    <input type="hidden" name="jimTypeId" value="${lockerType}">
                    <input type="hidden" name="location" id="searchLocation">
                    <input type="hidden" name="date" id="searchDate">
                    <input type="hidden" name="startTime" id="searchStartTime">
                    <input type="hidden" name="endTime" id="searchEndTime">
                </form>
            </div>
    </div>
</body>
</html>
