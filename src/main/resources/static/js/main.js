/**
 * HMS Shared Frontend Utility Configuration & Helpers
 * Centralized logic for API calls, Auth, UI components, and Role visibility.
 */

const HMS_CONFIG = {
    USER_KEY: 'hms_user',
    API_BASE: '/api/v1'
};

// ─── AUTH HELPERS ────────────────────────────────────────────────────────────

function getUser() {
    try {
        return JSON.parse(localStorage.getItem(HMS_CONFIG.USER_KEY) || '{}');
    } catch {
        return {};
    }
}

function logout() {
    // We now use a form POST to /logout for CSRF compatibility.
    // This JS helper is kept for cleanup if invoked.
    localStorage.clear();
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/logout';
    document.body.appendChild(form);
    form.submit();
}

function getRole() {
    // Fallback if not using server-side rendering for a specific element
    const user = getUser();
    return (user.roles && user.roles[0]) ? user.roles[0].replace('ROLE_', '') : null;
}

function isAdmin() { return getRole() === 'ADMIN'; }
function isDoctor() { return getRole() === 'DOCTOR'; }
function isNurse() { return getRole() === 'NURSE'; }

/**
 * Centralized API Fetch utility
 * Automatically includes session cookies. For POST/PUT/DELETE, Ensure CSRF is handled.
 */
async function apiFetch(url, options = {}) {
    options.headers = {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest', // Helps Spring detect AJAX
        ...options.headers
    };

    // Note: JSESSIONID cookie is sent automatically by the browser.
    // If CSRF is CookieCsrfTokenRepository.withHttpOnlyFalse(), we should read the XSRF-TOKEN cookie.
    const csrfToken = document.cookie.split('; ')
        .find(row => row.startsWith('XSRF-TOKEN='))
        ?.split('=')[1];
    
    if (csrfToken && ['POST', 'PUT', 'DELETE', 'PATCH'].includes(options.method?.toUpperCase())) {
        options.headers['X-XSRF-TOKEN'] = csrfToken;
    }

    try {
        let finalUrl = url;
        if (url.startsWith('api/')) finalUrl = '/' + url;
        
        const response = await fetch(finalUrl, options);

        if (response.status === 401) {
            console.warn('Unauthorized access, redirecting to login...');
            window.location.href = '/login';
            return null;
        }

        if (response.status === 403) {
            showToast('Permission denied for this action.', 'error');
            return null;
        }

        if (response.status >= 500) {
            const errData = await response.json().catch(() => ({}));
            showToast(errData.message || 'Server error occurred.', 'error');
            return null;
        }

        const result = await response.json();
        return result;
    } catch (error) {
        console.error('API Fetch Error:', error);
        showToast('Network error, check your connection.', 'error');
        throw error;
    }
}

// ─── UI HELPERS ──────────────────────────────────────────────────────────────

/**
 * Standardized toast system (Tailwind expected in layout)
 */
function showToast(message, type = 'success') {
    const t = document.getElementById('toast');
    const inner = document.getElementById('toast-inner');
    const icon = document.getElementById('toast-icon');
    const msgEl = document.getElementById('toast-msg');

    if (!t || !inner || !icon || !msgEl) {
        // Fallback to alert if toast elements don't exist
        alert(message);
        return;
    }

    msgEl.textContent = message;
    
    // Class mapping
    inner.className = 'flex items-center gap-3 px-5 py-3 rounded-xl shadow-xl text-white text-sm font-semibold ' +
        (type === 'error' ? 'bg-error' : type === 'warn' ? 'bg-tertiary' : 'bg-on-surface');
        
    icon.textContent = type === 'error' ? 'error' : type === 'warn' ? 'warning' : 'check_circle';

    t.classList.remove('opacity-0', 'translate-y-4', 'pointer-events-none');
    t.classList.add('opacity-100', 'translate-y-0');

    setTimeout(() => {
        t.classList.remove('opacity-100', 'translate-y-0');
        t.classList.add('opacity-0', 'translate-y-4', 'pointer-events-none');
    }, 4000);
}

/**
 * Build dynamic pagination controls
 */
function buildPaginationControls(containerId, totalPages, currentPage, loadFn) {
    const container = document.getElementById(containerId);
    if (!container) return;

    let html = `
        <div class="flex items-center gap-2">
            <button ${currentPage === 0 ? 'disabled' : ''} 
                    onclick="${loadFn}(${currentPage - 1})"
                    class="px-3 py-1.5 bg-surface-container rounded-lg hover:bg-surface-container-high transition-colors disabled:opacity-40">
                <span class="material-symbols-outlined text-sm">chevron_left</span>
            </button>
            <span class="text-xs font-bold text-outline">Page ${currentPage + 1} of ${totalPages || 1}</span>
            <button ${currentPage + 1 >= totalPages ? 'disabled' : ''} 
                    onclick="${loadFn}(${currentPage + 1})"
                    class="px-3 py-1.5 bg-surface-container rounded-lg hover:bg-surface-container-high transition-colors disabled:opacity-40">
                <span class="material-symbols-outlined text-sm">chevron_right</span>
            </button>
        </div>
    `;
    container.innerHTML = html;
}

/**
 * Role-based UI visibility management
 * data-admin-only
 * data-admin-doctor
 * data-admin-nurse
 */
function applyRoleVisibility() {
    const role = getRole();
    
    document.querySelectorAll('[data-admin-only]').forEach(el => {
        if (!isAdmin()) el.classList.add('hidden');
        else el.classList.remove('hidden');
    });

    document.querySelectorAll('[data-admin-doctor]').forEach(el => {
        if (!isAdmin() && !isDoctor()) el.classList.add('hidden');
        else el.classList.remove('hidden');
    });

    document.querySelectorAll('[data-admin-nurse]').forEach(el => {
        if (!isAdmin() && !isNurse()) el.classList.add('hidden');
        else el.classList.remove('hidden');
    });

    document.querySelectorAll('[data-admin-doctor-nurse]').forEach(el => {
        if (!isAdmin() && !isDoctor() && !isNurse()) el.classList.add('hidden');
        else el.classList.remove('hidden');
    });
}

/**
 * Standard confirmation dialog for destructive actions
 */
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

// ─── DROPDOWN BUILDERS ────────────────────────────────────────────────────────

const _dd = async (url, selectId, valField, labelFn) => {
    const select = document.getElementById(selectId);
    if (!select) return;
    try {
        const res = await apiFetch(url);
        if (!res || !res.data) return;
        
        // Handle both paged and list responses
        const items = res.data.content || res.data;
        
        select.innerHTML = '<option value="">— Select —</option>' + 
            items.map(item => `<option value="${item[valField]}">${labelFn(item)}</option>`).join('');
    } catch (e) { console.error(`Failed to load dropdown for ${selectId}`, e); }
};

const buildPhysicianDropdown = (id) => _dd('/api/v1/physicians?size=100', id, 'employeeId', p => `${p.name} (${p.position})`);
const buildNurseDropdown     = (id) => _dd('/api/v1/nurses?size=100', id, 'employeeId', n => `${n.name} (${n.position})`);
const buildMedicationDropdown  = (id) => _dd('/api/v1/medications?size=100', id, 'code', m => `${m.name} (${m.brand})`);
const buildProcedureDropdown   = (id) => _dd('/api/v1/procedures?size=100', id, 'code', p => `${p.name} ($${p.cost})`);
const buildRoomDropdown        = (id) => _dd('/api/v1/rooms/available', id, 'roomNumber', r => `Room ${r.roomNumber} (${r.roomType})`);

// ─── INIT ───────────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
    applyRoleVisibility();
});
