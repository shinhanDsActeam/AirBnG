let currentStates = ['CONFIRMED', 'PENDING'];
let currentPeriod = 'ALL';
let nextCursorId = null;
let loading = false;
let hasNextPage = true;

const svgPaths = document.getElementById('svg-paths');
const spotSvgUrl = svgPaths?.dataset.spotUrl || '/images/spot.svg';       // 이용완료용
const blodspotSvgUrl = svgPaths?.dataset.blodspotUrl || '/images/blodspot.svg'; // 취소됨용

console.log(`Spot SVG URL: ${spotSvgUrl}`);
console.log(`Blodspot SVG URL: ${blodspotSvgUrl}`);

// 초기화
document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    fetchReservations(true);

    window.addEventListener('scroll', () => {
        if (loading || !hasNextPage) return;

        const scrollTop = window.scrollY;
        const viewportHeight = window.innerHeight;
        const fullHeight = document.body.offsetHeight;

        if (scrollTop + viewportHeight >= fullHeight - 100) {
            fetchReservations(false);  // 다음 커서 로드
        }
    });
});

// ▶ 탭 이벤트 초기화
function initTabs() {
    document.querySelectorAll('.tab').forEach(tab => {
        tab.addEventListener('click', () => {
            const states = tab.getAttribute('data-states').split(',');
            changeTab(states, tab);
        });
    });
}

// ▶ 탭 변경 처리
function changeTab(newStates, tabElem) {
    if (loading) return;

    currentStates = newStates;
    currentPeriod = 'ALL';
    nextCursorId = null;
    hasNextPage = true;

     // 화면 맨 위로 스크롤
     window.scrollTo({ top: 0, behavior: 'smooth' });  // 👈 이 줄 추가!

    // UI 갱신
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    tabElem.classList.add('active');

    const filterSection = document.getElementById('filter-section');
    const showFilter = currentStates.includes('COMPLETED') || currentStates.includes('CANCELLED');
    filterSection.classList.toggle('show', showFilter);
    document.getElementById('selected-period').textContent = '전체';

    clearReservationList();
    fetchReservations(true);
}

// ▶ 기간 드롭다운 토글 및 선택
function toggleDropdown() {
    document.getElementById('period-dropdown').classList.toggle('show');
    document.querySelector('.dropdown-btn').classList.toggle('open');
}

function selectPeriod(newPeriod, label) {
    if (loading) return;

    currentPeriod = newPeriod;
    nextCursorId = null;
    hasNextPage = true;

    document.getElementById('selected-period').textContent = label;
    document.getElementById('period-dropdown').classList.remove('show');
    document.querySelector('.dropdown-btn').classList.remove('open');

    clearReservationList();
    fetchReservations(true);
}

// ▶ 외부 클릭 시 드롭다운/더보기 닫기
document.addEventListener('click', e => {
    if (!e.target.closest('.period-dropdown')) {
        document.getElementById('period-dropdown').classList.remove('show');
        document.querySelector('.dropdown-btn').classList.remove('open');
    }

    if (!e.target.closest('.more-btn')) {
        document.querySelectorAll('.more-menu').forEach(menu => menu.classList.remove('show'));
    }
});

// ▶ 더보기 메뉴 토글
function toggleMoreMenu(reservationId) {
    const target = document.getElementById(`more-menu-${reservationId}`);
    document.querySelectorAll('.more-menu').forEach(menu => {
        if (menu !== target) menu.classList.remove('show');
    });
    target.classList.toggle('show');
}

// ▶ API URL 생성
function getApiUrl() {
    // memberId가 'DROPPER'로 시작하면 isDropper=true, 아니면 false
//    const isDropper = memberId && memberId.startsWith('DROPPER');
    const isDropper = true;
    const baseUrl = `/AirBnG/reservations?isDropper=${isDropper}&memberId=${memberId}`;
    const statesParam = currentStates.map(s => `&state=${s}`).join('');

    // 기간 필터링: 완료되거나 취소된 예약에만 적용
    const shouldApplyPeriodFilter = (['COMPLETED', 'CANCELLED'].some(s => currentStates.includes(s)) && currentPeriod !== 'ALL');
    const periodParam = shouldApplyPeriodFilter ? `&period=${currentPeriod}` : '';

    const cursorParam = (nextCursorId !== null && nextCursorId !== -1) ? `&nextCursorId=${nextCursorId}` : '';

    const apiUrl = baseUrl + statesParam + periodParam + cursorParam;
    console.log(`API URL: ${apiUrl}`);
    return apiUrl;
}

