class NotificationManager {
    constructor() {
        this.notifications = [];
        this.sseManager = null;
        this.memberId = document.body.dataset.memberId;
    }

    // 초기화
    init() {
        if (!this.memberId) {
            console.warn('알림 기능 비활성화: 로그인하지 않은 사용자');
            return;
        }

        this.loadFromStorage();
        this.renderNotifications();
        this.initSSE();
        this.bindEvents();
    }

    // SSE 초기화
    initSSE() {
        this.sseManager = getSSEManager();

        // 알림 이벤트 리스너 등록
        this.sseManager.addEventListener('alarm', (alarmData) => {
            this.handleNotification(alarmData);
        });

        // SSE 연결 시작
        this.sseManager.init();
    }

    // 이벤트 바인딩
    bindEvents() {
        // 전체 삭제 버튼
        const clearAllBtn = document.getElementById('clearAllBtn');
        if (clearAllBtn) {
            clearAllBtn.addEventListener('click', () => {
                this.clearAllNotifications();
            });
        }
    }

    // 알림 처리
    handleNotification(alarmData) {
        const newNotification = {
            ...alarmData,
            receivedAt: this.formatDateTime(new Date()),
            id: Date.now() + Math.random()
        };

        this.notifications.unshift(newNotification);

        // 최대 50개까지만 보관
        if (this.notifications.length > 50) {
            this.notifications = this.notifications.slice(0, 50);
        }

        this.saveToStorage();
        this.renderNotifications();
        this.showBrowserNotification(alarmData);
    }

    // 시간 포맷 (12시간 형식 + 오전/오후)
    formatDateTime(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');

        let hours = date.getHours();
        const minutes = String(date.getMinutes()).padStart(2, '0');

        const ampm = hours >= 12 ? '오후' : '오전';
        hours = hours % 12;
        hours = hours ? hours : 12;
        const displayHours = String(hours).padStart(2, '0');

        return `${year}-${month}-${day} ${ampm} ${displayHours}:${minutes}`;
    }

    // 알림 렌더링
    renderNotifications() {
        const container = document.getElementById('notifications');
        if (!container) return;

        if (this.notifications.length === 0) {
            this.showEmptyState();
            return;
        }

        container.innerHTML = this.notifications.map(notification => {
            return `
                <div class="notification-item">
                    <div class="notification-content">
                        <div class="notification-header">
                            <span class="notification-type">${this.getTypeLabel(notification.type)}</span>
                            <div class="notification-actions">
                                <span class="notification-time">${notification.receivedAt}</span>
                                <button class="clear-btn" onclick="notificationManager.removeNotification('${notification.id}')">×</button>
                            </div>
                        </div>
                        <div class="notification-message">${notification.message}</div>
                        <div class="notification-details">
                            예약번호: ${notification.reservationId} |
                            사용자: ${notification.nickName}
                        </div>
                    </div>
                </div>
            `;
        }).join('');

        this.updateClearAllButton();
    }

    // 빈 상태 표시
    showEmptyState() {
        const container = document.getElementById('notifications');
        if (container) {
            container.innerHTML = '<div class="empty-message">알림이 없습니다.</div>';
        }
        this.updateClearAllButton();
    }

    // 전체 삭제 버튼 상태 업데이트
    updateClearAllButton() {
        const clearAllBtn = document.getElementById('clearAllBtn');
        if (clearAllBtn) {
            clearAllBtn.disabled = this.notifications.length === 0;
        }
    }

    // 개별 알림 삭제
    removeNotification(id) {
        this.notifications = this.notifications.filter(n => n.id != id);
        this.saveToStorage();
        this.renderNotifications();
    }

    // 모든 알림 삭제
    clearAllNotifications() {
        this.notifications = [];
        this.saveToStorage();
        this.renderNotifications();
    }

    // 알림 타입 라벨 반환
    getTypeLabel(type) {
        const labelMap = {
            'EXPIRED': '만료 알림',
            'REMINDER': '리마인더',
            'STATE_CHANGE': '상태 변경',
            'CANCEL_NOTICE': '취소 알림'
        };
        return labelMap[type] || type;
    }

    // 브라우저 알림 표시
    showBrowserNotification(alarmData) {
        if (this.sseManager) {
            this.sseManager.showBrowserNotification(
                '새 알림',
                alarmData.message,
                '/favicon.ico'
            );
        }
    }

    // 로컬 스토리지에 저장
    saveToStorage() {
        if (!this.memberId) return;
        localStorage.setItem(`alarmHistory_${this.memberId}`, JSON.stringify(this.notifications));
    }

    // 로컬 스토리지에서 불러오기
    loadFromStorage() {
        if (!this.memberId) return;

        const saved = localStorage.getItem(`alarmHistory_${this.memberId}`);
        if (saved) {
            try {
                this.notifications = JSON.parse(saved);

                // 기존 저장된 알림의 시간 형식 업데이트
                this.notifications = this.notifications.map(notification => {
                    if (notification.receivedAt && notification.receivedAt.includes('.')) {
                        const date = new Date(notification.receivedAt.replace(/\./g, '-').replace(' ', 'T'));
                        if (!isNaN(date.getTime())) {
                            notification.receivedAt = this.formatDateTime(date);
                        }
                    }
                    return notification;
                });
            } catch (e) {
                console.error("저장된 알림 불러오기 실패:", e);
                this.notifications = [];
            }
        }
    }
}

// 전역 알림 매니저 인스턴스
let notificationManager = null;

// DOM 로딩 완료 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    notificationManager = new NotificationManager();
    notificationManager.init();
});