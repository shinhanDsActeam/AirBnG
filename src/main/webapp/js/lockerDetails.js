window.addEventListener("DOMContentLoaded", function () {
    // 🔹 슬라이드 관련 로직
    let currentSlide = 0;
    const track = document.getElementById('carouselTrack');
    const totalSlides = track?.children.length || 0;

    window.moveSlide = function (direction) {
        if (!track || totalSlides === 0) return;

        currentSlide += direction;

        if (currentSlide < 0) {
            currentSlide = 0;
        } else if (currentSlide >= totalSlides) {
            currentSlide = totalSlides - 1;
        }

        track.style.transform = `translateX(-${currentSlide * 100}%)`;
    };

    // 🔹 보관소 선택 버튼 클릭 시 이동
    const reserveBtn = document.getElementById("reserveBtn");
    reserveBtn?.addEventListener("click", function () {
        const contextPath = reserveBtn.dataset.contextPath || '';
        const lockerId = reserveBtn.dataset.lockerId;
        const memberId = reserveBtn.dataset.memberId;

        const targetUrl = `${contextPath}/page/reservation?lockerId=${encodeURIComponent(lockerId)}&memberId=${encodeURIComponent(memberId)}`;
        console.log("이동할 URL:", targetUrl);
        window.location.href = targetUrl;
    });
});
