<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="<c:url value='/css/common/header.css' />" />

<div class="common-header">
    <c:if test="${not empty showBackButton and showBackButton}">
        <img class="back-icon" src="${pageContext.request.contextPath}/images/arrow-left.svg" alt="뒤로가기" onclick="history.back()">
    </c:if>

    <div class="header-title">${headerTitle}</div>

    <c:if test="${not empty showBackButton and showBackButton}">
        <div class="header-spacer"></div>
    </c:if>
</div>