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