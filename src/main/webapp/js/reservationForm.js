let selectedDateRange = {
    startDate: 0,
    endDate: 0
};

let currentJimTypes = [];
let jimTypeCounts = {};
let dateArray = [];

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function () {
    loadLockerData();
    generateDateButtons();
    updateTimeOptions();
    // 폼 제출 처리
    document.getElementById('reservationForm').addEventListener('submit', function (e) {
        e.preventDefault();

        if (!selectedDateRange.startDate && selectedDateRange.startDate !== 0) {
            alert('보관 날짜를 선택해주세요.');
            return;
        }

        if (!selectedStartTime || !selectedEndTime) {
            alert('보관 시간을 선택해주세요.');
            return;
        }

        // 선택된 짐 종류가 있는지 확인
        const hasSelectedItems = Object.values(jimTypeCounts).some(count => count > 0);
        if (!hasSelectedItems) {
            highlightJimSection();
            return;
        }

        // 날짜 버튼들에서 실제 날짜 가져오기
        const dateButtons = document.querySelectorAll('.date-btn');
        const startDateStr = dateButtons[selectedDateRange.startDate].dataset.date;
        const endDateStr = dateButtons[selectedDateRange.endDate].dataset.date;

        // 서버로 보낼 데이터 구성
        const requestData = {
            lockerId: parseInt(document.getElementById('lockerId').value),
            startTime: `${startDateStr} ${selectedStartTime}:00`,
            endTime: `${endDateStr} ${selectedEndTime}:00`,
            jimTypeCounts: Object.entries(jimTypeCounts)
                .filter(([_, count]) => count > 0)
                .map(([jimTypeId, count]) => ({
                    jimTypeId: parseInt(jimTypeId),
                    count: parseInt(count)
                }))
        };

        console.log('예약 데이터:', requestData);
        // 실제 서버 요청
        fetch('/AirBnG/reservations', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.code === 4000) {
                    ModalUtils.showSuccess("예약이 완료되었습니다!", "", () => {
                        // TODO : redirect 경로 변경 필요
                        // history.back();
                        window.location.href = `${contextPath}/page/home`;
                    });
                    // 성공 페이지로 이동 또는 다른 처리
                } else {
                    ModalUtils.showError(data.message, "예약 실패", () => {
                        // TODO : redirect 경로 변경 필요
                        // history.back();
                        window.location.href = `${contextPath}/page/home`;
                    });
                }
            })
            .catch(error => {
                console.error('예약 요청 실패:', error);
                ModalUtils.showError("네트워크 오류가 발생하였습니다.", "", () => {
                    // TODO : redirect 경로 변경 필요
                    // history.back();
                    window.location.href = `${contextPath}/page/home`;
                });
            });
    });
});

// 드롭다운 외부 클릭 시 닫기
document.addEventListener('click', function (event) {
    // 드롭다운 요소들 가져오기
    const dropdowns = document.querySelectorAll('.custom-dropdown');

    dropdowns.forEach(dropdown => {
        if (!dropdown.contains(event.target)) {
            // 해당 드롭다운 외부를 클릭했다면 닫기
            dropdown.querySelector('.dropdown-selected').classList.remove('active');
            dropdown.querySelector('.dropdown-options').classList.remove('show');
        }
    });
});

