// FR-6: raises the header's bell when someone new matches one of the member's searches, without a reload.
// Its own STOMP connection to the chat's endpoint, so it works on every page, not only the chat.
(() => {
    const count = document.getElementById('notificationCount');
    const bell = document.getElementById('notificationBell');
    const csrfToken = bell?.dataset.csrfToken;
    const csrfHeader = bell?.dataset.csrfHeader;
    if (!count || !bell || !csrfToken || !csrfHeader || typeof StompJs === 'undefined') return;

    const client = new StompJs.Client({
        brokerURL: `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/ws-chat`,
        connectHeaders: { [csrfHeader]: csrfToken },
        reconnectDelay: 10000,
        onConnect: () => client.subscribe('/user/queue/match', () => {
            const unread = (parseInt(count.textContent, 10) || 0) + 1;
            count.textContent = unread;
            count.classList.remove('d-none');
            bell.setAttribute('aria-label', `Notifications, ${unread} unread`);
        })
    });
    client.activate();
})();
