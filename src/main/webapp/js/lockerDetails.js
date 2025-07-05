window.addEventListener("DOMContentLoaded", function () {
    // URL에서 lockerId 추출
    const urlParams = new URLSearchParams(window.location.search);
    const lockerId = urlParams.get('lockerId');

    if (!lockerId) {
        showError('보관소 ID가 제공되지 않았습니다.');
        return;
    }

    // 보관소 데이터 로드
    loadLockerDetails(lockerId);

    // 보관소 선택 버튼 클릭 이벤트
    const reserveBtn = document.getElementById("reserveBtn");
    reserveBtn?.addEventListener("click", function () {
        // 버튼이 비활성화 상태면 동작하지 않음
        if (reserveBtn.disabled) {
            return;
        }

        const lockerId = reserveBtn.dataset.lockerId;

        if (!lockerId || !memberId) {
            alert('로그인이 필요합니다.');
            return;
        }

        window.location.href = `${contextPath}/page/reservations/form?lockerId=${lockerId}`;
    });
});

/**
 * 보관소 상세 정보를 API에서 가져오는 함수
 */
async function loadLockerDetails(lockerId) {
    try {
        showLoading(true);

        const response = await fetch(`${contextPath}/lockers/${lockerId}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        if (data.code === 1000 && data.result) {
            renderLockerDetails(data.result);
        } else {
            throw new Error(data.message || '보관소 정보를 불러오는데 실패했습니다.');
        }

    } catch (error) {
        console.error('API 호출 에러:', error);
        showError('보관소 정보를 불러오는데 실패했습니다.');
    } finally {
        showLoading(false);
    }
}

/**
 * 보관소 상세 정보를 화면에 렌더링
 */
function renderLockerDetails(lockerDetail) {
    // 보관소 이름
    document.getElementById('lockerTitle').textContent = lockerDetail.lockerName;

    // 이미지 갤러리 렌더링
    renderImageGallery(lockerDetail.images);

    // 가격 정보 렌더링
    renderPriceInfo(lockerDetail.jimTypeResults);

    // 보관소 정보 렌더링
    renderLockerInfo(lockerDetail);

    // 보관소 선택 버튼 설정
    setupReserveButton(lockerDetail.lockerId, lockerDetail.isAvailable);

    // 컨텐츠 표시
    document.getElementById('lockerDetailContent').style.display = 'block';
    document.getElementById('reserveBtn').style.display = 'flex';
}

/**
 * 이미지 갤러리 렌더링
 */
function renderImageGallery(images) {
    const imageGallery = document.getElementById('imageGallery');

    if (!images || images.length === 0) {
        imageGallery.style.display = 'none';
        return;
    }

    imageGallery.innerHTML = '';

    // 최대 6개 이미지만 표시
    const maxImages = Math.min(images.length, 6);

    for (let i = 0; i < maxImages; i++) {
        const img = document.createElement('img');
        img.className = 'locker-image';
        img.src = images[i];
        img.alt = `보관소 이미지 ${i + 1}`;
        img.onerror = function() {
            this.style.display = 'none'; // 이미지 로드 실패 시 숨김
        };
        imageGallery.appendChild(img);
    }

    imageGallery.style.display = 'block';
}

/**
 * 가격 정보 렌더링
 */
function renderPriceInfo(jimTypeResults) {
    const priceTitle = document.getElementById('priceTitle');
    const priceDetail = document.getElementById('priceDetail');

    if (!jimTypeResults || jimTypeResults.length === 0) {
        priceTitle.textContent = '가격 정보 없음';
        priceDetail.innerHTML = '• 가격 정보를 확인할 수 없습니다.';
        return;
    }

    // 짐 타입 이름들을 "/"로 구분하여 표시
    const typeNames = jimTypeResults.map(type => type.typeName).join('/');
    priceTitle.textContent = typeNames;

    // 가격 정보 생성
    let priceDetailHtml = '';

    // 각 짐 타입별 가격 정보
    jimTypeResults.forEach(type => {
        priceDetailHtml += `• ${type.typeName}: 시간당 ${type.pricePerHour.toLocaleString()}원<br>`;
    });

    // 고정 정보 추가 (필요시 수정)
    priceDetailHtml += '• 강남역 도보 3분';

    priceDetail.innerHTML = priceDetailHtml;
}

/**
 * 보관소 정보 렌더링
 */
function renderLockerInfo(lockerDetail) {
    // 주소 정보
    const fullAddress = `${lockerDetail.address} ${lockerDetail.addressDetail || ''}`.trim();
    document.getElementById('address').textContent = ` | ${fullAddress}`;
    document.getElementById('addressEnglish').textContent = ` | ${lockerDetail.addressEnglish}`;

    // 관리자 정보
    document.getElementById('keeperName').textContent = ` | ${lockerDetail.keeperName}`;

    // 전화번호 포맷팅
    const formattedPhone = formatPhoneNumber(lockerDetail.keeperPhone);
    document.getElementById('keeperPhone').textContent = ` | ${formattedPhone}`;
}

/**
 * 보관소 선택 버튼 설정
 */
function setupReserveButton(lockerId, isAvailable) {
    const reserveBtn = document.getElementById('reserveBtn');
    if (reserveBtn) {
        reserveBtn.dataset.lockerId = lockerId;

        // 이용 가능 여부에 따른 버튼 상태 설정
        if (isAvailable === 'NO') {
            reserveBtn.disabled = true;
            reserveBtn.classList.add('disabled');
            reserveBtn.textContent = '이용 불가';
        } else {
            reserveBtn.disabled = false;
            reserveBtn.classList.remove('disabled');
            reserveBtn.textContent = '보관소 선택';
        }
    }
}

/**
 * 전화번호 포맷팅 함수
 */
function formatPhoneNumber(phone) {
    if (!phone) return '';

    // 숫자만 추출
    const numbers = phone.replace(/\D/g, '');

    // 11자리 휴대폰 번호인 경우
    if (numbers.length === 11) {
        return `${numbers.substring(0, 3)}-${numbers.substring(3, 7)}-${numbers.substring(7, 11)}`;
    }

    // 10자리 일반 전화번호인 경우
    if (numbers.length === 10) {
        return `${numbers.substring(0, 3)}-${numbers.substring(3, 6)}-${numbers.substring(6, 10)}`;
    }

    // 기타 경우 그대로 반환
    return phone;
}

/**
 * 로딩 상태 표시/숨김
 */
function showLoading(show) {
    const loadingState = document.getElementById('loadingState');
    const errorState = document.getElementById('errorState');
    const detailContent = document.getElementById('lockerDetailContent');
    const reserveBtn = document.getElementById('reserveBtn');

    if (show) {
        loadingState.style.display = 'block';
        errorState.style.display = 'none';
        detailContent.style.display = 'none';
        reserveBtn.style.display = 'none';
    } else {
        loadingState.style.display = 'none';
    }
}

/**
 * 에러 상태 표시
 */
function showError(message) {
    const loadingState = document.getElementById('loadingState');
    const errorState = document.getElementById('errorState');
    const detailContent = document.getElementById('lockerDetailContent');
    const reserveBtn = document.getElementById('reserveBtn');

    loadingState.style.display = 'none';
    errorState.style.display = 'block';
    errorState.textContent = message;
    detailContent.style.display = 'none';
    reserveBtn.style.display = 'none';
}