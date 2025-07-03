<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>보관소 등록</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/gh/webfontworld/bmjua/BMJUA.css" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/register.css'/>" />
</head>

<body class="airbng-register">
    <div class="register-container">
        <div class="register-header">
            <h1>보관소 등록</h1>
        </div>

        <!-- 등록 폼 -->
        <form id="lockerRegisterForm" action="<c:url value='/api/lockers/register'/>" method="post">
            <div class="form-group">
                <label for="lockerName">보관소 이름</label>
                <input type="text" id="lockerName" name="lockerName" required placeholder="예: 강남역 1번 출구 근처 보관소" />
            </div>
            <div class="form-group">
                <label for="location">위치</label>
                <input type="text" id="location" name="location" required placeholder="예: 서울특별시 강남구 역삼동 123-45" />
            </div>
            <div class="form-group">
                <label for="description">설명</label>
                <textarea id="description" name="description" required placeholder="보관소에 대한 간단한 설명을 입력하세요"></textarea>
            </div>
            <div class="form-group">
                <label for="price">가격 (1일 기준)</label>
                <input type="number" id="price" name="price" required placeholder="예: 5000" />
            </div>
            <div class="form-group">
                <label for="image">이미지 업로드</label>
                <input type="file" id="image" name="image" accept="image/*" required />
            </div>
            <div class="form-group">
                <label for="contact">연락처</label>
                <input type="text" id="contact" name="contact" required placeholder="예: 010-1234-5678" />
            </div>
            <div class="form-group">
                <label for="terms">
                    <input type="checkbox" id="terms" name="terms" required />
                    이용 약관에 동의합니다
                </label>
            </div>
            <button type="submit" class="submit-button">등록하기</button>
        </form>
        <div class="back-link">
            <a href="${pageContext.request.contextPath}/page/lockers">보관소 페이지로 돌아가기</a>
        </div>
    </div>

    <script>
        const contextPath = '${pageContext.request.contextPath}';
    </script>
    <script src="<c:url value='/js/register.js'/>"></script>
</body>
</html>