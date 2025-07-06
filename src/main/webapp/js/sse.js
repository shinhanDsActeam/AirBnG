class SSEManager {
    constructor() {
        this.eventSource = null;
        this.isConnected = false;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.memberId = document.body.dataset.memberId;
        this.listeners = new Map(); // 이벤트 리스너 관리
        this.connectionStatusCallbacks = []; // 연결 상태 콜백
    }

    // 초기화
    init() {
        if (this.memberId && this.memberId !== 'null' && this.memberId !== '') {
            this.connect();
        } else {
            console.warn('SSE 비활성화: 로그인하지 않은 사용자');
        }
    }

    // SSE 연결
    connect() {
        if (this.isConnected || !this.memberId) return;

        try {
            this.eventSource = new EventSource(`/AirBnG/alarms/reservations/alarms`);

            // 연결 성공 이벤트
            this.eventSource.addEventListener('connect', (event) => {
                console.log('SSE 연결 성공:', event.data);
                this.updateConnectionStatus(true);
                this.reconnectAttempts = 0;
            });

            // 알림 이벤트
            this.eventSource.addEventListener('alarm', (event) => {
                try {
                    const alarmData = JSON.parse(event.data);
                    console.log('알림 수신:', alarmData);
                    this.handleAlarmEvent(alarmData);
                } catch (e) {
                    console.error('알림 데이터 파싱 오류:', e);
                }
            });

            // 연결 열림
            this.eventSource.onopen = () => {
                console.log('SSE 연결 열림');
                this.updateConnectionStatus(true);
                this.reconnectAttempts = 0;
            };

            // 연결 오류
            this.eventSource.onerror = (error) => {
                console.error('SSE 연결 오류:', error);
                this.updateConnectionStatus(false);
                this.attemptReconnect();
            };

        } catch (error) {
            console.error('SSE 연결 설정 오류:', error);
            this.updateConnectionStatus(false);
        }
    }

    // 재연결 시도
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

    // SSE 연결 해제
    disconnect() {
        if (this.eventSource) {
            this.eventSource.close();
            this.eventSource = null;
        }
        this.updateConnectionStatus(false);
    }

    // 연결 상태 업데이트
    updateConnectionStatus(connected) {
        this.isConnected = connected;

        // 연결 상태 UI 업데이트
        const indicator = document.getElementById('connectionIndicator');
        if (indicator) {
            indicator.className = connected ? 'connection-indicator connected' : 'connection-indicator';
            indicator.setAttribute('data-status', connected ? '실시간 알림 연결됨' : '연결 끊김');
        }

        // 연결 상태 콜백 실행
        this.connectionStatusCallbacks.forEach(callback => {
            try {
                callback(connected);
            } catch (error) {
                console.error('연결 상태 콜백 오류:', error);
            }
        });
    }

    // 알림 이벤트 핸들링
    handleAlarmEvent(alarmData) {
        // 등록된 알림 리스너들에게 이벤트 전파
        const alarmListeners = this.listeners.get('alarm') || [];
        alarmListeners.forEach(listener => {
            try {
                listener(alarmData);
            } catch (error) {
                console.error('알림 리스너 실행 오류:', error);
            }
        });
    }

    // 이벤트 리스너 등록
    addEventListener(eventType, callback) {
        if (!this.listeners.has(eventType)) {
            this.listeners.set(eventType, []);
        }
        this.listeners.get(eventType).push(callback);
    }

    // 이벤트 리스너 제거
    removeEventListener(eventType, callback) {
        if (this.listeners.has(eventType)) {
            const listeners = this.listeners.get(eventType);
            const index = listeners.indexOf(callback);
            if (index > -1) {
                listeners.splice(index, 1);
            }
        }
    }

    // 연결 상태 콜백 등록
    onConnectionStatusChange(callback) {
        this.connectionStatusCallbacks.push(callback);
    }

    // 브라우저 알림 표시
    showBrowserNotification(title, message, icon = null) {
        if ('Notification' in window && Notification.permission === 'granted') {
            const notification = new Notification(title, {
                body: message,
                icon: icon || '/favicon.ico'
            });

            setTimeout(() => notification.close(), 5000);
        }
    }

    // 브라우저 알림 권한 요청
    requestNotificationPermission() {
        if ('Notification' in window && Notification.permission === 'default') {
            return Notification.requestPermission();
        }
        return Promise.resolve(Notification.permission);
    }

    // 현재 연결 상태 반환
    getConnectionStatus() {
        return this.isConnected;
    }

    // 멤버 ID 반환
    getMemberId() {
        return this.memberId;
    }
}

// 전역 SSE 매니저 인스턴스
let globalSSEManager = null;

// SSE 매니저 초기화 함수
function initSSEManager() {
    if (!globalSSEManager) {
        globalSSEManager = new SSEManager();
    }
    return globalSSEManager;
}

// SSE 매니저 가져오기
function getSSEManager() {
    return globalSSEManager || initSSEManager();
}

// 페이지 언로드 시 SSE 연결 해제
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

// DOM 로딩 완료 시 브라우저 알림 권한 요청
document.addEventListener('DOMContentLoaded', () => {
    if ('Notification' in window && Notification.permission === 'default') {
        Notification.requestPermission().then(permission => {
            console.log('브라우저 알림 권한:', permission);
        });
    }
});