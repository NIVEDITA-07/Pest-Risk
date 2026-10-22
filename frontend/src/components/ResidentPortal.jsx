import React, { useState, useEffect, useCallback } from 'react';
import { api } from '../api';
import { RiskBadge, StatusBadge } from './StatusBadge';

const INITIAL_FORM = {
  ward: '',
  locationDescription: '',
  wasteQuantity: 'MEDIUM',
  wasteDurationDays: 1,
  stagnantWater: false,
  mosquitoSightings: false,
  rodentSightings: false,
  strayAnimalSightings: false,
  unsegregatedWaste: true,
};

export default function ResidentPortal() {
  const [formData, setFormData] = useState(INITIAL_FORM);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formError, setFormError] = useState(null);
  const [createdReport, setCreatedReport] = useState(null);

  // Live Risk Preview state (backed by POST /api/risk/preview)
  const [preview, setPreview] = useState(null);
  const [isPreviewLoading, setIsPreviewLoading] = useState(false);
  const [previewError, setPreviewError] = useState(null);

  // Privacy-safe Community Alerts (backed by GET /api/community/alerts)
  const [alerts, setAlerts] = useState([]);
  const [isAlertsLoading, setIsAlertsLoading] = useState(false);
  const [alertsError, setAlertsError] = useState(null);

  const fetchAlerts = useCallback(async () => {
    setIsAlertsLoading(true);
    setAlertsError(null);
    try {
      const data = await api.getCommunityAlerts();
      setAlerts(data || []);
    } catch (err) {
      setAlertsError(err.message || 'Unable to load community advisories.');
    } finally {
      setIsAlertsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchAlerts();
  }, [fetchAlerts]);

  // Request Java RiskEngine live preview
  const handlePreviewRisk = async () => {
    setIsPreviewLoading(true);
    setPreviewError(null);
    try {
      const payload = {
        wasteQuantity: formData.wasteQuantity,
        wasteDurationDays: Number(formData.wasteDurationDays),
        stagnantWater: formData.stagnantWater,
        mosquitoSightings: formData.mosquitoSightings,
        rodentSightings: formData.rodentSightings,
        strayAnimalSightings: formData.strayAnimalSightings,
        unsegregatedWaste: formData.unsegregatedWaste,
      };
      const result = await api.previewRisk(payload);
      setPreview(result);
    } catch (err) {
      setPreviewError(err.message || 'Failed to estimate risk score.');
    } finally {
      setIsPreviewLoading(false);
    }
  };

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormError(null);

    // Basic validation
    if (!formData.ward.trim()) {
      setFormError('Please specify the Ward or village area.');
      return;
    }
    if (!formData.locationDescription.trim()) {
      setFormError('Please provide a landmark or location description.');
      return;
    }
    if (formData.wasteDurationDays < 0) {
      setFormError('Waste duration cannot be negative.');
      return;
    }

    setIsSubmitting(true);
    try {
      const payload = {
        ward: formData.ward.trim(),
        locationDescription: formData.locationDescription.trim(),
        wasteQuantity: formData.wasteQuantity,
        wasteDurationDays: Number(formData.wasteDurationDays),
        stagnantWater: formData.stagnantWater,
        mosquitoSightings: formData.mosquitoSightings,
        rodentSightings: formData.rodentSightings,
        strayAnimalSightings: formData.strayAnimalSightings,
        unsegregatedWaste: formData.unsegregatedWaste,
      };

      const response = await api.createReport(payload);
      setCreatedReport(response);
      setFormData(INITIAL_FORM);
      setPreview(null);
      // Refresh community alerts in case a high/critical report was added
      fetchAlerts();
    } catch (err) {
      setFormError(err.message || 'Failed to submit report. Please check backend connection.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="resident-portal">
      <div className="portal-grid">
        {/* Left Column: Waste Report Form */}
        <div className="portal-main-col">
          <section className="card card-purple-glow">
            <div className="section-header">
              <div>
                <span className="badge-pill-purple">Village Waste Reporting</span>
                <h2 className="section-title">Report a Waste Hotspot</h2>
                <p className="section-subtitle">
                  Help identify vector breeding risks in your neighborhood. Submissions are processed by the Java RiskEngine.
                </p>
              </div>
            </div>

            {createdReport ? (
              <div className="submission-success-card" id="submission-success-card">
                <div className="success-icon-wrap">✓</div>
                <div className="success-content">
                  <span className="success-tag">Report Successfully Registered</span>
                  <h3 className="success-title">Report Reference #{createdReport.id}</h3>
                  <p className="success-desc">
                    Your report for <strong>{createdReport.ward}</strong> has been assigned to village sanitation staff.
                  </p>

                  <div className="success-stats-grid">
                    <div className="stat-pill">
                      <span className="stat-lbl">Calculated Score</span>
                      <span className="stat-val">{createdReport.riskScore} / 100</span>
                    </div>
                    <div className="stat-pill">
                      <span className="stat-lbl">Risk Classification</span>
                      <div>
                        <RiskBadge level={createdReport.riskLevel} />
                      </div>
                    </div>
                    <div className="stat-pill">
                      <span className="stat-lbl">Current Status</span>
                      <div>
                        <StatusBadge status={createdReport.status} />
                      </div>
                    </div>
                  </div>

                  {createdReport.riskExplanation && (
                    <div className="explanation-callout">
                      <strong>Java Risk Engine Assessment:</strong> {createdReport.riskExplanation}
                    </div>
                  )}

                  <button
                    className="btn btn-secondary mt-3"
                    onClick={() => setCreatedReport(null)}
                  >
                    Submit Another Report
                  </button>
                </div>
              </div>
            ) : (
              <form onSubmit={handleSubmit} className="report-form" noValidate>
                {formError && (
                  <div className="alert-box alert-error" role="alert">
                    <span className="alert-icon">⚠</span>
                    <span>{formError}</span>
                  </div>
                )}

                {/* Location & Ward */}
                <div className="form-group-grid">
                  <div className="form-field">
                    <label htmlFor="ward-input" className="form-label">
                      Ward / Village Sector <span className="required-star">*</span>
                    </label>
                    <input
                      id="ward-input"
                      type="text"
                      className="form-input"
                      placeholder="e.g. Ward 3 - Market Street"
                      value={formData.ward}
                      onChange={(e) => handleInputChange('ward', e.target.value)}
                      required
                    />
                    <span className="field-hint">e.g. Ward 1, North Colony, Gram Panchayat Square</span>
                  </div>

                  <div className="form-field">
                    <label htmlFor="duration-input" className="form-label">
                      Waste Duration (Days) <span className="required-star">*</span>
                    </label>
                    <input
                      id="duration-input"
                      type="number"
                      min="0"
                      className="form-input"
                      value={formData.wasteDurationDays}
                      onChange={(e) => handleInputChange('wasteDurationDays', Math.max(0, parseInt(e.target.value) || 0))}
                      required
                    />
                    <span className="field-hint">0-1 days: 0 pts, 2-3 days: 8 pts, 4+ days: 15 pts</span>
                  </div>
                </div>

                <div className="form-field">
                  <label htmlFor="location-desc-input" className="form-label">
                    Landmark &amp; Location Details <span className="required-star">*</span>
                  </label>
                  <textarea
                    id="location-desc-input"
                    rows="2"
                    className="form-textarea"
                    placeholder="Specific landmark, nearby house number, open plot, or drainage location..."
                    value={formData.locationDescription}
                    onChange={(e) => handleInputChange('locationDescription', e.target.value)}
                    required
                  />
                </div>

                {/* Waste Quantity Selector */}
                <div className="form-field">
                  <label className="form-label">Estimated Waste Quantity</label>
                  <div className="radio-pill-group">
                    {[
                      { value: 'LOW', label: 'Low', desc: 'Household bag (~5 pts)' },
                      { value: 'MEDIUM', label: 'Medium', desc: 'Cart / Pile (~10 pts)' },
                      { value: 'HIGH', label: 'High', desc: 'Overflowing dump (~15 pts)' },
                    ].map((opt) => (
                      <button
                        type="button"
                        key={opt.value}
                        className={`radio-pill-btn ${formData.wasteQuantity === opt.value ? 'selected' : ''}`}
                        onClick={() => handleInputChange('wasteQuantity', opt.value)}
                      >
                        <span className="pill-title">{opt.label}</span>
                        <span className="pill-sub">{opt.desc}</span>
                      </button>
                    ))}
                  </div>
                </div>

                {/* Environmental Risk Factors */}
                <div className="form-field">
                  <label className="form-label">Environmental &amp; Vector Conditions Observed</label>
                  <div className="checkboxes-grid">
                    <label className={`checkbox-card ${formData.stagnantWater ? 'checked' : ''}`}>
                      <input
                        type="checkbox"
                        checked={formData.stagnantWater}
                        onChange={(e) => handleInputChange('stagnantWater', e.target.checked)}
                      />
                      <div className="checkbox-info">
                        <span className="factor-title">💧 Stagnant Water (+20 pts)</span>
                        <span className="factor-sub">Puddles or water collection near waste</span>
                      </div>
                    </label>

                    <label className={`checkbox-card ${formData.mosquitoSightings ? 'checked' : ''}`}>
                      <input
                        type="checkbox"
                        checked={formData.mosquitoSightings}
                        onChange={(e) => handleInputChange('mosquitoSightings', e.target.checked)}
                      />
                      <div className="checkbox-info">
                        <span className="factor-title">🦟 Mosquito Sightings (+15 pts)</span>
                        <span className="factor-sub">Visible swarms or breeding activity</span>
                      </div>
                    </label>

                    <label className={`checkbox-card ${formData.rodentSightings ? 'checked' : ''}`}>
                      <input
                        type="checkbox"
                        checked={formData.rodentSightings}
                        onChange={(e) => handleInputChange('rodentSightings', e.target.checked)}
                      />
                      <div className="checkbox-info">
                        <span className="factor-title">🐀 Rodent Sightings (+15 pts)</span>
                        <span className="factor-sub">Rats, burrows, or rodent feeding</span>
                      </div>
                    </label>

                    <label className={`checkbox-card ${formData.strayAnimalSightings ? 'checked' : ''}`}>
                      <input
                        type="checkbox"
                        checked={formData.strayAnimalSightings}
                        onChange={(e) => handleInputChange('strayAnimalSightings', e.target.checked)}
                      />
                      <div className="checkbox-info">
                        <span className="factor-title">🐕 Stray Animals (+10 pts)</span>
                        <span className="factor-sub">Cattle or stray dogs scattering garbage</span>
                      </div>
                    </label>

                    <label className={`checkbox-card ${formData.unsegregatedWaste ? 'checked' : ''}`}>
                      <input
                        type="checkbox"
                        checked={formData.unsegregatedWaste}
                        onChange={(e) => handleInputChange('unsegregatedWaste', e.target.checked)}
                      />
                      <div className="checkbox-info">
                        <span className="factor-title">🗑️ Unsegregated Waste (+10 pts)</span>
                        <span className="factor-sub">Decomposing food mixed with plastics</span>
                      </div>
                    </label>
                  </div>
                </div>

                {/* Live Java Preview Bar */}
                <div className="live-preview-box">
                  <div className="preview-header">
                    <div>
                      <span className="preview-label">Live Java Risk Engine Preview</span>
                      <p className="preview-sub">Queries POST /api/risk/preview in real time</p>
                    </div>
                    <button
                      type="button"
                      id="btn-preview-risk"
                      className="btn btn-sm btn-outline-purple"
                      onClick={handlePreviewRisk}
                      disabled={isPreviewLoading}
                    >
                      {isPreviewLoading ? 'Calculating...' : 'Preview Score'}
                    </button>
                  </div>

                  {preview && (
                    <div className="preview-result-card">
                      <div className="preview-metrics">
                        <div className="preview-metric">
                          <span className="preview-metric-label">Estimated Score</span>
                          <span className="preview-metric-value">{preview.riskScore ?? preview.score} / 100</span>
                        </div>
                        <div className="preview-metric">
                          <span className="preview-metric-label">Assigned Level</span>
                          <RiskBadge level={preview.riskLevel} />
                        </div>
                      </div>
                      <p className="preview-explanation">{preview.riskExplanation || preview.explanation}</p>
                      {preview.contributingFactors && preview.contributingFactors.length > 0 && (
                        <div className="preview-factors-box mt-2">
                          <span className="preview-factors-title">Contributing Risk Factors:</span>
                          <ul className="preview-factors-list">
                            {preview.contributingFactors.map((factor, idx) => (
                              <li key={idx} className="preview-factor-item">{factor}</li>
                            ))}
                          </ul>
                        </div>
                      )}
                    </div>
                  )}

                  {previewError && (
                    <p className="preview-err-text">{previewError}</p>
                  )}
                </div>

                {/* Form Actions */}
                <div className="form-actions">
                  <button
                    type="submit"
                    id="submit-waste-report-btn"
                    className="btn btn-primary-gradient"
                    disabled={isSubmitting}
                  >
                    {isSubmitting ? (
                      <>
                        <span className="spinner-border spinner-border-sm" /> Submitting to Java API...
                      </>
                    ) : (
                      'Submit Waste Report'
                    )}
                  </button>
                </div>
              </form>
            )}
          </section>
        </div>

        {/* Right Column: Privacy-Safe Community Alerts Feed */}
        <div className="portal-side-col">
          <section className="card card-purple-subtle">
            <div className="alerts-card-header">
              <div>
                <span className="privacy-badge">🔒 Privacy Protected</span>
                <h3 className="alerts-title">Active Community Alerts</h3>
                <p className="alerts-sub">
                  Only high/critical hotspot zones are publicly broadcast. Exact citizen and home locations are withheld.
                </p>
              </div>
              <button
                type="button"
                className="btn btn-icon-round"
                onClick={fetchAlerts}
                title="Refresh community alerts"
                disabled={isAlertsLoading}
              >
                ⟳
              </button>
            </div>

            {isAlertsLoading && (
              <div className="loading-state">
                <span className="spinner-purple" />
                <p>Loading community advisories...</p>
              </div>
            )}

            {alertsError && (
              <div className="alert-box alert-error">
                <span>{alertsError}</span>
              </div>
            )}

            {!isAlertsLoading && alerts.length === 0 && (
              <div className="empty-alerts">
                <span className="empty-icon">🌱</span>
                <p className="empty-title">No Active High-Risk Alerts</p>
                <p className="empty-text">All reported hotspots are under control or undergoing mitigation.</p>
              </div>
            )}

            <div className="alerts-list">
              {alerts.map((alert) => (
                <div key={alert.alertId} className="community-alert-item">
                  <div className="alert-top">
                    <span className="alert-ward">{alert.ward}</span>
                    <RiskBadge level={alert.riskLevel} />
                  </div>
                  <p className="alert-advisory">{alert.advisory}</p>
                  <div className="alert-footer">
                    <StatusBadge status={alert.status} />
                    <span className="alert-time">
                      {new Date(alert.reportedAt).toLocaleDateString()}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}