// ▶ 리스트 초기화
function clearReservationList() {
    document.getElementById('reservation-list').innerHTML = '';
    document.getElementById('empty-state').style.display = 'none';
}

// ▶ 상태 텍스트
function getStatusText(state) {
    const statusMap = {
        CONFIRMED: { text: '예약완료', class: 'confirmed' },
        PENDING: { text: '예약대기', class: 'pending' },
        CANCELLED: { text: '취소완료', class: 'cancelled' },
        COMPLETED: { text: '이용완료', class: 'completed' }
    };
    const s = statusMap[state] || { text: state, class: 'pending' };
    return `<span class="status-text ${s.class}">${s.text}</span>`;
}

// ▶ 예약 취소 / 삭제 / 다시예약
function cancelReservation(id) {
    if (!confirm('정말 이 예약을 취소하시겠습니까?')) return;
    fetch(`/AirBnG/reservations/${id}/cancel`, { method: 'POST' })
        .then(res => res.json())
        .then(handleActionResponse)
        .catch(handleError);
}

function reBooking(lockerId) {
    window.location.href = `/AirBnG/lockers/${lockerId}/reservation`;
}

function handleActionResponse(data) {
    if (data.code === 1000) {
        alert('처리 완료');
        nextCursorId = null;
        hasNextPage = true;
        clearReservationList();
        fetchReservations(true);
    } else {
        alert('처리 실패: ' + data.message);
    }
}

function handleError(err) {
    alert('오류 발생');
    console.error(err);
}

