function goToRegisterLocker() {
    location.href = contextPath + '/page/lockers/register';
}

function goToLogin() {
    location.href = contextPath + '/page/login';
}

function goToSignup() {
    location.href = contextPath + '/page/signup';
}

function goToLockerDetails(buttonElement) {
    const lockerId = buttonElement.getAttribute("data-locker-id");
    if (!lockerId) {
        alert("보관소 ID가 존재하지 않습니다.");
        return;
    }
    location.href = contextPath + '/page/lockerDetails?lockerId=' + lockerId;
}

function goToLockerDetails(buttonElement) {
    const lockerId = buttonElement.getAttribute("data-locker-id");
    if (!lockerId) {
        ModalUtils.showWarning("보관소 ID가 존재하지 않습니다.", "유효하지 않은 요청");
        return;
    }
    location.href = contextPath + '/page/lockerDetails?lockerId=' + lockerId;
}

function toggleLockerAvailability(buttonElement) {
    const lockerId = buttonElement.getAttribute("data-locker-id");
    const isActive = buttonElement.classList.contains("btn-stop"); // 현재 운영중이면 중지
    const confirmMessage = isActive
        ? "정말 중지하겠습니까?"
        : "보관소를 다시 재개하시겠습니까?";
    const confirmTitle = "보관소 상태 변경";

    if (!lockerId) {
        ModalUtils.showWarning("보관소 ID가 존재하지 않습니다.", "유효하지 않은 요청");
        return;
    }

    ModalUtils.showConfirm(
        confirmTitle,
        confirmMessage,
        () => {
            // 확인 시 fetch 실행
            fetch(`${contextPath}/lockers/${lockerId}`, {
                method: 'PATCH',
            })
            .then(res => res.json())
            .then(data => {
                if (data.code === 1000) {
                    location.reload();  // 성공 시 새로고침
                } else {
                    ModalUtils.showError(data.message || "상태 변경 실패", "처리 실패");
                }
            })
            .catch(error => {
                console.error("에러 발생:", error);
                ModalUtils.showError("서버와 통신 중 오류가 발생했습니다.", "네트워크 오류");
            });
        },
        () => {
            // 취소 시 아무 일 없음
            console.log("상태 변경 취소됨");
        }
    );
}