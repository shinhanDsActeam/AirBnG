class SSEManager {
    constructor() {
        this.eventSource = null;
        this.isConnected = false;
        this.isConnecting = false;  // 중복 connect 방지 플래그
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.memberId = document.body.dataset.memberId;
        this.listeners = new Map();
        this.connectionStatusCallbacks = [];
        this.isInitialized = false;  // init 중복 방지용
    }

    init() {
        if (this.isInitialized) return;
        this.isInitialized = true;

        if (this.memberId && this.memberId !== 'null' && this.memberId !== '') {
            this.connect();
        } else {
            console.warn('SSE 비활성화: 로그인하지 않은 사용자');
        }
    }

    connect() {
        if (this.isConnected || this.isConnecting || !this.memberId) return;

        this.isConnecting = true;

        try {
            this.eventSource = new EventSource(`/AirBnG/alarms/reservations/alarms`);

            this.eventSource.addEventListener('connect', (event) => {
                console.log('SSE 연결 성공:', event.data);
                this.updateConnectionStatus(true);
                this.reconnectAttempts = 0;
                this.isConnecting = false;
            });

            this.eventSource.addEventListener('alarm', (event) => {
                try {
                    const alarmData = JSON.parse(event.data);
                    console.log('알림 수신:', alarmData);
                    this.handleAlarmEvent(alarmData);
                } catch (e) {
                    console.error('알림 데이터 파싱 오류:', e);
                }
            });

            this.eventSource.onopen = () => {
                console.log('SSE 연결 열림');
                this.updateConnectionStatus(true);
                this.reconnectAttempts = 0;
                this.isConnecting = false;
            };

            this.eventSource.onerror = (error) => {
                console.error('SSE 연결 오류:', error);
                this.updateConnectionStatus(false);
                this.isConnecting = false;
                this.attemptReconnect();
            };

        } catch (error) {
            console.error('SSE 연결 설정 오류:', error);
            this.updateConnectionStatus(false);
            this.isConnecting = false;
        }
    }

    attemptReconnect() {
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
        } else {
            console.error('최대 재연결 시도 횟수 초과');
        }
    }

    disconnect() {
        if (this.eventSource) {
            this.eventSource.close();
            this.eventSource = null;
        }
        this.updateConnectionStatus(false);
        this.isConnecting = false;
    }

    updateConnectionStatus(connected) {
        this.isConnected = connected;

        const indicator = document.getElementById('connectionIndicator');
        if (indicator) {
            indicator.className = connected ? 'connection-indicator connected' : 'connection-indicator';
            indicator.setAttribute('data-status', connected ? '실시간 알림 연결됨' : '연결 끊김');
        }

        this.connectionStatusCallbacks.forEach(callback => {
            try {
                callback(connected);
            } catch (error) {
                console.error('연결 상태 콜백 오류:', error);
            }
        });
    }

    handleAlarmEvent(alarmData) {
        const alarmListeners = this.listeners.get('alarm') || [];
        alarmListeners.forEach(listener => {
            try {
                listener(alarmData);
            } catch (error) {
                console.error('알림 리스너 실행 오류:', error);
            }
        });
    }

    addEventListener(eventType, callback) {
        if (!this.listeners.has(eventType)) {
            this.listeners.set(eventType, []);
        }
        const callbacks = this.listeners.get(eventType);
        if (!callbacks.includes(callback)) {
            callbacks.push(callback);
        }
    }

    removeEventListener(eventType, callback) {
        if (this.listeners.has(eventType)) {
            const listeners = this.listeners.get(eventType);
            const index = listeners.indexOf(callback);
            if (index > -1) {
                listeners.splice(index, 1);
            }
        }
    }

    onConnectionStatusChange(callback) {
        this.connectionStatusCallbacks.push(callback);
    }

    showBrowserNotification(title, message, icon = null) {
        if ('Notification' in window && Notification.permission === 'granted') {
            const notification = new Notification(title, {
                body: message,
                icon: icon || '/favicon.ico'
            });
            setTimeout(() => notification.close(), 5000);
        }
    }

    requestNotificationPermission() {
        if ('Notification' in window && Notification.permission === 'default') {
            return Notification.requestPermission();
        }
        return Promise.resolve(Notification.permission);
    }

    getConnectionStatus() {
        return this.isConnected;
    }

    getMemberId() {
        return this.memberId;
    }
}

// 전역 인스턴스
let globalSSEManager = null;

function initSSEManager() {
    if (!globalSSEManager) {
        globalSSEManager = new SSEManager();
    }
    return globalSSEManager;
}

function getSSEManager() {
    return globalSSEManager || initSSEManager();
}

// 페이지 언로드 시 연결 해제
window.addEventListener('beforeunload', () => {
    if (globalSSEManager) {
        globalSSEManager.disconnect();
    }
});

// 탭 재활성화 시 자동 재연결
document.addEventListener('visibilitychange', () => {
    if (!document.hidden && globalSSEManager && !globalSSEManager.isConnected) {
        globalSSEManager.connect();
    }
});

// DOMContentLoaded에서 권한 요청 + 초기화
document.addEventListener('DOMContentLoaded', () => {
    if ('Notification' in window && Notification.permission === 'default') {
        Notification.requestPermission().then(permission => {
            console.log('브라우저 알림 권한:', permission);
        });
    }

  // 이미 연결되어 있다면 init() 실행 안 함
    if (!globalSSEManager || !globalSSEManager.isConnected) {
        const sseManager = initSSEManager();
        sseManager.init();
    }

});