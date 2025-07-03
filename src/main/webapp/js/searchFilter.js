document.addEventListener('DOMContentLoaded', function() {
    initializeSearchFilter();
});

// 초기화 함수
function initializeSearchFilter() {
    // 현재 날짜 설정
    const today = new Date();
    const dateInput = document.getElementById('dateInput');
    if (dateInput) {
        dateInput.value = today.toISOString().split('T')[0];
    }

    // 이벤트 리스너 추가
    addEventListeners();

    // 검색 랭킹 클릭 이벤트 추가
    initializeRankingItems();

    // 커스텀 드롭다운 초기화 추가
    initializeCustomDropdowns();
}

// 이벤트 리스너 추가
function addEventListeners() {
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                closeModal(this.id);
            }
        });
    });

    const locationSearchInput = document.getElementById('locationSearchInput');
    if (locationSearchInput) {
        locationSearchInput.addEventListener('input', filterLocations);
    }

    document.querySelectorAll('.location-option').forEach(option => {
        option.addEventListener('click', function() {
            selectLocation(this.dataset.location);
        });
    });

    // 기존 <select> 이벤트는 제거해도 무방
}

// 검색 랭킹 아이템 초기화
function initializeRankingItems() {
    document.querySelectorAll('.ranking-item').forEach(item => {
        item.addEventListener('click', function() {
            const location = this.dataset.location;
            searchByLocation(location);
        });
    });
}

// 위치 검색 모달 열기
function openLocationSearch() {
    openModal('locationModal');
}

// 날짜 선택 모달 열기
function openDatePicker() {
    openModal('dateModal');
}

// 시간 선택 모달 열기
function openTimePicker() {
    openModal('timeModal');
}

// 모달 열기
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
    }
}

// 모달 닫기
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
        document.body.style.overflow = 'auto';
    }
}

// 위치 필터링
function filterLocations() {
    const searchTerm = document.getElementById('locationSearchInput').value.toLowerCase();
    const locationOptions = document.querySelectorAll('.location-option');

    locationOptions.forEach(option => {
        const locationName = option.textContent.toLowerCase();
        option.style.display = locationName.includes(searchTerm) ? 'block' : 'none';
    });
}

// 위치 선택
function selectLocation(location) {
    const searchInput = document.querySelector('.search-input');
    if (searchInput) {
        searchInput.value = location + ' 근처 짐 맡길 곳';
    }

    document.getElementById('searchLocation').value = location;
    closeModal('locationModal');
    performSearch();
}

// 날짜 선택
function selectDate() {
    const dateInput = document.getElementById('dateInput');
    const selectedDateSpan = document.getElementById('selected-date');

    if (dateInput && selectedDateSpan && dateInput.value) {
        const selectedDate = new Date(dateInput.value);
        const formattedDate = formatDate(selectedDate);
        selectedDateSpan.textContent = formattedDate;

        document.getElementById('searchDate').value = dateInput.value;
        closeModal('dateModal');
    }
}

// 날짜 포맷팅
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const weekdays = ['일', '월', '화', '수', '목', '금', '토'];
    return `${year}.${month}.${day} (${weekdays[date.getDay()]})`;
}

// 시간 차이 계산
function calculateDuration(startTime, endTime) {
    const start = parseInt(startTime.split(':')[0]);
    const end = parseInt(endTime.split(':')[0]);
    return end - start;
}

// 커스텀 드롭다운 초기화
function initializeCustomDropdowns() {
    document.querySelectorAll('.custom-dropdown').forEach(dropdown => {
        const selected = dropdown.querySelector('.selected');
        const options = dropdown.querySelector('.dropdown-options');

        selected.addEventListener('click', () => {
            dropdown.classList.toggle('open');
        });

        options.querySelectorAll('li').forEach(option => {
            option.addEventListener('click', () => {
                selected.textContent = option.textContent;
                dropdown.classList.remove('open');
            });
        });
    });
}

// 시간 선택
function selectTime() {
    const startTime = document.querySelector('#startDropdown .selected').textContent;
    const endTime = document.querySelector('#endDropdown .selected').textContent;
    const selectedTimeSpan = document.getElementById('selected-time');

    const duration = calculateDuration(startTime, endTime);
    selectedTimeSpan.textContent = `${startTime} - ${endTime} (${duration}시간)`;

    document.getElementById('searchStartTime').value = startTime;
    document.getElementById('searchEndTime').value = endTime;

    closeModal('timeModal');
}

// 위치별 검색
function searchByLocation(location) {
    const searchInput = document.querySelector('.search-input');
    if (searchInput) {
        searchInput.value = location + ' 근처 짐 맡길 곳';
    }

    document.getElementById('searchLocation').value = location;
    performSearch();
}

// 검색 실행
function performSearch() {
    const searchForm = document.getElementById('searchForm');
    const location = document.getElementById('searchLocation').value;
    const date = document.getElementById('searchDate').value;
    const startTime = document.getElementById('searchStartTime').value;
    const endTime = document.getElementById('searchEndTime').value;

    if (!location) {
        alert('위치를 선택해 주세요.');
        return;
    }

    if (!date) {
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('searchDate').value = today;
    }

    if (!startTime) {
        document.getElementById('searchStartTime').value = '18:00';
    }

    if (!endTime) {
        document.getElementById('searchEndTime').value = '20:00';
    }

    searchForm.submit(); // ← 폼 전송
}
