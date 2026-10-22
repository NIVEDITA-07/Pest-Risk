import React, { useState, useEffect, useCallback } from 'react';
import { api } from './api';
import ResidentPortal from './components/ResidentPortal';
import StaffDashboard from './components/StaffDashboard';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export default function App() {
  const [activeTab, setActiveTab] = useState(() => (window.location.hash === '#staff' ? 'staff' : 'resident'));
  const [healthStatus, setHealthStatus] = useState('loading'); // 'loading' | 'connected' | 'disconnected'
  const [healthService, setHealthService] = useState('');
  const [latency, setLatency] = useState(null);
  const [isCheckingHealth, setIsCheckingHealth] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

  useEffect(() => {
    const onHashChange = () => {
      setActiveTab(window.location.hash === '#staff' ? 'staff' : 'resident');
    };
    window.addEventListener('hashchange', onHashChange);
    return () => window.removeEventListener('hashchange', onHashChange);
  }, []);

  const checkHealth = useCallback(async () => {
    setIsCheckingHealth(true);
    const start = performance.now();
    try {
      const data = await api.getHealth();
      const elapsed = Math.round(performance.now() - start);
      setLatency(elapsed);
      setHealthService(data?.service || 'PestRisk');
      setHealthStatus('connected');
      setErrorMessage(null);
    } catch (err) {
      setHealthStatus('disconnected');
      setHealthService('');
      setErrorMessage(err.message || 'Cannot reach Spring Boot API.');
    } finally {
      setIsCheckingHealth(false);
    }
  }, []);

  useEffect(() => {
    checkHealth();
    // Periodically verify connection every 30 seconds
    const interval = setInterval(checkHealth, 30000);
    return () => clearInterval(interval);
  }, [checkHealth]);

  return (
    <div className="app-shell">
      {/* Top Navbar */}
      <header className="navbar">
        <div className="nav-container">
          <div className="brand-group">
            <div className="brand-logo-gem">
              <span className="logo-sparkle">✦</span>
            </div>
            <div>
              <div className="brand-meta">
                <span className="brand-badge-purple">UHV Project • Rural Waste Assessment</span>
                <span className="source-of-truth-pill">Java 21 Core Backend</span>
              </div>
              <h1 className="brand-title">PestRisk Platform</h1>
              <p className="brand-tagline">
                Village Waste Hotspot Prioritization &amp; Vector Control System
              </p>
            </div>
          </div>

          <div className="nav-controls">
            {/* Health pill */}
            <div className={`health-pill ${healthStatus}`} title={healthService ? `${healthService} Service (Spring Boot)` : 'Backend API Status'}>
              <span className="health-dot" />
              <div className="health-info">
                <span className="health-status-text">
                  {healthStatus === 'connected' && (healthService ? `${healthService} ONLINE` : 'API ONLINE')}
                  {healthStatus === 'loading' && 'CONNECTING...'}
                  {healthStatus === 'disconnected' && 'API OFFLINE'}
                </span>
                {healthStatus === 'connected' && latency !== null && (
                  <span className="health-latency">{latency}ms</span>
                )}
              </div>
              <button
                type="button"
                className="btn-health-refresh"
                onClick={checkHealth}
                disabled={isCheckingHealth}
                title="Recheck backend health"
              >
                ⟳
              </button>
            </div>

            {/* Navigation Switcher */}
            <div className="tab-switcher">
              <button
                id="tab-btn-resident"
                className={`tab-btn ${activeTab === 'resident' ? 'active' : ''}`}
                onClick={() => {
                  setActiveTab('resident');
                  window.location.hash = '#resident';
                }}
              >
                <span className="tab-icon">🏡</span>
                <span>Resident Portal</span>
              </button>
              <button
                id="tab-btn-staff"
                className={`tab-btn ${activeTab === 'staff' ? 'active' : ''}`}
                onClick={() => {
                  setActiveTab('staff');
                  window.location.hash = '#staff';
                }}
              >
                <span className="tab-icon">🛡️</span>
                <span>Staff Operations</span>
                <span className="demo-tag">Local Demo</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Offline Alert Banner */}
      {healthStatus === 'disconnected' && (
        <div className="backend-offline-banner">
          <div className="banner-content">
            <span className="banner-icon">⚠️</span>
            <div className="banner-text">
              <strong>Backend Unavailable:</strong> Unable to connect to Spring Boot at <code>{API_BASE_URL}</code>.
              Please start the Java backend (`.\mvnw.cmd spring-boot:run` in <code>D:\uhv\backend</code>).
              {errorMessage && <span className="banner-err"> Error: {errorMessage}</span>}
            </div>
            <button
              className="btn btn-sm btn-reconnect"
              onClick={checkHealth}
              disabled={isCheckingHealth}
            >
              {isCheckingHealth ? 'Checking...' : 'Retry Connection'}
            </button>
          </div>
        </div>
      )}

      {/* Main Content Area */}
      <main className="main-content">
        {activeTab === 'resident' ? (
          <ResidentPortal />
        ) : (
          <StaffDashboard />
        )}
      </main>

      {/* Footer */}
      <footer className="footer">
        <div className="footer-container">
          <div className="footer-left">
            <strong>PestRisk Platform</strong> • Universal Human Values (UHV) Course Project
            <span className="footer-sep">|</span>
            SQLite Database: <code>D:\uhv\data\pestrisk.db</code>
          </div>
          <div className="footer-right">
            Risk Engine, Hotspot Prioritization &amp; Workflow Rules Executed on Java/Spring Boot
          </div>
        </div>
      </footer>
    </div>
  );
}
