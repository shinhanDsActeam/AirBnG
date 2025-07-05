<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="<c:url value='/css/common/header.css' />"/>

<div class="common-header">
    <c:choose>
        <c:when test="${not empty showBackButton and showBackButton}">
            <img class="back-icon" src="${pageContext.request.contextPath}/images/arrow-left.svg" alt="뒤로가기"
                 onclick="history.back()">
        </c:when>
        <c:otherwise>
            <div class="back-spacer"></div>
        </c:otherwise>
    </c:choose>

    <div class="header-title">${headerTitle}</div>
    <div class="header-spacer"></div>
</div>