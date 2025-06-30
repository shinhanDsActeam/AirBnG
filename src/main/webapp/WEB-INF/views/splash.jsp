<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>에어비앤집</title>
    <link rel="stylesheet" href="css/splash.css">
</head>
<body>
<div class="container">
    <div class="main-content">
        <div class="splash-content">
            <div class="logo-section">
                <div class="logo-container">
                    <img src="images/logo.svg" alt="에어비앤집 로고">
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    setTimeout(function() {
        window.location.href = 'main.jsp';
    }, 2000);

    document.addEventListener('click', function() {
        window.location.href = 'main.jsp';
    });

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' || e.key === ' ') {
            window.location.href = 'main.jsp';
        }
    });

    document.addEventListener('touchstart', function() {
        window.location.href = 'main.jsp';
    });
</script>
</body>
</html>