# -*- coding: utf-8 -*-
"""
update_assets_vadama.py
Regenerates the 4 visualizer assets in report_assets/ specifically tailored to:
Vadama Village - Group 5:
'Attracts stray animals, mosquitoes, and rodents, raising the risk of communicable disease (Vadama)'
"""

import matplotlib.pyplot as plt
import matplotlib.patches as patches
import os

os.makedirs('report_assets', exist_ok=True)
plt.rcParams['font.family'] = 'sans-serif'
plt.rcParams['font.sans-serif'] = ['DejaVu Sans', 'Arial', 'Helvetica']

# ----------------- FIGURE 1: Resident Portal (Vadama) -----------------
fig, ax = plt.subplots(figsize=(6.5, 7.5), dpi=300)
ax.set_facecolor('#0f172a')
fig.patch.set_facecolor('#0f172a')
ax.axis('off')

# Title Bar
rect = patches.FancyBboxPatch((0.03, 0.90), 0.94, 0.08, boxstyle='round,pad=0.01', facecolor='#1e293b', edgecolor='#334155')
ax.add_patch(rect)
ax.text(0.06, 0.945, 'PestRisk', color='#10b981', fontsize=14, fontweight='bold')
ax.text(0.24, 0.945, '|  Resident Hotspot Reporting Portal', color='#94a3b8', fontsize=11)
ax.text(0.06, 0.915, 'Community Vector Early Warning — Vadama Village (Group 5 Study)', color='#64748b', fontsize=8)

# Location Section
sec1 = patches.FancyBboxPatch((0.03, 0.67), 0.94, 0.21, boxstyle='round,pad=0.01', facecolor='#1e293b', edgecolor='#334155')
ax.add_patch(sec1)
ax.text(0.06, 0.84, '1. VADAMA VILLAGE LOCATION & OBSERVATION', color='#38bdf8', fontsize=10, fontweight='bold')
ax.text(0.06, 0.795, 'Village Ward / Sector:', color='#cbd5e1', fontsize=9)
ax.text(0.50, 0.795, 'Vadama Central — Residential Cluster A', color='#f8fafc', fontsize=9, fontweight='bold')
ax.text(0.06, 0.750, 'Landmark / Public Reference:', color='#cbd5e1', fontsize=9)
ax.text(0.50, 0.750, 'Culvert Drainage near Canal Road Junction', color='#f8fafc', fontsize=9)
ax.text(0.06, 0.705, 'Estimated Accumulation Duration:', color='#cbd5e1', fontsize=9)
ax.text(0.50, 0.705, '5 Days (Exceeds 4-day critical vector cycle)', color='#fbbf24', fontsize=9, fontweight='bold')

# Vector Factors Section
sec2 = patches.FancyBboxPatch((0.03, 0.35), 0.94, 0.30, boxstyle='round,pad=0.01', facecolor='#1e293b', edgecolor='#334155')
ax.add_patch(sec2)
ax.text(0.06, 0.61, '2. VECTOR BREEDING & ANIMAL ATTRACTION (GROUP 5 FOCUS)', color='#38bdf8', fontsize=9.5, fontweight='bold')

factors = [
    ('[X] Stagnant Water / Clogged Drainage', '+20 Points', '#ef4444', 0.56),
    ('[X] Mosquito Swarms Observed near Homes', '+15 Points', '#ef4444', 0.51),
    ('[X] Active Rodent Infestation / Burrows', '+15 Points', '#ef4444', 0.46),
    ('[X] Stray Animals Drawn to Open Food Waste', '+10 Points', '#f59e0b', 0.41),
    ('[X] Unsegregated Decomposing Organic Waste', '+10 Points', '#f59e0b', 0.36)
]
for label, pts, col, y in factors:
    ax.text(0.06, y, label, color='#e2e8f0', fontsize=8.5)
    ax.text(0.80, y, pts, color=col, fontsize=8.5, fontweight='bold')

# Worker Safety Notice
sec3 = patches.FancyBboxPatch((0.03, 0.17), 0.94, 0.16, boxstyle='round,pad=0.01', facecolor='#2d1f1f', edgecolor='#ef4444')
ax.add_patch(sec3)
ax.text(0.06, 0.29, 'WORKER SAFETY & DIGNITY HAZARD ALERT (UHV PROTOCOL)', color='#f87171', fontsize=9, fontweight='bold')
ax.text(0.06, 0.245, 'Reported Physical Hazards: Broken Glass, Sharp Metal Scrap, Contaminated Debris', color='#fca5a5', fontsize=8)
ax.text(0.06, 0.200, 'Sanitation crew pre-notified to deploy heavy puncture-resistant gloves & rubber gumboots.', color='#fecaca', fontsize=7.5, style='italic')

