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

// 로딩 애니메이션 숨기기
function hideLoadingAnimation() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.remove();
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

// 로그인 페이지로 이동
function goToLogin() {
    const ctx = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.replace(`${ctx}/page/login`);
    }, 150);
}

// 회원가입 페이지로 이동
function goToSignup() {
    const ctx = getContextPath();
    showLoadingAnimation();

    setTimeout(() => {
        window.location.href = `${ctx}/page/signup`;
    }, 150);
}
