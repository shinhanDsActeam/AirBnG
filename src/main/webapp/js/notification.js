class NotificationSSE {
    constructor() {
        this.notifications = [];
        // 삭제된 알림 ID와 삭제 시각을 저장 (Map: key=알림id, value=삭제시간 timestamp)
        this.deletedNotificationIds = new Map();
        this.sseManager = null;
        this.memberId = window.memberId || document.body.dataset.memberId;
    }

    // 초기화
    init() {
        if (!this.memberId) {
            console.warn('알림 기능 비활성화: 로그인하지 않은 사용자');
            return;
        }

        this.loadFromStorage();
        this.cleanExpiredDeletions(); // 만료된 삭제 기록 정리
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
        const clearAllBtn = document.getElementById('clearAllBtn');
        if (clearAllBtn) {
            clearAllBtn.addEventListener('click', () => {
                this.clearAllNotifications();
            });
        }
    }

    // 알림 처리
    handleNotification(alarmData) {
        // 삭제 기록 확인 (23시간 이내면 무시)
        const deletedAt = this.deletedNotificationIds.get(alarmData.id);
        const now = Date.now();
        if (deletedAt && (now - deletedAt) < 23 * 60 * 60 * 1000) {
            // 삭제된 지 23시간 안됐으면 무시
            return;
        } else if (deletedAt) {
            // 23시간 넘었으면 삭제 기록에서 제거
            this.deletedNotificationIds.delete(alarmData.id);
        }

        // 새 알림 생성 (id는 timestamp+랜덤값)
        const newNotification = {
            ...alarmData,
            receivedAt: this.formatDateTime(new Date()),
            id: Date.now() + Math.random()
        };

        this.notifications.unshift(newNotification);

        if (this.notifications.length > 50) {
            this.notifications = this.notifications.slice(0, 50);
        }

        this.saveToStorage();
        this.renderNotifications();
        this.showBrowserNotification(alarmData);
    }

    // 시간 포맷 (12시간 + 오전/오후)
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
                                <button class="clear-btn" onclick="notificationSSE.removeNotification('${notification.id}')">×</button>
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

    showEmptyState() {
        const container = document.getElementById('notifications');
        if (container) {
            container.innerHTML = '<div class="empty-message">알림이 없습니다.</div>';
        }
        this.updateClearAllButton();
    }

    updateClearAllButton() {
        const clearAllBtn = document.getElementById('clearAllBtn');
        if (clearAllBtn) {
            const visibleCount = this.notifications.filter(n => !this.deletedNotificationIds.has(n.id)).length;
            clearAllBtn.disabled = visibleCount === 0;
        }
    }

    // 개별 알림 삭제
    removeNotification(id) {
        this.notifications = this.notifications.filter(n => n.id != id);
        // 삭제한 시간 기록
        this.deletedNotificationIds.set(id, Date.now());
        this.saveToStorage();
        this.renderNotifications();
    }

    // 전체 삭제
    clearAllNotifications() {
        // 모든 알림 삭제 기록에 추가 (삭제 시간 기록)
        const now = Date.now();
        this.notifications.forEach(n => this.deletedNotificationIds.set(n.id, now));
        this.notifications = [];
        this.saveToStorage();
        this.renderNotifications();
    }

    getTypeLabel(type) {
        const labelMap = {
            'EXPIRED': '만료 알림',
            'REMINDER': '리마인더',
            'STATE_CHANGE': '상태 변경',
            'CANCEL_NOTICE': '취소 알림'
        };
        return labelMap[type] || type;
    }

    showBrowserNotification(alarmData) {
        if (this.sseManager) {
            this.sseManager.showBrowserNotification(
                '새 알림',
                alarmData.message,
                '/favicon.ico'
            );
        }
    }

    saveToStorage() {
        if (!this.memberId) return;
        localStorage.setItem(`alarmHistory_${this.memberId}`, JSON.stringify(this.notifications));
        localStorage.setItem(`deletedAlarms_${this.memberId}`, JSON.stringify([...this.deletedNotificationIds.entries()]));
    }

    loadFromStorage() {
        if (!this.memberId) return;

        const saved = localStorage.getItem(`alarmHistory_${this.memberId}`);
        const deleted = localStorage.getItem(`deletedAlarms_${this.memberId}`);

        if (saved) {
            try {
                this.notifications = JSON.parse(saved);

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

        if (deleted) {
            try {
                const entries = JSON.parse(deleted);
                this.deletedNotificationIds = new Map(entries);
            } catch (e) {
                this.deletedNotificationIds = new Map();
            }
        }
    }

    // 23시간 넘은 삭제 기록 삭제
    cleanExpiredDeletions() {
        const now = Date.now();
        for (const [id, deletedAt] of this.deletedNotificationIds.entries()) {
            if (now - deletedAt > 23 * 60 * 60 * 1000) {
                this.deletedNotificationIds.delete(id);
            }
        }
    }
}

// 전역 알림 매니저 인스턴스
let notificationSSE = null;

document.addEventListener('DOMContentLoaded', () => {
    notificationSSE = new NotificationSSE();
    notificationSSE.init();
});
