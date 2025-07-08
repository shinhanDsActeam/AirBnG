// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeMyPage();

    // 페이지 로드시 로그인 상태에 따른 UI 표시
    if (sessionData.isLoggedIn) {
        document.getElementById('loggedOutSection').style.display = 'none';
        document.getElementById('loggedInSection').style.display = 'block';
    } else {
        document.getElementById('loggedOutSection').style.display = 'block';
        document.getElementById('loggedInSection').style.display = 'none';
    }
});

// 마이페이지 초기화
function initializeMyPage() {
    checkLoginStatus(); // 로그인 상태 확인
    animatePageElements();
    setupEventListeners();
}

// 로그인 상태 확인 (서버 세션 데이터 기반)
function checkLoginStatus() {
    // sessionData는 JSP에서 전역 변수로 설정됨
    if (typeof sessionData !== 'undefined' && sessionData.isLoggedIn && sessionData.memberId && sessionData.memberId !== 'null' && sessionData.memberId !== '') {
        showLoggedInSection();
        loadUserInfoFromSession();
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
        window.location.replace(`${ctx}/page/login?redirect=${ctx}/page/mypage`);
    }, 300);
}

// 회원가입 페이지로 이동
function goToSignup() {
    const ctx = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.replace(`${ctx}/page/signup?redirect=${ctx}/page/mypage`);
    }, 300);
}

// 내 정보 페이지로 이동
function goToMyInfo() {
    // 로그인 상태 재확인
    if (!isLoggedIn()) {
        showInfoModal('로그인 필요', '로그인이 필요한 서비스입니다.', function() {
            goToLogin();
        });
        return;
    }

    const ctx = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${ctx}/page/myInfo`;
    }, 300);
}

// 예약 내역 페이지로 이동
function goToReservations() {
    // 로그인 상태 재확인
    if (!isLoggedIn()) {
        showInfoModal('로그인 필요', '로그인이 필요한 서비스입니다.', function() {
            goToLogin();
        });
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
        showInfoModal('로그인 필요', '로그인이 필요한 서비스입니다.', function() {
            goToLogin();
        });
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

// 개선된 로그아웃 함수 (서버 API 사용)
function logout() {
    ModalUtils.showConfirm('로그아웃', '정말로 로그아웃하시겠습니까?', function() {
        showLoadingAnimation();

        // 서버 로그아웃 API 호출
        fetch('/AirBnG/members/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include' // 세션 쿠키 포함
        })
        .then(response => {
            console.log('서버 응답 상태:', response.status);
            return response.json();
        })
        .then(data => {
            console.log('서버 응답 데이터:', data);

            // BaseResponse 구조에 맞게 수정
            // code가 2000인 경우 성공으로 판단 (HTTP 200 OK와 유사한 성공 코드)
            if (data.code === 2000) {
                // 서버 로그아웃 성공
                console.log('서버 로그아웃 성공:', data.message);

                // 클라이언트 쿠키 삭제
                clearAllCookies();

                // 세션 데이터 초기화
                resetSessionData();

                // UI 업데이트
                setTimeout(() => {
                    hideLoadingAnimation();
                    showLoggedOutSection();
                    console.log('로그아웃 완료');
                }, 500);

            } else {
                // 서버 응답은 받았지만 로그아웃 실패
                console.error('로그아웃 실패:', data.message);
                hideLoadingAnimation();
                showErrorModal('로그아웃 실패', data.message || '로그아웃 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('로그아웃 API 요청 실패:', error);

            // 네트워크 오류 등으로 서버 요청이 실패해도 클라이언트 정리는 수행
            clearAllCookies();
            resetSessionData();
            hideLoadingAnimation();
            showLoggedOutSection();

            // 사용자에게 알림 (선택사항)
            showErrorModal('알림', '서버와의 통신에 문제가 있었지만 로그아웃이 완료되었습니다.');
        });
    });
}

function showLoggedOutSection() {
    console.log('로그아웃 상태 UI 업데이트 시작');

    const loggedOutSection = document.getElementById('loggedOutSection');
    const loggedInSection = document.getElementById('loggedInSection');

    // 메인 섹션 업데이트
    if (loggedOutSection) {
        loggedOutSection.style.display = 'block';
        console.log('로그아웃 섹션 표시');
    } else {
        console.error('loggedOutSection 요소를 찾을 수 없습니다.');
    }

    if (loggedInSection) {
        loggedInSection.style.display = 'none';
        console.log('로그인 섹션 숨김');
    } else {
        console.error('loggedInSection 요소를 찾을 수 없습니다.');
    }

    // 추가: 클래스 기반 UI 업데이트
    document.querySelectorAll('.logged-in-only').forEach(el => {
        el.style.display = 'none';
        console.log('로그인 전용 요소 숨김:', el);
    });

    document.querySelectorAll('.logged-out-only').forEach(el => {
        el.style.display = 'block';
        console.log('로그아웃 전용 요소 표시:', el);
    });

    // 사용자 정보 초기화
    const usernameElement = document.querySelector('.username');
    if (usernameElement) {
        usernameElement.textContent = '';
        console.log('사용자명 초기화');
    }

    console.log('로그아웃 상태 UI 업데이트 완료');
}

// 개선된 쿠키 삭제 함수
function clearAllCookies() {
    // 현재 도메인 정보 가져오기
    const currentDomain = window.location.hostname;
    const currentPath = window.location.pathname;
    const contextPath = getContextPath();

    console.log('쿠키 삭제 시작...');
    console.log('현재 도메인:', currentDomain);
    console.log('현재 경로:', currentPath);
    console.log('컨텍스트 경로:', contextPath);

    // 삭제할 쿠키 목록
    const cookiesToDelete = [
        'JSESSIONID',
        'memberId',
        'nickname',
        'email',
        'loginMemberId',
        'loginNickname',
        'loginEmail',
        'remember-me',
        'SESSION'
    ];

    // 시도할 도메인 조합
    const domains = [
        '', // 도메인 설정 없음
        currentDomain,
        `.${currentDomain}`,
        'localhost',
        '.localhost'
    ];

    // 시도할 경로 조합
    const paths = [
        '/',
        contextPath + '/',
        contextPath,
        currentPath,
        ''
    ];

    // 모든 쿠키에 대해 모든 도메인/경로 조합으로 삭제 시도
    cookiesToDelete.forEach(cookieName => {
        domains.forEach(domain => {
            paths.forEach(path => {
                let deleteString = `${cookieName}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; max-age=0;`;

                if (path) {
                    deleteString += ` path=${path};`;
                }

                if (domain) {
                    deleteString += ` domain=${domain};`;
                }

                // Secure 속성도 시도 (HTTPS인 경우)
                if (window.location.protocol === 'https:') {
                    deleteString += ' secure;';
                }

                document.cookie = deleteString;

                // SameSite 속성 추가 시도
                document.cookie = deleteString + ' samesite=lax;';
                document.cookie = deleteString + ' samesite=strict;';
                document.cookie = deleteString + ' samesite=none;';
            });
        });
    });

    // 기존 쿠키 파싱해서 모든 쿠키 삭제 시도
    const allCookies = document.cookie.split(';');
    allCookies.forEach(cookie => {
        const eqPos = cookie.indexOf('=');
        const name = eqPos > -1 ? cookie.substr(0, eqPos).trim() : cookie.trim();

        if (name) {
            domains.forEach(domain => {
                paths.forEach(path => {
                    let deleteString = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; max-age=0;`;

                    if (path) {
                        deleteString += ` path=${path};`;
                    }

                    if (domain) {
                        deleteString += ` domain=${domain};`;
                    }

                    document.cookie = deleteString;
                });
            });
        }
    });

    console.log('쿠키 삭제 완료');
    console.log('남은 쿠키:', document.cookie);
}

