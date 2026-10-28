// Chat page. One STOMP connection with:
//  - /topic/chat/{id}         events of the open chat (only that one is subscribed to)
//  - /user/queue/notification chat list updates, for every chat
//  - /user/queue/errors       errors caused by our own requests
// Anything written by another user is inserted with textContent, never innerHTML.
// Needs CHAT_CONFIG (set by chat.html) and utilities.js.

const { csrfHeader, csrfToken, userId: UserID, username: Username } = CHAT_CONFIG;
const csrfHeaders = { [csrfHeader]: csrfToken };
const TYPING_SIGNAL_INTERVAL = 2000; // send "typing" at most this often
const DRAFT_SAVE_DELAY = 1000;       // save the draft this long after the last keystroke

const leftpane = document.getElementById('leftpane');
const rightpane = document.getElementById('rightpane');
const chatlist = leftpane.querySelector('#chatlist');
setupDivider(document.getElementById('divider'), leftpane, rightpane);

const chatHeader = rightpane.querySelector('.chat-header');
const chatContainer = rightpane.querySelector('#chat-container');
const chatFooter = rightpane.querySelector('#chat-footer');
const messagesContainer = chatContainer.querySelector('#message-container');
const menu3Dot = chatContainer.querySelector('#messageMenu');
const deleteReactionBtn = chatContainer.querySelector('#deleteReaction');
const typingIndicator = chatContainer.querySelector('#typing');
const draftFileInput = chatFooter.querySelector('#draft-file-input');
const draftAttachmentBox = chatFooter.querySelector('#draft-attachment-box');
const draftField = chatFooter.querySelector('#draft-message-txtField');
const sendButton = chatFooter.querySelector('#send-btn');

let currentChatId = null;    // string, as in data-chat-id
let requestedChatId = null;  // last chat asked for, to ignore late responses
let PartnerName = '';
let chatSubscription = null;


// ---------- Helpers ----------

// el('a', { className: 'x', href: '/', text: 'Home', style: {...}, dataset: {...} }, [children])
function el(tag, { text, style, dataset, ...props } = {}, children = []) {
    const node = Object.assign(document.createElement(tag), props);
    if (text !== undefined) node.textContent = text;
    Object.assign(node.style, style);
    Object.assign(node.dataset, dataset);
    node.append(...children);
    return node;
}
function findMessage(messageId) {
    return messagesContainer.querySelector(`[data-message-id="${messageId}"]`);
}
// The message text, without the "(edited)" mark.
function messageText(contentEl) {
    return contentEl.firstChild?.nodeValue ?? '';
}
function closeMenus() {
    menu3Dot.classList.add('d-none');
    deleteReactionBtn.classList.add('d-none');
}


// ---------- Connection ----------

const stompClient = new StompJs.Client({
    brokerURL: `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/ws-chat`,
    connectHeaders: csrfHeaders,
    reconnectDelay: 5000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    onConnect: () => {
        const isReconnect = chatSubscription !== null;
        chatSubscription = null; // subscriptions don't survive a reconnect
        stompClient.subscribe('/user/queue/errors', frame => console.error('Chat error: ' + frame.body));
        stompClient.subscribe('/user/queue/notification/receive', frame => updateChatListItem(JSON.parse(frame.body), true));
        stompClient.subscribe('/user/queue/notification/update', frame => updateChatListItem(JSON.parse(frame.body), false));
        if (currentChatId) {
            subscribeToChat(currentChatId);
            if (isReconnect) reloadMessages(); // catch up on what happened while disconnected
        }
        setConnected(true);
    },
    onWebSocketClose: () => setConnected(false),
    onStompError: frame => console.error('STOMP error: ' + frame.headers.message),
});

function setConnected(connected) {
    sendButton.disabled = !connected;
    draftField.placeholder = connected ? 'Type a message...' : 'Reconnecting...';
}

function subscribeToChat(chatId) {
    if (!stompClient.connected) return; // onConnect subscribes once connected
    chatSubscription?.unsubscribe();
    chatSubscription = stompClient.subscribe(`/topic/chat/${chatId}`, frame => {
        const event = JSON.parse(frame.body);
        chatEventHandlers[event.Type]?.(event.Data);
    });
}

