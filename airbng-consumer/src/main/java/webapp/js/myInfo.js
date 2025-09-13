let selectedProfileImage = null;
let nicknameChecked = false;
let originalNickname = '';

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
    // 로그인 여부 확인
    if (!sessionData.isLoggedIn || !sessionData.memberId) {
        ModalUtils.showWarning('로그인이 필요합니다.');
        window.location.href = `${contextPath}/page/login`;
        return;
    }

    // 사용자 정보 로드
    loadUserProfile();
});

function goBack() {
    window.history.back();
}

function goToMyPage() {
    window.location.href = `${contextPath}/page/mypage`;
}

// 사용자 정보 로드
async function loadUserProfile() {
    const loadingContainer = document.getElementById('loading-container');
    const form = document.querySelector('.edit-profile-form');

    loadingContainer.classList.remove('hidden');
    form.style.display = 'none';

    try {
        const response = await fetch(`${contextPath}/members/my-page/${sessionData.memberId}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        const result = await response.json();

        if (response.ok && result.code === 1000) {
            const userData = result.result;

            // 폼 데이터 설정
            document.getElementById('email').value = userData.email || '';
            document.getElementById('name').value = userData.name || '';
            document.getElementById('phone').value = formatPhoneNumber(userData.phone || '');
            document.getElementById('nickname').value = userData.nickname || '';

            // 원래 닉네임 저장 (중복 체크용)
            originalNickname = userData.nickname || '';

            // 프로필 이미지 설정
            const profilePreview = document.getElementById('profile-preview');
            if (userData.url) {
                profilePreview.src = userData.url;
            } else {
                profilePreview.src = 'https://airbngbucket.s3.ap-northeast-2.amazonaws.com/profiles/8e99db50-0a6c-413e-a42c-c5213dc9d64a_default.jpg';
            }

            // 전화번호 포맷팅 이벤트 추가
            setupPhoneNumberFormatting();

            // 닉네임 실시간 검증 이벤트 추가
            setupNicknameValidation();

        } else {
            showError(result.message || '사용자 정보를 불러오는데 실패했습니다.');
        }

    } catch (error) {
        console.error('사용자 정보 로드 오류:', error);
        showError('서버 오류로 정보를 불러오는데 실패했습니다.');
    } finally {
        loadingContainer.classList.add('hidden');
        form.style.display = 'flex';
    }
}

// 전화번호 포맷팅 함수
function formatPhoneNumber(phone) {
    if (!phone) return '';

    // 숫자만 추출
    const numbers = phone.replace(/\D/g, '');

    if (numbers.length === 11) {
        return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7)}`;
    }
    return phone;
}

// 전화번호 입력 포맷팅 설정
function setupPhoneNumberFormatting() {
    const phoneInput = document.getElementById('phone');

    phoneInput.addEventListener('input', function(e) {
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
}

// 닉네임 실시간 검증 설정
function setupNicknameValidation() {
    const nicknameInput = document.getElementById('nickname');

    nicknameInput.addEventListener('input', function(e) {
        const currentNickname = e.target.value.trim();

        // 닉네임이 원래 값과 동일한지 확인
        if (currentNickname === originalNickname) {
            // 원래 닉네임과 같으면 자동으로 검증 완료 상태로 설정
            showValidationMessage('nickname', '현재 사용 중인 닉네임입니다.', 'success');
            const nicknameButton = document.getElementById('nickname-check-btn');
            if (nicknameButton) {
                nicknameButton.classList.add('success');
                nicknameButton.textContent = '확인완료';
            }
            nicknameChecked = true;
        } else {
            // 닉네임이 변경되었으면 검증 상태 리셋
            resetNicknameValidation();
        }
    });
}

// 프로필 이미지 변경 처리
function handleProfileImageChange(event) {
    const file = event.target.files[0];
    if (file) {
        // 파일 크기 체크 (5MB 제한)
        if (file.size > 10 * 1024 * 1024) {
            ModalUtils.showWarning('파일 크기는 10MB 이하여야 합니다.');
            return;
        }

        // 이미지 파일 체크
        if (!file.type.startsWith('image/')) {
            ModalUtils.showWarning('이미지 파일만 업로드 가능합니다.');
            return;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('profile-preview').src = e.target.result;
        };
        reader.readAsDataURL(file);
        selectedProfileImage = file;
    }
}

// 닉네임 검증 리셋
function resetNicknameValidation() {
    const input = document.getElementById('nickname');
    const message = document.getElementById('nickname-message');
    const button = document.getElementById('nickname-check-btn');

    input.classList.remove('success', 'error');
    message.textContent = '';

    if (button) {
        button.classList.remove('success');
        button.textContent = '중복확인';
        nicknameChecked = false;
    }
}

// 닉네임 중복 확인
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

    // 기존 닉네임과 같으면 중복 확인 생략
    if (nickname === originalNickname) {
        showValidationMessage('nickname', '현재 사용 중인 닉네임입니다.', 'success');
        nicknameButton.classList.add('success');
        nicknameButton.textContent = '확인완료';
        nicknameChecked = true;
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
            showValidationMessage('nickname', '너무 많은 요청 발생. 잠시 후 다시 시도해주세요.', 'error');
        } else {
            showValidationMessage('nickname', '이미 사용 중인 닉네임입니다.', 'error');
        }
    } catch (error) {
        console.error('닉네임 중복 검사 오류:', error);
        showValidationMessage('nickname', '중복 확인 중 오류가 발생했습니다.', 'error');
    } finally {
        nicknameButton.disabled = false;
        if (!nicknameChecked) nicknameButton.textContent = '중복확인';
    }
}

// 닉네임 변경 여부 확인 함수
function isNicknameChanged() {
    const currentNickname = document.getElementById('nickname').value.trim();
    return currentNickname !== originalNickname;
}

// 검증 메시지 출력 함수
function showValidationMessage(fieldId, message, status) {
    const input = document.getElementById(fieldId);
    const messageEl = document.getElementById(`${fieldId}-message`);

    input.classList.remove('success', 'error');
    messageEl.classList.remove('success', 'error');

    input.classList.add(status);
    messageEl.classList.add(status);
    messageEl.textContent = message;
}

// 에러 메시지 출력
function showError(message) {
    const container = document.getElementById('error-container');
    container.innerHTML = `
      <div class="error-message">
        ${message}
      </div>
    `;
}

// 성공 모달 표시
function showSuccessModal() {
    const modal = document.getElementById('success-modal');
    modal.classList.remove('hidden');
}

// 프로필 수정 제출 처리
async function handleProfileUpdate(event) {
    event.preventDefault();

    const nickname = document.getElementById('nickname').value.trim();
    const phone = document.getElementById('phone').value.replace(/\D/g, '');
    const email = document.getElementById('email').value.trim();
    const name = document.getElementById('name').value.trim();

    // 닉네임이 변경되었고 중복 확인이 안된 경우만 체크
    if (isNicknameChanged() && !nicknameChecked) {
        ModalUtils.showWarning('닉네임 중복 확인을 해주세요.');
        return;
    }

    // MemberUpdateRequest 객체 생성
    const memberUpdateRequest = {
        memberId: parseInt(sessionData.memberId),
        email: email,
        name: name,
        phone: phone,
        nickname: nickname
    };

    const formData = new FormData();

    // JSON 데이터를 Blob으로 변환하여 추가
    formData.append('memberUpdateRequest', new Blob([JSON.stringify(memberUpdateRequest)], {
        type: 'application/json'
    }));

    // 프로필 이미지가 있으면 추가
    if (selectedProfileImage) {
        formData.append('profileImage', selectedProfileImage);
    }

    const saveBtn = document.getElementById('save-btn');
    saveBtn.disabled = true;
    saveBtn.textContent = '저장 중...';

    try {
        const response = await fetch(`${contextPath}/members/my-page/update`, {
            method: 'POST',
            body: formData
        });

        const result = await response.json();

        if (response.ok && result.code === 1000) {
            showSuccessModal();
        } else {
            showError(result.message || '정보 수정에 실패했습니다.');
        }

    } catch (error) {
        console.error('정보 수정 오류:', error);
        showError('서버 오류로 인해 정보를 수정할 수 없습니다.');
    } finally {
        saveBtn.disabled = false;
        saveBtn.textContent = '수정 완료';
    }
}