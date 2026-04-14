/**
 * HMS Shared Frontend Utility Configuration & Helpers
 * Centralized logic for API calls, Auth, UI components, and Role visibility.
 */

const HMS_CONFIG = {
    TOKEN_KEY: 'hms_accessToken',
    USER_KEY: 'hms_user',
    API_BASE: '/api/v1'
};

// ─── AUTH HELPERS ────────────────────────────────────────────────────────────

function getToken() {
    return localStorage.getItem(HMS_CONFIG.TOKEN_KEY);
}

function getUser() {
    try {
        return JSON.parse(localStorage.getItem(HMS_CONFIG.USER_KEY) || '{}');
    } catch {
        return {};
    }
}

function logout() {
    localStorage.removeItem(HMS_CONFIG.TOKEN_KEY);
    localStorage.removeItem('hms_refreshToken');
    localStorage.removeItem('hms_tokenType');
    localStorage.removeItem(HMS_CONFIG.USER_KEY);
    window.location.href = '/login';
}

function getRole() {
    const user = getUser();
    return (user.roles && user.roles[0]) ? user.roles[0].replace('ROLE_', '') : null;
}

function isAdmin() { return getRole() === 'ADMIN'; }
function isDoctor() { return getRole() === 'DOCTOR'; }
function isNurse() { return getRole() === 'NURSE'; }

// Global Auth Guard: Redirect to login if token missing (unless already on login page)
if (!localStorage.getItem('hms_accessToken') && !window.location.pathname.includes('/login')) {
    window.location.href = '/login';
}

/**
 * Handle Silent Token Refresh
 */
async function refreshAuthToken() {
    const refreshToken = localStorage.getItem('hms_refreshToken');
    if (!refreshToken || window.location.pathname.includes('/login')) return;

    try {
        const res = await fetch('/api/v1/auth/refresh', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken })
        });
        const data = await res.json();
        if (data.success && data.data.accessToken) {
            localStorage.setItem(HMS_CONFIG.TOKEN_KEY, data.data.accessToken);
            console.log('Token refreshed successfully');
        } else {
            logout();
        }
    } catch (e) {
        console.error('Silent refresh failed', e);
    }
}

// Refresh every 15 minutes
if (localStorage.getItem('hms_accessToken')) {
    setInterval(refreshAuthToken, 15 * 60 * 1000);
}

/**
 * Centralized API Fetch utility with interceptors
 */
async function apiFetch(url, options = {}) {
    // 1. Token attachment
    const token = getToken();
    const isAuthRequest = url.includes('/auth/login') || url.includes('/auth/register');

    if (!token && !isAuthRequest) {
        window.location.href = '/login';
        return null;
    }

    options.headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        // Auto-fix paths if needed
        let finalUrl = url;
        if (url.startsWith('api/')) finalUrl = '/' + url;
        
        const response = await fetch(finalUrl, options);

        if (response.status === 401) {
            console.warn('Unauthorized access, redirecting to login...');
            logout();
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

        // 3. Return parsed JSON and map status to success if needed
        const result = await response.json();
        if (typeof result.success === 'undefined' && result.status) {
            result.success = (result.status === 'success');
        }
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
