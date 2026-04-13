/**
 * HMS Main JS Application Logic
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Initialize Icons (Feather)
    if (typeof feather !== 'undefined') {
        feather.replace();
    }

    // 2. Sidebar Toggle Logic
    const sidebarToggleBtn = document.getElementById('sidebarToggle');
    const sidebar = document.getElementById('sidebar');
    
    if (sidebarToggleBtn && sidebar) {
        sidebarToggleBtn.addEventListener('click', () => {
            sidebar.classList.toggle('show');
        });
    }

    // 3. Highlight Active Menu Item
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.sidebar-nav .nav-link');
    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });

    // 4. JWT Auth Interceptors
    setupFetchInterceptor();
});

/**
 * Setup interceptor for global fetch calls to inject JWT and handle 401/403
 */
function setupFetchInterceptor() {
    const originalFetch = window.fetch;
    window.fetch = async (...args) => {
        let [resource, config] = args;
        
        // Don't intercept auth calls potentially
        if (resource.includes('/api/auth/')) {
            return originalFetch(resource, config);
        }

        const token = localStorage.getItem('hms_jwt_token');
        if (token) {
            config = config || {};
            config.headers = {
                ...config.headers,
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            };
        }

        try {
            const response = await originalFetch(resource, config);
            
            if (response.status === 401) {
                // Unauthorized
                showToast('Session expired. Please login again.', 'danger');
                localStorage.removeItem('hms_jwt_token');
                window.location.href = '/login';
            } else if (response.status === 403) {
                // Forbidden
                showToast('You do not have permission to perform this action.', 'warning');
            }

            return response;
        } catch (error) {
            showToast('Network error occurred.', 'danger');
            throw error;
        }
    };
}

/**
 * Utility to show bootstrap toasts dynamically
 */
function showToast(message, type = 'primary') {
    const toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        const container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'position-fixed bottom-0 end-0 p-3';
        container.style.zIndex = '1055';
        document.body.appendChild(container);
    }
    
    const toastHtml = `
        <div class="toast align-items-center text-bg-${type} border-0 show" role="alert" aria-live="assertive" aria-atomic="true">
          <div class="d-flex">
            <div class="toast-body">
              ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
          </div>
        </div>
    `;
    
    const div = document.createElement('div');
    div.innerHTML = toastHtml;
    const target = document.getElementById('toast-container');
    target.appendChild(div.firstElementChild);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        target.lastElementChild?.remove();
    }, 3000);
}