// Returns false (and sends nothing) while disconnected.
function sendToChat(action, payload = {}, chatId = currentChatId) {
    if (!chatId || !stompClient.connected) return false;
    stompClient.publish({ destination: `/app/chat/${chatId}/${action}`, body: JSON.stringify(payload) });
    return true;
}


// ---------- Events of the open chat ----------

let typingTimer = null;
const chatEventHandlers = {
    'typing': (typer) => {
        if (typer === Username) return;
        typingIndicator.style.display = 'inline-block';
        if (isNearBottom(chatContainer, 80)) chatContainer.scrollTop = chatContainer.scrollHeight;
        clearTimeout(typingTimer);
        typingTimer = setTimeout(() => typingIndicator.style.display = 'none', 5000);
    },
    'message/receive': (msg) => {
        typingIndicator.style.display = 'none';
        messagesContainer.querySelector('.chat-start')?.remove();
        renderMessage(msg);
        if (msg.UserID !== UserID) sendToChat('read');
    },
    'message/update': (msg) => {
        const content = findMessage(msg.ID)?.querySelector('.message-content');
        if (content) setMessageContent(content, msg);
    },
    'message/delete': (messageId) => findMessage(messageId)?.remove(),
    'reaction/refresh': ({ MessageID, reactions }) => {
        const box = findMessage(MessageID);
        if (box) renderReactions(box, reactions);
    },
    'attachment/delete': (attachment) => {
        const attachmentBox = messagesContainer.querySelector(`[data-attachment-box-message-id="${attachment.MessageID}"]`);
        attachmentBox?.querySelector(`[data-attachment-id="${attachment.ID}"]`)?.remove();
        if (attachmentBox && attachmentBox.children.length === 0) attachmentBox.remove();
    },
};


// ---------- Chat list ----------

function selectChat(item) {
    if (!item || item.dataset.chatId === currentChatId) return;
    flushDraftSave();
    openChat(item.dataset.chatId, true);
}
function exitChat() {
    leftpane.classList.remove('disablemobile');
    rightpane.classList.add('disablemobile');
}

function openChat(chatId, withDraft) {
    requestedChatId = chatId;
    return fetch(`/chat/user/${chatId}`)
        .then(res => res.ok ? res.json() : Promise.reject(res.status))
        .then(({ messages, partner, draft }) => {
            if (requestedChatId !== chatId) return; // the user already clicked another chat
            const item = chatlist.querySelector(`[data-chat-id="${chatId}"]`);
            item.querySelector('.notification-badge').style.display = 'none';
            chatlist.querySelectorAll('.chat-item').forEach(i => i.classList.toggle('selectedChat', i === item));
            leftpane.classList.add('disablemobile');
            rightpane.classList.remove('disablemobile');

            currentChatId = chatId;
            PartnerName = partner.Name;
            renderChatHeader(partner);
            if (withDraft) {
                draftField.value = draft?.Content ?? '';
                loadDraftAttachments(draft?.attachments ?? []);
            }
            loadMessages(messages);
            subscribeToChat(chatId);
        })
        .catch(err => console.error('Could not open chat ' + chatId, err));
}
function reloadMessages() {
    openChat(currentChatId, false);
}

function updateChatListItem(chat, isNewMessage) {
    const item = chatlist.querySelector(`[data-chat-id="${chat.ID}"]`);
    if (!item) return; // a chat created after this page was loaded
    item.querySelector('.chat-item-date').textContent = chat.LatestMessageID
        ? new Date(chat.LatestMessageID).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '';
    item.querySelector('.chat-item-content').textContent = chat.Content ?? '';
    const badge = item.querySelector('.notification-badge');
    badge.textContent = chat.Unreads;
    badge.style.display = chat.Unreads > 0 && String(chat.ID) !== currentChatId ? 'block' : 'none';
    if (isNewMessage) chatlist.prepend(item);
}


// ---------- Rendering ----------

