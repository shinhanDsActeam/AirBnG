let currentStates = ['CONFIRMED', 'PENDING'];
let currentPeriod = 'ALL';
let nextCursorId = null;
let loading = false;
let hasNextPage = true;

// 페이지 로드시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initTabs();
    fetchReservations(true);

    // 더보기 버튼 이벤트
    document.getElementById('load-more-btn').addEventListener('click', function() {
        fetchReservations(false);
    });
});

// 탭 초기화
function initTabs() {
    document.querySelectorAll('.tab').forEach(tab => {
        tab.addEventListener('click', function() {
            const states = this.getAttribute('data-states').split(',');
            changeTab(states, this);
        });
    });
}

// 탭 변경
function changeTab(newStates, tabElem) {
    if (loading) return;

    currentStates = newStates;
    nextCursorId = null;
    hasNextPage = true;
    currentPeriod = 'ALL';

    // 탭 UI 업데이트
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    tabElem.classList.add('active');

    // 필터 섹션 표시/숨김 (이용후, 취소됨 탭에서만 표시)
    const filterSection = document.getElementById('filter-section');
    if (newStates.includes('COMPLETED') || newStates.includes('CANCELLED')) {
        filterSection.classList.add('show');
        document.getElementById('selected-period').textContent = '전체';
    } else {
        filterSection.classList.remove('show');
    }

    // 리스트 초기화
    clearReservationList();

    // 데이터 로드
    fetchReservations(true);
}

// 기간 드롭다운 토글
function toggleDropdown() {
    const dropdown = document.getElementById('period-dropdown');
    const btn = document.querySelector('.dropdown-btn');

    dropdown.classList.toggle('show');
    btn.classList.toggle('open');
}

// 기간 선택
function selectPeriod(newPeriod, text) {
    if (loading) return;

    currentPeriod = newPeriod;
    document.getElementById('selected-period').textContent = text;
    document.getElementById('period-dropdown').classList.remove('show');
    document.querySelector('.dropdown-btn').classList.remove('open');

    // 데이터 리셋
    nextCursorId = null;
    hasNextPage = true;

    // 리스트 초기화
    clearReservationList();

    // 데이터 로드
    fetchReservations(true);
}

// 드롭다운 외부 클릭시 닫기
document.addEventListener('click', function(event) {
    if (!event.target.closest('.period-dropdown')) {
        document.getElementById('period-dropdown').classList.remove('show');
        document.querySelector('.dropdown-btn').classList.remove('open');
    }
});

// 더보기 메뉴 외부 클릭시 닫기
document.addEventListener('click', function(event) {
    if (!event.target.closest('.more-btn')) {
        document.querySelectorAll('.more-menu').forEach(menu => {
            menu.classList.remove('show');
        });
    }
});

// 더보기 메뉴 토글
function toggleMoreMenu(reservationId) {
    const menu = document.getElementById(`more-menu-${reservationId}`);
    // 다른 모든 메뉴 닫기
    document.querySelectorAll('.more-menu').forEach(m => {
        if (m !== menu) m.classList.remove('show');
    });
    menu.classList.toggle('show');
}

// API URL 생성
function getApiUrl() {
    let url = '/AirBnG/reservations?isDropper=true&memberId=3';

    currentStates.forEach(state => {
        url += '&state=' + state;
    });

    // 이용후, 취소됨 탭에서만 period 파라미터 추가
    if ((currentStates.includes('COMPLETED') || currentStates.includes('CANCELLED')) &&
        currentPeriod && currentPeriod !== 'ALL') {
        url += '&period=' + currentPeriod;
    }

    if (nextCursorId !== null && nextCursorId !== -1) {
        url += '&nextCursorId=' + nextCursorId;
    }

    return url;
}

// 예약 리스트 초기화
function clearReservationList() {
    document.getElementById('reservation-list').innerHTML = '';
    document.getElementById('empty-state').style.display = 'none';
    document.getElementById('load-more').style.display = 'none';
}

// 상태 텍스트 생성
function getStatusText(state) {
    const statusMap = {
        'CONFIRMED': { text: '예약완료', class: 'confirmed' },
        'PENDING': { text: '예약대기', class: 'pending' },
        'CANCELLED': { text: '취소완료', class: 'cancelled' },
        'COMPLETED': { text: '이용완료', class: 'completed' }
    };

    const status = statusMap[state] || { text: state, class: 'pending' };
    return `<span class="status-text ${status.class}">${status.text}</span>`;
}

