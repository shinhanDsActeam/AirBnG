// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeMyPage();
});

// 마이페이지 초기화
function initializeMyPage() {
    checkLoginStatus(); // 로그인 상태 확인
    animatePageElements();
    setupEventListeners();
    console.log('마이페이지가 초기화되었습니다.');
}

// 로그인 상태 확인 (서버 세션 데이터 기반)
function checkLoginStatus() {
    // sessionData는 JSP에서 전역 변수로 설정됨
    if (typeof sessionData !== 'undefined' && sessionData.isLoggedIn && sessionData.memberId && sessionData.memberId !== 'null' && sessionData.memberId !== '') {
        showLoggedInSection();
        loadUserInfoFromSession();
        console.log('로그인 상태 확인됨:', sessionData.nickname);
    } else {
        showLoggedOutSection();
        console.log('로그아웃 상태');
    }
}

// 로그인된 상태 UI 표시
function showLoggedInSection() {
    const loggedOutSection = document.getElementById('loggedOutSection');
    const loggedInSection = document.getElementById('loggedInSection');

    // 섹션 표시/숨김
    if (loggedOutSection) {
        loggedOutSection.style.display = 'none';
    }
    if (loggedInSection) {
        loggedInSection.style.display = 'block';
    }
}

// 로그아웃된 상태 UI 표시
function showLoggedOutSection() {
    const loggedOutSection = document.getElementById('loggedOutSection');
    const loggedInSection = document.getElementById('loggedInSection');

    if (loggedOutSection) {
        loggedOutSection.style.display = 'block';
    }
    if (loggedInSection) {
        loggedInSection.style.display = 'none';
    }
}

// 서버 세션에서 사용자 정보 로드
function loadUserInfoFromSession() {
    if (typeof sessionData !== 'undefined' && sessionData.isLoggedIn) {
        updateUserInfo({
            name: sessionData.nickname
        });
    }
}

// 사용자 정보 업데이트 (이메일 제거)
function updateUserInfo(user) {
    const usernameElement = document.querySelector('.username');

    if (usernameElement && user.name && user.name !== 'null' && user.name !== '') {
        usernameElement.textContent = `${user.name}님`;
    }
}

// 페이지 요소 애니메이션
function animatePageElements() {
    const elements = document.querySelectorAll('.welcome-section, .user-info-section, .menu-section, .limited-menu');

    elements.forEach((element, index) => {
        if (element.offsetParent !== null) { // 보이는 요소만 애니메이션
            element.style.opacity = '0';
            element.style.transform = 'translateY(20px)';

            setTimeout(() => {
                element.style.transition = 'all 0.6s ease-out';
                element.style.opacity = '1';
                element.style.transform = 'translateY(0)';
            }, index * 100);
        }
    });
}

// 이벤트 리스너 설정
function setupEventListeners() {
    const menuItems = document.querySelectorAll('.menu-item.active');
    menuItems.forEach(item => {
        item.addEventListener('mouseenter', handleMenuHover);
        item.addEventListener('mouseleave', handleMenuLeave);
    });
}

// 메뉴 호버 효과
function handleMenuHover(event) {
    const menuItem = event.currentTarget;
    const arrow = menuItem.querySelector('.menu-arrow');

    if (arrow) {
        arrow.style.transform = 'translateX(5px)';
        arrow.style.transition = 'transform 0.2s ease';
    }
}

function handleMenuLeave(event) {
    const menuItem = event.currentTarget;
    const arrow = menuItem.querySelector('.menu-arrow');

    if (arrow) {
        arrow.style.transform = 'translateX(0)';
    }
}

// 컨텍스트 패스 가져오기 (JSP에서 설정한 전역 변수 사용)
function getContextPath() {
    // JSP에서 설정한 contextPath 전역 변수 사용
    if (typeof contextPath !== 'undefined') {
        return contextPath;
    }

    // fallback: URL에서 추출
    const path = window.location.pathname;
    const contextPathFromUrl = path.substring(0, path.indexOf('/', 1));
    return contextPathFromUrl || '';
}

// 로그인 페이지로 이동
function goToLogin() {
    const ctx = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.replace(`${ctx}/page/login`);
    }, 300);
}

