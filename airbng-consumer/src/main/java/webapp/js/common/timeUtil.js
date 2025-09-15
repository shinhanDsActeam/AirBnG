// 시간 포맷팅 함수
function formatHours(hours) {
    const totalMinutes = Math.round(hours * 60);
    const days = Math.floor(totalMinutes / 1440); // 24 * 60
    const remainingMinutesAfterDays = totalMinutes % 1440;
    const hrs = Math.floor(remainingMinutesAfterDays / 60);
    const mins = remainingMinutesAfterDays % 60;

    const parts = ['('];
    if (days > 0) parts.push(`${days}일`);
    if (hrs > 0) parts.push(`${hrs}시간`);
    if (mins > 0) parts.push(`${mins}분`);
    parts.push(')');

    return parts.join(' ');
}