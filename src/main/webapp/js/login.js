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
        .then(response => response.json())
        .then(data => {
            if (data.code === 2000) {
                document.getElementById('success-modal').classList.remove('hidden');
            } else {
                showErrorModal();
            }
        })
        .catch(err => {
            console.error(err);
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