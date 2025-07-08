<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>채팅</title>
  <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg" />
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/gh/webfontworld/bmjua/BMJUA.css" rel="stylesheet">
  <link rel="stylesheet" href="<c:url value='/css/chatList.css' />" />
</head>
<body>
<div class="page-container">

  <!-- 헤더 -->
  <c:set var="headerTitle" value="채팅"/>
  <c:set var="showBackButton" value="false"/>
  <%@ include file="/WEB-INF/views/common/header.jsp" %>

  <ul class="chat-list">
    <li class="chat-item" onclick="location.href='chatRoom.jsp'">
      <img src="https://airbngbucket.s3.ap-northeast-2.amazonaws.com/profiles/7ff0a713-da36-488c-9aa1-ecb1c4e0107f_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-06-23+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+9.35.14.png"
           alt="profile" class="chat-profile" />
      <div class="chat-content">
        <div class="chat-top">
          <span class="chat-name">현준이네 보관소</span>
          <span class="chat-time">오후 4:32</span>
        </div>
        <div class="chat-message">현준님이 승인했습니다.</div>
      </div>
    </li>
    <li class="chat-item" onclick="location.href='chatRoom.jsp'">
      <img src="https://airbngbucket.s3.ap-northeast-2.amazonaws.com/profiles/1057a91e-5ca2-48c4-a21e-f12613a664bc_default.jpeg"
           alt="profile" class="chat-profile" />
      <div class="chat-content">
        <div class="chat-top">
          <span class="chat-name">잠실공유센터</span>
          <span class="chat-time">오후 3:18</span>
        </div>
        <div class="chat-message">안녕하세요! 지금 픽업 가능해요</div>
      </div>
    </li>
  </ul>

  <%@ include file="navbar.jsp" %>
</div>
</body>
</html>