function loadLockerData() {
    fetch("/AirBnG/reservations/form?lockerId=" + lockerId)
        .then(res => res.json())
        .then(data => {
            if (data.code === 1000) {
                const result = data.result;
                document.getElementById("lockerName").textContent = result.lockerName;
                document.getElementById("lockerAddress").textContent = result.addressKr;
                document.getElementById("lockerId").value = lockerId;

                currentJimTypes = result.lockerJimTypes;
                generateJimTypes(result.lockerJimTypes);
                calculateTotal();
            }else if (data.code === 3005) { // 비활성화된 보관소
                ModalUtils.showWarning("보관소가 비활성화 되었습니다.", "이용 불가",() => {
                    // TODO : redirect 경로 변경 필요
                    // history.back();
                    window.location.href = `${contextPath}/page/home`;
                });
            }else if (data.code === 1007) { // uri 오류
                ModalUtils.showError("서버 연결에 실패했습니다. 다시 시도해주세요.", "서버 오류",() => {
                    // TODO : redirect 경로 변경 필요
                    // history.back();
                    window.location.href = `${contextPath}/page/home`;
                });
            }
            else {
                ModalUtils.showError("보관소 정보를 불러올 수 없습니다.", "정보 없음", () => {
                    // history.back();
                    // TODO : redirect 경로 변경 필요
                    window.location.href = `${contextPath}/page/home`;
                });
            }
        })
        .catch(err => {
            console.error("API 요청 실패:", err);
            alert("서버 오류가 발생했습니다.");
        });
}


/***************************************************************************************/
/*                                    날짜 관련 함수                                   */
/***************************************************************************************/

function formatDateTimeForServer(date) {
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
}

function generateDateButtons() {
    const container = document.getElementById('date-buttons');
    const labelContainer = document.createElement('div');
    const btnContainer = document.createElement('div');
    labelContainer.className = 'flex justify-around w-full mb-2 text-center text-sm text-gray-500';
    btnContainer.className = 'inline-flex overflow-hidden w-full rounded-md text-sm p-1 gap-0';

    const today = new Date();
    const days = ['일', '월', '화', '수', '목', '금', '토'];

    for (let i = 0; i < 7; i++) {
        const targetDate = new Date(today);
        targetDate.setDate(today.getDate() + i);
        const dayName = days[targetDate.getDay()];
        const dayNum = targetDate.getDate();

        const label = document.createElement('div');
        label.className = 'flex-1 text-center'; // 또는 w-10

        label.textContent = dayName;

        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'date-btn flex-1 h-12 text-sm font-semibold border-r last:border-r-0 flex items-center justify-center';
        if (i === 0) {
            button.className += ' selected';
        }
        button.textContent = dayNum;
        const formatted = formatDateTimeForServer(targetDate);
        button.dataset.date = formatted;
        dateArray.push(formatted);
        button.dataset.index = i;

        button.onclick = function () {
            selectDate(i);
        };

        labelContainer.appendChild(label);
        btnContainer.appendChild(button);
    }
    container.appendChild(labelContainer);
    container.appendChild(btnContainer);
}

function selectDate(index) {

    if (selectedDateRange.startDate === -1) {
        // 첫 번째 날짜 선택
        selectedDateRange.startDate = selectedDateRange.endDate = index;
    } else if (selectedDateRange.startDate === selectedDateRange.endDate) {
        // 두 번째 날짜 선택
        if (index < selectedDateRange.startDate) {
            selectedDateRange.startDate = index;
        } else if (index > selectedDateRange.endDate) {
            selectedDateRange.endDate = index;
        } else if (index === selectedDateRange.startDate) {
            // 같은 날짜 선택 시 초기화
            selectedDateRange.startDate = selectedDateRange.endDate = index;
        }
    } else {
        // 범위가 이미 설정된 상태에서 추가 선택
        if (index === selectedDateRange.startDate || index === selectedDateRange.endDate) {
            // 시작/끝 날짜 클릭 시 초기화
            selectedDateRange.startDate = selectedDateRange.endDate = index;
        } else if (index < selectedDateRange.startDate) {
            selectedDateRange.startDate = index;
        } else if (index > selectedDateRange.endDate) {
            selectedDateRange.endDate = index;
        } else {
            // 범위 내부 클릭 시 끝 날짜 갱신
            selectedDateRange.endDate = index;
        }
    }


    updateDateButtons();
    // 날짜 변경시 시간 옵션 업데이트
    updateTimeOptions();
    calculateTotal();
}

