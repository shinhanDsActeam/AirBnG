<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>보관소</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/gh/webfontworld/bmjua/BMJUA.css" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/locker.css'/>" />
</head>

<body class="airbng-locker">
<div class="locker-container">
    <div class="locker-header">
        <h1>보관소</h1>
    </div>

    <c:choose>
        <c:when test="${not empty sessionScope.memberId}">
            <div class="menu-section">
                <!-- 보관소 등록하기 -->
                <div class="menu-item active" onclick="goToRegisterLocker()">
                    <div class="menu-icon register-icon"></div>
                    <div class="menu-content">
                        <h3>보관소 등록하기</h3>
                        <p>새로운 보관소를 등록해보세요</p>
                    </div>
                    <div class="menu-arrow right-arrow"></div>
                </div>

                <div class="menu-item active" onclick="goToManageLocker()">
                    <div class="menu-icon manage-icon"></div>
                    <div class="menu-content">
                        <h3>보관소 관리하기</h3>
                        <p>등록한 보관소를 확인하고 수정하세요</p>
                    </div>
                    <div class="menu-arrow right-arrow"></div>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div class="menu-section">
                <div class="menu-item active" onclick="goToLogin()">
                    <div class="menu-icon logout-icon"></div>
                    <div class="menu-content">
                        <h3>로그인이 필요합니다</h3>
                        <p>보관소 등록/관리를 하시려면 먼저 로그인해주세요</p>
                    </div>
                    <div class="menu-arrow right-arrow"></div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script>
    const contextPath = '${pageContext.request.contextPath}';

    function goToRegisterLocker() {
        location.href = contextPath + '/page/lockers/register';
    }

    function goToManageLocker() {
        location.href = contextPath + '/page/lockers/manage';
    }

    function goToLogin() {
        location.href = contextPath + '/page/login';
    }
</script>
</body>
</html>
