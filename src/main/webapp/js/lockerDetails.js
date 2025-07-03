window.addEventListener("DOMContentLoaded", function () {
    let currentSlide = 0;
    const track = document.getElementById('carouselTrack');
    const totalSlides = track?.children.length || 0;

    // HTML onclick 에서 접근 가능하도록 전역 등록
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
});
