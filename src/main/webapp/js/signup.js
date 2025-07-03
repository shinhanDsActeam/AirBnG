let selectedProfileImage = null;
let emailChecked = false;
let nicknameChecked = false;

function goBack() {
    window.history.back();
}

function goToLogin() {
    window.location.href = `${contextPath}/page/login`;
}

function handleProfileImageChange(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('profile-preview').src = e.target.result;
        };
        reader.readAsDataURL(file);
        selectedProfileImage = file;
    }
}

function resetValidation(field) {
    const input = document.getElementById(field);
    const message = document.getElementById(field + '-message');
    const button = document.getElementById(field + '-check-btn');

    input.classList.remove('success', 'error');
    message.textContent = '';

    if (button) {
        button.classList.remove('success');
        button.textContent = '중복확인';
        if (field === 'email') emailChecked = false;
        else if (field === 'nickname') nicknameChecked = false;
    }
}

async function checkEmailDuplicate() {
    const email = document.getElementById('email').value.trim();
    const emailButton = document.getElementById('email-check-btn');

    if (!email) {
        showValidationMessage('email', '이메일을 입력해주세요.', 'error');
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showValidationMessage('email', '올바른 이메일 형식이 아닙니다.', 'error');
        return;
    }

    emailButton.disabled = true;
    emailButton.textContent = '확인중...';

    try {
        const response = await fetch(`${contextPath}/members/check-email?email=${encodeURIComponent(email)}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        const data = await response.json();

        if (response.ok && data.code === 1000) {
            showValidationMessage('email',  '사용 가능한 이메일입니다.', 'success');
            emailButton.classList.add('success');
            emailButton.textContent = '확인완료';
            emailChecked = true;
        } else {
            showValidationMessage('email', data.message || '이미 사용 중인 이메일입니다.', 'error');
        }

    } catch (error) {
        console.error('이메일 중복 검사 오류:', error);
        showValidationMessage('email', '서버 오류로 이메일 확인에 실패했습니다.', 'error');
    } finally {
        emailButton.disabled = false;
        if (emailButton.textContent === '확인중...') {
            emailButton.textContent = '중복확인';
        }
    }
}

document.getElementById('phone').addEventListener('input', function (e) {
    let value = e.target.value.replace(/\D/g, ''); // 숫자만 추출

    if (value.length < 4) {
        e.target.value = value;
    } else if (value.length < 8) {
        e.target.value = `${value.slice(0, 3)}-${value.slice(3)}`;
    } else if (value.length <= 11) {
        e.target.value = `${value.slice(0, 3)}-${value.slice(3, 7)}-${value.slice(7)}`;
    } else {
        e.target.value = `${value.slice(0, 3)}-${value.slice(3, 7)}-${value.slice(7, 11)}`;
    }
});

async function checkNicknameDuplicate() {
    const nickname = document.getElementById('nickname').value.trim();
    const nicknameButton = document.getElementById('nickname-check-btn');

    if (!nickname) {
        showValidationMessage('nickname', '닉네임을 입력해주세요.', 'error');
        return;
    }

    if (nickname.length < 2) {
        showValidationMessage('nickname', '닉네임은 2자 이상이어야 합니다.', 'error');
        return;
    }

    nicknameButton.disabled = true;
    nicknameButton.textContent = '확인중...';

    try {
        const response = await fetch(`${contextPath}/members/check-nickname?nickname=${encodeURIComponent(nickname)}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        const data = await response.json();

        if (response.ok && data.code === 1000) {
            showValidationMessage('nickname', '사용 가능한 닉네임입니다.', 'success');
            nicknameButton.classList.add('success');
            nicknameButton.textContent = '확인완료';
            nicknameChecked = true;
        } else if (response.status === 429) {
            showValidationMessage('nickname', '너무 많은 요청 발생. 잠시 후 다시 시도해주세요.', 'error')
        }
        else {
            showValidationMessage('nickname',  '이미 사용 중인 닉네임입니다.', 'error');
        }
    } catch (error) {
        console.error('닉네임 중복 검사 오류:', error);
        showValidationMessage('nickname', '서버 오류로 닉네임 확인에 실패했습니다.', 'error');
    } finally {
        nicknameButton.disabled = false;
        if (nicknameButton.textContent === '확인중...') {
            nicknameButton.textContent = '중복확인';
        }
    }
}

function showValidationMessage(field, message, type) {
    const input = document.getElementById(field);
    const messageElement = document.getElementById(field + '-message');

    input.classList.remove('success', 'error');
    input.classList.add(type);

    messageElement.textContent = message;
    messageElement.classList.remove('success', 'error');
    messageElement.classList.add(type);
}

function validatePassword(password) {
    const minLength = 8;
    const hasUpper = /[A-Z]/.test(password);
    const hasLower = /[a-z]/.test(password);
    const hasDigit = /\d/.test(password);

    if (password.length < minLength) {
        return '비밀번호는 8자 이상이어야 합니다.';
    }
    if (!hasUpper || !hasLower) {
        return '대문자와 소문자를 모두 포함해야 합니다.';
    }
    if (!hasDigit) {
        return '숫자를 포함해야 합니다.';
    }

    return null; // 통과
}

document.getElementById('password').addEventListener('input', function () {
    const msg = validatePassword(this.value);
    if (this.value) {
        showValidationMessage('password', msg || '사용 가능한 비밀번호입니다.', msg ? 'error' : 'success');
    } else {
        document.getElementById('password-message').textContent = '';
        this.classList.remove('success', 'error');
    }
});

document.getElementById('password-confirm').addEventListener('input', function () {
    const pw = document.getElementById('password').value;
    if (this.value) {
        showValidationMessage('password-confirm', pw === this.value ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.', pw === this.value ? 'success' : 'error');
    } else {
        document.getElementById('password-confirm-message').textContent = '';
        this.classList.remove('success', 'error');
    }
});

function showError(message) {
    const container = document.getElementById('error-container');
    container.innerHTML = `<div class=\"error-message\">${message}</div>`;
    container.scrollIntoView({ behavior: 'smooth' });
}

async function handleSignup(event) {
    event.preventDefault();

    const email = document.getElementById('email').value.trim();
    const name = document.getElementById('name').value.trim();
    const phoneRaw = document.getElementById('phone').value.trim();
    const phone = phoneRaw.replace(/-/g, ''); //숫자만 추출
    const nickname = document.getElementById('nickname').value.trim();
    const password = document.getElementById('password').value;
    const passwordConfirm = document.getElementById('password-confirm').value;

    if (!email || !name || !phone || !nickname || !password || !passwordConfirm) {
        return showError('모든 필드를 입력해주세요.');
    }
    if (!emailChecked) {
        return showError('이메일 중복 확인을 해주세요.');
    }
    if (!nicknameChecked) {
        return showError('닉네임 중복 확인을 해주세요.');
    }
    if (password !== passwordConfirm) {
        return showError('비밀번호가 일치하지 않습니다.');
    }

    const passwordError = validatePassword(password);
    if (passwordError) {
        return showError(passwordError);
    }

    const signupButton = document.getElementById('signup-btn');
    signupButton.disabled = true;
    signupButton.textContent = '가입 중...';

    try {
        const formData = new FormData();

        const memberData = {
            email,
            name,
            phone,
            nickname,
            password
        };

        formData.append('profile', new Blob([JSON.stringify(memberData)], {
            type: 'application/json'
        }));

        if (selectedProfileImage) {
            formData.append('profileImage', selectedProfileImage);
        }

        const response = await fetch(`${contextPath}/members/signup`, {
            method: 'POST',
            body: formData
        });

        const result = await response.json();

        if (response.ok && result.code === 1000) {
            document.getElementById('success-modal').classList.remove('hidden');
        } else {
            showError(result.message || '회원가입에 실패했습니다.');
        }

    } catch (error) {
        console.error('회원가입 오류:', error);
        showError('서버 오류로 회원가입에 실패했습니다.');
    } finally {
        signupButton.disabled = false;
        signupButton.textContent = '회원가입';
    }
}