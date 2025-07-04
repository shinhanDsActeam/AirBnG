/**
 * 공통 모달 유틸리티 클래스
 * 모든 모달 관련 기능을 관리하는 통합 클래스
 */
class ModalUtils {
    static confirmCallback = null;
    static currentModal = null;

    /**
     * 모달 표시
     * @param {string} modalId - 모달 ID
     * @param {Object} options - 모달 옵션
     */
    static showModal(modalId, options = {}) {
        const modal = document.getElementById(modalId);
        if (!modal) {
            console.error(`Modal with ID '${modalId}' not found`);
            return;
        }

        // 기본 옵션 설정
        const defaultOptions = {
            title: '',
            message: '',
            confirmText: '확인',
            cancelText: '취소',
            showCancel: true,
            onConfirm: null,
            onCancel: null
        };

        const config = {...defaultOptions, ...options};

        // 모달 내용 업데이트
        this.updateModalContent(modal, config);

        // 콜백 저장
        this.confirmCallback = config.onConfirm;
        this.currentModal = modalId;

        // 모달 표시
        modal.classList.remove('hidden');

        // ESC 키 이벤트 등록
        document.addEventListener('keydown', this.handleEscKey);

        // 배경 클릭 이벤트 등록
        // modal.addEventListener('click', this.handleBackgroundClick);

        // 포커스 관리
        this.manageFocus(modal);
    }

    /**
     * 모달 숨기기
     * @param {string} modalId - 모달 ID
     */
    static hideModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('hidden');
            this.currentModal = null;
            this.confirmCallback = null;