function updateDateButtons() {
    const buttons = document.querySelectorAll('.date-btn');
    buttons.forEach((btn, index) => {
        btn.classList.remove('selected', 'in-range');
        if (index === selectedDateRange.startDate || index === selectedDateRange.endDate) {
            btn.classList.add('selected');
            console.log(index, " = selected");
        } else if (index > selectedDateRange.startDate && index < selectedDateRange.endDate) {
            btn.classList.add('in-range');
            console.log(index, " = in-range");
        }
    });
}


/***************************************************************************************/
/*                                 시간 옵션 관련 함수                                 */
/***************************************************************************************/
let selectedStartTime = '';
let selectedEndTime = '';

// 드롭다운 토글 함수
function toggleDropdown(type) {
    const dropdown = document.getElementById(type + 'Dropdown');
    const selected = dropdown.querySelector('.dropdown-selected');
    const options = dropdown.querySelector('.dropdown-options');

    // 다른 드롭다운 닫기
    document.querySelectorAll('.custom-dropdown').forEach(dd => {
        if (dd.id !== type + 'Dropdown') {
            dd.querySelector('.dropdown-selected').classList.remove('active');
            dd.querySelector('.dropdown-options').classList.remove('show');
        }
    });

    // 현재 드롭다운 토글
    selected.classList.toggle('active');
    options.classList.toggle('show');

    // 선택된 옵션이 보이도록 스크롤 이동
    if (options.classList.contains('show')) {
        const selectedOption = options.querySelector('.dropdown-option.selected');
        if (selectedOption) {
            selectedOption.scrollIntoView({ block: 'start', behavior: 'auto' });
        }
    }
}

// 옵션 선택 함수
function selectTimeOption(type, value) {
    const display = document.getElementById(type + 'Display');
    const dropdown = document.getElementById(type + 'Dropdown');

    display.textContent = value;

    if (type === 'startTime') {
        selectedStartTime = value;
    } else {
        selectedEndTime = value;
    }
    updateTimeOptions();

    // 드롭다운 닫기
    dropdown.querySelector('.dropdown-selected').classList.remove('active');
    dropdown.querySelector('.dropdown-options').classList.remove('show');

    // 선택된 옵션 색상 표시 업데이트
    const options = document.getElementById(type + 'Options');
    options.querySelectorAll('.dropdown-option').forEach(option => {
        if (option.dataset.value === value) {
            option.classList.add('selected');
        } else {
            option.classList.remove('selected');
        }
    });

    calculateTotal();
}

function updateTimeOptions() {
    updateStartTimeOptions();
    updateEndTimeOptions();
}

function updateStartTimeOptions() {
    const optionsContainer = document.getElementById('startTimeOptions');

    optionsContainer.innerHTML = '';

    const isToday = selectedDateRange.startDate === 0;
    let startH = 0;
    let startM = 0;

    if (isToday) {
        const now = new Date();
        const hour = now.getHours();
        const min = now.getMinutes();

        // 현재 시간에서 가장 가까운 30분 단위로 올림
        if (min <= 30) {
            startH = hour;
            startM = 30;
        } else {
            startH = hour + 1;
            startM = 0;
        }

        // 24시를 넘어가면 다음날 00:00부터
        if (startH >= 24) {
            startH = 0;
            startM = 0;
        }
    }

    // 시작 시간 옵션 생성 (30분 단위)
    let currentHour = startH;
    let currentMin = startM;


    while (true) {
        const timeStr = `${currentHour.toString().padStart(2, '0')}:${currentMin.toString().padStart(2, '0')}`;
        createOption('startTime', optionsContainer, timeStr);

        // 다음 30분 단위로 증가
        currentMin += 30;
        if (currentMin >= 60) {
            currentHour += 1;
            currentMin = 0;
        }

        // 오늘인 경우 23:30까지, 오늘이 아닌 경우 23:30에서 다시 00:00으로
        if (isToday && currentHour >= 24) {
            break;
        } else if (!isToday && currentHour >= 24) {
            currentHour = 0;
        }

        // 오늘이 아닌 경우 한 바퀴 돌면 종료
        if (!isToday && currentHour === startH && currentMin === startM) {
            break;
        }
    }

    updateSelectedTime('startTime', optionsContainer);
}

