setTimeout(function () {
    window.location.href = contextPath + '/page/home';
}, 5000);

document.addEventListener('click', function () {
    window.location.href = contextPath + '/page/home';
});

document.addEventListener('keydown', function (e) {
    if (e.key === 'Enter' || e.key === ' ') {
        window.location.href = contextPath + '/page/home';
    }
});

document.addEventListener('touchstart', function () {
    window.location.href = contextPath + '/page/home';
});