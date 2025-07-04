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
                        <img class="search-back-icon" src="${pageContext.request.contextPath}/images/arrow-left.svg" alt="뒤로가기" onclick="history.back()">
                    </button>
                    <h1 class="header-title">검색</h1>
                </header>

                <!-- 검색 필터 박스 -->
                <div class="search-filter-container">
                    <div class="searchFilter-input-wrapper">
                        <img class="searchFilter-icon" src="${pageContext.request.contextPath}/images/Group 2.svg" alt="검색">
                        <input type="text" class="searchFilter-input" placeholder="내 근처 짐 맡길 곳 검색하기">
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
                        <div class="dropdown-wrapper">
                            <div class="filter-item bag-filter" onclick="toggleBagDropdown()">
                                <img class="filter-icon" src="${pageContext.request.contextPath}/images/bag-2.svg" alt="짐">
                                <span id="selected-bag">${jimTypeId}</span>
                            </div>

                            <!-- 드롭다운 목록 -->
                            <div id="bag-dropdown" class="dropdown hidden">
                                <div class="dropdown-option" onclick="selectBagType('백팩/가방')">백팩/가방</div>
                                <div class="dropdown-option" onclick="selectBagType('캐리어')">캐리어</div>
                                <div class="dropdown-option" onclick="selectBagType('박스/큰 짐')">박스/큰 짐</div>
                                <div class="dropdown-option" onclick="selectBagType('유아용품')">유아용품</div>
                            </div>
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

                <!-- 검색 버튼 -->
                <div class="searchFilter-button-container">
                    <button class="searchFilter-button" onclick="performSearch()">검색</button>
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
                            <c:forEach var="locker" items="${results}">
                                <div class="locker-item">
                                    <p>${locker.name}</p>
                                    <p>짐 타입: ${locker.jimTypeName}</p>
                                    <p>주소: ${locker.address}</p>
                                </div>
                            </c:forEach>
                        </div>
                </div>

                <!-- 숨겨진 폼 (검색 실행용) -->
                <form id="searchForm" method="get" action="${pageContext.request.contextPath}/lockerSearch" style="display: none;">
                    <input type="hidden" name="jimTypeId" id="searchJimType">
                    <input type="hidden" name="address" id="searchAddress">
                    <input type="hidden" name="date" id="searchDate">
                    <input type="hidden" name="startTime" id="searchStartTime">
                    <input type="hidden" name="endTime" id="searchEndTime">
                </form>
            </div>
    </div>
</body>
</html>