function updateEndTimeOptions() {
    const optionsContainer = document.getElementById('endTimeOptions');

    optionsContainer.innerHTML = ''; // 원래있던 옵션들 싹 비우기

    const [startH, startM] = selectedStartTime.split(':').map(Number);
    const isSameDate = selectedDateRange.startDate === selectedDateRange.endDate;
    let currentHour = 0
    let currentMin = 0;

    if (isSameDate) {
        currentHour = startH;
        currentMin = startM + 30;
    }

    while (true) {
        // 24:00 까지만 추가
        if (currentHour >= 24 && currentMin > 0) break;

        if (currentMin >= 60) {
            currentHour += 1;
            currentMin = 0;
        }

        const timeStr = `${currentHour.toString().padStart(2, '0')}:${currentMin.toString().padStart(2, '0')}`;
        createOption('endTime', optionsContainer, timeStr);
        // 24:00까지 돌았으면 끝
        if (timeStr === "24:00") break;

        // 다음 30분 단위로 증가
        currentMin += 30;
        if (currentMin >= 60) {
            currentHour += 1;
            currentMin = 0;
        }
    }

    updateSelectedTime('endTime', optionsContainer);
}

function createOption(type, optionsContainer, timeStr){
    const option = document.createElement('div');
    option.className = 'dropdown-option'
    option.dataset.value = timeStr;
    option.textContent = timeStr;
    option.onclick = () => selectTimeOption(type, timeStr);

    // 타입에 따라 올바른 선택된 시간과 비교
    const selectedTime = (type === 'startTime') ? selectedStartTime : selectedEndTime;
    if(timeStr === selectedTime){
        option.classList.add('selected');
    }

    optionsContainer.appendChild(option);
}

function updateSelectedTime(type, optionsContainer){
    let selectedTime = (type === 'startTime') ? selectedStartTime : selectedEndTime;
    // 기존에 선택된 값이 있어. 그런데 얘가 새로 만든 리스트에 없다면?
    if(optionsContainer.children.length > 0 // TODO 나중에 오늘 날짜가 23:31 이후인 경우에 대한 예외처리 들어가면 빼도 됨.
        && !optionsContainer.querySelector(`[data-value="${selectedTime}"]`)){
        const firstOption = optionsContainer.children[0];
        selectTimeOption(type, firstOption.dataset.value);
    }
}


/***************************************************************************************/
/*                                     짐 관련 함수                                    */
/***************************************************************************************/

function generateJimTypes(jimTypes) {
    const container = document.getElementById('jimTypes');
    container.innerHTML = '';

    jimTypes.forEach((jimType, index) => {
        const card = document.createElement('div');
        card.className = 'jim-type-card';

        card.innerHTML = `
            <div class="flex items-center justify-between">
                <div class="flex-1">
                    <div class="font-medium">${jimType.typeName}</div>
                    <div class="text-sm text-gray-600">시간당 ${jimType.pricePerHour.toLocaleString()}원</div>
                </div>
                <div class="quantity flex items-center space-x-2">
                    <button type="button" class="quantity-btn" onclick="changeQuantity(${jimType.jimTypeId}, -1)">-</button>
                    <input type="number" class="quantity-input rounded text-center" 
                           id="qty_${jimType.jimTypeId}" value="0" min="0" 
                           onchange="updateQuantity(${jimType.jimTypeId}, this.value)" readonly>
                    <button type="button" class="quantity-btn" onclick="changeQuantity(${jimType.jimTypeId}, 1)">+</button>
                </div>
            </div>
        `;

        container.appendChild(card);
        jimTypeCounts[jimType.jimTypeId] = 0;
    });
}