            // 이벤트 리스너 제거
            document.removeEventListener('keydown', this.handleEscKey);
            modal.removeEventListener('click', this.handleBackgroundClick);
        }
    }

    /**
     * 확인 액션 처리
     * @param {string} modalId - 모달 ID
     */
    static confirmAction(modalId) {
        console.log("confirmAction called for modal:", modalId);
        const callback = this.confirmCallback;

        this.hideModal(modalId);

        if (callback && typeof callback === 'function') {
            callback();
        }
    }


    /**
     * 성공 모달 표시
     * @param {string} message - 성공 메시지
     * @param {string} title - 성공 타이틀
     * @param {Function} callback - 콜백 함수
     */
    static showSuccess(message = '작업이 성공적으로 완료되었습니다.', title = '성공', callback = null) {
        this.showModal('success-modal', {
            title: title,
            message: message,
            onConfirm: callback,
            showCancel: false
        });
    }

    /**
     * 에러 모달 표시
     * @param {string} message - 에러 메시지
     * @param {string} title - 에러 타이틀
     * @param {Function} callback - 콜백 함수
     */
    static showError(message = '처리 중 오류가 발생했습니다.', title = '오류', callback = null) {
        this.showModal('error-modal', {
            title: title,
            message: message,
            onConfirm: callback,
            showCancel: false
        });
    }

    /**
     * 정보 모달 표시
     * @param {string} message - 정보 메시지
     * @param {Function} callback - 콜백 함수
     */
    static showInfo(message = '안내 메시지가 표시됩니다.', callback = null) {
        this.showModal('info-modal', {
            message: message,
            onConfirm: callback,
            showCancel: false
        });
    }

    /**
     * 경고 모달 표시
     * @param {string} message - 경고 메시지
     * @param {string} title - 경고 타이틀
     * @param {Function} callback - 콜백 함수
     */
    static showWarning(message = '주의가 필요합니다.', title = '경고', callback = null) {
        this.showModal('warning-modal', {
            title: title,
            message: message,
            onConfirm: callback,
            showCancel: false
        });
    }

    /**
     * 확인 모달 표시
     * @param {string} message - 확인 메시지
     * @param {Function} onConfirm - 확인 콜백
     * @param {Function} onCancel - 취소 콜백
     */
    static showConfirm(message = '정말로 진행하시겠습니까?', onConfirm = null, onCancel = null) {
        this.showModal('confirm-modal', {
            message: message,
            onConfirm: onConfirm,
            onCancel: onCancel,
            showCancel: true
        });
    }

    /**
     * 커스텀 모달 생성
     * @param {Object} config - 모달 설정
     */
    static createCustomModal(config) {
        const modalId = config.id || 'custom-modal-' + Date.now();
        const modal = document.createElement('div');
        modal.id = modalId;
        modal.className = 'modal-overlay hidden';

        const iconClass = this.getIconClass(config.type || 'info');
        const iconSymbol = this.getIconSymbol(config.type || 'info');

        modal.innerHTML = `
            <div class="modal">
                <div class="modal-content">
                    <div class="modal-icon ${iconClass}">${iconSymbol}</div>
                    <div class="modal-title">${config.title || '알림'}</div>
                    <div class="modal-text">${config.message || ''}</div>
                </div>
                <div class="modal-buttons">
                    ${config.showCancel ?
            `<button class="modal-btn" onclick="ModalUtils.hideModal('${modalId}')">${config.cancelText || '취소'}</button>` :
            ''
        }
                    <button class="modal-btn" onclick="ModalUtils.confirmAction('${modalId}')">${config.confirmText || '확인'}</button>
                </div>
            </div>
        `;

        document.body.appendChild(modal);

        // 모달 표시
        this.showModal(modalId, config);

        return modalId;
    }

    /**
     * 모달 내용 업데이트
     * @param {Element} modal - 모달 요소
     * @param {Object} config - 설정
     */
    static updateModalContent(modal, config) {
        if (config.title) {
            const titleElement = modal.querySelector('.modal-title');
            if (titleElement) titleElement.textContent = config.title;
        }

        if (config.message) {
            const messageElement = modal.querySelector('.modal-message') || modal.querySelector('.modal-text');
            if (messageElement) messageElement.textContent = config.message;
        }

        // 버튼 텍스트 업데이트
        const buttons = modal.querySelectorAll('.modal-btn');
        if (buttons.length > 1) {
            buttons[0].textContent = config.cancelText;
            buttons[1].textContent = config.confirmText;

            // 취소 버튼 표시/숨김
            if (!config.showCancel) {
                buttons[0].style.display = 'none';
                buttons[1].classList.add('single');
            }
        } else if (buttons.length === 1) {
            buttons[0].textContent = config.confirmText;
        }
    }

    /**
     * ESC 키 이벤트 처리
     * @param {Event} event - 키보드 이벤트
     */
    static handleEscKey = (event) => {
        if (event.key === 'Escape' && this.currentModal) {
            this.hideModal(this.currentModal);
        }
    }


    // 빼자.. 무조건 취소 혹은 확인을 눌러야만 꺼지게!
    /**
     * 배경 클릭 이벤트 처리
     * @param {Event} event - 클릭 이벤트
     */
    static handleBackgroundClick = (event) => {
        if (event.target.classList.contains('modal-overlay')) {
            this.hideModal(this.currentModal);
        }
    }

    /**
     * 포커스 관리
     * @param {Element} modal - 모달 요소
     */
    static manageFocus(modal) {
        const focusableElements = modal.querySelectorAll('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
        if (focusableElements.length > 0) {
            focusableElements[focusableElements.length - 1].focus();
        }
    }

    /**
     * 아이콘 클래스 반환
     * @param {string} type - 모달 타입
     * @returns {string} 아이콘 클래스
     */
    static getIconClass(type) {
        const iconClasses = {
            success: 'success-rotate',
            error: 'error-shake',
            warning: 'warning-pulse',
            info: 'info',
            confirm: 'warning'
        };
        return iconClasses[type] || 'info';
    }

    /**
     * 아이콘 심볼 반환
     * @param {string} type - 모달 타입
     * @returns {string} 아이콘 심볼
     */
    static getIconSymbol(type) {
        const iconSymbols = {
            success: '✓',
            error: '!',
            warning: 'i',
            info: 'i',
            confirm: '?'
        };
        return iconSymbols[type] || '!';
    }

    /**
     * 모든 모달 숨기기
     */
    static hideAllModals() {
        const modals = document.querySelectorAll('.modal-overlay');
        modals.forEach(modal => {
            modal.classList.add('hidden');
        });
        this.currentModal = null;
        this.confirmCallback = null;
    }

    /**
     * 모달 체인 - 여러 모달을 순서대로 표시
     * @param {Array} modals - 모달 배열
     */
    static showModalChain(modals) {
        console.log('showModalChain');
        if (modals.length === 0) return;

        const showNext = (index) => {
            console.log('showNext ', index);

            if (index >= modals.length) return;

            const modalConfig = modals[index];
            const originalCallback = modalConfig.onConfirm;

            modalConfig.onConfirm = () => {
                if (originalCallback) originalCallback();
                showNext(index + 1);
            };

            // 모달 타입에 따라 적절한 메서드 호출
            switch (modalConfig.type) {
                case 'success':
                    this.showSuccess(modalConfig.message, modalConfig.onConfirm);
                    break;
                case 'error':
                    this.showError(modalConfig.message, modalConfig.onConfirm);
                    break;
                case 'warning':
                    this.showWarning(modalConfig.message, modalConfig.onConfirm);
                    break;
                case 'info':
                    this.showInfo(modalConfig.message, modalConfig.onConfirm);
                    break;
                case 'confirm':
                    this.showConfirm(modalConfig.message, modalConfig.onConfirm, modalConfig.onCancel);
                    break;
                default:
                    this.showInfo(modalConfig.message, modalConfig.onConfirm);
            }
        };

        showNext(0);
    }
}

// 전역 함수들 (기존 코드와의 호환성을 위해)
function closeWarningModal() {
    ModalUtils.hideModal('warning-modal');
}

// DOM 로드 완료 시 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', function () {
    // 기본 모달들의 ESC 키 이벤트 처리
    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            ModalUtils.hideAllModals();
        }
    });
});