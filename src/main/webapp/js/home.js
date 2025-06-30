document.addEventListener('DOMContentLoaded', function () {
    fetch(`${contextPath}/lockers/popular`)
        .then(function (response) {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(function (data) {
            console.log("인기 보관소 데이터:", data);

            if (data.code === 1000 && data.result && data.result.lockers) {
                const list = data.result.lockers;
                const container = document.getElementById('popularList');
                container.innerHTML = '';

                list.forEach(function (locker) {
                    const item = document.createElement('div');
                    item.className = 'popular-item';
                    item.innerHTML = `
                        <div class="thumb">
                            <img src="${locker.url || '/images/default_locker.svg'}" alt="" />
                        </div>
                        <div class="info">
                            <div class="locker-name">${locker.lockerName}</div>
                            <div class="locker-address">${locker.address}</div>
                        </div>
                    `;
                    container.appendChild(item);
                });
            } else {
                console.warn("데이터 형식이 올바르지 않습니다.");
            }
        })
        .catch(function (error) {
            console.error("인기 보관소 가져오기 실패:", error);
        });
});