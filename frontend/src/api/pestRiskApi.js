const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const TOKEN_KEY = 'pestrisk_token';
const USER_KEY = 'pestrisk_user';

export const authStorage = {
  getToken() {
    return localStorage.getItem(TOKEN_KEY);
  },
  setAuth(token, user) {
    if (token) localStorage.setItem(TOKEN_KEY, token);
    if (user) localStorage.setItem(USER_KEY, JSON.stringify(user));
  },
  getUser() {
    try {
      const data = localStorage.getItem(USER_KEY);
      return data ? JSON.parse(data) : null;
    } catch {
      return null;
    }
  },
  clear() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },
};

/**
 * Helper to handle fetch responses and parse Spring Boot errors
 */
async function handleResponse(response) {
  if (response.ok) {
    if (response.status === 204) {
      return null;
    }
    return response.json();
  }

  if (response.status === 401) {
    // Token expired or invalid
    authStorage.clear();
  }

  let errorDetails;
  try {
    const errorJson = await response.json();
    errorDetails = errorJson.message || errorJson.error || JSON.stringify(errorJson);
  } catch {
    errorDetails = response.statusText || `HTTP ${response.status}`;
  }

  const error = new Error(errorDetails);
  error.status = response.status;
  throw error;
}

/**
 * Construct headers with Bearer token when available
 */
function getAuthHeaders(contentType = 'application/json') {
  const headers = {
    'Accept': 'application/json',
  };
  if (contentType) {
    headers['Content-Type'] = contentType;
  }
  const token = authStorage.getToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

export const api = {
  authStorage,

  /**
   * Health check endpoint (Public)
   */
  async getHealth() {
    const res = await fetch(`${BASE_URL}/api/health`, {
      headers: { 'Accept': 'application/json' },
    });
    return handleResponse(res);
  },

  /**
   * Login (Public)
   */
  async login(username, password) {
    const res = await fetch(`${BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      body: JSON.stringify({ username, password }),
    });
    const data = await handleResponse(res);
    authStorage.setAuth(data.token, { username: data.username, role: data.role });
    return data;
  },

  /**
   * Get Current Authenticated User (Protected)
   */
  async getMe() {
    const res = await fetch(`${BASE_URL}/api/auth/me`, {
      headers: getAuthHeaders(null),
    });
    return handleResponse(res);
  },

  /**
   * Logout (Client-side token disposal)
   */
  logout() {
    authStorage.clear();
  },

  /**
   * Create staff account (ADMIN only)
   */
  async createStaffUser(username, password) {
    const res = await fetch(`${BASE_URL}/api/admin/users`, {
      method: 'POST',
      headers: getAuthHeaders('application/json'),
      body: JSON.stringify({ username, password }),
    });
    return handleResponse(res);
  },

  /**
   * List all accounts (ADMIN only)
   */
  async getAllUsers() {
    const res = await fetch(`${BASE_URL}/api/admin/users`, {
      headers: getAuthHeaders(null),
    });
    return handleResponse(res);
  },

  /**
   * Live Risk Calculation Preview (Public)
   */
  async previewRisk(data) {
    const res = await fetch(`${BASE_URL}/api/risk/preview`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  /**
   * Submit new waste report (Public)
   */
  async createReport(data) {
    const res = await fetch(`${BASE_URL}/api/reports`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  /**
   * Get reports list with optional filtering (Protected: STAFF/ADMIN)
   */
  async getReports(params = {}) {
    const url = new URL(`${BASE_URL}/api/reports`);
    if (params.status) url.searchParams.append('status', params.status);
    if (params.riskLevel) url.searchParams.append('riskLevel', params.riskLevel);
    if (params.ward) url.searchParams.append('ward', params.ward);

    const res = await fetch(url.toString(), {
      headers: getAuthHeaders(null),
    });
    return handleResponse(res);
  },

  /**
   * Get priority reports (Protected: STAFF/ADMIN)
   */
  async getPriorityReports() {
    const res = await fetch(`${BASE_URL}/api/reports/priority`, {
      headers: getAuthHeaders(null),
    });
    return handleResponse(res);
  },

  /**
   * Get single report by ID with status history (Protected: STAFF/ADMIN)
   */
  async getReportById(id) {
    const res = await fetch(`${BASE_URL}/api/reports/${id}`, {
      headers: getAuthHeaders(null),
    });
    return handleResponse(res);
  },

  /**
   * Update report status (Protected: STAFF/ADMIN)
   */
  async updateReportStatus(id, status, remarks = '') {
    const res = await fetch(`${BASE_URL}/api/reports/${id}/status`, {
      method: 'PATCH',
      headers: getAuthHeaders('application/json'),
      body: JSON.stringify({ status, remarks: remarks || undefined }),
    });
    return handleResponse(res);
  },

  /**
   * Staff dashboard summary counts (Protected: STAFF/ADMIN)
   */
  async getDashboardSummary() {
    const res = await fetch(`${BASE_URL}/api/staff/dashboard/summary`, {
      headers: getAuthHeaders(null),
    });
    return handleResponse(res);
  },

  /**
   * Privacy-safe community alerts for active hotspots (Public)
   */
  async getCommunityAlerts() {
    const res = await fetch(`${BASE_URL}/api/community/alerts`, {
      headers: { 'Accept': 'application/json' },
    });
    return handleResponse(res);
  },
};

export default api;
