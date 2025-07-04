/**
 * 네비게이션 바 활성화 상태 관리
 * navbar.jsp와 함께 사용
 */

function setActiveNavigation() {
    const path = window.location.pathname;
    let currentPage;

    // 현재 페이지 결정 로직 개선
    if (path.includes('shopping-cart.jsp')) {
        currentPage = 'shopping-cart';
    } else if (path.includes('messages.jsp')) {
        currentPage = 'messages';
    } else if (path.includes('calendar.jsp')) {
        currentPage = 'calendar';
    } else if (path.includes('mypage.jsp')) {
        currentPage = 'mypage';
    } else if (path.includes('/page/home') || path.endsWith('/') || path.includes('home')) {
        currentPage = 'home';
    } else {
        // JSP 파일명에서 추출
        const fileName = path.split('/').pop();
        if (fileName.includes('.jsp')) {
            currentPage = fileName.replace('.jsp', '');
        } else {
            // RESTful 경로에서 추출
            const segments = path.split('/').filter(segment => segment.length > 0);
            currentPage = segments[segments.length - 1] || 'home';
        }
    }

    // DOM 요소 존재 확인
    const allNavItems = document.querySelectorAll('.nav-item');

    if (allNavItems.length === 0) {
        setTimeout(setActiveNavigation, 100);
        return;
    }

    // 모든 아이템의 active 클래스 제거
    allNavItems.forEach(item => {
        item.classList.remove('active');
    });

    // 현재 페이지에 해당하는 아이템에 active 클래스 추가
    const activeItem = document.querySelector(`.nav-item[data-page="${currentPage}"]`);

    if (activeItem) {
        activeItem.classList.add('active');
    } else {
        // 대체 방법: 부분 매칭으로 시도
        const fallbackItem = Array.from(allNavItems).find(item => {
            const href = item.getAttribute('href');
            return href && (href.includes(currentPage) || currentPage.includes(item.getAttribute('data-page')));
        });

        if (fallbackItem) {
            fallbackItem.classList.add('active');
        } else {
            // 기본값으로 홈 활성화
            const homeItem = document.querySelector('.nav-item[data-page="home"]');
            if (homeItem) {
                homeItem.classList.add('active');
            }
        }
    }
}

/**
 * 네비게이션 클릭 이벤트 처리
 */
function setupNavigationEvents() {
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', function(e) {
            // 즉시 시각적 피드백 (페이지 이동 전)
            document.querySelectorAll('.nav-item').forEach(navItem => {
                navItem.classList.remove('active');
            });
            this.classList.add('active');
        });
    });
}

/**
 * 초기화 함수
 */
function initNavigation() {
    setActiveNavigation();
    setupNavigationEvents();
}

// DOM 준비 완료 시 실행
document.addEventListener('DOMContentLoaded', initNavigation);

// 추가 보장을 위한 window load 이벤트
window.addEventListener('load', function() {
    setTimeout(setActiveNavigation, 50);
});

// 브라우저 뒤로가기/앞으로가기 시에도 상태 업데이트
window.addEventListener('popstate', function() {
    setTimeout(setActiveNavigation, 100);
});