// dot.js
function showDotIndicator(contextPath = '') {
    const bellWrapper = document.querySelector('.bell-wrapper');

    // bell-wrapper가 없는 페이지에서는 점 표시 안 함
    if (!bellWrapper) {
        console.log("bell-wrapper 요소가 없는 페이지입니다. 점 표시를 건너뜁니다.");
        return;
    }

    let dotIndicator = document.getElementById('dotIndicator');

    if (dotIndicator) {
        // 기존 점이 있으면 애니메이션 재시작
        dotIndicator.style.animation = 'none';
        dotIndicator.offsetHeight; // 강제 리플로우
        dotIndicator.style.animation = 'dot-appear 0.4s ease-out, dot-pulse 2s ease-in-out infinite 0.8s';
        dotIndicator.style.display = 'block';
        dotIndicator.style.visibility = 'visible';
        dotIndicator.style.opacity = '1';

        dotIndicator.classList.remove('hide');
        dotIndicator.classList.add('show');
    } else {
        // 새 점 생성
        console.log("새 dot 생성");
        const newDot = document.createElement('img');
        newDot.src = `${contextPath}/images/dot.svg`;
        newDot.alt = '새 알림 표시';
        newDot.className = 'dot-indicator show';
        newDot.id = 'dotIndicator';
        newDot.style.display = 'block';
        newDot.style.visibility = 'visible';
        newDot.style.opacity = '1';
        newDot.style.animation = 'dot-appear 0.4s ease-out, dot-pulse 2s ease-in-out infinite 0.8s';

        bellWrapper.insertBefore(newDot, bellWrapper.firstChild);
    }
}

function hideDotIndicator() {
    const dotIndicator = document.getElementById('dotIndicator');
    if (dotIndicator) {
        dotIndicator.classList.remove('show');
        dotIndicator.classList.add('hide');

        dotIndicator.style.opacity = '0';
        dotIndicator.style.transition = 'opacity 0.3s ease-out';

        setTimeout(() => {
            dotIndicator.style.display = 'none';
            dotIndicator.style.visibility = 'hidden';
            dotIndicator.classList.remove('hide');
        }, 300);
    }
}

// 전역 알림 핸들러 - 모든 페이지에서 사용
function handleGlobalNotification(alarmData, contextPath = '') {
    // 1. 점 표시 (bell-wrapper가 있는 페이지에서만)
    showDotIndicator(contextPath);

    // 2. 전역 알림 매니저에 알림 추가
    if (typeof getNotificationSSE === 'function') {
        const notificationManager = getNotificationSSE();
        if (notificationManager) {
            notificationManager.addNotification(alarmData);
        }
    }

    console.log('전역 알림 처리 완료:', alarmData);
}

// localStorage에 직접 알림 저장 (NotificationSSE가 없는 페이지용)
function saveNotificationToStorage(alarmData) {
    const memberId = window.memberId || document.body.dataset.memberId;
    if (!memberId) return;

    try {
        const storageKey = `alarmHistory_${memberId}`;
        const existingData = localStorage.getItem(storageKey);
        let notifications = existingData ? JSON.parse(existingData) : [];

        const newNotification = {
            ...alarmData,
            receivedAt: formatDateTime(new Date()),
            id: Date.now() + Math.random()
        };

        notifications.unshift(newNotification);

        // 최대 50개까지만 보관
        if (notifications.length > 50) {
            notifications = notifications.slice(0, 50);
        }

        localStorage.setItem(storageKey, JSON.stringify(notifications));
        console.log('알림 localStorage에 직접 저장 완료:', newNotification);
    } catch (e) {
        console.error('알림 저장 실패:', e);
    }
}