// 회원가입 페이지로 이동
function goToSignup() {
    const ctx = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${ctx}/page/signup`;
    }, 300);
}

// 내 정보 페이지로 이동
function goToMyInfo() {
    // 로그인 상태 재확인
    if (!isLoggedIn()) {
        alert('로그인이 필요한 서비스입니다.');
        goToLogin();
        return;
    }

    const ctx = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${ctx}/page/myinfo`;
    }, 300);
}

// 예약 내역 페이지로 이동
function goToReservations() {
    // 로그인 상태 재확인
    if (!isLoggedIn()) {
        alert('로그인이 필요한 서비스입니다.');
        goToLogin();
        return;
    }

    const ctx = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${ctx}/page/reservations`;
    }, 300);
}

// 후기 페이지로 이동
function goToReviews() {
    // 로그인 상태 재확인
    if (!isLoggedIn()) {
        alert('로그인이 필요한 서비스입니다.');
        goToLogin();
        return;
    }

    const ctx = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${ctx}/page/reviews`;
    }, 300);
}

// 로그인 상태 확인 헬퍼 함수
function isLoggedIn() {
    return typeof sessionData !== 'undefined' &&
           sessionData.isLoggedIn &&
           sessionData.memberId &&
           sessionData.memberId !== 'null' &&
           sessionData.memberId !== '';
}

// 로그아웃 (클라이언트 단에서 처리)
function logout() {
    if (!confirm('정말로 로그아웃하시겠습니까?')) {
        return;
    }

    showLoadingAnimation();

    // 세션 데이터 초기화
    if (typeof sessionData !== 'undefined') {
        sessionData.isLoggedIn = false;
        sessionData.memberId = null;
        sessionData.nickname = null;
        sessionData.email = null;
    }

    // 세션 스토리지 삭제 (혹시 사용중이라면)
    if (typeof(Storage) !== "undefined") {
        sessionStorage.clear();
    }

    // 쿠키에서 세션 관련 정보 삭제
    deleteCookie('JSESSIONID');
    deleteCookie('memberId');
    deleteCookie('nickname');
    deleteCookie('email');

    // UI를 로그아웃 상태로 변경
    setTimeout(() => {
        hideLoadingAnimation();
        showLoggedOutSection();

        // 사용자에게 로그아웃 완료 알림
        alert('로그아웃되었습니다.');

        // 필요하다면 홈페이지로 리다이렉트
        // const ctx = getContextPath();
        // window.location.href = `${ctx}/page/home`;

    }, 500);
}

// 쿠키 삭제 헬퍼 함수
function deleteCookie(name) {
    // 현재 도메인에서 삭제
    document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';

    // 컨텍스트 패스가 있다면 해당 경로에서도 삭제
    const ctx = getContextPath();
    if (ctx) {
        document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=' + ctx + '/;';
    }

    // 루트 도메인에서도 삭제 (서브도메인 대응)
    const domain = window.location.hostname;
    if (domain.includes('.')) {
        const rootDomain = '.' + domain.split('.').slice(-2).join('.');
        document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/; domain=' + rootDomain + ';';
    }
}

// 로딩 애니메이션 표시
function showLoadingAnimation() {
    // 기존 로딩 오버레이가 있으면 제거
    hideLoadingAnimation();

    const loadingOverlay = document.createElement('div');
    loadingOverlay.id = 'loadingOverlay';
    loadingOverlay.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(255, 255, 255, 0.9);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 9999;
        backdrop-filter: blur(5px);
    `;

    const spinner = document.createElement('div');
    spinner.style.cssText = `
        width: 40px;
        height: 40px;
        border: 4px solid #f0f0f0;
        border-top: 4px solid #4561DB;
        border-radius: 50%;
        animation: spin 1s linear infinite;
    `;

    // 애니메이션 스타일이 없으면 추가
    if (!document.getElementById('loadingStyles')) {
        const style = document.createElement('style');
        style.id = 'loadingStyles';
        style.textContent = `
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        `;
        document.head.appendChild(style);
    }

    loadingOverlay.appendChild(spinner);
    document.body.appendChild(loadingOverlay);
}

// 로딩 애니메이션 숨기기
function hideLoadingAnimation() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.remove();
    }
}

// 수동 새로고침 함수 (필요시 사용)
function refreshUserInfo() {
    checkLoginStatus();
    console.log('사용자 정보가 새로고침되었습니다.');
}