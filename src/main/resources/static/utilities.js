function formatChatDate(timestamp) {
    let date = new Date(timestamp);
    let now = new Date();
    let today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    let yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);
    let messageDay = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    if (messageDay.getTime() === today.getTime()) {
        return date.toLocaleTimeString('en-GB', {
            hour: '2-digit',
            minute: '2-digit'
        });
    } else if (messageDay.getTime() === yesterday.getTime()) {
        return "Yesterday " + date.toLocaleTimeString('en-GB', {
            hour: '2-digit',
            minute: '2-digit'
        });
    } else {
        return date.toLocaleDateString('en-GB', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        }) + " " + date.toLocaleTimeString('en-GB', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }
}

function formatFileSize(data) {
    let bytes;
    if (typeof data === "string") {
        let base64Match = data.match(/^data:.*;base64,(.*)$/);
        let base64String = base64Match ? base64Match[1] : data;
        let padding = (base64String.endsWith("==") ? 2 : base64String.endsWith("=") ? 1 : 0);
        bytes = Math.ceil((base64String.length * 3) / 4 - padding);
        if (bytes === 0) bytes = new Blob([data]).size;
    } else if (data instanceof ArrayBuffer) {
        bytes = data.byteLength;
    } else if (ArrayBuffer.isView(data)) {
        bytes = data.byteLength; // covers Uint8Array, Int32Array, etc.
    } else if (Array.isArray(data)) {
        bytes = data.length; // fallback if it's a plain JS array
    } else if (typeof data === "number") {
        bytes = data; // allow passing raw number of bytes
    } else {
        throw new Error("Unsupported type. Pass ArrayBuffer, TypedArray, Array, or number.");
    }

    if (bytes < 1024) {
        return bytes + " B";
    } else if (bytes < 1024 * 1024) {
        return (bytes / 1024).toFixed(1) + " KB";
    } else if (bytes < 1024 * 1024 * 1024) {
        return (bytes / (1024 * 1024)).toFixed(1) + " MB";
    } else {
        return (bytes / (1024 * 1024 * 1024)).toFixed(1) + " GB";
    }
}

function moveElementToTop(container, element) {
    container.prepend(element);
}

function isNearBottom(element, threshold = 500) {
    return element.scrollHeight - (element.scrollTop + element.clientHeight) <= threshold;
}

function setupDivider(divider, leftpane) {
    let isResizing = false;
    // mouse press
    divider.addEventListener("mousedown", (e) => {
        e.preventDefault();
        isResizing = true;
        document.body.style.cursor = "col-resize";
    });
    // mouse unpress
    document.addEventListener("mouseup", () => {
        isResizing = false;
        document.body.style.cursor = "default";
    });
    // resizing...
    document.addEventListener("mousemove", (e) => {
        if (!isResizing) return;
        let newWidth = Math.min(Math.max(e.clientX, 300), 900);
        leftpane.style.flex = `0 0 ${newWidth}px`;
    });
}

document.addEventListener('click', function(e) {
    chatlist.querySelectorAll('.popup-menu').forEach(item => item.classList.add('d-none'));
})

function getMaxWidthPx(el) {
    let style = window.getComputedStyle(el);
    return parseFloat(style.maxWidth);
}
