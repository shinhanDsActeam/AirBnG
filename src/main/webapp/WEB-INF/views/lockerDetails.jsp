<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="phone" value="${lockerDetail.keeperPhone}" />
<c:set var="formattedPhone" value="${fn:substring(phone, 0, 3)}-${fn:substring(phone, 3, 7)}-${fn:substring(phone, 7, 11)}" />
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
    <div class="header">
        <button class="back-btn" onclick="history.back()">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
        </button>
        <div class="header-title">보관소 상세</div>
    </div>

    <div class="content">
        <div id="lockerContent">
            <div class="locker-title">${lockerDetail.lockerName}</div>

            <!-- 이미지 여러 개 출력 -->
            <c:if test="${not empty lockerDetail.images}">
                <div class="image-gallery">
                    <c:forEach var="img" items="${lockerDetail.images}" varStatus="status">
                        <c:if test="${status.index < 6}">
                            <img class="locker-image" src="${img}" alt="보관소 이미지 ${status.index + 1}">
                        </c:if>
                    </c:forEach>
                </div>
            </c:if>


            <div class="price-info">
                 <c:if test="${not empty lockerDetail.jimTypeResults}">
                    <div class="price-title">
                        <c:forEach var="type" items="${lockerDetail.jimTypeResults}" varStatus="status">
                            ${type.typeName}<c:if test="${!status.last}">/</c:if>
                        </c:forEach>
                    </div>
                    <div class="price-detail">
                        • 시간당 2,000원<br>
                        • 강남역 도보 3분
                    </div>
                </c:if>
            </div>

            <div class="info-section">
                <div class="info-item">
                    <div class="info-label">주소 </div>
                    <div class="info-value"> | ${lockerDetail.address} ${lockerDetail.addressDetail}</div>
                </div>

                <div class="info-item">
                    <div class="info-label">Address</div>
                    <div class="info-value"> | ${lockerDetail.addressEnglish}</div>
                </div>

                <div class="info-item">
                    <div class="info-label">맡아주는 사람</div>
                    <div class="info-value"> | ${lockerDetail.keeperName}</div>
                </div>

                <div class="info-item">
                    <div class="info-label">전화번호</div>
                    <div class="info-value"> | ${formattedPhone}</div>
                </div>
            </div>

        </div>
    </div>

    <!-- contextPath 반영해서 JS에서 URL 구성 -->
    <button class="reserve-btn" id="reserveBtn" type="button" data-locker-id="${lockerDetail.lockerId}" data-member-id="${loginMemberId}">
      보관소 선택
    </button>

    </div>

    <script>
                const memberId = '${loginMemberId}';
                console.log('로그인한 회원 ID:', memberId);
    </script>

    <script src="${pageContext.request.contextPath}/js/lockerDetails.js"></script>
</body>
</html>