function renderChatHeader(partner) {
    const profileUrl = `/profile/${encodeURIComponent(partner.Username)}`;
    chatHeader.replaceChildren(
        el('button', { className: 'enablemobile btn btn-sm text-muted', onclick: exitChat }, [el('h1', { text: '←', style: { margin: 0 } })]),
        el('a', { href: profileUrl }, [
            el('img', { src: `/file/avatar/${encodeURIComponent(partner.Username)}.png`, alt: 'Profile Pic', className: 'rounded-circle me-2', width: 30, height: 30 })
        ]),
        el('a', { href: profileUrl, className: 'fw-bold text-dark text-decoration-none', text: partner.Name }),
    );
}

function loadMessages(messages) {
    messagesContainer.replaceChildren();
    if (messages.length === 0) {
        messagesContainer.append(el('div', { className: 'text-center chat-start' }, [
            'This is the start of your conversation with ', el('b', { text: PartnerName }),
            ', remember to', el('br'), 'always be respectful no matter the person.',
        ]));
    }
    messages.forEach(msg => renderMessage(msg));
    chatContainer.scrollTop = chatContainer.scrollHeight;
}

function renderMessage(msg) {
    const content = el('p', { className: 'mb-0 message-content' });
    setMessageContent(content, msg);
    const menuButton = renderMessageMenuButton(msg, content);
    const body = el('div', { className: 'card-body position-relative p-2 d-flex message-body', oncontextmenu: menuButton.onclick }, [
        ...(msg.attachments?.length ? [renderMessageAttachments(msg)] : []),
        content,
        menuButton,
        el('div', { className: 'reactions-container d-flex gap-1' }),
        el('small', { className: 'text-muted message-time', text: formatChatDate(msg.ID) }),
    ]);
    const box = el('div', { className: `message ${msg.UserID === UserID ? 'self' : 'other'}`, dataset: { messageId: msg.ID } }, [el('div', {}, [body])]);
    messagesContainer.append(box);
    if (msg.reactions?.length) renderReactions(box, msg.reactions);
    if (isNearBottom(chatContainer)) chatContainer.scrollTop = chatContainer.scrollHeight;
}

function setMessageContent(contentEl, msg) {
    contentEl.textContent = msg.Content;
    if (msg.isEdited) contentEl.append(el('span', { className: 'text-muted ms-1 edited-mark', text: '(edited)' }));
}

function renderMessageAttachments(msg) {
    const box = el('div', { className: `attachment-box ${msg.Content ? 'content' : 'no-content'}`, dataset: { attachmentBoxMessageId: msg.ID } });
    for (const att of msg.attachments) {
        const url = `/file/message/attachment/${att.ID}`;
        const open = () => window.open(url, '_blank');
        const file = el('div', { className: 'filebox', dataset: { attachmentId: att.ID } });
        if (att.FileType?.startsWith('image/')) {
            file.append(el('img', { src: url, className: 'image-attachment', onclick: open }));
        } else if (att.FileType?.startsWith('video/')) {
            file.append(el('video', { src: url, controls: true, className: 'video-attachment' }));
        } else {
            file.append(
                el('img', { src: '/img/file.png', className: 'other-attachment' }),
                el('div', { className: 'details', onclick: open }, [el('p', { text: att.FileName }), el('p', { text: formatFileSize(att.FileSize) })]),
            );
        }
        if (msg.UserID === UserID) {
            file.append(el('div', { className: 'remove-img-button', text: 'X',
                onclick: () => confirm('Delete this attachment?') && sendToChat('message/attachment/delete', att.ID) }));
        }
        box.append(file);
    }
    return box;
}

function renderMessageMenuButton(msg, content) {
    return el('button', { className: 'btn btn-sm position-absolute message-menu-btn', text: '⋮', onclick: (event) => {
        event.stopPropagation();
        event.preventDefault();
        Object.assign(menu3Dot.style, { top: `${event.pageY - 100}px`, left: `${event.pageX - 240}px` });
        menu3Dot.classList.remove('d-none');

        const mine = msg.UserID === UserID;
        const action = (id, run) => {
            const button = document.getElementById(id);
            button.classList.toggle('d-none', !mine && (id === 'editBtn' || id === 'deleteBtn'));
            button.onclick = () => { closeMenus(); run(); };
        };
        action('editBtn', () => editMessage(msg.ID, messageText(content)));
        action('deleteBtn', () => deleteMessage(msg.ID));
        action('copyBtn', () => navigator.clipboard.writeText(messageText(content)));
        action('reactBtn', () => openEmojiPicker(event.target, msg.ID));
    }});
}

