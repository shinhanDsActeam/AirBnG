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

    updateDateButtons();
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

function generateJimTypes(jimTypes) {
    const container = document.getElementById('jimTypes');
    container.innerHTML = '';

    jimTypes.forEach((jimType, index) => {
        const card = document.createElement('div');
        card.className = 'jim-type-card';

        card.innerHTML = `
            <div class="flex items-center justify-between">
                <div class="jimtype flex items-center flex-1">
                    <div class="flex-1">
                        <div class="font-medium">${jimType.typeName}</div>
                        <div class="text-sm text-gray-600">시간당 ${jimType.pricePerHour.toLocaleString()}원</div>
                    </div>
                </div>
                <div class="quantity flex items-center space-x-2">
                    <button type="button" class="quantity-btn" onclick="changeQuantity(${jimType.jimTypeId}, -1)">-</button>
                    <input type="number" class="quantity-input" id="qty_${jimType.jimTypeId}" value="0" min="0" 
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
