let selectedDateRange = {
    startDate: 0,
    endDate: 0
};

let currentJimTypes = [];
let jimTypeCounts = {};

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    loadLockerData();
    generateDateButtons();
    updateTimeOptions();
    // 폼 제출 처리
    document.getElementById('reservationForm').addEventListener('submit', function(e) {
        e.preventDefault();

        if (!selectedDateRange.startDate && selectedDateRange.startDate !== 0) {
            alert('보관 날짜를 선택해주세요.');
            return;
        }
        const startTimeSelect = document.getElementById('startTimeSelect');
        const endTimeSelect = document.getElementById('endTimeSelect');
        if (!startTimeSelect.value || !endTimeSelect.value) {
            alert('보관 시간을 선택해주세요.');
            return;
        }

        // 선택된 짐 종류가 있는지 확인
        const hasSelectedItems = Object.values(jimTypeCounts).some(count => count > 0);
        if (!hasSelectedItems) {
            alert('짐 종류를 선택해주세요.');
            return;
        }

        // 날짜 버튼들에서 실제 날짜 가져오기
        const dateButtons = document.querySelectorAll('.date-btn');
        const startDateStr = dateButtons[selectedDateRange.startDate].dataset.date;
        const endDateStr = dateButtons[selectedDateRange.endDate].dataset.date;

        // 서버로 보낼 데이터 구성
        const requestData = {
            lockerId: parseInt(document.getElementById('lockerId').value),
            startTime: `${startDateStr} ${startTimeSelect.value}:00`,
            endTime: `${endDateStr} ${endTimeSelect.value}:00`,
            jimTypeCounts: Object.entries(jimTypeCounts)
                .filter(([_, count]) => count > 0)
                .map(([jimTypeId, count]) => ({
                    jimTypeId: parseInt(jimTypeId),
                    count: parseInt(count)
                }))
        };

        console.log('예약 데이터:', requestData);

        // 실제 서버 요청
        // fetch('/AirBnG/reservations', {
        //     method: 'POST',
        //     headers: {
        //         'Content-Type': 'application/json',
        //     },
        //     body: JSON.stringify(requestData)
        // })
        //     .then(response => response.json())
        //     .then(data => {
        //         if (data.code === 1000) {
        //             alert('예약이 완료되었습니다!');
        //             // 성공 페이지로 이동 또는 다른 처리
        //         } else {
        //             alert('예약 실패: ' + data.message);
        //         }
        //     })
        //     .catch(error => {
        //         console.error('예약 요청 실패:', error);
        //         alert('서버 오류가 발생했습니다.');
        //     });
    });
});

const lockerId = 1; // TODO : 예시로 1번 보관소 ID를 사용 - 수정!

