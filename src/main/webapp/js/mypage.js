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

// 페이지에서 사용자 정보 로드 (서버에서 렌더링된 데이터 사용)
function loadUserInfoFromPage() {
    // 서버에서 body에 설정한 사용자 정보 데이터 속성 읽기
    const body = document.body;
    const userName = body.dataset.userName;
    const userEmail = body.dataset.userEmail;

    if (userName || userEmail) {
        updateUserInfo({
            name: userName,
            email: userEmail
        });
    }

    // 또는 개별 요소의 data 속성에서 읽기
    // const username = document.querySelector('.username')?.dataset.username;
    // const email = document.querySelector('.user-email')?.dataset.email;
    // if (username && email) {
    //     updateUserInfo({ name: username, email: email });
    // }
}

// 사용자 정보 업데이트 (필요시 사용)
function updateUserInfo(user) {
    const usernameElement = document.querySelector('.username');
    const userEmailElement = document.querySelector('.user-email');

    if (usernameElement && user.name) {
        usernameElement.textContent = `${user.name}님`;
    }

    if (userEmailElement && user.email) {
        userEmailElement.textContent = user.email;
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

// 컨텍스트 패스 가져오기
function getContextPath() {
    // 현재 URL에서 컨텍스트 패스 추출
    const path = window.location.pathname;
    const contextPath = path.substring(0, path.indexOf('/', 1));
    return contextPath || '';
}

// 로그인 페이지로 이동
function goToLogin() {
    const contextPath = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.replace(`${contextPath}/page/login`);
    }, 300);
}

// 회원가입 페이지로 이동
function goToSignup() {
    const contextPath = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${contextPath}/page/signup`;
    }, 300);
}

// 내 정보 페이지로 이동
function goToMyInfo() {
    const contextPath = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${contextPath}/page/myinfo`;
    }, 300);
}

// 예약 내역 페이지로 이동
function goToReservations() {
    const contextPath = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${contextPath}/page/reservations`;
    }, 300);
}

// 후기 페이지로 이동
function goToReviews() {
    const contextPath = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${contextPath}/page/reviews`;
    }, 300);
}

// 로그아웃
function logout() {
    if (confirm('정말로 로그아웃하시겠습니까?')) {
        const contextPath = getContextPath();
        showLoadingAnimation();

        fetch(`${contextPath}/auth/logout`, {
            method: 'POST',
            credentials: 'include', // 세션 쿠키 포함
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(response => {
            if (response.ok) {
                setTimeout(() => {
                    window.location.href = `${contextPath}/page/home`;
                }, 500);
            } else {
                alert('로그아웃 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('로그아웃 오류:', error);
            alert('로그아웃 중 오류가 발생했습니다.');
        })
        .finally(() => {
            hideLoadingAnimation();
        });
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
}// 페이지 로드 시 초기화
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

// 로그인 상태 확인 (HTML 데이터 속성 기반)
function checkLoginStatus() {
    // 서버에서 렌더링 시점에 설정한 데이터 속성 확인
    const body = document.body;
    const isLoggedIn = body.dataset.loggedIn === 'true';

    if (isLoggedIn) {
        showLoggedInSection();
        loadUserInfoFromPage();
    } else {
        showLoggedOutSection();
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

// 페이지에서 사용자 정보 로드 (서버에서 렌더링된 데이터 사용)
function loadUserInfoFromPage() {
    // 서버에서 body에 설정한 사용자 정보 데이터 속성 읽기
    const body = document.body;
    const userName = body.dataset.userName;
    const userEmail = body.dataset.userEmail;

    if (userName || userEmail) {
        updateUserInfo({
            name: userName,
            email: userEmail
        });
    }

    // 또는 개별 요소의 data 속성에서 읽기
    // const username = document.querySelector('.username')?.dataset.username;
    // const email = document.querySelector('.user-email')?.dataset.email;
    // if (username && email) {
    //     updateUserInfo({ name: username, email: email });
    // }
}

// 사용자 정보 업데이트 (필요시 사용)
function updateUserInfo(user) {
    const usernameElement = document.querySelector('.username');
    const userEmailElement = document.querySelector('.user-email');

    if (usernameElement && user.name) {
        usernameElement.textContent = `${user.name}님`;
    }

    if (userEmailElement && user.email) {
        userEmailElement.textContent = user.email;
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

// 컨텍스트 패스 가져오기
function getContextPath() {
    // 현재 URL에서 컨텍스트 패스 추출
    const path = window.location.pathname;
    const contextPath = path.substring(0, path.indexOf('/', 1));
    return contextPath || '';
}

// 로그인 페이지로 이동
function goToLogin() {
    const contextPath = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.replace(`${contextPath}/page/login`);
    }, 300);
}

// 회원가입 페이지로 이동
function goToSignup() {
    const contextPath = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${contextPath}/page/signup`;
    }, 300);
}

// 내 정보 페이지로 이동
function goToMyInfo() {
    const contextPath = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${contextPath}/page/myinfo`;
    }, 300);
}

// 예약 내역 페이지로 이동
function goToReservations() {
    const contextPath = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${contextPath}/page/reservations`;
    }, 300);
}

// 후기 페이지로 이동
function goToReviews() {
    const contextPath = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${contextPath}/page/reviews`;
    }, 300);
}

// 로그아웃
function logout() {
    if (confirm('정말로 로그아웃하시겠습니까?')) {
        const contextPath = getContextPath();
        showLoadingAnimation();

        fetch(`${contextPath}/auth/logout`, {
            method: 'POST',
            credentials: 'include', // 세션 쿠키 포함
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(response => {
            if (response.ok) {
                setTimeout(() => {
                    window.location.href = `${contextPath}/page/home`;
                }, 500);
            } else {
                alert('로그아웃 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('로그아웃 오류:', error);
            alert('로그아웃 중 오류가 발생했습니다.');
        })
        .finally(() => {
            hideLoadingAnimation();
        });
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
}