# Submit button
btn = patches.FancyBboxPatch((0.25, 0.05), 0.50, 0.08, boxstyle='round,pad=0.02', facecolor='#10b981', edgecolor='#059669')
ax.add_patch(btn)
ax.text(0.31, 0.08, 'SUBMIT SECURE VADAMA REPORT', color='#ffffff', fontsize=9.5, fontweight='bold')

plt.tight_layout()
plt.savefig('report_assets/pestrisk_fig1_resident_portal.png', dpi=300)
plt.close()

# ----------------- FIGURE 2: Vector Risk Gauge (Vadama) -----------------
fig, ax = plt.subplots(figsize=(6.5, 7.5), dpi=300)
ax.set_facecolor('#0f172a')
fig.patch.set_facecolor('#0f172a')
ax.axis('off')

# Title
rect = patches.FancyBboxPatch((0.03, 0.90), 0.94, 0.08, boxstyle='round,pad=0.01', facecolor='#1e293b', edgecolor='#334155')
ax.add_patch(rect)
ax.text(0.06, 0.945, 'Deterministic RiskEngine Analysis — Vadama', color='#10b981', fontsize=13, fontweight='bold')
ax.text(0.06, 0.915, 'Audit Trail & Mathematical Score for Group 5 Case (0 - 100 Scale)', color='#94a3b8', fontsize=8.5)

# Gauge Box
sec_gauge = patches.FancyBboxPatch((0.03, 0.58), 0.94, 0.30, boxstyle='round,pad=0.01', facecolor='#1e293b', edgecolor='#334155')
ax.add_patch(sec_gauge)

# Center Score
ax.text(0.50, 0.77, '85', color='#ef4444', fontsize=48, fontweight='bold', ha='center')
ax.text(0.50, 0.71, 'CRITICAL COMMUNICABLE DISEASE RISK', color='#ef4444', fontsize=11, fontweight='bold', ha='center')
ax.text(0.50, 0.66, 'Immediate Sanitation Triage Priority: Stagnant Water + Strays + Pests', color='#fca5a5', fontsize=8.5, ha='center')
ax.text(0.50, 0.62, 'Risk of Dengue, Leptospirosis, & Animal Dispersal to Vadama Homes', color='#94a3b8', fontsize=7.5, style='italic', ha='center')

# Factor Breakdown Bars
sec_bars = patches.FancyBboxPatch((0.03, 0.08), 0.94, 0.48, boxstyle='round,pad=0.01', facecolor='#1e293b', edgecolor='#334155')
ax.add_patch(sec_bars)
ax.text(0.06, 0.52, 'ADDITIVE RISK ENGINE DECOMPOSITION (VADAMA PROBLEM)', color='#38bdf8', fontsize=9.5, fontweight='bold')

factors = [
    ('Waste Quantity (HIGH in Vadama)', 15, 15, 0.46, '#10b981'),
    ('Accumulation >= 4 Days (5 days)', 15, 15, 0.40, '#f59e0b'),
    ('Stagnant Water / Poor Drainage', 20, 20, 0.34, '#ef4444'),
    ('Mosquito Swarms Active', 15, 15, 0.28, '#ef4444'),
    ('Rodent Activity Detected', 15, 15, 0.22, '#ef4444'),
    ('Stray Animals Attracted', 10, 10, 0.16, '#f97316'),
    ('Unsegregated Waste Decomposition', 10, 10, 0.10, '#f59e0b'),
]

for label, pts, max_p, y, col in factors:
    ax.text(0.06, y + 0.015, f'{label} ({pts}/{max_p} pts)', color='#e2e8f0', fontsize=8)
    bar_bg = patches.Rectangle((0.55, y + 0.01), 0.38, 0.02, facecolor='#334155')
    ax.add_patch(bar_bg)
    if pts > 0:
        bar_fill = patches.Rectangle((0.55, y + 0.01), 0.38 * (pts/max_p), 0.02, facecolor=col)
        ax.add_patch(bar_fill)

plt.tight_layout()
plt.savefig('report_assets/pestrisk_fig2_risk_gauge.png', dpi=300)
plt.close()

# ----------------- FIGURE 3: Staff Dashboard (Vadama) -----------------
fig, ax = plt.subplots(figsize=(6.5, 7.5), dpi=300)
ax.set_facecolor('#0f172a')
fig.patch.set_facecolor('#0f172a')
ax.axis('off')