function changeQuantity(jimTypeId, change) {
    clearHighlight();
    const input = document.getElementById(`qty_${jimTypeId}`);
    let newValue = parseInt(input.value) + change;
    if (newValue < 0) newValue = 0;

    input.value = newValue;
    jimTypeCounts[jimTypeId] = newValue;

    updateJimTypeCardState(jimTypeId);
    calculateTotal();
}

function updateQuantity(jimTypeId, value) {
    const newValue = Math.max(0, parseInt(value) || 0);
    document.getElementById(`qty_${jimTypeId}`).value = newValue;
    jimTypeCounts[jimTypeId] = newValue;

    updateJimTypeCardState(jimTypeId);
    calculateTotal();
}

function updateJimTypeCardState(jimTypeId) {
    const input = document.getElementById(`qty_${jimTypeId}`);
    const card = input.closest('.jim-type-card');

    if (jimTypeCounts[jimTypeId] > 0) {
        card.classList.add('selected');
    } else {
        card.classList.remove('selected');
    }
}

function calculateTotal() {
    if (!selectedDateRange.startDate && selectedDateRange.startDate !== 0) return;

    if (!selectedStartTime || !selectedEndTime) return;


    const startDate = dateArray[selectedDateRange.startDate];
    const endDate = dateArray[selectedDateRange.endDate];

    const startDateTime = new Date(startDate + "T" + selectedStartTime);
    const endDateTime = new Date(endDate + "T" + selectedEndTime);

    const diffMs = endDateTime - startDateTime; // 밀리초 차이
    const totalHours = diffMs / (1000 * 60 * 60); // 밀리초 → 시간으로 변환

    let totalItemPrice = 0;
    const itemsList = document.getElementById('itemsList');
    itemsList.innerHTML = '';

    currentJimTypes.forEach(jimType => {
        const count = jimTypeCounts[jimType.jimTypeId] || 0;
        if (count > 0) {
            const itemPrice = jimType.pricePerHour * count * totalHours;
            totalItemPrice += itemPrice;

            const itemDiv = document.createElement('div');
            itemDiv.className = 'flex justify-between text-sm';
            itemDiv.innerHTML = `
                <span>${jimType.typeName} × ${count}개 × ${totalHours}시간</span>
                <span>${itemPrice.toLocaleString()}원</span>
            `;
            itemsList.appendChild(itemDiv);
        }
    });

    const serviceFee = totalItemPrice * 0.05;
    const totalPrice = totalItemPrice + serviceFee;

    document.getElementById('serviceFee').textContent = serviceFee.toLocaleString() + '원';
    document.getElementById('totalPrice').textContent = totalPrice.toLocaleString() + '원';
}
// 짐 타입 박스들에 하이라이팅 효과 적용
function highlightJimSection() {
    const jimTitle = document.querySelector('#jimSection h3'); // h3로 변경
    const jimItems = document.querySelectorAll('#jimTypes > div'); // 실제 짐 타입 박스들

    // 기존 효과 제거
    clearHighlight();

    // 제목 하이라이팅
    if (jimTitle) {
        jimTitle.classList.add('jim-title-highlight');
    }

    // 각 짐 타입 박스에 하이라이팅 효과 (순차적으로)
    jimItems.forEach((item, index) => {
        setTimeout(() => {
            item.classList.add('jim-item-highlight');
        }, index * 150); // 0.15초씩 차이나게 순차 적용
    });

    // 스크롤하여 해당 섹션으로 이동
    const jimSection = document.getElementById('jimSection');
    jimSection.scrollIntoView({
        behavior: 'smooth',
        block: 'center'
    });

    // 3초 후 효과 제거
    setTimeout(() => {
        clearHighlight();
    }, 3000);
}

function clearHighlight() {
    const jimTitle = document.querySelector('#jimSection h3');
    const jimItems = document.querySelectorAll('#jimTypes > div');

    if (jimTitle) {
        jimTitle.classList.remove('jim-title-highlight');
    }

    jimItems.forEach(item => {
        item.classList.remove('jim-item-highlight');
    });
}