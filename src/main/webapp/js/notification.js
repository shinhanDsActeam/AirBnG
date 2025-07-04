// 로그인 여부 확인 후에만 SSE 연결
class NotificationSSE {
    constructor() {
        this.eventSource = null;
        this.isConnected = false;
        this.notifications = [];
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.memberId = document.body.dataset.memberId;

        if (this.memberId) {
            this.init();
        } else {
            console.warn('SSE 비활성화: 로그인하지 않은 사용자');
        }
    }

    init() {
        this.autoConnect();
    }

    autoConnect() {
        this.connect();
    }

    connect() {
        if (this.isConnected || !this.memberId) return;

        try {
            this.eventSource = new EventSource(`/AirBnG/alarms/reservations/alarms`);

            this.eventSource.addEventListener('connect', (event) => {
                console.log('SSE 연결 성공:', event.data);
                this.updateConnectionStatus(true);
                this.reconnectAttempts = 0;
            });

            this.eventSource.addEventListener('alarm', (event) => {
                try {
                    const alarmData = JSON.parse(event.data);
                    console.log('알림 수신:', alarmData);
                    this.handleNotification(alarmData);
                } catch (e) {
                    console.error('알림 데이터 파싱 오류:', e);
                }
            });

            this.eventSource.onopen = () => {
                console.log('SSE 연결 열림');
                this.updateConnectionStatus(true);
                this.reconnectAttempts = 0;
                this.showEmptyState();
            };

            this.eventSource.onerror = (error) => {
                console.error('SSE 연결 오류:', error);
                this.updateConnectionStatus(false);

                if (this.reconnectAttempts < this.maxReconnectAttempts) {
                    const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000);
                    this.reconnectAttempts++;

                    setTimeout(() => {
                        if (!this.isConnected) {
                            console.log(`재연결 시도 ${this.reconnectAttempts}/${this.maxReconnectAttempts}`);
                            this.disconnect();
                            this.connect();
                        }
                    }, delay);
                }
            };
        } catch (error) {
            console.error('SSE 연결 설정 오류:', error);
            this.updateConnectionStatus(false);
        }
    }

    disconnect() {
        if (this.eventSource) {
            this.eventSource.close();
            this.eventSource = null;
        }
        this.updateConnectionStatus(false);
    }

    updateConnectionStatus(connected) {
        this.isConnected = connected;
        const indicator = document.getElementById('connectionIndicator');

        if (indicator) {
            indicator.className = connected ? 'connection-indicator connected' : 'connection-indicator';
            indicator.setAttribute('data-status', connected ? '실시간 알림 연결됨' : '연결 끊김');
        }
    }

    handleNotification(alarmData) {
        this.notifications.unshift({
            ...alarmData,
            receivedAt: new Date().toLocaleString(),
            id: Date.now() + Math.random()
        });

        if (this.notifications.length > 50) {
            this.notifications = this.notifications.slice(0, 50);
        }

        this.renderNotifications();
        this.showBrowserNotification(alarmData);
    }

    showEmptyState() {
        if (this.notifications.length === 0) {
            const container = document.getElementById('notifications');
            if (container) container.innerHTML = '<div class="empty-message">알림이 없습니다.</div>';
        }
        this.updateClearAllButton();
    }

    updateClearAllButton() {
        const clearAllBtn = document.getElementById('clearAllBtn');
        if (clearAllBtn) {
            clearAllBtn.disabled = this.notifications.length === 0;
        }
    }

    renderNotifications() {
        const container = document.getElementById('notifications');
        if (!container) return;

        if (this.notifications.length === 0) {
            this.showEmptyState();
            return;
        }

        container.innerHTML = this.notifications.map(notification => {
            const typeClass = this.getTypeClass(notification.type);
            const typeLabel = this.getTypeLabel(notification.type);

            return `
                <div class="notification-item ${typeClass}">
                    <button class="clear-btn" onclick="notificationSSE.removeNotification('${notification.id}')" title="알림 삭제">×</button>
                    <div class="notification-header">
                        <span class="notification-type type-${typeClass}">${typeLabel}</span>
                        <span class="notification-time">${notification.receivedAt}</span>
                    </div>
                    <div class="notification-message">${notification.message}</div>
                    <div class="notification-details">
                        예약번호: ${notification.reservationId} |
                        사용자: ${notification.nickName} (${notification.role})
                    </div>
                </div>
            `;
        }).join('');

        this.updateClearAllButton();
    }

    removeNotification(id) {
        this.notifications = this.notifications.filter(n => n.id != id);
        this.renderNotifications();
    }

    clearAllNotifications() {
        this.notifications = [];
        this.renderNotifications();
    }

    getTypeClass(type) {
        const typeMap = {
            'EXPIRED': 'expired',
            'REMINDER': 'reminder',
            'STATE_CHANGE': 'state-change',
            'CANCEL_NOTICE': 'cancel-notice'
        };
        return typeMap[type] || 'default';
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
                icon: '/favicon.ico',
                tag: `alarm-${alarmData.reservationId}`
            });

            setTimeout(() => notification.close(), 5000);
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if ('Notification' in window && Notification.permission === 'default') {
        Notification.requestPermission();
    }

    window.notificationSSE = new NotificationSSE();
});

window.addEventListener('beforeunload', () => {
    if (window.notificationSSE) {
        window.notificationSSE.disconnect();
    }
});

document.addEventListener('visibilitychange', () => {
    if (!document.hidden && window.notificationSSE && !window.notificationSSE.isConnected) {
        window.notificationSSE.connect();
    }
});
