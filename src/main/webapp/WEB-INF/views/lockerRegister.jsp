<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>보관소 등록</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/gh/webfontworld/bmjua/BMJUA.css" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/lockerRegister.css'/>" />
</head>

<body class="airbng-register">
<div class="register-container">
    <div class="step-header">
        <button class="back-button" onclick="goBack()">
            <img src="<c:url value='/images/arrow-back.svg'/>" alt="뒤로가기" />
        </button>
        <div class="header-center">
            <span class="step-title">보관소 등록</span>
        </div>
    </div>

    <!-- STEP 인디케이터 -->
    <div class="step-indicator">
        <div class="step-wrapper">
            <c:forEach begin="1" end="4" var="step">
                <c:choose>
                    <c:when test="${step eq currentStep}">
                        <img src="<c:url value='/images/${step}_full_ic.svg'/>" class="step-icon" alt="step${step}" />
                    </c:when>
                    <c:when test="${step lt currentStep}">
                        <img src="<c:url value='/images/check_ic.svg'/>" class="step-icon" alt="step${step}" />
                    </c:when>
                    <c:otherwise>
                        <img src="<c:url value='/images/${step}_blank_ic.svg'/>" class="step-icon" alt="step${step}" />
                    </c:otherwise>
                </c:choose>

                <c:if test="${step lt 4}">
                    <img src="<c:url value='/images/check_line.svg'/>" class="step-line" alt="line" />
                </c:if>
            </c:forEach>
        </div>
    </div>

    <div id="lockerRegisterForm">
        <!-- 1단계: 호스트 이름 -->
        <div class="step step-1 active">
            <div class="form-group">
                <label for="hostName">호스트 이름</label>
                <input type="text" id="hostName" name="hostName" maxlength="10" required placeholder="10자 이내 입력" />
            </div>
            <button type="button" class="next-btn" disabled>다음</button>
        </div>

        <!-- 2단계: 보관소 이름, 주소, 사진 업로드 -->
        <div class="step step-2">
            <div class="form-group">
                <label for="lockerName">보관소 이름</label>
                <input type="text" id="lockerName" name="lockerName" required placeholder="한글, 영어, 숫자만 사용 가능" />
            </div>

            <div class="form-group">
                <label for="location">주소</label>
                <div class="input-with-icon" id="locationInputWrapper">
                    <img src="<c:url value='/images/location_search_ic.svg'/>" class="location-icon" alt="주소 검색" />
                    <input type="text" id="location" name="location" class="address-field" readonly required placeholder="건물, 지번 또는 도로명 검색" />
                </div>
                <input type="text" id="detailAddress" name="detailAddress" placeholder="상세주소" />
                <input type="hidden" id="latitude" />
                <input type="hidden" id="longitude" />
            </div>

            <div class="form-group">
                <label for="image">사진 업로드</label>

                <div class="image-upload-area">
                    <div class="image-upload-box" id="uploadIconBox">
                        <label for="image">
                            <img src="<c:url value='/images/img_upload_ic.svg'/>" alt="사진 업로드" />
                        </label>
                        <input type="file" id="image" name="image" accept="image/*" multiple />
                    </div>

                    <div id="image-preview-container" class="preview-list"></div>
                </div>
            </div>

            <button type="button" class="next-btn" disabled>다음</button>
        </div>

       <!-- 3단계: 운영 시간 -->
       <div class="step step-3">
         <div class="form-group">
           <label for="operatingTime">운영 시간</label>
           <div class="time-select-group">
             <div class="time-select-box">
               <div class="custom-select-wrapper" onclick="toggleDropdown('startTime')">
                 <div class="select-top">
                   <div class="time-label">Start with</div>
                   <div class="selected-time" id="selected-startTime">08:00 AM</div>
                 </div>
                 <img src="${pageContext.request.contextPath}/images/arrow_down_ic.svg" class="select-arrow-icon" />
               </div>
               <ul class="custom-select-dropdown" id="dropdown-startTime"></ul>
             </div>
             <div class="time-select-box">
               <div class="custom-select-wrapper" onclick="toggleDropdown('endTime')">
                 <div class="select-top">
                   <div class="time-label">End with</div>
                   <div class="selected-time" id="selected-endTime">21:00 PM</div>
                 </div>
                 <img src="${pageContext.request.contextPath}/images/arrow_down_ic.svg" class="select-arrow-icon" />
               </div>
               <ul class="custom-select-dropdown" id="dropdown-endTime"></ul>
             </div>

           </div>
           <button type="button" class="next-btn" disabled>다음</button>
         </div>
       </div>

        <!-- 4단계: 짐 보관 조건 -->
        <div class="step step-4">
            <div class="form-group">
                <label>짐 보관 조건</label>

                <div class="condition-row">
                    <span>백팩/가방</span>
                    <div class="input-wrapper">
                      <div class="price-display">2000</div>
                      <input type="hidden" name="price_bag" value="2000" />
                    </div>
                    <button type="button" class="check-btn" data-target="price_bag">
                        <img src="${pageContext.request.contextPath}/images/circle_check_blank.svg" alt="선택" />
                    </button>
                </div>

                <div class="condition-row">
                    <span>캐리어 소형</span>
                    <div class="input-wrapper">
                        <div class="price-display">2500</div>
                        <input type="hidden" name="price_small" value="2500" />
                    </div>
                    <button type="button" class="check-btn" data-target="price_small">
                        <img src="${pageContext.request.contextPath}/images/circle_check_blank.svg" alt="선택" />
                    </button>
                </div>

                <div class="condition-row">
                    <span>캐리어 대형</span>
                    <div class="input-wrapper">
                        <div class="price-display">3000</div>
                        <input type="hidden" name="price_large" value="3000" />
                    </div>
                    <button type="button" class="check-btn" data-target="price_large">
                        <img src="${pageContext.request.contextPath}/images/circle_check_blank.svg" alt="선택" />
                    </button>
                </div>

                <div class="condition-row">
                    <span>박스/큰 짐</span>
                    <div class="input-wrapper">
                        <div class="price-display">4000</div>
                        <input type="hidden" name="price_box" value="4000" />
                    </div>
                    <button type="button" class="check-btn" data-target="price_box">
                        <img src="${pageContext.request.contextPath}/images/circle_check_blank.svg" alt="선택" />
                    </button>
                </div>

                <div class="condition-row">
                    <span>유모차</span>
                    <div class="input-wrapper">
                        <div class="price-display">5000</div>
                        <input type="hidden" name="price_stroller" value="5000" />
                    </div>
                    <button type="button" class="check-btn" data-target="price_stroller">
                        <img src="${pageContext.request.contextPath}/images/circle_check_blank.svg" alt="선택" />
                    </button>
                </div>
            </div>

            <button type="button" class="submit-btn" disabled>등록</button>
        </div>
    </div>
</div>

<script>
    const contextPath = '${pageContext.request.contextPath}';

    const memberId = '${sessionScope.memberId}';
</script>
<script src="<c:url value='/js/config/kakao.config.js'/>"></script>
<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="<c:url value='/js/lockerRegister.js'/>" defer></script>
</body>
</html>