# Header
rect = patches.FancyBboxPatch((0.03, 0.90), 0.94, 0.08, boxstyle='round,pad=0.01', facecolor='#1e293b', edgecolor='#334155')
ax.add_patch(rect)
ax.text(0.06, 0.945, 'Vadama Sanitation Command & Triage Console', color='#10b981', fontsize=13, fontweight='bold')
ax.text(0.06, 0.915, 'Prioritized Hotspots for Group 5 Documented Cases in Vadama', color='#94a3b8', fontsize=8.5)

# KPI Cards
kpis = [
    ('Vadama Sites', '16', '#38bdf8', 0.03),
    ('Critical Priority', '5', '#ef4444', 0.27),
    ('In Progress', '4', '#f59e0b', 0.51),
    ('Resolved (MTD)', '11', '#10b981', 0.75)
]
for title, val, col, x in kpis:
    c = patches.FancyBboxPatch((x, 0.77), 0.22, 0.11, boxstyle='round,pad=0.01', facecolor='#1e293b', edgecolor='#334155')
    ax.add_patch(c)
    ax.text(x + 0.02, 0.84, title, color='#94a3b8', fontsize=7.5)
    ax.text(x + 0.02, 0.79, val, color=col, fontsize=18, fontweight='bold')

# Triage Queue Table
tbl = patches.FancyBboxPatch((0.03, 0.06), 0.94, 0.69, boxstyle='round,pad=0.01', facecolor='#1e293b', edgecolor='#334155')
ax.add_patch(tbl)
ax.text(0.06, 0.71, 'VADAMA HOTSPOT QUEUE (SORTED BY COMMUNICABLE RISK)', color='#38bdf8', fontsize=9.5, fontweight='bold')

ax.text(0.06, 0.66, 'ID #', color='#cbd5e1', fontsize=8, fontweight='bold')
ax.text(0.14, 0.66, 'Vadama Sector', color='#cbd5e1', fontsize=8, fontweight='bold')
ax.text(0.27, 0.66, 'Score', color='#cbd5e1', fontsize=8, fontweight='bold')
ax.text(0.38, 0.66, 'Documented Risk / Vector', color='#cbd5e1', fontsize=8, fontweight='bold')
ax.text(0.62, 0.66, 'Crew Hazard', color='#cbd5e1', fontsize=8, fontweight='bold')
ax.text(0.81, 0.66, 'Status', color='#cbd5e1', fontsize=8, fontweight='bold')

rows = [
    ('104', 'Vadama East', '85 (CRIT)', 'Strays + Mosquitoes + Water', '[!] Broken Glass', 'IN_PROGRESS', '#ef4444', 0.59),
    ('102', 'Vadama Mkt', '75 (HIGH)', 'Strays Scattering Food Waste', '[!] Metal Cans', 'VERIFIED', '#f97316', 0.51),
    ('098', 'Vadama Canal', '70 (HIGH)', 'Rodent Burrows + Drainage', 'None Reported', 'REPORTED', '#eab308', 0.43),
    ('106', 'Vadama West', '45 (MOD)', 'Unsegregated Yard Waste', 'Organic Leaf Piles', 'VERIFIED', '#3b82f6', 0.35),
    ('091', 'Vadama North', '30 (MOD)', 'Dry Commercial Packaging', 'None Reported', 'REPORTED', '#3b82f6', 0.27),
    ('088', 'Vadama South', '20 (LOW)', 'Segregated Household Bin', 'None Reported', 'RESOLVED', '#10b981', 0.19),
    ('085', 'Vadama School', '80 (CRIT)', 'Drain Waterlogging + Pests', '[!] Medical Waste', 'RESOLVED', '#10b981', 0.11),
]

for rid, ward, score, vec, haz, stat, col, y in rows:
    sep = patches.Rectangle((0.05, y - 0.02), 0.90, 0.001, facecolor='#334155')
    ax.add_patch(sep)
    ax.text(0.06, y, rid, color='#94a3b8', fontsize=7.5)
    ax.text(0.14, y, ward, color='#f8fafc', fontsize=7.5)
    ax.text(0.27, y, score, color=col, fontsize=7.5, fontweight='bold')
    ax.text(0.38, y, vec, color='#e2e8f0', fontsize=7)
    ax.text(0.62, y, haz, color='#fca5a5' if '[!]' in haz else '#94a3b8', fontsize=7)
    ax.text(0.81, y, stat, color='#ffffff', fontsize=7, bbox=dict(boxstyle='round,pad=0.2', facecolor=col, alpha=0.8))

