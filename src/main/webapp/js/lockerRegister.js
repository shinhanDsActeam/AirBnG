let selectedFiles = [];

document.addEventListener('DOMContentLoaded', () => {
    let currentStep = 1;
    const totalSteps = 4;

    const steps = document.querySelectorAll('.step');
    const nextButtons = document.querySelectorAll('.next-btn');
    const submitButton = document.querySelector('.submit-btn');
    const indicators = document.querySelectorAll('.step-icon');

    function showStep(step) {
        steps.forEach((el, idx) => {
            if (idx === step - 1) {
                el.classList.add('active');
            } else {
                el.classList.remove('active');
            }
        });
    }

    // 다음 버튼 클릭 시 단계 전환
    nextButtons.forEach((btn) => {
        btn.addEventListener('click', () => {
            if (currentStep < totalSteps) {
                currentStep++;
                updateIndicators();
                showStep(currentStep);
                checkInputs(currentStep);
            }
        });
    });

    // 등록 버튼 유효성 확인
    submitButton?.addEventListener('mouseover', () => {
        if (checkInputs(4)) {
            submitButton.disabled = false;
        } else {
            submitButton.disabled = true;
        }
    });

    // 입력 필드 감지 (각 단계별)
    document.querySelectorAll('input, select, textarea').forEach(input => {
        input.addEventListener('input', () => checkInputs(currentStep));
        input.addEventListener('change', () => checkInputs(currentStep));
    });

    // STEP 이미지 변경
    function updateIndicators() {
        indicators.forEach((img, idx) => {
            const stepNum = idx + 1;
            let src = '';

            if (stepNum < currentStep) {
                src = '/images/check_ic.svg';
            } else if (stepNum === currentStep) {
                src = `/images/${stepNum}_full_ic.svg`;
            } else {
                src = `/images/${stepNum}_blank_ic.svg`;
            }

            img.src = contextPath + src;
        });
    }

    // 각 단계 유효성 검사
    function checkInputs(step) {
        const current = document.querySelector(`.step-${step}`);
        const inputs = current.querySelectorAll('input[required], select[required], textarea[required]');
        let isValid = Array.from(inputs).every(el => el.value.trim() !== '');

        if (step === 4) {
            const selectedRows = current.querySelectorAll('.condition-row.selected');
            isValid = selectedRows.length > 0;
        }

        const btn = current.querySelector('.next-btn') || submitButton;
        if (btn) btn.disabled = !isValid;

        return isValid;
    }

    function generateTimeOptions(selectId, defaultTime) {
        const select = document.getElementById(selectId);
        select.innerHTML = '';

        const [defaultHour, defaultMin] = defaultTime.split(':').map(Number);

        for (let isPM = 0; isPM <= 1; isPM++) {
            for (let hour = 0; hour < 12; hour++) {
                const realHour = isPM ? hour + 12 : hour;
                for (let min = 0; min < 60; min += 30) {
                    const value = `${String(realHour).padStart(2, '0')}:${String(min).padStart(2, '0')}`;
                    const label = `${value} ${isPM ? 'PM' : 'AM'}`;

                    const option = document.createElement('option');
                    option.value = value;
                    option.textContent = label;

                    if (realHour === defaultHour && min === defaultMin) {
                        option.selected = true;
                    }

                    select.appendChild(option);
                }
            }
        }
    }

    document.querySelectorAll('.check-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const row = btn.closest('.condition-row');
        const inputName = btn.dataset.target;
        const input = document.querySelector(`input[name="${inputName}"]`);
        const img = btn.querySelector('img');

        const isSelected = row.classList.contains('selected');

        if (isSelected) {
          // 선택 해제
          row.classList.remove('selected');
          img.src = contextPath + '/images/circle_check_blank.svg';
          input.disabled = true;
        } else {
          // 선택
          row.classList.add('selected');
          img.src = contextPath + '/images/circle_check_full.svg';
          input.disabled = false;
        }

        checkInputs(currentStep);  // 등록 버튼 활성화 여부 검사
      });
    });

    // 주소 검색 버튼 클릭 → daum.Postcode 호출
    document.getElementById('locationInputWrapper').addEventListener('click', () => {
        new daum.Postcode({
            oncomplete: function(data) {
                const addr = data.roadAddress || data.jibunAddress;
                document.getElementById('location').value = addr;

                // 주소를 좌표로 변환
                const geocoder = new kakao.maps.services.Geocoder();
                geocoder.addressSearch(addr, function(result, status) {
                    if (status === kakao.maps.services.Status.OK) {
                        document.getElementById('latitude').value = result[0].y;
                        document.getElementById('longitude').value = result[0].x;
                    } else {
                        alert("위치 좌표를 찾을 수 없습니다.");
                    }
                });

                checkInputs(currentStep);
            },
            width: 500, // 팝업 너비
            height: 400,
            left: Math.ceil((window.screen.width - 500) / 2)
        }).open();
    });

    // 이미지 업로드 로직 개선
    const uploadInput = document.getElementById('image');
    const previewContainer = document.getElementById('image-preview-container');
    const uploadBox = document.getElementById('uploadIconBox');

    uploadInput.addEventListener('change', (e) => {
        const newFiles = Array.from(e.target.files);

        // 총 5장 제한
        if (selectedFiles.length + newFiles.length > 5) {
            alert("사진은 최대 5장까지 업로드할 수 있습니다.");
            e.target.value = '';
            return;
        }

        newFiles.forEach(file => {
            selectedFiles.push(file);
            const reader = new FileReader();
            reader.onload = function(evt) {
                const wrapper = document.createElement('div');
                wrapper.className = 'preview-item';

                const img = document.createElement('img');
                img.src = evt.target.result;

                const delBtn = document.createElement('button');
                delBtn.innerText = '×';
                delBtn.onclick = () => {
                    selectedFiles = selectedFiles.filter(f => f !== file);
                    wrapper.remove();
                    updateUploadIconState();
                };

                wrapper.appendChild(img);
                wrapper.appendChild(delBtn);
                previewContainer.appendChild(wrapper);
            };
            reader.readAsDataURL(file);
        });

        // reset input (중복 선택 방지)
        e.target.value = '';
        updateUploadIconState();
    });

    function updateUploadIconState() {
        if (selectedFiles.length >= 5) {
            uploadBox.style.display = 'none';
        } else {
            uploadBox.style.display = 'flex';
        }
    }

    function renderDropdownOptions(selectId, defaultTime = '08:00') {
      const dropdown = document.getElementById(`dropdown-${selectId}`);
      const selected = document.getElementById(`selected-${selectId}`);
      dropdown.innerHTML = '';

      for (let hour = 0; hour < 24; hour++) {
        for (let min = 0; min < 60; min += 30) {
          const displayHour = String(hour).padStart(2, '0');
          const displayMin = String(min).padStart(2, '0');
          const ampm = hour < 12 ? 'AM' : 'PM';
          const value = `${displayHour}:${displayMin}`;
          const selectedText = `${displayHour}:${displayMin} ${ampm}`;

          // li 생성
          const li = document.createElement('li');
          li.dataset.value = value;
          li.dataset.display = selectedText;

          // 각각의 span 요소 생성
          const hourSpan = document.createElement('span');
          hourSpan.textContent = displayHour;

          const colonSpan = document.createElement('span');
          colonSpan.textContent = ":";

          const minSpan = document.createElement('span');
          minSpan.textContent = displayMin;

          // li에 span들 추가
          li.appendChild(hourSpan);
          li.appendChild(colonSpan);
          li.appendChild(minSpan);

          // 기본값이면 selected에 표시
          if (value === defaultTime) {
            li.classList.add('selected');
            selected.textContent = selectedText;
            selected.dataset.value = value;
          }

          // 클릭 이벤트
          li.addEventListener('click', () => {
            selected.textContent = li.dataset.display;
            selected.dataset.value = value;

            dropdown.querySelectorAll('li').forEach(el => el.classList.remove('selected'));
            li.classList.add('selected');

            dropdown.classList.remove('show');
            checkInputs(currentStep);
          });

          dropdown.appendChild(li);
        }
      }
    }

    function goBack() {
        if (currentStep === 1) {
          window.history.back();
        } else {
          currentStep--;
          updateIndicators();
          showStep(currentStep);
          checkInputs(currentStep);
        }
    }

    // lockerRegister.js
    function loadKakaoMapScript() {
      const script = document.createElement('script');
      script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${window.KAKAO_APP_KEY}&libraries=services`;
      script.async = true;
      document.head.appendChild(script);
    }

    loadKakaoMapScript();
    window.goBack = goBack;

    // 초기화
    showStep(currentStep);
    updateIndicators();
    checkInputs(currentStep);
    // 운영 시간 옵션 초기화
    renderDropdownOptions('startTime', '08:00');
    renderDropdownOptions('endTime', '21:00');
});

// 등록 버튼 클릭 시 실제 fetch 전송
document.querySelector('.submit-btn').addEventListener('click', async () => {
    const locker = {
        lockerName: document.querySelector('#lockerName').value,
        isAvailable: 'YES',
        keeperId: Number(memberId),
        address: document.querySelector('#location').value,
        addressEnglish: '',
        addressDetail: document.querySelector('#detailAddress').value,
        latitude: parseFloat(document.getElementById('latitude').value),
        longitude: parseFloat(document.getElementById('longitude').value),
        jimTypeIds: []
    };

    const JIM_TYPE_MAP = {
        price_bag: 1,
        price_small: 2,
        price_large: 3,
        price_box: 4,
        price_stroller: 5
    };

    for (const key in JIM_TYPE_MAP) {
        const row = document.querySelector(`.check-btn[data-target="${key}"]`)?.closest('.condition-row');
        if (row && row.classList.contains('selected')) {
            locker.jimTypeIds.push(JIM_TYPE_MAP[key]);
        }
    }

    const formData = new FormData();
    formData.append("locker", new Blob([JSON.stringify(locker)], { type: "application/json" }));

    const imageInput = document.querySelector('#image');
    if (selectedFiles.length > 0) {
        const filesToUpload = selectedFiles.slice(0, 5);
        for (let i = 0; i < filesToUpload.length; i++) {
            formData.append("images", filesToUpload[i]);
        }
    }

    try {
        const res = await fetch(`${contextPath}/lockers/register`, {
            method: "POST",
            body: formData
        });

        if (res.ok) {
            alert("보관소 등록 완료!");
            location.href = contextPath + "/page/lockers";
        } else {
            alert("등록 실패 - 서버 오류");
        }
    } catch (err) {
        console.error("전송 실패:", err);
        alert("전송 중 오류가 발생했습니다.");
    }
});

function toggleDropdown(selectId) {
  const dropdown = document.getElementById(`dropdown-${selectId}`);
  const isVisible = dropdown.classList.contains('show');

  // 모두 닫고 이 하나만 열기
  document.querySelectorAll('.custom-select-dropdown').forEach(el => el.classList.remove('show'));
  if (!isVisible) {
    dropdown.classList.add('show');

    // 외부 클릭 시 닫기
    const closeOnClickOutside = (e) => {
      if (!dropdown.previousElementSibling.contains(e.target)) {
        dropdown.classList.remove('show');
        window.removeEventListener('click', closeOnClickOutside);
      }
    };
    setTimeout(() => window.addEventListener('click', closeOnClickOutside), 0);
  }
}