<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>보관소 관리</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/gh/webfontworld/bmjua/BMJUA.css" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/lockerManage.css'/>" />
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.svg" />
</head>

<body class="airbng-manage">
<div class="manage-container">
    <div class="manage-header">
        <div class="back-wrapper">
            <img src="${pageContext.request.contextPath}/images/arrow-back.svg"
                 alt="뒤로가기"
                 class="back-icon"
                 onclick="location.href='${pageContext.request.contextPath}/page/lockers'" />
        </div>
        <h1 class="center-title">보관소 수정</h1>
    </div>

    <div id="locker-manage-content">
        <!-- JavaScript로 동적으로 렌더링 -->
    </div>
</div>

<%@ include file="/WEB-INF/views/common/modal.jsp" %>

<script>
    const contextPath = '${pageContext.request.contextPath}';
    const urlParams = new URLSearchParams(window.location.search);
    const lockerId = urlParams.get('lockerId');
</script>
<script src="<c:url value='/js/config/kakao.config.js'/>"></script>
<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="<c:url value='/js/lockerManage.js'/>"></script>
</body>