// ▶ 예약 카드 렌더링
function renderReservations(reservations) {
    const list = document.getElementById('reservation-list');

    reservations.forEach(res => {
        const card = document.createElement('div');
        card.className = 'reservation-card';
        if (res.state === 'CANCELLED') card.classList.add('cancelled');

        // 상태에 따라 다른 클래스 추가
        if (res.state === 'COMPLETED') card.classList.add('completed');
        if (res.state === 'CANCELLED') card.classList.add('cancelled-state');

        const jimTypes = Array.isArray(res.jimTypeResults) && res.jimTypeResults.length
            ? res.jimTypeResults.map(j => j.typeName || '타입명 없음').join(', ')
            : '정보 없음';

        const imageHtml = res.lockerImage
            ? `<img src="${res.lockerImage}" alt="보관소" class="item-image">`
            : `<div class="item-image"></div>`;

        // 이용완료와 취소완료 상태에 따라 다른 HTML 구조 사용
        if (res.state === 'COMPLETED') {
            // 이용완료: 날짜, 시작날짜, 찾는날짜 제거
            card.innerHTML = `
                <div class="reservation-header">
                    <div class="reservation-info-row">
                        <a href="/AirBnG/page/reservations?id=${res.reservationId}" class="view-details">예약 상세 &gt;</a>
                        ${getStatusText(res.state)}
                    </div>
                </div>
                <div class="reservation-item">
                    ${imageHtml}
                    <div class="item-info">
                        <div class="item-title">${res.lockerName || '보관소 정보 없음'}</div>
                        <div class="item-time">이용 시간: ${formatDuration(res.durationHours)}</div>
                        <div class="item-types">짐 종류: ${jimTypes}</div>
                    </div>
                </div>
                ${getActionButtons(res)}
            `;
        } else if (res.state === 'CANCELLED') {
            // 취소완료: 날짜, 시작날짜, 찾는날짜, 버튼 모두 제거, 더보기 메뉴 추가
            card.innerHTML = `
                <div class="reservation-inner">
                    <div class="reservation-header">
                        <div class="reservation-info-row">
                            <div class="more-btn always-visible" onclick="toggleMoreMenu(${res.reservationId})">
                                <img src="${blodspotSvgUrl}" class="more-icon" alt="더보기 아이콘" />
                                <div class="more-menu" id="more-menu-${res.reservationId}">
                                    <div class="more-menu-item" onclick="deleteReservation(${res.reservationId})">삭제</div>
                                </div>
                            </div>
                            ${getStatusText(res.state)}
                        </div>
                    </div>
                    <div class="reservation-body">
                        <div class="reservation-item">
                            ${imageHtml}
                            <div class="item-info">
                                <div class="item-title">${res.lockerName || '보관소 정보 없음'}</div>
                                <div class="item-time">이용 시간: ${formatDuration(res.durationHours)}</div>
                                <div class="item-types">짐 종류: ${jimTypes}</div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        } else {
            // 기존 구조 (예약완료, 예약대기)
            card.innerHTML = `
                <div class="reservation-header">
                    <div class="reservation-date">${formatDate(res.dateOnly)}</div>
                    <div class="reservation-info-row">
                        <a href="/AirBnG/page/reservations?id=${res.reservationId}" class="view-details">예약 상세 &gt;</a>
                        ${getStatusText(res.state)}
                    </div>
                </div>
                <div class="reservation-item">
                    ${imageHtml}
                    <div class="item-info">
                        <div class="item-title">${res.lockerName || '보관소 정보 없음'}</div>
                        <div class="item-time">이용 시간: ${formatDuration(res.durationHours)}</div>
                        <div class="item-types">짐 종류: ${jimTypes}</div>
                    </div>
                </div>
                <div class="item-date-row">
                    <div class="item-date-col">
                        <div class="label">시작 날짜</div>
                        <div class="value">${formatDateTime(res.startTime)}</div>
                    </div>
                    <div class="item-date-col">
                        <div class="label">찾는 날짜</div>
                        <div class="value">${formatDateTime(res.endTime)}</div>
                    </div>
                </div>
                ${getActionButtons(res)}
            `;
        }
        list.appendChild(card);
    });
}

// ▶ 버튼 렌더링
function getActionButtons(reservation) {
    if (reservation.state === 'PENDING') {
        return `<div class="action-buttons">
            <button class="btn btn-cancel" onclick="goToReservationDetail(${reservation.reservationId})">취소 요청</button>
        </div>`;
    }

    if (reservation.state === 'COMPLETED') {
        return `
            <div class="completed-actions">
                <button class="rebook-btn" onclick="reBooking(${reservation.lockerId || 1})">다시 예약</button>
                <div class="more-btn" onclick="toggleMoreMenu(${reservation.reservationId})">
                    <img src="${spotSvgUrl}" class="more-icon" alt="더보기 아이콘" />
                    <div class="more-menu" id="more-menu-${reservation.reservationId}">
                        <div class="more-menu-item" onclick="deleteReservation(${reservation.reservationId})">삭제</div>
                    </div>
                </div>
            </div>`;
    }

    if (reservation.state === 'CONFIRMED') {
        return `<div class="action-buttons">
            <button class="btn btn-cancel" onclick="goToReservationDetail(${reservation.reservationId})">취소 요청</button>
        </div>`;
    }

    // 취소완료 상태는 버튼 없음
    return '';
}

// ▶ 예약 상세 페이지로 이동하는 함수 추가
function goToReservationDetail(reservationId) {
    window.location.href = `/AirBnG/page/reservations?id=${reservationId}`;
}

// ▶ 날짜 포맷
function formatDate(dateStr) {
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return '날짜 정보 없음';
    const year = date.getFullYear();
    const month = `${date.getMonth() + 1}`.toString().padStart(2, '0');
    const day = `${date.getDate()}`.toString().padStart(2, '0');
    const weekday = ['일', '월', '화', '수', '목', '금', '토'][date.getDay()];
    return `${year}-${month}-${day} (${weekday})`;
}

function formatDateTime(dateStr) {
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return '시간 정보 없음';
    const M = date.getMonth() + 1;
    const D = date.getDate();
    const H = String(date.getHours()).padStart(2, '0');
    const m = String(date.getMinutes()).padStart(2, '0');
    const wd = ['일', '월', '화', '수', '목', '금', '토'][date.getDay()];
    return `${M}.${D} (${wd}) ${H}:${m}`;
}

function formatDuration(hours) {
    const mins = Math.round((+hours || 0) * 60);
    const d = Math.floor(mins / 1440);
    const h = Math.floor((mins % 1440) / 60);
    const m = mins % 60;
    return `${d > 0 ? d + '일 ' : ''}${h > 0 ? h + '시간 ' : ''}${m > 0 ? m + '분' : ''}`.trim() || '0분';
}

// ▶ API 호출
function fetchReservations(isFirst = false) {
    if (loading || (!hasNextPage && !isFirst)) return;

    loading = true;
    if (isFirst) document.getElementById('loading').style.display = 'block';

    fetch(getApiUrl())
        .then(res => res.ok ? res.json() : Promise.reject(`HTTP ${res.status}`))
        .then(data => {
            if (data.code !== 1000) throw new Error(data.message || 'API 오류');

            const reservations = data.result.reservations || [];
            if (isFirst && reservations.length === 0) {
                document.getElementById('empty-state').style.display = 'block';
            } else {
                const filtered = reservations.filter(r => currentStates.includes(r.state));
                if (filtered.length > 0) renderReservations(filtered);
                nextCursorId = data.result.nextCursorId;
                hasNextPage = data.result.hasNextPage;
//                document.getElementById('load-more').style.display = hasNextPage ? 'block' : 'none';
//                document.getElementById('load-more').style.display = 'none';
            }
        })
        .catch(err => {
            console.error('Fetch 오류:', err);
            if (isFirst) document.getElementById('empty-state').style.display = 'block';
        })
        .finally(() => {
            loading = false;
            if (isFirst) document.getElementById('loading').style.display = 'none';
        });
}

// 모달 관련 변수
let currentReservationId = null;

// 삭제 확인 모달 열기
function showConfirmModal(reservationId) {
    currentReservationId = reservationId;
    document.getElementById('confirm-modal').classList.remove('hidden');
    document.body.classList.add('modal-open');
}

// 삭제 확인 모달 닫기
function closeConfirmModal() {
    document.getElementById('confirm-modal').classList.add('hidden');
    document.body.classList.remove('modal-open');
    currentReservationId = null;
}

// 삭제 실행
function proceedDelete() {
    if (!currentReservationId) return;

    closeConfirmModal();

    // 실제 삭제 API 호출
    deleteReservation(currentReservationId);
}

// 성공 모달 열기
function showSuccessModal(refundAmount) {
    document.getElementById('refund-amount').textContent = refundAmount + '원';
    document.getElementById('success-modal').classList.remove('hidden');
    document.body.classList.add('modal-open');
}

// 성공 모달 확인 버튼
function confirmDelete() {
    document.getElementById('success-modal').classList.add('hidden');
    document.body.classList.remove('modal-open');

    // 현재 탭 새로고침
    const activeTab = document.querySelector('.tab.active');
    if (activeTab) {
        const states = activeTab.getAttribute('data-states').split(',');
        changeTab(states, activeTab);
    }
}

// 실패 모달 열기
function showErrorModal() {
    document.getElementById('error-modal').classList.remove('hidden');
    document.body.classList.add('modal-open');
}

// 실패 모달 닫기
function closeErrorModal() {
    document.getElementById('error-modal').classList.add('hidden');
    document.body.classList.remove('modal-open');
}

function deleteReservation(reservationId) {
    // confirm 제거하고 바로 API 호출
    fetch(`/AirBnG/reservations/delete?reservationId=${reservationId}`, { method: 'POST' })
        .then(res => res.json())
        .then(data => {
            if (data.code === 1000) {
                showSuccessModal(data.refundAmount || 0);
                // 목록 새로고침
                nextCursorId = null;
                hasNextPage = true;
                clearReservationList();
                fetchReservations(true);
            } else {
                showErrorModal();
            }
        })
        .catch(error => {
            console.error('삭제 오류:', error);
            showErrorModal();
        });
}

function showCancelModal(reservationId) {
    // 취소 확인 모달 표시 (기존 confirm-modal 재사용 가능)
    currentReservationId = reservationId;
    document.getElementById('confirm-modal').classList.remove('hidden');
    document.body.classList.add('modal-open');
}

// 모달 외부 클릭 시 닫기
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('modal-overlay')) {
        if (e.target.id === 'confirm-modal') {
            closeConfirmModal();
        } else if (e.target.id === 'error-modal') {
            closeErrorModal();
        }
        // 성공 모달은 외부 클릭으로 닫지 않음
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        if (!document.getElementById('confirm-modal').classList.contains('hidden')) {
            closeConfirmModal();
        } else if (!document.getElementById('error-modal').classList.contains('hidden')) {
            closeErrorModal();
        }
        // 성공 모달은 ESC로 닫지 않음
    }
});