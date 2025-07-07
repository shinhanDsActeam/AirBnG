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
                ModalUtils.showWarning('잠시 후 다시 시도해주세요.','요청이 너무 많습니다');
                return;
            }

            if (data.code === 2000) {
                ModalUtils.showSuccess(' ', '로그인 성공',
                    ()=>{ window.location.href = `${contextPath}/page/home` });
            } else {
                showErrorModal();
            }
        })
        .catch(err => {
            console.error('로그인 요청 실패:', err);
            ModalUtils.showError('이메일 또는 비밀번호가 올바르지 않습니다.', '로그인 실패', closeErrorModal);
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
