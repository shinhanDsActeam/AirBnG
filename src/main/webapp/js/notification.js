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
        this.loadFromStorage();         // 저장된 알림 불러오기
        this.renderNotifications();     // 초기 렌더링
        this.autoConnect();             // SSE 연결
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
        const newNotification = {
            ...alarmData,
            receivedAt: this.formatDateTime(new Date()),
            id: Date.now() + Math.random()
        };

        this.notifications.unshift(newNotification);

        if (this.notifications.length > 50) {
            this.notifications = this.notifications.slice(0, 50);
        }

        this.saveToStorage(); // 알림 저장
        this.renderNotifications();
        this.showBrowserNotification(alarmData);
    }

    // 시간 포맷 함수 수정 (12시간 형식 + 오전/오후)
    formatDateTime(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');

        let hours = date.getHours();
        const minutes = String(date.getMinutes()).padStart(2, '0');

        // 오전/오후 구분
        const ampm = hours >= 12 ? '오후' : '오전';

        // 12시간 형식으로 변환
        hours = hours % 12;
        hours = hours ? hours : 12; // 0시는 12시로 표시
        const displayHours = String(hours).padStart(2, '0');

        return `${year}-${month}-${day} ${ampm} ${displayHours}:${minutes}`;
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

    removeNotification(id) {
        this.notifications = this.notifications.filter(n => n.id != id);
        this.saveToStorage(); // 삭제 후 저장
        this.renderNotifications();
    }

    clearAllNotifications() {
        this.notifications = [];
        this.saveToStorage(); // 모두 삭제 후 저장
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

    // 저장 / 불러오기 기능 추가
    saveToStorage() {
        if (!this.memberId) return;
        localStorage.setItem(`alarmHistory_${this.memberId}`, JSON.stringify(this.notifications));
    }

    loadFromStorage() {
        if (!this.memberId) return;
        const saved = localStorage.getItem(`alarmHistory_${this.memberId}`);
        if (saved) {
            try {
                this.notifications = JSON.parse(saved);
                // 기존 저장된 알림의 시간 형식도 업데이트
                this.notifications = this.notifications.map(notification => {
                    if (notification.receivedAt && notification.receivedAt.includes('.')) {
                        // 기존 초 단위 시간을 분 단위로 변환
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

// DOM 로딩 후 실행
document.addEventListener('DOMContentLoaded', () => {
    if ('Notification' in window && Notification.permission === 'default') {
        Notification.requestPermission();
    }

    window.notificationSSE = new NotificationSSE();
});

// 브라우저 종료 시 SSE 닫기
window.addEventListener('beforeunload', () => {
    if (window.notificationSSE) {
        window.notificationSSE.disconnect();
    }
});

// 탭 재활성화 시 자동 재연결
document.addEventListener('visibilitychange', () => {
    if (!document.hidden && window.notificationSSE && !window.notificationSSE.isConnected) {
        window.notificationSSE.connect();
    }
});