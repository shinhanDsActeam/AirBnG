class NotificationSSE {
    constructor() {
        this.notifications = [];
        this.deletedNotificationIds = new Map(); // key: alarmData.id, value: timestamp
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

    initSSE() {
        this.sseManager = getSSEManager();
        this.sseManager.addEventListener('alarm', (alarmData) => {
            this.handleNotification(alarmData);
        });
        this.sseManager.init();
    }

    bindEvents() {
        const clearAllBtn = document.getElementById('clearAllBtn');
        if (clearAllBtn) {
            clearAllBtn.addEventListener('click', () => {
                this.clearAllNotifications();
            });
        }
    }

    handleNotification(alarmData) {
        const now = Date.now();

        // 삭제 기록 확인 (23시간 이내면 무시)
        const deletedAt = this.deletedNotificationIds.get(alarmData.id);
        if (deletedAt && (now - deletedAt) < 23 * 60 * 60 * 1000) return;
        if (deletedAt) this.deletedNotificationIds.delete(alarmData.id); // 만료된 삭제 기록 제거

        // 중복 알림이면 무시
        if (this.notifications.some(n => n.id === alarmData.id)) return;

        const newNotification = {
            ...alarmData,
            receivedAt: this.formatDateTime(new Date())
        };

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

    renderNotifications() {
        const container = document.getElementById('notifications');
        if (!container) return;

        if (this.notifications.length === 0) {
            this.showEmptyState();
            return;
        }

        container.innerHTML = this.notifications.map(notification => `
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

    updateClearAllButton() {
        const clearAllBtn = document.getElementById('clearAllBtn');
        if (clearAllBtn) {
            const visibleCount = this.notifications.filter(n => !this.deletedNotificationIds.has(n.id)).length;
            clearAllBtn.disabled = visibleCount === 0;
        }
    }

    removeNotification(id) {
        this.notifications = this.notifications.filter(n => n.id !== id);
        this.deletedNotificationIds.set(id, Date.now());
        this.saveToStorage();
        this.renderNotifications();
    }

    clearAllNotifications() {
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
        if ('Notification' in window && Notification.permission === 'granted') {
            const notification = new Notification('새 알림', {
                body: alarmData.message,
                icon: '/favicon.ico'
            });
            setTimeout(() => notification.close(), 5000);
        }
    }

    saveToStorage() {
        if (!this.memberId) return;
        localStorage.setItem(`alarmHistory_${this.memberId}`, JSON.stringify(this.notifications));
        localStorage.setItem(`deletedAlarms_${this.memberId}`, JSON.stringify([...this.deletedNotificationIds.entries()]));
    }

    loadFromStorage() {
        if (!this.memberId) return;

        try {
            const saved = JSON.parse(localStorage.getItem(`alarmHistory_${this.memberId}`)) || [];
            this.notifications = saved.map(n => {
                if (n.receivedAt && n.receivedAt.includes('.')) {
                    const date = new Date(n.receivedAt.replace(/\./g, '-').replace(' ', 'T'));
                    n.receivedAt = !isNaN(date.getTime()) ? this.formatDateTime(date) : n.receivedAt;
                }
                return n;
            });
        } catch {
            this.notifications = [];
        }

        try {
            const deleted = JSON.parse(localStorage.getItem(`deletedAlarms_${this.memberId}`));
            this.deletedNotificationIds = new Map(deleted);
        } catch {
            this.deletedNotificationIds = new Map();
        }
    }

    cleanExpiredDeletions() {
        const now = Date.now();
        for (const [id, deletedAt] of this.deletedNotificationIds.entries()) {
            if (now - deletedAt > 23 * 60 * 60 * 1000) {
                this.deletedNotificationIds.delete(id);
            }
        }
    }
}

let notificationSSE = null;
document.addEventListener('DOMContentLoaded', () => {
    notificationSSE = new NotificationSSE();
    notificationSSE.init();
});