// 세션 데이터 초기화
function resetSessionData() {
    if (typeof sessionData !== 'undefined') {
        sessionData.memberId = '';
        sessionData.nickname = '';
        sessionData.email = '';
        sessionData.isLoggedIn = false;
    }

    console.log('세션 데이터 초기화 완료');
}

// 로그아웃 상태 확인 (디버깅용)
function checkLogoutStatus() {
    console.log('=== 로그아웃 상태 확인 ===');
    console.log('현재 쿠키:', document.cookie);
    console.log('세션 데이터:', sessionData);
    console.log('로그인 상태:', isLoggedIn());
    console.log('========================');
}

// 강제 새로고침 로그아웃 (최후 수단)
function forceLogout() {
    clearAllCookies();
    resetSessionData();

    // 페이지 새로고침으로 서버에서 세션 상태 다시 확인
    setTimeout(() => {
        window.location.reload();
    }, 100);
}

// 세션 관련 쿠키 삭제
function clearSessionCookies() {
    // 모든 쿠키 가져오기
    const cookies = document.cookie.split(';');

    // 각 쿠키에 대해 삭제 처리
    cookies.forEach(cookie => {
        const eqPos = cookie.indexOf('=');
        const name = eqPos > -1 ? cookie.substr(0, eqPos).trim() : cookie.trim();

        if (name) {
            // 다양한 경로와 도메인에서 삭제 시도
            const deletePaths = ['/', contextPath + '/', contextPath];
            const domains = ['', `.${window.location.hostname}`, window.location.hostname];

            deletePaths.forEach(path => {
                domains.forEach(domain => {
                    let deleteString = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=${path};`;
                    if (domain) {
                        deleteString += ` domain=${domain};`;
                    }
                    document.cookie = deleteString;
                });
            });
        }
    });

    // 추가로 특정 쿠키들 강제 삭제
    const specificCookies = ['JSESSIONID', 'memberId', 'nickname', 'email', 'loginMemberId', 'loginNickname', 'loginEmail'];
    specificCookies.forEach(cookieName => {
        document.cookie = `${cookieName}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/; max-age=0;`;
        document.cookie = `${cookieName}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=${contextPath}/; max-age=0;`;
        document.cookie = `${cookieName}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=${contextPath}; max-age=0;`;
    });

    console.log('모든 쿠키 삭제 완료');
    console.log('남은 쿠키:', document.cookie);
}

// 수동 새로고침 함수 (필요시 사용)
function refreshUserInfo() {
    checkLoginStatus();
    console.log('사용자 정보가 새로고침되었습니다.');
}