function renderReactions(messageBox, reactions) {
    const container = messageBox.querySelector('.reactions-container');
    container.replaceChildren(...reactions.map(reaction => el('button', {
        className: 'reaction',
        text: reaction.Emoji + reaction.Count,
        onclick: (event) => {
            event.stopPropagation();
            event.preventDefault();
            Object.assign(deleteReactionBtn.style, { top: `${event.pageY - 30}px`, left: `${event.pageX - 60}px` });
            deleteReactionBtn.classList.remove('d-none');
            deleteReactionBtn.onclick = () => {
                closeMenus();
                sendToChat('reaction/delete', { MessageID: reaction.MessageID, Emoji: reaction.Emoji });
            };
        },
    })));
    messageBox.style.minWidth = (window.innerWidth <= 420
        ? Math.min(27.5 + reactions.length * 17.5, 90)
        : Math.min(15 + reactions.length * 7.5, 50)) + '%';
    setTimeout(() => messageBox.style.marginBottom = (container.offsetHeight - 5) + 'px', 0);
}


// ---------- Drafts ----------

function setDraftBoxVisible(visible) {
    chatFooter.style.height = visible ? '20vh' : '8vh';
    chatContainer.style.height = visible ? '66vh' : '78vh';
    draftAttachmentBox.classList.toggle('d-none', !visible);
}

function loadDraftAttachments(attachments) {
    draftAttachmentBox.replaceChildren(...attachments.map(renderDraftAttachment));
    setDraftBoxVisible(attachments.length > 0);
}

function renderDraftAttachment(att) {
    const url = `/file/draft/attachment/${att.ID}`;
    const isImage = att.FileType?.startsWith('image/');
    const div = el('div', { className: 'draft-attachment' }, [
        el('img', { src: isImage ? url : '/img/file.png', onclick: () => window.open(url, '_blank') }),
    ]);
    if (!isImage) div.append(el('div', { className: 'details align-items-center' }, [
        el('h5', { text: att.FileName }), el('p', { text: formatFileSize(att.FileSize) }),
    ]));
    div.append(el('div', { className: 'remove-img-button', text: 'X', onclick: () => {
        fetch(`/chat/draft/attachment/delete?attachmentId=${att.ID}`, { method: 'DELETE', headers: csrfHeaders })
            .then(res => {
                if (!res.ok) return;
                div.remove();
                if (draftAttachmentBox.children.length === 0) setDraftBoxVisible(false);
            });
    }}));
    return div;
}

function uploadFiles(files) {
    if (!currentChatId || files.length === 0) return;
    const formData = new FormData();
    formData.append('chatId', currentChatId);
    for (const file of files) formData.append('attachments', file);
    fetch('/chat/draft/attachment/upload', { method: 'POST', body: formData, headers: csrfHeaders })
        .then(res => res.ok ? res.json() : Promise.reject(res.status))
        .then(loadDraftAttachments)
        .catch(err => console.error('Upload failed', err));
}

// The draft text is saved once typing pauses, for the chat it was typed in.
let draftTimer = null;
let pendingDraftSave = null;
function scheduleDraftSave() {
    const chatId = currentChatId, Content = draftField.value;
    pendingDraftSave = () => sendToChat('draft', { Content }, chatId);
    clearTimeout(draftTimer);
    draftTimer = setTimeout(flushDraftSave, DRAFT_SAVE_DELAY);
}
function flushDraftSave() {
    clearTimeout(draftTimer);
    pendingDraftSave?.();
    pendingDraftSave = null;
}
function cancelDraftSave() {
    clearTimeout(draftTimer);
    pendingDraftSave = null;
}


// ---------- Sending ----------

