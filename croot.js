/**
 * croot.js - Lightweight ES6 helper library (JSCroot compliant)
 * Provides DOM manipulation, REST API helpers, and formatting utilities.
 */

/**
 * Perform a GET request and parse JSON response
 * @param {string} targetUrl - URL to fetch
 * @param {Function} callback - Function called with (result, error)
 * @param {string} [tokenHeader] - Optional authorization header name
 * @param {string} [tokenValue] - Optional authorization token value
 */
export function getJSON(targetUrl, callback, tokenHeader, tokenValue) {
    const headers = {
        'Accept': 'application/vnd.github.v3+json, application/json'
    };
    if (tokenHeader && tokenValue) {
        headers[tokenHeader] = tokenValue;
    }

    fetch(targetUrl, {
        method: 'GET',
        headers: headers
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        return response.json();
    })
    .then(data => {
        if (typeof callback === 'function') {
            callback(data, null);
        }
    })
    .catch(error => {
        console.warn('[croot.js] getJSON error:', error);
        if (typeof callback === 'function') {
            callback(null, error);
        }
    });
}

/**
 * Set innerHTML of an element by ID
 * @param {string} id - Element ID
 * @param {string} content - HTML or text content
 */
export function setInner(id, content) {
    const element = document.getElementById(id);
    if (element) {
        element.innerHTML = content;
    }
}

/**
 * Set textContent of an element by ID
 * @param {string} id - Element ID
 * @param {string} text - Text content
 */
export function setText(id, text) {
    const element = document.getElementById(id);
    if (element) {
        element.textContent = text;
    }
}

/**
 * Set href attribute of a link element by ID
 * @param {string} id - Element ID
 * @param {string} url - Target URL
 */
export function setHref(id, url) {
    const element = document.getElementById(id);
    if (element) {
        element.setAttribute('href', url);
    }
}

/**
 * Attach a click event listener to an element by ID
 * @param {string} id - Element ID
 * @param {Function} handler - Click event handler function
 */
export function onClick(id, handler) {
    const element = document.getElementById(id);
    if (element && typeof handler === 'function') {
        element.addEventListener('click', handler);
    }
}

/**
 * Show an element by removing hidden style/class
 * @param {string} id - Element ID
 */
export function show(id) {
    const element = document.getElementById(id);
    if (element) {
        element.style.display = '';
        element.classList.remove('hidden');
    }
}

/**
 * Hide an element
 * @param {string} id - Element ID
 */
export function hide(id) {
    const element = document.getElementById(id);
    if (element) {
        element.style.display = 'none';
        element.classList.add('hidden');
    }
}

/**
 * Format bytes to human readable format (MB, KB)
 * @param {number} bytes - Number of bytes
 * @param {number} decimals - Precision
 * @returns {string} Formatted string e.g. "24.5 MB"
 */
export function formatBytes(bytes, decimals = 2) {
    if (!bytes || bytes === 0) return '0 Bytes';
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

/**
 * Format ISO date string into Indonesian readable date
 * @param {string} isoString - ISO date string
 * @returns {string} Formatted date
 */
export function formatDate(isoString) {
    if (!isoString) return '-';
    try {
        const date = new Date(isoString);
        return date.toLocaleDateString('id-ID', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch {
        return isoString;
    }
}