// 예약 취소 함수
function cancelReservation(reservationId) {
    if (!confirm('정말 이 예약을 취소하시겠습니까?')) return;

    fetch(`/AirBnG/reservations/${reservationId}/cancel`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(res => res.json())
    .then(data => {
        if (data.code === 1000) {
            alert('예약이 취소되었습니다.');
            nextCursorId = null;
            hasNextPage = true;
            clearReservationList();
            fetchReservations(true);
        } else {
            alert('취소 실패: ' + data.message);
        }
    })
    .catch(err => {
        alert('취소 중 오류 발생');
        console.error(err);
    });
}

// 예약 삭제 함수
function deleteReservation(reservationId) {
    if (!confirm('정말 이 예약 기록을 삭제하시겠습니까?')) return;

    fetch(`/AirBnG/reservations/${reservationId}/members/3/delete`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(res => res.json())
    .then(data => {
        if (data.code === 1000) {
            alert('삭제가 완료되었습니다.');
            nextCursorId = null;
            hasNextPage = true;
            clearReservationList();
            fetchReservations(true);
        } else {
            alert('삭제 실패: ' + data.message);
        }
    })
    .catch(err => {
        alert('삭제 중 오류 발생');
        console.error(err);
    });
}

// 다시예약 함수
function reBooking(lockerId) {
    // 해당 보관소 예약 페이지로 이동
    window.location.href = `/AirBnG/lockers/${lockerId}/reservation`;
}

// 액션 버튼 생성
function getActionButtons(reservation) {
    switch(reservation.state) {
        case 'CONFIRMED':
            return `
                <div class="action-buttons">
                    <button class="btn btn-disabled">취소불가</button>
                </div>
            `;
        case 'PENDING':
            return `
                <div class="action-buttons">
                    <button class="btn btn-cancel" onclick="cancelReservation(${reservation.reservationId})">취소요청</button>
                </div>
            `;
        case 'COMPLETED':
            return `
                <div class="completed-actions">
                    <button class="rebook-btn" onclick="reBooking(${reservation.lockerId || 1})">다시예약</button>
                    <div class="more-btn" onclick="toggleMoreMenu(${reservation.reservationId})">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="1"></circle>
                            <circle cx="12" cy="5" r="1"></circle>
                            <circle cx="12" cy="19" r="1"></circle>
                        </svg>
                        <div class="more-menu" id="more-menu-${reservation.reservationId}">
                            <div class="more-menu-item" onclick="deleteReservation(${reservation.reservationId})">삭제</div>
                        </div>
                    </div>
                </div>
            `;
        case 'CANCELLED':
        default:
            return '';
    }
}

// 날짜 포맷팅
function formatDate(dateString) {
    if (!dateString) return '날짜 정보 없음';

    try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return '날짜 정보 없음';

        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const weekdays = ['일', '월', '화', '수', '목', '금', '토'];
        const weekday = weekdays[date.getDay()];

        return `${month}.${day} (${weekday})`;
    } catch (e) {
        return '날짜 정보 없음';
    }
}

// 이용시간 포맷
function formatDuration(hours) {
    if (!hours || isNaN(hours)) return '0분';

    const totalMinutes = Math.round(hours * 60);
    const days = Math.floor(totalMinutes / (60 * 24));
    const remainingMinutes = totalMinutes % (60 * 24);
    const h = Math.floor(remainingMinutes / 60);
    const m = remainingMinutes % 60;

    let result = '';

    if (days > 0) result += `${days}일 `;
    if (h > 0 || days > 0) result += `${h}시간 `;
    if (m > 0 || (days === 0 && h === 0)) result += `${m}분`;

    return result.trim();
}

// 날짜 포맷팅 (MM.DD (요일) HH:mm)
function formatDateTime(dateStr) {
    if (!dateStr) return '';

    // API에서 이미 포맷된 문자열이 오는 경우 처리
    if (typeof dateStr === 'string' && dateStr.includes('.') && dateStr.includes(' ')) {
        return dateStr;
    }

    try {
        const date = new Date(dateStr);
        const month = date.getMonth() + 1;
        const day = date.getDate();
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');

        const weekdays = ['일', '월', '화', '수', '목', '금', '토'];
        const weekday = weekdays[date.getDay()];

        return `${month}.${day} (${weekday}) ${hours}:${minutes}`;
    } catch (e) {
        return dateStr || '';
    }
}

// 예약 카드 렌더링
function renderReservations(reservations) {
    const list = document.getElementById('reservation-list');

    console.log('렌더링할 예약 데이터:', reservations);
    if (!reservations || reservations.length === 0) {
        return;
    }

    reservations.forEach(reservation => {
        const card = document.createElement('div');
        card.className = 'reservation-card';

        // 취소된 예약은 블러 처리
        if (reservation.state === 'CANCELLED') {
            card.classList.add('cancelled');
        }

        const statusText = getStatusText(reservation.state);
        const actionButtons = getActionButtons(reservation);

        // 안전한 데이터 처리
        const jimTypes = Array.isArray(reservation.jimTypeResults) && reservation.jimTypeResults.length > 0
            ? reservation.jimTypeResults.map(jim => jim.typeName || '타입명 없음').join(', ')
            : '정보 없음';

        const imageHtml = reservation.lockerImage
            ? `<img src="${reservation.lockerImage}" alt="보관소" class="item-image">`
            : `<div class="item-image"></div>`;

        const formattedDate = formatDate(reservation.dateOnly);
        const formattedDuration = formatDuration(reservation.durationHours);

        const formattedstart = formatDateTime(reservation.startTime);
        const formattedend = formatDateTime(reservation.endTime);

        card.innerHTML = `
            <div class="reservation-header">
                <div class="reservation-date">${formattedDate}</div>
                <div class="reservation-info-row">
                    <a href="/AirBnG/reservations/${reservation.reservationId || 0}" class="view-details">예약 상세 &gt;</a>
                    ${statusText}
                </div>
            </div>
            <div class="reservation-item">
                ${imageHtml}
                <div class="item-info">
                    <div class="item-title">${reservation.lockerName || '보관소 정보 없음'}</div>
                    <div class="item-time">이용 시간: ${formattedDuration}</div>
                    <div class="item-types">짐 종류: ${jimTypes}</div>
                </div>
            </div>
            <div>
                <div class="item-date-row">
                    <div class="item-date-col">
                        <div class="label">시작 날짜</div>
                        <div class="value">${formattedstart || '시간 정보 없음'}</div>
                    </div>
                    <div class="item-date-col">
                        <div class="label">찾는 날짜</div>
                        <div class="value">${formattedend || '시간 정보 없음'}</div>
                    </div>
                </div>
            </div>
            ${actionButtons}
        `;

        list.appendChild(card);
    });
}

// 예약 데이터 로드
function fetchReservations(isFirst = false) {
    if (loading || (!hasNextPage && !isFirst)) return;

    loading = true;

    if (isFirst) {
        document.getElementById('loading').style.display = 'block';
    }

    const apiUrl = getApiUrl();
    console.log('API 호출 URL:', apiUrl);

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('API 응답:', data);

            if (data.code === 1000) {
                const reservations = data.result.reservations || [];

                if (isFirst && reservations.length === 0) {
                    document.getElementById('empty-state').style.display = 'block';
                } else {
                    const filteredReservations = reservations.filter(reservation =>
                        currentStates.includes(reservation.state)
                    );

                    if (filteredReservations.length > 0) {
                        renderReservations(filteredReservations);
                    }

                    // 페이지네이션 처리
                    nextCursorId = data.result.nextCursorId;
                    hasNextPage = data.result.hasNextPage;

                    if (hasNextPage) {
                        document.getElementById('load-more').style.display = 'block';
                    } else {
                        document.getElementById('load-more').style.display = 'none';
                    }
                }
            } else {
                console.error('API 오류:', data.message);
                if (isFirst) {
                    document.getElementById('empty-state').style.display = 'block';
                }
            }
        })
        .catch(error => {
            console.error('Fetch 오류:', error);
            if (isFirst) {
                document.getElementById('empty-state').style.display = 'block';
            }
        })
        .finally(() => {
            loading = false;
            if (isFirst) {
                document.getElementById('loading').style.display = 'none';
            }
        });
}