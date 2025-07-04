/**
 * 모달 사용 예제 코드
 * 다양한 상황에서 모달을 사용하는 방법을 보여줍니다.
 */

// 1. 기본 성공 모달
function showSuccessExample() {
    ModalUtils.showSuccess('데이터가 성공적으로 저장되었습니다!');
}

// 1-2. 타이틀 커스텀 성공 모달
// 확인버튼 눌러도 아무런 동작 없이 모달이 닫히기만 하는 예제
function showCustomSuccessExample() {
    ModalUtils.showSuccess('데이터가 성공적으로 저장되었습니다!', '커스텀 성공');
}

// 2. 콜백과 함께 성공 모달
// 로그인 완료 후 메인 페이지로 이동하는 예제
function showSuccessWithCallback() {
    ModalUtils.showSuccess('로그인이 완료되었습니다!', " ",function() {
        location.href = `${contextPath}/page/home`;
    });
}

// 3. 에러 모달
function showErrorExample() {
    ModalUtils.showError('서버 연결에 실패했습니다. 다시 시도해주세요.');
}

// 4. 정보 모달
function showInfoExample() {
    ModalUtils.showInfo('시스템 점검이 예정되어 있습니다. (2024-01-15 02:00~06:00)');
}

// 5. 경고 모달
function showWarningExample() {
    ModalUtils.showWarning('세션이 곧 만료됩니다. 계속 사용하시겠습니까?');
}

// 6. 확인 모달 (기본)
function showConfirmExample() {
    ModalUtils.showConfirm('정말로 삭제하시겠습니까?',
        function() {
            // 확인 버튼 클릭 시
            console.log('삭제 확인됨');
            ModalUtils.showSuccess('삭제되었습니다.');
        },
        function() {
            // 취소 버튼 클릭 시
            console.log('삭제 취소됨');
        }
    );
}

// 7. 복합 작업 예제 - 삭제 확인 후 결과 표시
function deleteUserExample(userId) {
    ModalUtils.showConfirm(`사용자 ID: ${userId}를 삭제하시겠습니까?`,
        function() {
            // 삭제 API 호출 시뮬레이션
            console.log(`사용자 ${userId} 삭제 중...`);

            // 실제로는 Ajax 요청
            setTimeout(() => {
                const success = Math.random() > 0.3; // 70% 성공률

                if (success) {
                    ModalUtils.showSuccess('사용자가 성공적으로 삭제되었습니다.');
                } else {
                    ModalUtils.showError('삭제 중 오류가 발생했습니다.');
                }
            }, 1000);
        }
    );
}

// 8. 폼 제출 예제
function submitFormExample() {
    const formData = {
        name: '홍길동',
        email: 'hong@example.com'
    };

    ModalUtils.showConfirm('입력한 정보로 회원가입을 진행하시겠습니까?',
        function() {
            // 폼 제출 시뮬레이션
            console.log('폼 제출 중...', formData);

            // Ajax 요청 시뮬레이션
            setTimeout(() => {
                const success = Math.random() > 0.2; // 80% 성공률

                if (success) {
                    ModalUtils.showSuccess('회원가입이 완료되었습니다!', function() {
                        console.log('로그인 페이지로 이동');
                        // window.location.href = '/login';
                    });
                } else {
                    ModalUtils.showError('회원가입 중 오류가 발생했습니다.');
                }
            }, 1500);
        }
    );
}

// 9. 커스텀 모달 생성 예제
function createCustomModalExample() {
    const customModalId = ModalUtils.createCustomModal({
        type: 'warning',
        title: '데이터 손실 경고',
        message: '저장하지 않은 변경사항이 있습니다. 정말로 페이지를 떠나시겠습니까?',
        confirmText: '떠나기',
        cancelText: '머물기',
        showCancel: true,
        onConfirm: function() {
            console.log('페이지 떠남');
            ModalUtils.showInfo('변경사항이 저장되지 않았습니다.');
        },
        onCancel: function() {
            console.log('페이지에 머물기');
        }
    });
}

// 10. 모달 체인 예제 - 여러 모달을 순서대로 표시
function showModalChainExample() {
    const modals = [
        {
            type: 'info',
            message: '업데이트를 시작합니다.'
        },
        {
            type: 'warning',
            message: '업데이트 중입니다. 잠시만 기다려주세요.'
        },
        {
            type: 'success',
            message: '업데이트가 완료되었습니다!'
        }
    ];

    ModalUtils.showModalChain(modals);
}

// 11. 파일 업로드 예제
function uploadFileExample() {
    const file = { name: 'document.pdf', size: '2.5MB' };

    ModalUtils.showConfirm(`파일 "${file.name}" (${file.size})를 업로드하시겠습니까?`,
        function() {
            console.log('파일 업로드 시작');

            // 업로드 진행 시뮬레이션
            setTimeout(() => {
                const success = Math.random() > 0.25; // 75% 성공률

                if (success) {
                    ModalUtils.showSuccess('파일이 성공적으로 업로드되었습니다.');
                } else {
                    ModalUtils.showError('파일 업로드에 실패했습니다. 다시 시도해주세요.');
                }
            }, 2000);
        }
    );
}