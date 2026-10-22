import React, { useState, useEffect, useCallback } from 'react';
import { api } from '../api';
import { RiskBadge, StatusBadge } from './StatusBadge';

// Helper to determine valid next transitions based on Java RiskEngine & workflow rules
const VALID_NEXT_STATUSES = {
  REPORTED: ['VERIFIED', 'REJECTED'],
  VERIFIED: ['IN_PROGRESS', 'REJECTED'],
  IN_PROGRESS: ['RESOLVED'],
  RESOLVED: [],
  REJECTED: [],
};

export default function StaffDashboard() {
  // Authentication state
  const [currentUser, setCurrentUser] = useState(() => api.authStorage.getUser());
  const [loginUsername, setLoginUsername] = useState('admin');
  const [loginPassword, setLoginPassword] = useState('');
  const [isLoggingIn, setIsLoggingIn] = useState(false);
  const [loginError, setLoginError] = useState(null);
  const [showPassword, setShowPassword] = useState(false);

  // Active view in Staff dashboard: 'operations' | 'admin-users'
  const [activeSubTab, setActiveSubTab] = useState('operations');

  // Operations data
  const [summary, setSummary] = useState(null);
  const [reports, setReports] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  // Admin user management state
  const [usersList, setUsersList] = useState([]);
  const [isLoadingUsers, setIsLoadingUsers] = useState(false);
  const [newStaffUsername, setNewStaffUsername] = useState('');
  const [newStaffPassword, setNewStaffPassword] = useState('');
  const [isCreatingStaff, setIsCreatingStaff] = useState(false);
  const [staffCreateSuccess, setStaffCreateSuccess] = useState(null);
  const [staffCreateError, setStaffCreateError] = useState(null);

  // View mode: 'priority' (GET /api/reports/priority) or 'all' (GET /api/reports with filters)
  const [viewMode, setViewMode] = useState('priority');

  // Filter states
  const [filters, setFilters] = useState({
    status: '',
    riskLevel: '',
    ward: '',
  });

  // Modal / Selected Report state for Inspection & Status Update
  const [selectedReport, setSelectedReport] = useState(null);
  const [newStatus, setNewStatus] = useState('');
  const [statusRemarks, setStatusRemarks] = useState('');
  const [isUpdatingStatus, setIsUpdatingStatus] = useState(false);
  const [statusUpdateError, setStatusUpdateError] = useState(null);
  const [statusUpdateSuccess, setStatusUpdateSuccess] = useState(null);

  // Verify current user with GET /api/auth/me on mount
  useEffect(() => {
    if (api.authStorage.getToken()) {
      api.getMe()
        .then((userData) => {
          setCurrentUser(userData);
          api.authStorage.setAuth(api.authStorage.getToken(), userData);
        })
        .catch(() => {
          setCurrentUser(null);
          api.authStorage.clear();
        });
    }
  }, []);

  // Perform authentication
  const performLogin = async (username, password) => {
    setLoginError(null);
    setIsLoggingIn(true);
    const cleanUser = username ? username.trim() : '';
    const cleanPass = password ? password.trim() : '';

    if (!cleanUser || !cleanPass) {
      setLoginError('Please enter both Staff ID and Password.');
      setIsLoggingIn(false);
      return;
    }

    try {
      const res = await api.login(cleanUser, cleanPass);
      setCurrentUser({ username: res.username, role: res.role });
      setLoginPassword('');
    } catch (err) {
      setLoginError(err.message || 'Login failed. Please check credentials or ensure backend is running.');
    } finally {
      setIsLoggingIn(false);
    }
  };

  // Handle manual Login submission
  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    await performLogin(loginUsername, loginPassword);
  };

  // Handle 1-Click Fast Demo Login
  const handleFastLogin = async (staffId, password) => {
    setLoginError(null);
    setLoginUsername(staffId);
    setLoginPassword(password);
    await performLogin(staffId, password);
  };

  // Handle Logout
  const handleLogout = () => {
    api.logout();
    setCurrentUser(null);
    setSummary(null);
    setReports([]);
  };

  // Load summary metrics from GET /api/staff/dashboard/summary
  const loadSummary = useCallback(async () => {
    if (!currentUser) return;
    try {
      const data = await api.getDashboardSummary();
      setSummary(data);
    } catch (err) {
      if (err.status === 401) {
        handleLogout();
      }
    }
  }, [currentUser]);

  // Load reports based on viewMode & filters
  const loadReports = useCallback(async () => {
    if (!currentUser) return;
    setIsLoading(true);
    setError(null);
    try {
      let data;
      if (viewMode === 'priority') {
        data = await api.getPriorityReports();
      } else {
        data = await api.getReports(filters);
      }
      setReports(data || []);
    } catch (err) {
      if (err.status === 401) {
        handleLogout();
      } else {
        setError(err.message || 'Failed to fetch reports from Java backend.');
      }
    } finally {
      setIsLoading(false);
    }
  }, [currentUser, viewMode, filters]);

  // Load users (ADMIN only)
  const loadUsers = useCallback(async () => {
    if (!currentUser || currentUser.role !== 'ADMIN') return;
    setIsLoadingUsers(true);
    try {
      const list = await api.getAllUsers();
      setUsersList(list || []);
    } catch (err) {
      console.error('Failed to load user accounts:', err);
    } finally {
      setIsLoadingUsers(false);
    }
  }, [currentUser]);

  // Trigger loads when user is authenticated
  useEffect(() => {
    if (currentUser) {
      loadSummary();
      loadReports();
      if (currentUser.role === 'ADMIN') {
        loadUsers();
      }
    }
  }, [currentUser, loadSummary, loadReports, loadUsers]);

  // Create staff account handler (ADMIN)
  const handleCreateStaff = async (e) => {
    e.preventDefault();
    setStaffCreateError(null);
    setStaffCreateSuccess(null);

    if (!newStaffUsername.trim() || newStaffUsername.trim().length < 3) {
      setStaffCreateError('Username must be at least 3 characters.');
      return;
    }
    if (!newStaffPassword || newStaffPassword.length < 6) {
      setStaffCreateError('Password must be at least 6 characters.');
      return;
    }

    setIsCreatingStaff(true);
    try {
      const created = await api.createStaffUser(newStaffUsername.trim(), newStaffPassword);
      setStaffCreateSuccess(`Staff account '${created.username}' created successfully.`);
      setNewStaffUsername('');
      setNewStaffPassword('');
      loadUsers();
    } catch (err) {
      setStaffCreateError(err.message || 'Failed to provision staff account.');
    } finally {
      setIsCreatingStaff(false);
    }
  };

  // Handle opening status update & inspection modal
  const handleOpenReport = (report) => {
    setSelectedReport(report);
    const validNext = VALID_NEXT_STATUSES[report.status] || [];
    setNewStatus(validNext[0] || '');
    setStatusRemarks('');
    setStatusUpdateError(null);
    setStatusUpdateSuccess(null);
  };

  const handleCloseModal = () => {
    setSelectedReport(null);
    setStatusUpdateError(null);
    setStatusUpdateSuccess(null);
  };

  // Perform PATCH /api/reports/{id}/status
  const handleStatusUpdateSubmit = async (e) => {
    e.preventDefault();
    if (!selectedReport || !newStatus) return;

    setIsUpdatingStatus(true);
    setStatusUpdateError(null);
    setStatusUpdateSuccess(null);

    try {
      const updatedReport = await api.updateReportStatus(
        selectedReport.id,
        newStatus,
        statusRemarks.trim()
      );

      setStatusUpdateSuccess(`Status updated to ${newStatus} successfully!`);
      setSelectedReport(updatedReport);
      const nextAllowed = VALID_NEXT_STATUSES[updatedReport.status] || [];
      setNewStatus(nextAllowed[0] || '');
      setStatusRemarks('');

      await Promise.all([loadSummary(), loadReports()]);
    } catch (err) {
      setStatusUpdateError(err.message || 'Failed to update status.');
    } finally {
      setIsUpdatingStatus(false);
    }
  };

  const allowedTransitions = selectedReport ? (VALID_NEXT_STATUSES[selectedReport.status] || []) : [];

  // ==========================================
  // VIEW: Unauthenticated -> Show Login Form
  // ==========================================
  if (!currentUser) {
    return (
      <div className="login-screen-wrap">
        <div className="login-card card card-purple-glow">
          <div className="login-header">
            <span className="badge-pill-purple">Official Portal Access</span>
            <h2 className="login-title">Staff &amp; Admin Sign In</h2>
            <p className="login-subtitle">
              Authorized personnel access. Citizens can report waste hotspots directly on the public map without signing in.
            </p>
          </div>

          {/* 1-Click Fast Demo Login for Quick Demonstrations */}
          <div className="fast-login-box">
            <div className="fast-login-header">
              <span className="fast-login-badge">⚡ Quick Access</span>
              <span className="fast-login-header-title">1-Click Fast Demo Login</span>
            </div>
            <p className="fast-login-desc">
              Select an account below to sign in instantly without typing credentials:
            </p>
            <div className="fast-login-grid">
              <button
                type="button"
                id="fast-login-staff-btn"
                className="btn-fast-demo"
                disabled={isLoggingIn}
                onClick={() => handleFastLogin('STAFF-101', 'StaffPassword123')}
              >
                <div className="fast-btn-top">
                  <span className="fast-btn-emoji">👷</span>
                  <span className="fast-btn-title">Sanitation Staff</span>
                </div>
                <div className="fast-btn-id">ID: STAFF-101</div>
                <div className="fast-btn-sub">Inspect &amp; Resolve Hotspots</div>
              </button>

              <button
                type="button"
                id="fast-login-admin-btn"
                className="btn-fast-demo"
                disabled={isLoggingIn}
                onClick={() => handleFastLogin('ADMIN-01', 'AdminPassword123')}
              >
                <div className="fast-btn-top">
                  <span className="fast-btn-emoji">🛡️</span>
                  <span className="fast-btn-title">System Admin</span>
                </div>
                <div className="fast-btn-id">ID: ADMIN-01</div>
                <div className="fast-btn-sub">Manage Staff &amp; System</div>
              </button>
            </div>
          </div>

          <div className="login-divider-line">
            <span>or sign in manually with Staff ID</span>
          </div>

          <form onSubmit={handleLoginSubmit} className="login-form">
            {loginError && (
              <div className="alert-box alert-error" role="alert">
                <span className="alert-icon">⚠</span>
                <span>{loginError}</span>
              </div>
            )}

            <div className="form-field">
              <label htmlFor="login-username" className="form-label">
                Staff ID <span className="required-star">*</span>
              </label>
              <input
                id="login-username"
                type="text"
                className="form-input"
                placeholder="e.g. STAFF-101 or ADMIN-01"
                value={loginUsername}
                onChange={(e) => {
                  setLoginUsername(e.target.value);
                  if (loginError) setLoginError(null);
                }}
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="login-password" className="form-label">
                Password <span className="required-star">*</span>
              </label>
              <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
                <input
                  id="login-password"
                  type={showPassword ? 'text' : 'password'}
                  className="form-input"
                  placeholder="Enter password"
                  value={loginPassword}
                  onChange={(e) => {
                    setLoginPassword(e.target.value);
                    if (loginError) setLoginError(null);
                  }}
                  required
                  style={{ paddingRight: '2.5rem' }}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  style={{
                    position: 'absolute',
                    right: '0.75rem',
                    background: 'transparent',
                    border: 'none',
                    cursor: 'pointer',
                    fontSize: '1.1rem',
                    padding: 0,
                    lineHeight: 1,
                  }}
                  title={showPassword ? 'Hide password' : 'Show password'}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  {showPassword ? '🙈' : '👁️'}
                </button>
              </div>
            </div>

            <button
              type="submit"
              id="login-submit-btn"
              className="btn btn-primary-gradient mt-2"
              disabled={isLoggingIn}
            >
              {isLoggingIn ? 'Signing In...' : 'Sign In'}
            </button>

            <div className="login-credential-hint">
              <strong>Demo Credentials:</strong><br />
              &bull; Staff: ID <code>STAFF-101</code> &bull; Password: <code>StaffPassword123</code><br />
              &bull; Admin: ID <code>ADMIN-01</code> &bull; Password: <code>AdminPassword123</code><br />
              <span style={{ fontSize: '0.72rem', color: '#a78bfa', display: 'inline-block', marginTop: '0.25rem' }}>
                💡 Tip: Click the 1-Click Fast Demo Login buttons above to sign in immediately without typing!
              </span>
            </div>
          </form>
        </div>
      </div>
    );
  }

  // ==========================================
  // VIEW: Authenticated Dashboard
  // ==========================================
  return (
    <div className="staff-dashboard">
      {/* User Session Bar */}
      <div className="auth-session-bar">
        <div className="user-profile-badge">
          <span className="user-avatar-dot" />
          <span className="user-name">Staff ID: <strong>{currentUser.username}</strong></span>
          <span className={`role-badge ${currentUser.role === 'ADMIN' ? 'role-admin' : 'role-staff'}`}>
            {currentUser.role}
          </span>
        </div>

        <div className="session-actions">
          {currentUser.role === 'ADMIN' && (
            <div className="sub-tabs-group">
              <button
                className={`sub-tab-btn ${activeSubTab === 'operations' ? 'active' : ''}`}
                onClick={() => setActiveSubTab('operations')}
              >
                Hotspots Operations
              </button>
              <button
                className={`sub-tab-btn ${activeSubTab === 'admin-users' ? 'active' : ''}`}
                onClick={() => setActiveSubTab('admin-users')}
              >
                Staff Account Management
              </button>
            </div>
          )}

          <button
            type="button"
            id="logout-btn"
            className="btn btn-sm btn-ghost"
            onClick={handleLogout}
            title="Log out of staff session"
          >
            Sign Out ➔
          </button>
        </div>
      </div>

      {/* ========================================================
          SUB-TAB: ADMIN USER MANAGEMENT (ADMIN ONLY)
          ======================================================== */}
      {activeSubTab === 'admin-users' && currentUser.role === 'ADMIN' && (
        <section className="admin-users-section mt-3">
          <div className="portal-grid">
            {/* Create Staff Account Form */}
            <div className="portal-main-col">
              <div className="card card-purple-glow">
                <div className="section-header">
                  <span className="badge-pill-purple">ADMINISTRATOR PRIVILEGE</span>
                  <h3 className="section-title">Provision New Staff Account</h3>
                  <p className="section-subtitle">
                    Create authorized staff credentials. Staff members can inspect waste hotspots and transition workflow statuses.
                  </p>
                </div>

                <form onSubmit={handleCreateStaff} className="report-form">
                  {staffCreateError && (
                    <div className="alert-box alert-error">
                      <span>{staffCreateError}</span>
                    </div>
                  )}
                  {staffCreateSuccess && (
                    <div className="alert-box alert-success">
                      <span>{staffCreateSuccess}</span>
                    </div>
                  )}

                  <div className="form-group-grid">
                    <div className="form-field">
                      <label htmlFor="new-staff-username" className="form-label">
                        Staff ID <span className="required-star">*</span>
                      </label>
                      <input
                        id="new-staff-username"
                        type="text"
                        className="form-input"
                        placeholder="e.g. STAFF-102"
                        value={newStaffUsername}
                        onChange={(e) => setNewStaffUsername(e.target.value)}
                        required
                      />
                    </div>

                    <div className="form-field">
                      <label htmlFor="new-staff-password" className="form-label">
                        Initial Password <span className="required-star">*</span>
                      </label>
                      <input
                        id="new-staff-password"
                        type="password"
                        className="form-input"
                        placeholder="Min 6 characters"
                        value={newStaffPassword}
                        onChange={(e) => setNewStaffPassword(e.target.value)}
                        required
                      />
                    </div>
                  </div>

                  <button
                    type="submit"
                    className="btn btn-primary-gradient mt-2"
                    disabled={isCreatingStaff}
                  >
                    {isCreatingStaff ? 'Creating Account...' : 'Provision Staff Account'}
                  </button>
                </form>
              </div>
            </div>

            {/* Existing Accounts Table */}
            <div className="portal-side-col">
              <div className="card card-purple-subtle">
                <div className="alerts-card-header">
                  <div>
                    <span className="privacy-badge">RBAC ACCOUNTS</span>
                    <h4 className="alerts-title">Active System Accounts</h4>
                    <p className="alerts-sub">Stored with BCrypt hashes in SQLite</p>
                  </div>
                  <button
                    type="button"
                    className="btn btn-icon-round"
                    onClick={loadUsers}
                    title="Refresh users"
                    disabled={isLoadingUsers}
                  >
                    ⟳
                  </button>
                </div>

                <div className="accounts-list">
                  {usersList.map((u) => (
                    <div key={u.id} className="account-list-item">
                      <div>
                        <strong>Staff ID: {u.username}</strong>
                        <span className="account-date">
                          Created {new Date(u.createdAt).toLocaleDateString()}
                        </span>
                      </div>
                      <span className={`role-badge ${u.role === 'ADMIN' ? 'role-admin' : 'role-staff'}`}>
                        {u.role}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </section>
      )}

      {/* ========================================================
          SUB-TAB: HOTSPOTS OPERATIONS (STAFF & ADMIN)
          ======================================================== */}
      {activeSubTab === 'operations' && (
        <>
          {/* Summary KPI Cards */}
          <section className="summary-section mt-3">
            <div className="summary-header">
              <h2 className="section-title">Waste Hotspot Operations Overview</h2>
              <button
                type="button"
                className="btn btn-sm btn-outline-purple"
                onClick={() => {
                  loadSummary();
                  loadReports();
                }}
                disabled={isLoading}
              >
                ⟳ Refresh Operations Data
              </button>
            </div>

            <div className="kpi-grid">
              <div className="kpi-card">
                <span className="kpi-label">Total Reports</span>
                <div className="kpi-val">{summary ? summary.totalReports : '—'}</div>
                <span className="kpi-sub">All-time logged reports</span>
              </div>

              <div className="kpi-card kpi-card-priority">
                <span className="kpi-label">Priority Unresolved</span>
                <div className="kpi-val text-accent-red">
                  {summary ? summary.unresolvedHighCriticalReports : '—'}
                </div>
                <span className="kpi-sub">High &amp; Critical hotspots</span>
              </div>

              <div className="kpi-card">
                <span className="kpi-label">Pending Triage</span>
                <div className="kpi-val text-accent-amber">
                  {summary ? summary.pendingReports : '—'}
                </div>
                <span className="kpi-sub">{summary?.pendingDefinition || 'Awaiting action'}</span>
              </div>

              <div className="kpi-card">
                <span className="kpi-label">In Progress</span>
                <div className="kpi-val text-accent-cyan">
                  {summary ? summary.inProgressReports : '—'}
                </div>
                <span className="kpi-sub">Field action active</span>
              </div>

              <div className="kpi-card">
                <span className="kpi-label">Resolved</span>
                <div className="kpi-val text-accent-emerald">
                  {summary ? summary.resolvedReports : '—'}
                </div>
                <span className="kpi-sub">Cleaned &amp; mitigated</span>
              </div>
            </div>
          </section>

          {/* Reports Control Bar & Filters */}
          <section className="card card-purple mt-4">
            <div className="toolbar-row">
              <div className="view-mode-tabs">
                <button
                  className={`mode-tab ${viewMode === 'priority' ? 'active' : ''}`}
                  onClick={() => setViewMode('priority')}
                >
                  🔥 Priority Unresolved Queue ({summary ? summary.unresolvedHighCriticalReports : 0})
                </button>
                <button
                  className={`mode-tab ${viewMode === 'all' ? 'active' : ''}`}
                  onClick={() => setViewMode('all')}
                >
                  📋 All Reports &amp; Filters
                </button>
              </div>

              <div className="reports-count-indicator">
                Showing <strong>{reports.length}</strong> reports
              </div>
            </div>

            {/* Filter controls active when in 'all' view */}
            {viewMode === 'all' && (
              <div className="filter-controls-row">
                <div className="filter-item">
                  <label htmlFor="filter-status" className="filter-label">Filter Status:</label>
                  <select
                    id="filter-status"
                    className="filter-select"
                    value={filters.status}
                    onChange={(e) => setFilters((prev) => ({ ...prev, status: e.target.value }))}
                  >
                    <option value="">All Statuses</option>
                    <option value="REPORTED">REPORTED</option>
                    <option value="VERIFIED">VERIFIED</option>
                    <option value="IN_PROGRESS">IN_PROGRESS</option>
                    <option value="RESOLVED">RESOLVED</option>
                    <option value="REJECTED">REJECTED</option>
                  </select>
                </div>

                <div className="filter-item">
                  <label htmlFor="filter-risk" className="filter-label">Risk Level:</label>
                  <select
                    id="filter-risk"
                    className="filter-select"
                    value={filters.riskLevel}
                    onChange={(e) => setFilters((prev) => ({ ...prev, riskLevel: e.target.value }))}
                  >
                    <option value="">All Risk Levels</option>
                    <option value="CRITICAL">CRITICAL</option>
                    <option value="HIGH">HIGH</option>
                    <option value="MODERATE">MODERATE</option>
                    <option value="LOW">LOW</option>
                  </select>
                </div>

                <div className="filter-item">
                  <label htmlFor="filter-ward" className="filter-label">Ward:</label>
                  <input
                    id="filter-ward"
                    type="text"
                    className="filter-input"
                    placeholder="Search ward..."
                    value={filters.ward}
                    onChange={(e) => setFilters((prev) => ({ ...prev, ward: e.target.value }))}
                  />
                </div>

                {(filters.status || filters.riskLevel || filters.ward) && (
                  <button
                    className="btn btn-sm btn-ghost"
                    onClick={() => setFilters({ status: '', riskLevel: '', ward: '' })}
                  >
                    Clear Filters
                  </button>
                )}
              </div>
            )}

            {/* Reports Table */}
            {isLoading && (
              <div className="loading-state">
                <span className="spinner-purple" />
                <p>Loading reports from Spring Boot backend...</p>
              </div>
            )}

            {error && (
              <div className="alert-box alert-error">
                <span>{error}</span>
              </div>
            )}

            {!isLoading && !error && reports.length === 0 && (
              <div className="empty-reports-box">
                <span className="empty-icon">✓</span>
                <p className="empty-title">No reports match current criteria</p>
                <p className="empty-text">No active records found in the SQLite database.</p>
              </div>
            )}

            {!isLoading && !error && reports.length > 0 && (
              <div className="table-responsive">
                <table className="reports-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Ward</th>
                      <th>Location Landmark</th>
                      <th>Quantity / Days</th>
                      <th>Risk Score &amp; Level</th>
                      <th>Status</th>
                      <th>Reported At</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reports.map((report) => (
                      <tr key={report.id} className="report-row">
                        <td className="cell-id">#{report.id}</td>
                        <td className="cell-ward">
                          <strong>{report.ward}</strong>
                        </td>
                        <td className="cell-desc" title={report.locationDescription}>
                          {report.locationDescription}
                        </td>
                        <td className="cell-factors">
                          <span className="qty-tag">{report.wasteQuantity}</span>
                          <span className="days-tag">{report.wasteDurationDays}d</span>
                          <div className="factors-icons">
                            {report.stagnantWater && <span title="Stagnant Water">💧</span>}
                            {report.mosquitoSightings && <span title="Mosquitoes">🦟</span>}
                            {report.rodentSightings && <span title="Rodents">🐀</span>}
                            {report.strayAnimalSightings && <span title="Strays">🐕</span>}
                            {report.unsegregatedWaste && <span title="Unsegregated">🗑️</span>}
                          </div>
                        </td>
                        <td>
                          <RiskBadge level={report.riskLevel} score={report.riskScore} />
                        </td>
                        <td>
                          <StatusBadge status={report.status} />
                        </td>
                        <td className="cell-time">
                          {new Date(report.createdAt).toLocaleString(undefined, {
                            month: 'short',
                            day: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit',
                          })}
                        </td>
                        <td>
                          <button
                            className="btn btn-sm btn-purple-outline"
                            onClick={() => handleOpenReport(report)}
                          >
                            Inspect &amp; Manage
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        </>
      )}

      {/* Status Management & History Modal */}
      {selectedReport && (
        <div className="modal-backdrop" onClick={handleCloseModal}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <div>
                <span className="modal-sub">Hotspot Management &amp; Audit Trail</span>
                <h3 className="modal-title">
                  Report #{selectedReport.id} • {selectedReport.ward}
                </h3>
              </div>
              <button
                type="button"
                className="modal-close-btn"
                onClick={handleCloseModal}
              >
                ✕
              </button>
            </div>

            <div className="modal-body">
              {/* Report Summary Details */}
              <div className="modal-summary-grid">
                <div className="modal-info-box">
                  <span className="info-box-lbl">Location Landmark</span>
                  <p className="info-box-val">{selectedReport.locationDescription}</p>
                </div>
                <div className="modal-info-box">
                  <span className="info-box-lbl">Risk Assessment</span>
                  <div className="mt-1">
                    <RiskBadge level={selectedReport.riskLevel} score={selectedReport.riskScore} />
                  </div>
                  <p className="risk-exp-text mt-1">{selectedReport.riskExplanation}</p>
                </div>
                <div className="modal-info-box">
                  <span className="info-box-lbl">Current Status</span>
                  <div className="mt-1">
                    <StatusBadge status={selectedReport.status} />
                  </div>
                </div>
                <div className="modal-info-box">
                  <span className="info-box-lbl">Observed Conditions</span>
                  <p className="conditions-list">
                    Quantity: <strong>{selectedReport.wasteQuantity}</strong> • Duration:{' '}
                    <strong>{selectedReport.wasteDurationDays} days</strong>
                    <br />
                    {[
                      selectedReport.stagnantWater && 'Stagnant Water',
                      selectedReport.mosquitoSightings && 'Mosquitoes',
                      selectedReport.rodentSightings && 'Rodents',
                      selectedReport.strayAnimalSightings && 'Stray Animals',
                      selectedReport.unsegregatedWaste && 'Unsegregated',
                    ]
                      .filter(Boolean)
                      .join(', ') || 'No critical factors flagged'}
                  </p>
                </div>
              </div>

              {/* Status Update Form */}
              <div className="status-update-section">
                <h4 className="section-subtitle-sm">Update Hotspot Status</h4>

                {allowedTransitions.length === 0 ? (
                  <div className="terminal-status-notice">
                    <span>🔒 This report is in terminal status (<strong>{selectedReport.status}</strong>). No further transitions permitted.</span>
                  </div>
                ) : (
                  <form onSubmit={handleStatusUpdateSubmit} className="status-form">
                    {statusUpdateError && (
                      <div className="alert-box alert-error mb-2">
                        <span>{statusUpdateError}</span>
                      </div>
                    )}
                    {statusUpdateSuccess && (
                      <div className="alert-box alert-success mb-2">
                        <span>{statusUpdateSuccess}</span>
                      </div>
                    )}

                    <div className="status-inputs-row">
                      <div className="form-field flex-1">
                        <label htmlFor="next-status-select" className="form-label">
                          Next Valid Status <span className="required-star">*</span>
                        </label>
                        <select
                          id="next-status-select"
                          className="form-select"
                          value={newStatus}
                          onChange={(e) => setNewStatus(e.target.value)}
                          required
                        >
                          {allowedTransitions.map((st) => (
                            <option key={st} value={st}>
                              {st}
                            </option>
                          ))}
                        </select>
                        <span className="field-hint">
                          Valid rule: {selectedReport.status} → {allowedTransitions.join(' or ')}
                        </span>
                      </div>

                      <div className="form-field flex-2">
                        <label htmlFor="status-remarks-input" className="form-label">
                          Status Transition Remarks / Notes
                        </label>
                        <input
                          id="status-remarks-input"
                          type="text"
                          className="form-input"
                          placeholder="e.g. Dispatched sanitation vehicle, spraying larvicide..."
                          value={statusRemarks}
                          onChange={(e) => setStatusRemarks(e.target.value)}
                          maxLength={500}
                        />
                      </div>
                    </div>

                    <div className="modal-actions-row">
                      <button
                        type="submit"
                        className="btn btn-primary-gradient"
                        disabled={isUpdatingStatus || !newStatus}
                      >
                        {isUpdatingStatus ? 'Transitioning...' : `Transition Status to ${newStatus}`}
                      </button>
                    </div>
                  </form>
                )}
              </div>

              {/* Status History Audit Trail */}
              <div className="history-section">
                <h4 className="section-subtitle-sm">Status Transition History &amp; Audit Trail</h4>
                {(!selectedReport.statusHistory || selectedReport.statusHistory.length === 0) ? (
                  <p className="no-history-text">No recorded status changes yet. Initial state: {selectedReport.status}</p>
                ) : (
                  <div className="timeline">
                    {selectedReport.statusHistory.map((item, idx) => (
                      <div key={item.id || idx} className="timeline-item">
                        <div className="timeline-dot" />
                        <div className="timeline-content">
                          <div className="timeline-header">
                            <span className="transition-badge">
                              {item.fromStatus ? item.fromStatus : 'INITIAL'} → <strong>{item.toStatus}</strong>
                            </span>
                            <span className="timeline-time">
                              {new Date(item.changedAt).toLocaleString()}
                            </span>
                          </div>
                          {item.remarks && (
                            <p className="timeline-remarks">"{item.remarks}"</p>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
