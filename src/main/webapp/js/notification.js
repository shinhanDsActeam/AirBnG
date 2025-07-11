class NotificationSSE {
    constructor() {
        this.notifications = [];
        this.deletedNotificationIds = new Map(); // key: alarmKey, value: timestamp
        this.sseManager = null;
        this.memberId = window.memberId || document.body.dataset.memberId;
    }

    init() {
        if (!this.memberId) {
            console.warn('알림 기능 비활성화: 로그인하지 않은 사용자');
            return;
        }

        this.loadFromStorage();
        this.cleanExpiredDeletions();
        this.renderNotifications();
        this.initSSE();
        this.bindEvents();
    }

    /**
     * 알림의 고유 키를 생성
     * 동일한 id라도 message나 type이 다르면 다른 알림으로 간주
     */
    getAlarmKey(alarmData) {
        return `${alarmData.id}|${alarmData.message}|${alarmData.type}|${alarmData.reservationId}`;
    }

    initSSE() {
        this.sseManager = getSSEManager();
        this.sseManager.addEventListener('alarm', (alarmData) => {
            this.handleNotification(alarmData);
        });
        this.sseManager.init();
    }

    /**
     * 전체 삭제 버튼 이벤트 바인딩
     */
    bindEvents() {
        const clearAllBtn = document.getElementById('clearAllBtn');
        if (clearAllBtn) {
            clearAllBtn.addEventListener('click', () => {
                this.clearAllNotifications();
            });
        }
    }

    /**
     * 수신한 알림 처리 (중복/삭제 체크 후 추가)
     */
    handleNotification(alarmData) {
        const now = Date.now();
        const alarmKey = this.getAlarmKey(alarmData);

        // 삭제 기록 확인 (23시간 이내면 무시)
        const deletedAt = this.deletedNotificationIds.get(alarmKey);
        if (deletedAt && (now - deletedAt) < 23 * 60 * 60 * 1000) return;
//        if (deletedAt) this.deletedNotificationIds.delete(alarmKey); // 만료된 삭제 기록 제거

        // 중복 알림이면 무시
        if (this.notifications.some(n => this.getAlarmKey(n) === alarmKey)) return;

        const newNotification = {
            ...alarmData,
            receivedAt: this.formatDateTime(new Date())
        };

        // 가장 앞에 추가 / 50개 초과시 오래된 것 제거
        this.notifications.unshift(newNotification);
        if (this.notifications.length > 50) {
            this.notifications = this.notifications.slice(0, 50);
        }

        this.saveToStorage();
        this.renderNotifications();
        this.showBrowserNotification(alarmData);
    }

    formatDateTime(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        let hours = date.getHours();
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const ampm = hours >= 12 ? '오후' : '오전';
        hours = hours % 12 || 12;
        return `${year}-${month}-${day} ${ampm} ${String(hours).padStart(2, '0')}:${minutes}`;
    }

    // 알림 목록을 렌더링
    renderNotifications() {
        const container = document.getElementById('notifications');
        if (!container) return;

        const visibleNotifications = this.notifications.filter(n => {
            const alarmKey = this.getAlarmKey(n);
            return !this.deletedNotificationIds.has(alarmKey);
        });

        if (visibleNotifications.length === 0) {
            this.showEmptyState();
            return;
        }

        container.innerHTML = visibleNotifications.map(notification => `
            <div class="notification-item">
                <div class="notification-content">
                    <div class="notification-header">
                        <span class="notification-type">${this.getTypeLabel(notification.type)}</span>
                        <div class="notification-actions">
                            <span class="notification-time">${notification.receivedAt}</span>
                            <button class="clear-btn" onclick="notificationSSE.removeNotification('${notification.id}', '${notification.message}', '${notification.type}')">×</button>
                        </div>
                    </div>
                    <div class="notification-message">${notification.message}</div>
                    <div class="notification-details">
                        예약번호: ${notification.reservationId} |
                        사용자: ${notification.nickName}
                    </div>
                </div>
            </div>
        `).join('');

        this.updateClearAllButton();
    }

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
            const visibleCount = this.notifications.filter(n => {
                return !this.deletedNotificationIds.has(this.getAlarmKey(n));
            }).length;
            clearAllBtn.disabled = visibleCount === 0;
        }
    }

    // 알림 삭제
    removeNotification(id, message, type) {
        const key = `${id}|${message}|${type}`;
        this.notifications = this.notifications.filter(n => this.getAlarmKey(n) !== key);
        this.deletedNotificationIds.set(key, Date.now());
        this.saveToStorage();
        this.renderNotifications();
    }

    // 전체 알림 삭제
    clearAllNotifications() {
        const now = Date.now();
        this.notifications.forEach(n => {
            this.deletedNotificationIds.set(this.getAlarmKey(n), now);
        });
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
        if ('Notification' in window && Notification.permission === 'granted') {
            const notification = new Notification('새 알림', {
                body: alarmData.message,
                icon: `${contextPath}/images/favicon.svg`
            });
            setTimeout(() => notification.close(), 5000);
        }
    }

    // 로컬 스토리지에 알림 및 삭제 기록 저장
    saveToStorage() {
        if (!this.memberId) return;
        localStorage.setItem(`alarmHistory_${this.memberId}`, JSON.stringify(this.notifications));
        localStorage.setItem(`deletedAlarms_${this.memberId}`, JSON.stringify([...this.deletedNotificationIds.entries()]));
    }

    // 로컬 스토리지에서 알림 및 삭제 기록 불러오기
    loadFromStorage() {
        if (!this.memberId) return;

        try {
            const deleted = JSON.parse(localStorage.getItem(`deletedAlarms_${this.memberId}`));
            this.deletedNotificationIds = new Map(deleted);
        } catch {
            this.deletedNotificationIds = new Map();
        }

        try {
            const saved = JSON.parse(localStorage.getItem(`alarmHistory_${this.memberId}`)) || [];
            const now = Date.now();
            this.notifications = saved
                .filter(n => {
                    const alarmKey = this.getAlarmKey(n);
                    const deletedAt = this.deletedNotificationIds.get(alarmKey);
                    return !(deletedAt && (now - deletedAt < 23 * 60 * 60 * 1000));
                })
                .map(n => {
                    if (n.receivedAt && n.receivedAt.includes('.')) {
                        const date = new Date(n.receivedAt.replace(/\./g, '-').replace(' ', 'T'));
                        n.receivedAt = !isNaN(date.getTime()) ? this.formatDateTime(date) : n.receivedAt;
                    }
                    return n;
                });
        } catch {
            this.notifications = [];
        }
    }
    // 만료된 삭제 기록 정리 (23시간 이상 지난 것)
    cleanExpiredDeletions() {
        const now = Date.now();
        for (const [key, deletedAt] of this.deletedNotificationIds.entries()) {
            if (now - deletedAt > 23 * 60 * 60 * 1000) {
                this.deletedNotificationIds.delete(key);
            }
        }
    }
}

let notificationSSE = null;
document.addEventListener('DOMContentLoaded', () => {
    notificationSSE = new NotificationSSE();
    notificationSSE.init();
});