function loadLockerData() {

    if (!lockerId) {
        console.log("lockerId가 존재하지 않습니다.");
        return;
    }

    console.log("lockerId: " + lockerId);
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
            } else {
                alert("보관소 정보를 불러오지 못했습니다.");
            }
        })
        .catch(err => {
            console.error("API 요청 실패:", err);
            alert("서버 오류가 발생했습니다.");
        });
}

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
    btnContainer.className = 'inline-flex overflow-hidden w-full rounded-md text-sm';

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
        if(i === 0){
            button.className += ' selected';
        }
        button.textContent = dayNum;
        button.dataset.date = formatDateTimeForServer(targetDate);
        button.dataset.index = i;

        button.onclick = function() {
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

function updateTimeOptions() {
    updateStartTimeOptions();
    updateEndTimeOptions();
}

function updateStartTimeOptions() {
    const startSelect = document.getElementById('startTimeSelect');
    const currentStartTime = startSelect.value; // 현재 선택된 값 저장

    startSelect.innerHTML = '';

    const isToday = selectedDateRange.startDate === 0;
    let startHour = 0;
    let startMinute = 0;

    if (isToday) {
        const now = new Date();
        const currentHour = now.getHours();
        const currentMinute = now.getMinutes();

        // 현재 시간에서 가장 가까운 30분 단위로 올림
        if (currentMinute <= 30) {
            startHour = currentHour;
            startMinute = 30;
        } else {
            startHour = currentHour + 1;
            startMinute = 0;
        }

        // 24시를 넘어가면 다음날 00:00부터
        if (startHour >= 24) {
            startHour = 0;
            startMinute = 0;
        }
    }

    // 시작 시간 옵션 생성 (30분 단위)
    let currentHour = startHour;
    let currentMin = startMinute;

    while (true) {
        const timeStr = `${currentHour.toString().padStart(2, '0')}:${currentMin.toString().padStart(2, '0')}`;
        const option = document.createElement('option');
        option.value = timeStr;
        option.textContent = timeStr;
        startSelect.appendChild(option);

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
        if (!isToday && currentHour === startHour && currentMin === startMinute) {
            break;
        }
    }

    // 이전에 선택된 값이 새로운 옵션에 있으면 그대로 선택
    if (currentStartTime) {
        const optionExists = Array.from(startSelect.options).some(opt => opt.value === currentStartTime);
        if (optionExists) {
            startSelect.value = currentStartTime;
        } else if (startSelect.options.length > 0) {
            startSelect.selectedIndex = 0;
        }
    } else if (startSelect.options.length > 0) {
        startSelect.selectedIndex = 0;
    }
}

function updateEndTimeOptions() {
    const startSelect = document.getElementById('startTimeSelect');
    const endSelect = document.getElementById('endTimeSelect');
    const currentEndTime = endSelect.value; // 현재 선택된 값 저장
    const startTime = startSelect.value;

    if (!startTime) return;

    endSelect.innerHTML = '';

    const [startH, startM] = startTime.split(':').map(Number);
    const isSameDate = selectedDateRange.startDate === selectedDateRange.endDate;

    if (isSameDate) {
        // 같은 날: 시작시간 + 30분부터 00:00까지
        let currentHour = startH;
        let currentMin = startM + 30;

        // 30분 추가 후 시간 조정
        if (currentMin >= 60) {
            currentHour += 1;
            currentMin = 0;
        }

        // 24시를 넘어가면 00:00
        if (currentHour >= 24) {
            currentHour = 0;
            currentMin = 0;
        }

        // 종료 시간 옵션 생성
        const startPoint = { hour: currentHour, min: currentMin };

        while (true) {
            const timeStr = `${currentHour.toString().padStart(2, '0')}:${currentMin.toString().padStart(2, '0')}`;
            const option = document.createElement('option');
            option.value = timeStr;
            option.textContent = timeStr;
            endSelect.appendChild(option);

            // 00:00에 도달하면 종료
            if (currentHour === 0 && currentMin === 0 && endSelect.options.length > 1) {
                break;
            }

            // 다음 30분 단위로 증가
            currentMin += 30;
            if (currentMin >= 60) {
                currentHour += 1;
                currentMin = 0;
            }

            // 24시를 넘어가면 00:00으로
            if (currentHour >= 24) {
                currentHour = 0;
                currentMin = 0;
            }

            // 무한루프 방지: 시작점으로 돌아오면 종료 (단, 00:00이 아닌 경우)
            if (currentHour === startPoint.hour && currentMin === startPoint.min && !(currentHour === 0 && currentMin === 0)) {
                break;
            }
        }

    } else {
        // 다른 날: 00:00부터 23:30까지 모든 시간
        for (let h = 0; h < 24; h++) {
            for (let m = 0; m < 60; m += 30) {
                const timeStr = `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}`;
                const option = document.createElement('option');
                option.value = timeStr;
                option.textContent = timeStr;
                endSelect.appendChild(option);
            }
        }
    }

    // 이전에 선택된 값 복원 로직
    if (currentEndTime) {
        const optionExists = Array.from(endSelect.options).some(opt => opt.value === currentEndTime);

        if (optionExists) {
            // 이전 값이 새로운 옵션에 있으면 그대로 선택
            endSelect.value = currentEndTime;
        } else if (isSameDate) {
            // 같은 날인데 이전 값이 없으면 (시작시간 >= 종료시간인 경우)
            // 첫 번째 옵션 선택 (시작시간 + 30분)
            if (endSelect.options.length > 0) {
                endSelect.selectedIndex = 0;
            }
        } else {
            // 다른 날인데 이전 값이 없으면 원래 값 유지 시도
            // 만약 시작시간보다 큰 값이었다면 그대로 두고, 작은 값이었다면 첫 번째 옵션
            const [currentEndH, currentEndM] = currentEndTime.split(':').map(Number);
            const currentEndTotalMin = currentEndH * 60 + currentEndM;
            const startTotalMin = startH * 60 + startM;

            if (currentEndTotalMin > startTotalMin) {
                // 시작시간보다 큰 경우 원래 값으로 복원 (새로운 옵션에는 있을 것임)
                endSelect.value = currentEndTime;
            } else {
                // 시작시간보다 작거나 같은 경우 첫 번째 옵션
                if (endSelect.options.length > 0) {
                    endSelect.selectedIndex = 0;
                }
            }
        }
    } else if (endSelect.options.length > 0) {
        // 이전 선택값이 없으면 첫 번째 옵션
        endSelect.selectedIndex = 0;
    }
}

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

    const startTimeSelect = document.getElementById('startTimeSelect');
    const endTimeSelect = document.getElementById('endTimeSelect');

    if (!startTimeSelect.value || !endTimeSelect.value) return;

    const startTime = startTimeSelect.value;
    const endTime = endTimeSelect.value;

    // 시간 차이 계산
    const [startH, startM] = startTime.split(':').map(Number);
    const [endH, endM] = endTime.split(':').map(Number);

    let totalMinutes = (endH * 60 + endM) - (startH * 60 + startM);
    if (totalMinutes <= 0) {
        totalMinutes += 24 * 60; // 다음날로 넘어가는 경우
    }

    const hours = totalMinutes / 60;
    const days = selectedDateRange.endDate - selectedDateRange.startDate + 1;
    const totalHours = hours * days;

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

    const serviceFee = 400;
    const totalPrice = totalItemPrice + serviceFee;

    document.getElementById('totalPrice').textContent = totalPrice.toLocaleString() + '원';
}
