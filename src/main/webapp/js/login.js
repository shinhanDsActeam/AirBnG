// 페이지 로드시 로그인 쿨다운 상태 확인
document.addEventListener('DOMContentLoaded', () => {
    if (sessionStorage.getItem('loginCooldownUntil')) {
        updateCountdown();
    }
});

// login.js
document.querySelector('.login-form').addEventListener('submit', function(e) {
    e.preventDefault();

    const email = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();

    if (!email || !password) {
        showErrorModal();
        return;
    }

    const requestData = {
        email: email,
        password: password
    };

    fetch(`${contextPath}/members/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
        .then(async response => {
            const data = await response.json();
            if (response.status === 429 || data.code === 8003) {
                startCooldown(30); // 30초동안 로그인 시도 제한
                showWarningModal();
                return;
            }

            if (data.code === 2000) {
                document.getElementById('success-modal').classList.remove('hidden');
                document.addEventListener('keydown', function handleEnter(e) {
                    if (e.key === 'Enter') {
                        confirmLoginSuccess();
                        document.removeEventListener('keydown', handleEnter);
                    }
                });
            } else {
                showErrorModal();
            }
        })
        .catch(err => {
            console.error('로그인 요청 실패:', err);
            showErrorModal();
        });
});

const loginButton = document.querySelector('.login-button');

function startCooldown(seconds) {
    const endTime = Date.now() + seconds * 1000;
    sessionStorage.setItem('loginCooldownUntil', endTime);

    updateCountdown();
}

function updateCountdown() {
    const originalText = '로그인';

    function applyCountdown() {
        const endTime = parseInt(sessionStorage.getItem('loginCooldownUntil'), 10);
        const now = Date.now();
        const remaining = Math.max(0, Math.floor((endTime - now) / 1000));

        if (remaining > 0) {
            loginButton.disabled = true;
            loginButton.textContent = `${remaining}초 후 시도해주세요`;
            loginButton.classList.add('disabled');
            return true;
        } else {
            loginButton.disabled = false;
            loginButton.textContent = originalText;
            loginButton.classList.remove('disabled');
            sessionStorage.removeItem('loginCooldownUntil');
            return false;
        }
    }

    // 최초 한 번 즉시 실행
    const shouldContinue = applyCountdown();

    if (shouldContinue) {
        const interval = setInterval(() => {
            const stillCounting = applyCountdown();
            if (!stillCounting) {
                clearInterval(interval);
            }
        }, 1000);
    }
}

function confirmLoginSuccess() {
    document.getElementById('success-modal').classList.add('hidden');
    location.href = `${contextPath}/page/home`;
}

function closeErrorModal() {
    document.getElementById('error-modal').classList.add('hidden');
}

function showErrorModal() {
    document.getElementById('error-modal').classList.remove('hidden');
}

function showWarningModal() {
    document.getElementById('warning-modal').classList.remove('hidden');

    function handleWarningEnter(e) {
        if (e.key === 'Enter') {
            closeWarningModal();
            document.removeEventListener('keydown', handleWarningEnter);
        }
    }
    document.addEventListener('keydown', handleWarningEnter);
}

function closeWarningModal() {
    document.getElementById('warning-modal').classList.add('hidden');
}