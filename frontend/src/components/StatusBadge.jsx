import React from 'react';

export function RiskBadge({ level, score }) {
  const normalized = (level || '').toUpperCase();
  let className = 'risk-badge risk-low';

  if (normalized === 'CRITICAL') {
    className = 'risk-badge risk-critical';
  } else if (normalized === 'HIGH') {
    className = 'risk-badge risk-high';
  } else if (normalized === 'MODERATE') {
    className = 'risk-badge risk-moderate';
  }

  return (
    <span className={className}>
      <span className="badge-dot" />
      {normalized}
      {score !== undefined && score !== null && <span className="score-tag">{score}/100</span>}
    </span>
  );
}

export function StatusBadge({ status }) {
  const normalized = (status || '').toUpperCase();
  let className = 'status-badge status-reported';

  if (normalized === 'VERIFIED') {
    className = 'status-badge status-verified';
  } else if (normalized === 'IN_PROGRESS') {
    className = 'status-badge status-inprogress';
  } else if (normalized === 'RESOLVED') {
    className = 'status-badge status-resolved';
  } else if (normalized === 'REJECTED') {
    className = 'status-badge status-rejected';
  }

  const formatText = (text) => text.replace('_', ' ');

  return (
    <span className={className}>
      <span className="badge-dot" />
      {formatText(normalized)}
    </span>
  );
}