let lastTypingSignal = 0;
draftField.addEventListener('input', () => {
    if (Date.now() - lastTypingSignal > TYPING_SIGNAL_INTERVAL && sendToChat('typing')) lastTypingSignal = Date.now();
    scheduleDraftSave();
});
draftFileInput.addEventListener('change', () => uploadFiles(draftFileInput.files));
draftField.addEventListener('paste', (e) => {
    const files = [...e.clipboardData.items].filter(item => item.kind === 'file').map(item => item.getAsFile()).filter(Boolean);
    if (files.length > 0) uploadFiles(files);
});
messagesContainer.addEventListener('dragover', (e) => e.preventDefault());
messagesContainer.addEventListener('drop', (e) => {
    e.preventDefault();
    uploadFiles(e.dataTransfer.files);
});
messagesContainer.addEventListener('click', closeMenus);
window.addEventListener('beforeunload', flushDraftSave);

// Send the message (the server turns the draft into a message)
document.getElementById('submitMessage').addEventListener('submit', (e) => {
    e.preventDefault();
    if (!draftField.value.trim() && draftAttachmentBox.children.length === 0) return;
    cancelDraftSave(); // sending deletes the draft; a late save would bring it back
    if (!sendToChat('message/send', { Content: draftField.value })) return; // keep the text while disconnected
    draftField.value = '';
    loadDraftAttachments([]);
});

function editMessage(messageId, currentText) {
    const newContent = prompt('Edit your message:', currentText);
    if (newContent && newContent.trim()) sendToChat('message/edit', { ID: messageId, Content: newContent });
}
function deleteMessage(messageId) {
    if (confirm('Delete this message?')) sendToChat('message/delete', messageId);
}


// ---------- Emoji picker (writing and reactions) ----------

let emojiPicker = null;
let emojiTarget = null; // 'draft' or the ID of the message being reacted to
import('https://cdn.jsdelivr.net/npm/@joeattardi/emoji-button@4.6.0/dist/index.min.js').then(({ EmojiButton }) => {
    emojiPicker = new EmojiButton({ search: true, showPreview: true, autoHide: true });
    emojiPicker.on('emoji', ({ emoji }) => {
        if (emojiTarget === 'draft') {
            draftField.value += emoji;
            scheduleDraftSave();
        } else if (emojiTarget) {
            sendToChat('reaction/send', { MessageID: emojiTarget, Emoji: emoji });
        }
        emojiTarget = null;
    });
});
function openEmojiPicker(anchor, target) {
    emojiTarget = target;
    emojiPicker?.togglePicker(anchor);
}
function writeEmoji(button) {
    openEmojiPicker(button, 'draft');
}


// ---------- Calls (UI only; signaling goes through sendToChat('call/sdp' | 'call/ice' | 'call/action', ...)) ----------

function showCall(state, user, avatarUrl) { // state: 'outgoing' | 'incoming' | 'active' | null (hidden)
    document.getElementById('call-box').classList.toggle('d-none', state === null);
    if (user !== undefined) document.getElementById('call-username').innerText = user;
    if (avatarUrl !== undefined) document.getElementById('call-avatar').src = avatarUrl || '/img/default-pfp.png';
    document.getElementById('call-controls').classList.toggle('d-none', state === 'active');
    document.getElementById('cancel-call-btn').classList.toggle('d-none', state !== 'outgoing');
    document.getElementById('incoming-actions').classList.toggle('d-none', state !== 'incoming');
    document.getElementById('active-call-controls').classList.toggle('d-none', state !== 'active');
}
function startCall(user, avatarUrl) { showCall('outgoing', `Calling ${user}...`, avatarUrl); }
function incomingCall(user, avatarUrl) { showCall('incoming', user, avatarUrl); }
function acceptCall() { showCall('active'); }
function cancelCall() { showCall(null); }
function declineCall() { showCall(null); }
function endCall() { showCall(null); }
function toggleMute() { console.log('Toggle mute'); }
function toggleCamera() { console.log('Toggle camera'); }


// ---------- Start ----------

setConnected(false);
stompClient.activate();
selectChat(chatlist.querySelector('.chat-item'));
