let selectedFiles = [];

function loadKakaoMapScript(callback) {
    const script = document.createElement('script');
    script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${window.KAKAO_APP_KEY}&autoload=false&libraries=services`;
    script.async = true;
    script.onload = () => kakao.maps.load(callback);
    document.head.appendChild(script);
}

document.addEventListener('DOMContentLoaded', () => {
    loadKakaoMapScript(() => {
        fetchLockerDetail(lockerId);
    });
});

function fetchLockerDetail(id) {
    fetch(`${contextPath}/lockers/update/${id}`)
        .then(res => res.json())
        .then(data => {
            if (data.code === 1000) {
                renderLockerManage(data.result);
            } else {
                ModalUtils.showError(data.message || "불러오기 실패", "에러");
            }
        })
        .catch(err => {
            console.error(err);
            ModalUtils.showError("데이터를 불러오는 중 오류가 발생했습니다.", "네트워크 오류");
        });
}

function renderLockerManage(detail) {
    const container = document.getElementById('locker-manage-content');
    container.innerHTML = `
        <div class="form-group">
            <label>보관소 이름</label>
            <input type="text" id="lockerName" value="${detail.lockerName}" required />
        </div>

        <div class="form-group">
            <label>주소</label>

            <div class="input-with-icon">
                <img src="${contextPath}/images/location_search_ic.svg" class="location-icon" />
                <input type="text" id="location" value="${detail.address}" readonly onclick="openAddressSearch()" />
            </div>

            <input type="text" id="detailAddress" value="${detail.addressDetail}" placeholder="상세주소" />
            <input type="hidden" id="englishAddress" name="englishAddress" value="${detail.addressEnglish}" />
            <input type="hidden" id="latitude" value="${detail.latitude}" />
            <input type="hidden" id="longitude" value="${detail.longitude}" />
        </div>

        <div class="form-group">
          <label for="image">사진 업로드</label>
          <div class="image-upload-area">
            <div class="image-upload-box" id="uploadIconBox">
              <label for="image">
                <img src="${contextPath}/images/img_upload_ic.svg" alt="사진 업로드" />
              </label>
              <input type="file" id="image" name="images" accept="image/*" multiple style="display:none" />
            </div>

            <div id="image-preview-container"></div>

            <!-- 기존 이미지 미리보기 -->
            ${detail.images.map((img, i) => `
              <div class="preview-item">
                <img src="${img}" alt="preview-${i}" />
                <button type="button" onclick="removePreview(this)">x</button>
              </div>
            `).join('')}
          </div>
        </div>

        <div class="form-group">
          <label>짐 part</label>
          ${detail.jimTypeResults.map(jim =>
            `<div class="condition-row ${jim.enabled ? 'selected' : ''}">
                <span>${jim.typeName}</span>
                <div class="input-wrapper">
                  <div class="price-display">${jim.pricePerHour}</div>
                  <input type="hidden" name="price_${jim.jimTypeId}" value="${jim.pricePerHour}" />
                </div>
                <button type="button" class="check-btn" data-target="price_${jim.jimTypeId}">
                    <img src="${contextPath}/images/${jim.enabled ? 'circle_check_full' : 'circle_check_blank'}.svg" alt="선택" />
                </button>
            </div>`
          ).join('')}
        </div>

        <div class="form-actions">
            <button class="save-btn" onclick="submitLockerUpdate(${detail.lockerId})">저장하기</button>
        </div>
    `;
    const imageInput = document.getElementById("image");
    if (imageInput) {
        imageInput.addEventListener("change", handleImagePreview);
    }
}

function openAddressSearch() {
    new daum.Postcode({
        oncomplete: function(data) {
            const korAddr = data.roadAddress || data.jibunAddress;
            const engAddr = data.roadAddressEnglish || data.jibunAddressEnglish;

            document.getElementById("location").value = korAddr;

            let engInput = document.getElementById("englishAddress");
            if (!engInput) {
                engInput = document.createElement("input");
                engInput.type = "hidden";
                engInput.id = "englishAddress";
                engInput.name = "englishAddress";
                document.getElementById("locker-manage-content").appendChild(engInput);
            }
            engInput.value = engAddr;

            // 주소 → 좌표 변환
            const geocoder = new kakao.maps.services.Geocoder();
            geocoder.addressSearch(korAddr, function(result, status) {
                if (status === kakao.maps.services.Status.OK) {
                    document.getElementById("latitude").value = result[0].y;
                    document.getElementById("longitude").value = result[0].x;
                } else {
                    ModalUtils.showError("좌표 변환 실패", "주소를 다시 확인해주세요.");
                }
            });
        }
    }).open();
}

function handleImagePreview(event) {
    const files = Array.from(event.target.files);
    const previewContainer = document.getElementById("image-preview-container");

    // 1. 기존 미리보기 모두 제거 (기존 이미지 포함)
    document.querySelectorAll(".preview-wrapper, .preview-item").forEach(el => el.remove());
    selectedFiles = [];  // 초기화

    // 2. 이미지 수 제한
    if (files.length > 5) {
        ModalUtils.showWarning("이미지는 최대 5장까지 업로드할 수 있습니다.", "제한 초과");
        return;
    }

    // 3. 이미지 렌더링
    files.forEach(file => {
        if (selectedFiles.find(f => f.name === file.name && f.size === file.size)) {
            // 중복 체크
            console.warn("중복된 파일은 무시됨:", file.name);
            return;
        }

        selectedFiles.push(file);

        const reader = new FileReader();
        reader.onload = function(e) {
            const wrapper = document.createElement("div");
            wrapper.classList.add("preview-wrapper");

            const img = document.createElement("img");
            img.src = e.target.result;
            img.classList.add("preview-img");

            const btn = document.createElement("button");
            btn.textContent = "x";
            btn.classList.add("remove-btn");
            btn.onclick = function () {
                wrapper.remove();
                selectedFiles = selectedFiles.filter(f => f.name !== file.name || f.size !== file.size);
                updateFileInput();
            };

            wrapper.appendChild(img);
            wrapper.appendChild(btn);
            previewContainer.appendChild(wrapper);
        };
        reader.readAsDataURL(file);
    });

    // 4. FileList 업데이트
    updateFileInput();
}

// File input에 선택된 파일 덮어쓰기
function updateFileInput() {
    const dataTransfer = new DataTransfer();
    selectedFiles.forEach(file => dataTransfer.items.add(file));
    document.getElementById("image").files = dataTransfer.files;
}

function removePreview(button) {
    button.parentElement.remove();
}

function submitLockerUpdate(lockerId) {
    const lockerName = document.getElementById("lockerName").value;
    const address = document.getElementById("location").value;
    const addressEnglish = document.getElementById("englishAddress")?.value || "";
    const lat = document.getElementById("latitude").value;
    const lng = document.getElementById("longitude").value;
    const detailAddress = document.getElementById("detailAddress").value;

    if (!lockerName || !address || !lat || !lng) {
        ModalUtils.showWarning("필수 항목을 모두 입력해주세요.", "입력 확인");
        return;
    }

    const formData = new FormData();

    const lockerUpdateRequest = {
        lockerId: lockerId,
        lockerName: lockerName,
        address: address,
        addressDetail: detailAddress,
        addressEnglish: addressEnglish,
        latitude: parseFloat(lat),
        longitude: parseFloat(lng),
        isAvailable: "YES",
        jimTypeIds: []
    };

    // 선택된 짐 타입만 전송
    const priceInputs = document.querySelectorAll('.condition-row.selected input[type="hidden"]');
    priceInputs.forEach(input => {
        const jimTypeId = parseInt(input.name.split("_")[1]);
        lockerUpdateRequest.jimTypeIds.push(jimTypeId);
    });

    // JSON 요청 본문
    formData.append("locker", new Blob([
        JSON.stringify(lockerUpdateRequest)
    ], { type: "application/json" }));

    // 새로 업로드된 이미지가 있으면 추가
    const files = document.getElementById("image").files;
    if (files.length > 0) {
        for (let i = 0; i < files.length; i++) {
            formData.append("images", files[i]);
        }
    }

    fetch(`${contextPath}/lockers/update/${lockerId}`, {
        method: "POST",
        body: formData
    }).then(res => res.json())
      .then(data => {
          if (data.code === 1000) {
              ModalUtils.showSuccess(
                  "보관소 정보가 성공적으로 수정되었습니다!",
                  "완료",
                  () => {
                      location.href = `${contextPath}/page/lockers`;
                  }
              );
          } else {
              ModalUtils.showError(data.message || "수정 실패", "에러");
          }
      }).catch(err => {
          console.error(err);
          ModalUtils.showError("수정 중 네트워크 오류가 발생했습니다.", "에러");
      });
}

// 짐 part 선택 toggle 처리
document.addEventListener('click', function (e) {
    const row = e.target.closest('.condition-row');
    if (!row) return;

    const icon = row.querySelector('.check-btn img');
    const hiddenInput = row.querySelector('input[type="hidden"]');
    const isSelected = row.classList.contains('selected');

    // 현재 선택된 항목 수
    const selectedRows = document.querySelectorAll('.condition-row.selected');

    // 해제 시, 선택된 항목이 1개뿐이면 막기
    if (isSelected && selectedRows.length === 1) {
        ModalUtils.showWarning("최소 하나 이상의 짐 타입을 선택해야 합니다.", "선택 필수");
        return;
    }

    if (isSelected) {
        row.classList.remove('selected');
        icon.src = `${contextPath}/images/circle_check_blank.svg`;
        hiddenInput.disabled = true;
    } else {
        row.classList.add('selected');
        icon.src = `${contextPath}/images/circle_check_full.svg`;
        hiddenInput.disabled = false;
    }
});