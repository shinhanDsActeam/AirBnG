<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="loginMemberId" value="${sessionScope.memberId}" />

<%
    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>보관소 상세</title>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg" />
    <link rel="stylesheet" href="<c:url value='/css/lockerDetails.css'/>">
</head>
<body>
<div class="container">
    <!-- 헤더 -->
    <c:set var="headerTitle" value="보관소 상세"/>
    <c:set var="showBackButton" value="true"/>
    <%@ include file="common/header.jsp" %>

    <div class="content">
        <div id="lockerContent">
            <!-- 로딩 상태 -->
            <div class="loading" id="loadingState">
                보관소 정보를 불러오는 중...
            </div>

            <!-- 에러 상태 -->
            <div class="error" id="errorState" style="display: none;">
                보관소 정보를 불러오는데 실패했습니다.
            </div>

            <!-- 실제 컨텐츠 (JS에서 동적으로 채움) -->
            <div id="lockerDetailContent" style="display: none;">
                <div class="locker-title" id="lockerTitle"></div>

                <!-- 이미지 갤러리 -->
                <div class="image-gallery" id="imageGallery" style="display: none;">
                </div>

                <!-- 가격 정보 -->
                <div class="price-info" id="priceInfo">
                    <div class="price-title" id="priceTitle"></div>
                    <div class="price-detail" id="priceDetail"></div>
                </div>

                <!-- 정보 섹션 -->
                <div class="info-section" id="infoSection">
                    <div class="info-item">
                        <div class="info-label">주소 </div>
                        <div class="info-value" id="address"></div>
                    </div>

                    <div class="info-item">
                        <div class="info-label">Address</div>
                        <div class="info-value" id="addressEnglish"></div>
                    </div>

                    <div class="info-item">
                        <div class="info-label">맡아주는 사람</div>
                        <div class="info-value" id="keeperName"></div>
                    </div>

                    <div class="info-item">
                        <div class="info-label">전화번호</div>
                        <div class="info-value" id="keeperPhone"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 보관소 선택 버튼 -->
    <button class="reserve-btn" id="reserveBtn" type="button" style="display: none;"
            data-context-path="<%=contextPath%>"
            data-member-id="${loginMemberId}">
        보관소 선택
    </button>
</div>

<script>
    // URL에서 lockerId 추출
    const urlParams = new URLSearchParams(window.location.search);
    const lockerId = urlParams.get('lockerId');
    const memberId = '${loginMemberId}';
    const contextPath = '<%=contextPath%>';

    console.log('Locker ID:', lockerId);
    console.log('Member ID:', memberId);
    console.log('Context Path:', contextPath);
</script>

<script src="${pageContext.request.contextPath}/js/lockerDetails.js"></script>
</body>
</html>