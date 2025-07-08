document.addEventListener('DOMContentLoaded', function () {
    setJimTypeNameFromURL();
    initializeSearchFilter();
});

function performSearch() {
    const visibleAddressInput = document.querySelector('.searchFilter-input');
    const address = visibleAddressInput?.value.trim();
    //const date = document.getElementById('searchDate')?.value || '';
    const jimTypeId = document.getElementById('searchJimType')?.value || '';

    if (!address) {
        alert('주소를 입력해주세요');
        return;
    }

    // 페이지 이동 시 URL에 파라미터 포함
    const params = new URLSearchParams({
        address,
        //reservationDate: date,
        jimTypeId
    });

    window.location.href = `/AirBnG/page/lockerSearchDetails?${params.toString()}`;
}

// 초기화 함수
function initializeSearchFilter() {
    // 현재 날짜 설정
    const today = new Date();

    const dateInput = document.getElementById('dateInput');
    if (dateInput) {
        dateInput.value = today.toISOString().split('T')[0];
    }

    // ✅ (2) 화면에 날짜 표시용 <span id="selected-date">에도 표시
    const selectedDateSpan = document.getElementById('selected-date');
    if (selectedDateSpan) {
        const weekdays = ['일', '월', '화', '수', '목', '금', '토'];
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const date = String(today.getDate()).padStart(2, '0');
        const day = weekdays[today.getDay()];
        selectedDateSpan.textContent = `${year}.${month}.${date} (${day})`;
    }

    // ✅ (3) 시간 설정
    const selectedTimeSpan = document.getElementById('selected-time');
    if (selectedTimeSpan) {
        const startHour = today.getHours();
        const endHour = startHour + 2;

        const formattedStart = `${String(startHour).padStart(2, '0')}:00`;
        const formattedEnd = `${String(endHour).padStart(2, '0')}:00`;
        selectedTimeSpan.textContent = `${formattedStart} - ${formattedEnd} (2시간)`;

        // 숨겨진 input에 기본값 세팅도 가능하면 같이
        const startInput = document.getElementById('searchStartTime');
        const endInput = document.getElementById('searchEndTime');
        if (startInput) startInput.value = formattedStart;
        if (endInput) endInput.value = formattedEnd;
    }

    // 이벤트 리스너 추가
    addEventListeners();

    // 검색 랭킹 클릭 이벤트 추가
    initializeRankingItems();

    // 커스텀 드롭다운 초기화 추가
    initializeCustomDropdowns();
}

function setJimTypeNameFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    const jimTypeId = urlParams.get('jimTypeId');

    const jimTypeMap = {
        0: "모든 짐",
        1: "백팩/가방",
        2: "캐리어 소형",
        3: "캐리어 대형",
        4: "박스/큰 짐",
        5: "유모차"
    };

    const bagName = jimTypeMap[jimTypeId];
    const bagSpan = document.getElementById('selected-bag');

    if (bagSpan && bagName) {
        bagSpan.textContent = bagName;
        const hiddenInput = document.getElementById('searchJimType');
        if (hiddenInput) {
            hiddenInput.value = jimTypeId;
        }
    }
}

// 이벤트 리스너 추가
function addEventListeners() {
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function (e) {
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
        option.addEventListener('click', function () {
            selectLocation(this.dataset.location);
        });
    });

    // 기존 <select> 이벤트는 제거해도 무방
}

// 짐 타입 드롭다운
function toggleBagDropdown() {
    const dropdown = document.getElementById("bag-dropdown");
    dropdown.classList.toggle("hidden");
}

// 짐 타입 선택 함수 - URL 파라미터 업데이트
function selectBagType(type) {
    const typeMap = {
        '모든 짐': 0,
        '백팩/가방': 1,
        '캐리어 소형': 2,
        '캐리어 대형': 3,
        '박스/큰 짐': 4,
        '유아용품': 5
    };

    const jimTypeId = typeMap[type];

    // 화면에 표시되는 텍스트만 변경
    document.getElementById("selected-bag").innerText = type;

    // 드롭다운 닫기
    document.getElementById("bag-dropdown").classList.add("hidden");

    // 숨겨진 폼의 값 업데이트 (검색 시 사용될 값)
    const hiddenInput = document.getElementById('searchJimType');
    if (hiddenInput) {
        hiddenInput.value = jimTypeId;
    }

    // URL 파라미터 업데이트
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set('jimTypeId', jimTypeId);

    // 페이지 리로드 없이 URL만 변경
    window.history.replaceState({}, '', currentUrl.toString());
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

// 검색 랭킹 아이템 초기화
function initializeRankingItems() {
    document.querySelectorAll('.ranking-item').forEach(item => {
        item.addEventListener('click', function () {
            const location = this.dataset.location;
            searchByLocation(location);
        });
    });
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