plt.tight_layout()
plt.savefig('report_assets/pestrisk_fig3_staff_dashboard.png', dpi=300)
plt.close()

# ----------------- FIGURE 4: Status Workflow & Audit (Vadama) -----------------
fig, ax = plt.subplots(figsize=(6.5, 7.5), dpi=300)
ax.set_facecolor('#0f172a')
fig.patch.set_facecolor('#0f172a')
ax.axis('off')

# Header
rect = patches.FancyBboxPatch((0.03, 0.90), 0.94, 0.08, boxstyle='round,pad=0.01', facecolor='#1e293b', edgecolor='#334155')
ax.add_patch(rect)
ax.text(0.06, 0.945, 'Hotspot Remediation Lifecycle — Vadama Village', color='#10b981', fontsize=13, fontweight='bold')
ax.text(0.06, 0.915, 'Resolution of Group 5 Communicable Disease Case in Vadama', color='#94a3b8', fontsize=8.5)

# Workflow Diagram Box
sec_flow = patches.FancyBboxPatch((0.03, 0.65), 0.94, 0.23, boxstyle='round,pad=0.01', facecolor='#1e293b', edgecolor='#334155')
ax.add_patch(sec_flow)
ax.text(0.06, 0.83, 'LIFECYCLE FINITE STATE MACHINE (STRICT PROGRESSION)', color='#38bdf8', fontsize=8.5, fontweight='bold')

states = [
    ('REPORTED', '#eab308', 0.07),
    ('VERIFIED', '#3b82f6', 0.31),
    ('IN_PROGRESS', '#f97316', 0.55),
    ('RESOLVED', '#10b981', 0.79)
]
for name, col, x in states:
    box = patches.FancyBboxPatch((x, 0.70), 0.16, 0.08, boxstyle='round,pad=0.01', facecolor=col, edgecolor='#ffffff')
    ax.add_patch(box)
    ax.text(x + 0.08, 0.735, name, color='#ffffff', fontsize=7.5, fontweight='bold', ha='center')
    if x < 0.70:
        ax.annotate('', xy=(x + 0.23, 0.74), xytext=(x + 0.17, 0.74),
                    arrowprops=dict(arrowstyle='->', color='#94a3b8', lw=1.5))

# Timeline Audit for Vadama Case #104
sec_audit = patches.FancyBboxPatch((0.03, 0.06), 0.94, 0.57, boxstyle='round,pad=0.01', facecolor='#1e293b', edgecolor='#334155')
ax.add_patch(sec_audit)
ax.text(0.06, 0.58, 'AUDIT LOG: REMEDIATION CASE #104 (VADAMA EAST RESIDENTIAL SECTOR)', color='#38bdf8', fontsize=9.5, fontweight='bold')

events = [
    ('STAGE 1: VADAMA CITIZEN REPORT FILED', 'Nov 02, 08:30 AM', 'Reported uncollected, unsegregated waste drawing stray animals, mosquitoes, and rodents near residential houses. RiskEngine assigned 85 pts (CRITICAL).', 0.51, '#eab308'),
    ('STAGE 2: ADMINISTRATIVE VERIFICATION', 'Nov 02, 10:15 AM', 'Site verified. Confirmed clogged drainage culvert and rodent burrows. Tagged worker hazard [!] Broken glass and rusted metals. Status marked VERIFIED.', 0.39, '#3b82f6'),
    ('STAGE 3: SANITATION CREW DISPATCH', 'Nov 02, 01:45 PM', 'Cleanup crew dispatched equipped with protective gloves and gumboots. Waste cordoned off to prevent stray animal scattering. Status marked IN_PROGRESS.', 0.27, '#f97316'),
    ('STAGE 4: SITE RESTORATION & WATERWAY CLEARANCE', 'Nov 03, 11:00 AM', 'Unsegregated waste cleared; stagnant water drained from culvert. Communicable disease vector threat neutralized. Status marked RESOLVED.', 0.15, '#10b981')
]

for title, timestamp, desc, y, col in events:
    dot = patches.Circle((0.08, y + 0.03), 0.012, facecolor=col)
    ax.add_patch(dot)
    ax.text(0.12, y + 0.025, title, color='#f8fafc', fontsize=8, fontweight='bold')
    ax.text(0.70, y + 0.025, timestamp, color='#94a3b8', fontsize=7, style='italic')
    ax.text(0.12, y - 0.035, desc, color='#cbd5e1', fontsize=6.8, wrap=True)

plt.tight_layout()
plt.savefig('report_assets/pestrisk_fig4_status_workflow.png', dpi=300)
plt.close()

print('Vadama assets generated successfully!')
