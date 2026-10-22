#!/usr/bin/env python3
"""
PestRisk - Complete Team Master Guide & Viva Voce Defense Handbook (PDF Generator)
Produces: D:\\uhv\\PestRisk_Complete_Team_Guide_A_to_Z.pdf
A comprehensive A-to-Z handbook covering architecture, algorithms, Vadama village field study,
UHV mapping, and 25 viva voce defense questions with ready-to-speak answers.
"""

import os
import sys
from reportlab.lib.pagesizes import A4
from reportlab.lib import colors
from reportlab.lib.units import inch
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.pdfgen import canvas

# ----------------------------------------------------------------------
# Numbered Canvas for Running Headers & Footers (Page X of Y)
# ----------------------------------------------------------------------
class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_header_footer(num_pages)
            super().showPage()
        super().save()

    def draw_header_footer(self, page_count):
        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748b"))

        # Skip running header/footer on cover page (page 1)
        if self._pageNumber > 1:
            # Running Header
            self.drawString(54, 800, "PESTRISK: UHV Community Waste & Disease Vector Prioritization Platform")
            self.drawRightString(A4[0] - 54, 800, "Team Master Guide & Viva Defense Handbook")
            self.setStrokeColor(colors.HexColor("#cbd5e1"))
            self.setLineWidth(0.5)
            self.line(54, 792, A4[0] - 54, 792)

            # Running Footer
            page_text = f"Page {self._pageNumber} of {page_count}"
            self.drawString(54, 38, "Sahrdaya College of Engineering & Technology — Vadama Gram Panchayat Study")
            self.drawRightString(A4[0] - 54, 38, page_text)
            self.line(54, 48, A4[0] - 54, 48)

        self.restoreState()


def create_team_guide_pdf(output_path):
    doc = SimpleDocTemplate(
        output_path,
        pagesize=A4,
        leftMargin=54,
        rightMargin=54,
        topMargin=60,
        bottomMargin=60
    )

    styles = getSampleStyleSheet()

    # Custom Color Palette
    primary_purple = colors.HexColor("#4c1d95")  # Deep Purple
    accent_purple = colors.HexColor("#7c3aed")   # Vibrant Purple
    dark_slate = colors.HexColor("#0f172a")      # Near Black
    body_charcoal = colors.HexColor("#334155")   # Charcoal
    bg_light_purple = colors.HexColor("#f5f3ff") # Very light purple tint
    border_purple = colors.HexColor("#ddd6fe")
    alert_red = colors.HexColor("#b91c1c")
    badge_green = colors.HexColor("#047857")

    # Typography Styles
    title_style = ParagraphStyle(
        'CoverTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=24,
        leading=28,
        textColor=primary_purple,
        alignment=0,
        spaceAfter=8
    )

    subtitle_style = ParagraphStyle(
        'CoverSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=12,
        leading=16,
        textColor=colors.HexColor("#64748b"),
        alignment=0,
        spaceAfter=15
    )

    h1_style = ParagraphStyle(
        'Heading1_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=15,
        leading=19,
        textColor=primary_purple,
        spaceBefore=14,
        spaceAfter=6,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'Heading2_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=15,
        textColor=accent_purple,
        spaceBefore=10,
        spaceAfter=4,
        keepWithNext=True
    )

    h3_style = ParagraphStyle(
        'Heading3_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=9.5,
        leading=13,
        textColor=dark_slate,
        spaceBefore=7,
        spaceAfter=2,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'Body_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.8,
        leading=12.2,
        textColor=body_charcoal,
        spaceAfter=5
    )

    bullet_style = ParagraphStyle(
        'Bullet_Custom',
        parent=body_style,
        leftIndent=14,
        firstLineIndent=-10,
        spaceAfter=3
    )

    table_header_style = ParagraphStyle(
        'TableHeader',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11,
        textColor=colors.white,
        alignment=0
    )

    table_cell_style = ParagraphStyle(
        'TableCell',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8,
        leading=10.5,
        textColor=dark_slate
    )

    table_cell_bold = ParagraphStyle(
        'TableCellBold',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8,
        leading=10.5,
        textColor=primary_purple
    )

    callout_style = ParagraphStyle(
        'CalloutText',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=12,
        textColor=dark_slate
    )

    question_style = ParagraphStyle(
        'VivaQuestion',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=9.5,
        leading=13,
        textColor=primary_purple,
        spaceBefore=6,
        spaceAfter=2,
        keepWithNext=True
    )

    answer_pitch = ParagraphStyle(
        'AnswerPitch',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11.5,
        textColor=colors.HexColor("#0f766e"),
        spaceAfter=2
    )

    story = []

    # ------------------------------------------------------------------
    # COVER / TITLE BLOCK
    # ------------------------------------------------------------------
    story.append(Spacer(1, 15))
    badge_table = Table(
        [[Paragraph("<b>UNIVERSAL HUMAN VALUES (UHV) &bull; ENGINEERING DEFENSE HANDBOOK</b>", 
                    ParagraphStyle('Badge', fontName='Helvetica-Bold', fontSize=8, textColor=colors.HexColor("#581c87")))]],
        colWidths=[487],
        style=[
            ('BACKGROUND', (0,0), (-1,-1), bg_light_purple),
            ('BOX', (0,0), (-1,-1), 1, border_purple),
            ('TOPPADDING', (0,0), (-1,-1), 4),
            ('BOTTOMPADDING', (0,0), (-1,-1), 4),
            ('LEFTPADDING', (0,0), (-1,-1), 8),
        ]
    )
    story.append(badge_table)
    story.append(Spacer(1, 10))

    story.append(Paragraph("PestRisk: A-to-Z Team Master Guide &amp; Viva Voce Defense Handbook", title_style))
    story.append(Paragraph(
        "Complete technical walkthrough, mathematical risk formulations, Vadama village field study results, "
        "Universal Human Values (UHV) alignment, and 25 essential defense questions with oral soundbites.",
        subtitle_style
    ))
    story.append(HRFlowable(width="100%", thickness=1.5, color=primary_purple, spaceBefore=4, spaceAfter=14))

    # Meta Quick Info Box
    meta_data = [
        [Paragraph("<b>Project Title:</b> PestRisk — Community Hotspot Prioritization", table_cell_style),
         Paragraph("<b>Target Location:</b> Vadama Gram Panchayat (Group 5)", table_cell_style)],
        [Paragraph("<b>Framework:</b> React 19 + Spring Boot 3.5 + SQLite", table_cell_style),
         Paragraph("<b>Domain:</b> Universal Human Values &bull; Public Health &bull; Ecology", table_cell_style)],
        [Paragraph("<b>Primary Credentials:</b> Staff ID: <code>STAFF-101</code> &bull; Admin ID: <code>ADMIN-01</code>", table_cell_style),
         Paragraph("<b>Report Matching:</b> 31-Page Academic Syllabus Compliant Report", table_cell_style)]
    ]
    meta_table = Table(meta_data, colWidths=[243, 244])
    meta_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#f8fafc")),
        ('BOX', (0,0), (-1,-1), 0.75, colors.HexColor("#cbd5e1")),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor("#e2e8f0")),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
        ('LEFTPADDING', (0,0), (-1,-1), 8),
        ('RIGHTPADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(meta_table)
    story.append(Spacer(1, 14    # ------------------------------------------------------------------
    # SECTION 1: EXECUTIVE OVERVIEW & UHV PHILOSOPHY
    # ------------------------------------------------------------------
    story.append(Paragraph("1. Executive Overview &amp; UHV Philosophy", h1_style))
    story.append(Paragraph(
        "<b>What is PestRisk?</b> PestRisk is a community-driven municipal environmental platform designed to assess, "
        "mathematically score, and prioritize uncollected waste hotspots across rural panchayats before they escalate into "
        "vector-borne epidemic outbreaks (Dengue, Leptospirosis/Rat Fever, Malaria).",
        body_style
    ))
    story.append(Paragraph(
        "<b>Why is PestRisk a Universal Human Values (UHV) Project, NOT just a software app?</b><br/>"
        "Most technical apps are purely transactional (CRUD portals). PestRisk was engineered specifically to embody "
        "four fundamental UHV ethical mandates:",
        body_style
    ))
    story.append(Paragraph("&bull; <b>Harmony in Self (Sanyam &amp; Swasthya):</b> Fosters personal self-restraint (<i>Sanyam</i>) by educating citizens on waste segregation at the source to preserve bodily health (<i>Swasthya</i>) and prevent disease.", bullet_style))
    story.append(Paragraph("&bull; <b>Harmony in Society (Sarvabhaumik Vyavastha):</b> Establishes an objective, algorithm-driven triage queue that eliminates political favoritism, ward bias, and socio-economic discrimination in panchayat response.", bullet_style))
    story.append(Paragraph("&bull; <b>Harmony in Nature (Sah-Astitva / Co-Existence):</b> Directly remedies ecological disruption caused by stagnant drainage and plastic accumulation, restoring natural water flow and microbial balance.", bullet_style))
    story.append(Paragraph("&bull; <b>Frontline Worker Dignity (Human Dignity &amp; Compassion):</b> Features proactive hazard detection (broken glass, needles, sharp metals) that automatically mandates puncture-resistant PPE for sanitation workers before dispatch.", bullet_style))
    story.append(Spacer(1, 10))

    # ------------------------------------------------------------------
    # SECTION 2: VADAMA VILLAGE FIELD STUDY & 4 SUB-CASES (Page 2)
    # ------------------------------------------------------------------
    story.append(PageBreak())
    story.append(Paragraph("2. Vadama Village Problem Statement &amp; 4 Documented Sub-Cases", h1_style))
    story.append(Paragraph(
        "<b>Assigned Group 5 Scope:</b> <i>\"Attracts stray animals, mosquitoes, and rodents, raising the risk of communicable disease (Vadama Village)\"</i>. "
        "Uncollected and unsegregated waste left in the open draws stray dogs, mosquitoes, and rodents into residential areas, "
        "compounding the public health burden already created by stagnant irrigation canals.",
        body_style
    ))
    story.append(Spacer(1, 4))

    # Sub-cases table
    subcases_data = [
        [Paragraph("<b>Sub-Case</b>", table_header_style), 
         Paragraph("<b>Location &amp; Waste Profile</b>", table_header_style), 
         Paragraph("<b>Vulnerability Factors</b>", table_header_style), 
         Paragraph("<b>Risk Score &amp; Tier</b>", table_header_style), 
         Paragraph("<b>UHV &amp; Health Impact</b>", table_header_style)],
        
        [Paragraph("<b>Case 1: Canal Culvert</b>", table_cell_bold),
         Paragraph("Vadama Canal Road; 400 kg mixed plastic, weed mass &amp; stagnant water.", table_cell_style),
         Paragraph("Blocked culvert; &lt;25m from main irrigation canal.", table_cell_style),
         Paragraph("<b>Score: 75</b><br/><font color='#dc2626'>HIGH</font>", table_cell_style),
         Paragraph("Mosquito breeding (Aedes aegypti); Dengue outbreak prevention.", table_cell_style)],

        [Paragraph("<b>Case 2: Open Market</b>", table_cell_bold),
         Paragraph("Vadama Weekly Market Junction; 300 kg decomposing meat/fish refuse.", table_cell_style),
         Paragraph("Commercial junction; &lt;60m from residential houses.", table_cell_style),
         Paragraph("<b>Score: 55</b><br/><font color='#d97706'>MODERATE</font>", table_cell_style),
         Paragraph("Aggressive stray dog packs; Rabies risk &amp; child transit safety.", table_cell_style)],

        [Paragraph("<b>Case 3: Rodent Canal Buffer</b>", table_cell_bold),
         Paragraph("Vadama Canal Bund (Ward 3); 650 kg mixed organic and food packaging.", table_cell_style),
         Paragraph("Adjacent to drinking water source; canal embankment.", table_cell_style),
         Paragraph("<b>Score: 85</b><br/><font color='#b91c1c'>CRITICAL</font>", table_cell_style),
         Paragraph("Rattus norvegicus burrows; Leptospirosis (Rat Fever) waterborne threat.", table_cell_style)],

        [Paragraph("<b>Case 4: Vacant Mixed Plot</b>", table_cell_bold),
         Paragraph("Central Vadama Junction; 500 kg mixed household trash + broken glass &amp; metal.", table_cell_style),
         Paragraph("&lt;40m from primary school footpath; residential zone.", table_cell_style),
         Paragraph("<b>Score: 65</b><br/><font color='#dc2626'>HIGH</font><br/><b>PPE ALERT</b>", table_cell_style),
         Paragraph("Worker cut injuries avoided; puncture-resistant gloves issued.", table_cell_style)]
    ]

    sc_table = Table(subcases_data, colWidths=[80, 115, 100, 75, 117])
    sc_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), primary_purple),
        ('BOX', (0,0), (-1,-1), 1, border_purple),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor("#e2e8f0")),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor("#f8fafc")]),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('LEFTPADDING', (0,0), (-1,-1), 5),
        ('RIGHTPADDING', (0,0), (-1,-1), 5),
    ]))
    story.append(sc_table)
    story.append(Spacer(1, 10))

    # ------------------------------------------------------------------
    # SECTION 3: MATHEMATICAL RISK FORMULA & ALGORITHM
    # ------------------------------------------------------------------
    story.append(Paragraph("3. Mathematical Risk Engine &amp; Triage Algorithm", h1_style))
    story.append(Paragraph(
        "The risk engine assigns an objective 0–100 integer score based on vector biology and environmental epidemiology. "
        "It eliminates subjective human guesswork:",
        body_style
    ))

    # Formula Callout Box
    formula_text = (
        "<b>Mathematical Risk Formulation:</b><br/>"
        "&nbsp;&nbsp;&nbsp;&nbsp;<b>Risk Score (S) = &Sigma; (Waste Hazard Weights) + &Sigma; (Environmental Vulnerabilities)</b><br/>"
        "&nbsp;&nbsp;&nbsp;&nbsp;<b>S = [ 10&middot;W_stag + 15&middot;W_org + 12&middot;W_carc + 10&middot;W_med ] + [ 20&middot;V_school + 15&middot;V_drain + 10&middot;V_water + 10&middot;V_res ]</b><br/>"
        "&nbsp;&nbsp;&nbsp;&nbsp;<i>Score clamped to [0, 100]. Physical hazards (broken glass, jagged metal) trigger the Worker PPE alert flag.</i>"
    )
    formula_table = Table([[Paragraph(formula_text, callout_style)]], colWidths=[487])
    formula_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), bg_light_purple),
        ('BOX', (0,0), (-1,-1), 1, border_purple),
        ('TOPPADDING', (0,0), (-1,-1), 5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 5),
        ('LEFTPADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(formula_table)
    story.append(Spacer(1, 6))

    # Priority Classification Table
    class_data = [
        [Paragraph("<b>Score Range</b>", table_header_style), 
         Paragraph("<b>Priority Tier</b>", table_header_style), 
         Paragraph("<b>Vector Profile &amp; Risk</b>", table_header_style), 
         Paragraph("<b>Mandated SLA Action</b>", table_header_style)],
        
        [Paragraph("<b>80 &ndash; 100</b>", table_cell_bold),
         Paragraph("<font color='#b91c1c'><b>CRITICAL</b></font>", table_cell_style),
         Paragraph("Immediate vector breeding (rats/mosquitoes) near water bodies or clinics.", table_cell_style),
         Paragraph("Dispatch emergency sanitation crew within <b>12 hours</b>.", table_cell_style)],

        [Paragraph("<b>60 &ndash; 79</b>", table_cell_bold),
         Paragraph("<font color='#dc2626'><b>HIGH</b></font>", table_cell_style),
         Paragraph("Stagnant water or food waste near schools or blocked drainage lines.", table_cell_style),
         Paragraph("Remediate and clear within <b>24 hours</b>.", table_cell_style)],

        [Paragraph("<b>40 &ndash; 59</b>", table_cell_bold),
         Paragraph("<font color='#d97706'><b>MODERATE</b></font>", table_cell_style),
         Paragraph("Organic decomposing waste; dog/stray animal scavenging in markets.", table_cell_style),
         Paragraph("Schedule within <b>48 hours</b> standard cycle.", table_cell_style)],

        [Paragraph("<b>0 &ndash; 39</b>", table_cell_bold),
         Paragraph("<font color='#047857'><b>LOW</b></font>", table_cell_style),
         Paragraph("Inert non-organic dry waste, roadside dry litter with no standing water.", table_cell_style),
         Paragraph("Routine weekly panchayat sweep.", table_cell_style)]
    ]
    cl_table = Table(class_data, colWidths=[70, 75, 192, 150])
    cl_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), primary_purple),
        ('BOX', (0,0), (-1,-1), 1, border_purple),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor("#e2e8f0")),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor("#f8fafc")]),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('LEFTPADDING', (0,0), (-1,-1), 5),
    ]))
    story.append(cl_table)
    story.append(Spacer(1, 10))

    # ------------------------------------------------------------------
    # SECTION 4 & 5 & 6: SYSTEM ENGINEERING & POs (Page 3)
    # ------------------------------------------------------------------
    story.append(PageBreak())
    story.append(Paragraph("4. Technical Architecture &amp; System Engineering", h1_style))
    story.append(Paragraph(
        "PestRisk uses an enterprise-grade decoupled microservice architecture ensuring 100% offline local capability "
        "and seamless cloud deployment:",
        body_style
    ))

    arch_data = [
        [Paragraph("<b>Layer</b>", table_header_style), 
         Paragraph("<b>Technology</b>", table_header_style), 
         Paragraph("<b>Engineering Responsibility</b>", table_header_style)],

        [Paragraph("<b>Frontend SPA</b>", table_cell_bold),
         Paragraph("React 19 &bull; Vite 8 &bull; CSS Tokens &bull; Leaflet", table_cell_style),
         Paragraph("Zero-dependency bundle; dynamic risk preview calculator; Leaflet OpenStreetMap pinning.", table_cell_style)],

        [Paragraph("<b>Backend API</b>", table_cell_bold),
         Paragraph("Java 21 &bull; Spring Boot 3.5 &bull; Spring Security 6", table_cell_style),
         Paragraph("REST microservice; stateless JWT auth (HMAC-SHA256); Bean validation; exception handling.", table_cell_style)],

        [Paragraph("<b>Persistence</b>", table_cell_bold),
         Paragraph("Embedded SQLite JDBC &bull; Hibernate 6 Dialects", table_cell_style),
         Paragraph("Single-file database (<code>data/pestrisk.db</code>); zero server setup; persistent schema across reboots.", table_cell_style)],

        [Paragraph("<b>Cloud / Hosting</b>", table_cell_bold),
         Paragraph("Vercel (Frontend) &bull; Render / Docker (Backend API)", table_cell_style),
         Paragraph("Vercel global CDN with SPA rewrites (<code>vercel.json</code>); Render multi-stage Docker container.", table_cell_style)]
    ]
    arch_table = Table(arch_data, colWidths=[90, 150, 247])
    arch_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), primary_purple),
        ('BOX', (0,0), (-1,-1), 1, border_purple),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor("#e2e8f0")),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor("#f8fafc")]),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('LEFTPADDING', (0,0), (-1,-1), 5),
    ]))
    story.append(arch_table)
    story.append(Spacer(1, 8))

    story.append(Paragraph("5. User Workflows &amp; Feature Portals", h1_style))
    story.append(Paragraph(
        "<b>&bull; Public Citizen Portal:</b> Anonymous reporting without login; live risk score preview calculator; location coordinates.<br/>"
        "<b>&bull; Community Alerts Feed:</b> Privacy-preserving public board; resident names/phones hidden to avoid village feuds.<br/>"
        "<b>&bull; Staff &amp; Admin Dashboard:</b> Auto-triage priority queue; worker PPE alerts; lifecycle: <code>REPORTED &rarr; VERIFIED &rarr; IN_PROGRESS &rarr; RESOLVED</code>.<br/>"
        "<b>&bull; ⚡ 1-Click Fast Demo Login:</b> Click <code>[👷 Staff (STAFF-101)]</code> or <code>[🛡️ Admin (ADMIN-01)]</code> to log in instantly without typing.",
        body_style
    ))
    story.append(Spacer(1, 8))

    story.append(Paragraph("6. Programme Outcomes (PO1 to PO11) Compliance Table", h1_style))
    po_data = [
        [Paragraph("<b>PO</b>", table_header_style), Paragraph("<b>Outcome Title</b>", table_header_style), Paragraph("<b>Specific Project Implementation in PestRisk</b>", table_header_style)],
        [Paragraph("<b>PO1</b>", table_cell_bold), Paragraph("Engineering Knowledge", table_cell_style), Paragraph("Applied mathematical risk algorithms, relational normalization, and REST microservices.", table_cell_style)],
        [Paragraph("<b>PO2</b>", table_cell_bold), Paragraph("Problem Analysis", table_cell_style), Paragraph("Empirical field study of Vadama canal culverts, fish market refuse, and rodent burrows.", table_cell_style)],
        [Paragraph("<b>PO3</b>", table_cell_bold), Paragraph("Design / Development", table_cell_style), Paragraph("Engineered multi-hazard risk engine and privacy-preserving notification board.", table_cell_style)],
        [Paragraph("<b>PO4</b>", table_cell_bold), Paragraph("Complex Investigations", table_cell_style), Paragraph("Correlated waste composition with vector breeding cycles (mosquitoes, rodents, strays).", table_cell_style)],
        [Paragraph("<b>PO5</b>", table_cell_bold), Paragraph("Modern Tool Usage", table_cell_style), Paragraph("Java 21, Spring Boot 3.5, React 19, Leaflet GIS, Docker, Vite, BCrypt encryption.", table_cell_style)],
        [Paragraph("<b>PO6</b>", table_cell_bold), Paragraph("Engineer &amp; Society", table_cell_style), Paragraph("Directly tackles communicable disease prevention (Dengue, Leptospirosis) in rural panchayats.", table_cell_style)],
        [Paragraph("<b>PO7</b>", table_cell_bold), Paragraph("Environment &amp; Sustainability", table_cell_style), Paragraph("Restores water quality in irrigation canals and prevents toxic leachate seepage.", table_cell_style)],
        [Paragraph("<b>PO8</b>", table_cell_bold), Paragraph("Ethics &amp; UHV", table_cell_style), Paragraph("Upholds worker dignity via PPE puncture alerts; fair algorithmic triage without political bias.", table_cell_style)],
        [Paragraph("<b>PO9</b>", table_cell_bold), Paragraph("Individual &amp; Team Work", table_cell_style), Paragraph("Collaborative Git workflows, shared documentation, modular decoupled architecture.", table_cell_style)],
        [Paragraph("<b>PO10</b>", table_cell_bold), Paragraph("Communication", table_cell_style), Paragraph("Clear public community feed, 31-page academic report, multi-stakeholder dashboard.", table_cell_style)],
        [Paragraph("<b>PO11</b>", table_cell_bold), Paragraph("Life-Long Learning", table_cell_style), Paragraph("Adapts to evolving environmental guidelines, Swachh Bharat directives, and vector surges.", table_cell_style)]
    ]

    po_table = Table(po_data, colWidths=[38, 122, 327])
    po_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), primary_purple),
        ('BOX', (0,0), (-1,-1), 1, border_purple),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor("#e2e8f0")),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor("#f8fafc")]),
        ('TOPPADDING', (0,0), (-1,-1), 2),
        ('BOTTOMPADDING', (0,0), (-1,-1), 2),
        ('LEFTPADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(po_table)
    story.append(Spacer(1, 10))

    # ------------------------------------------------------------------
    # SECTION 7: MASTER VIVA VOCE Q&A CHEAT SHEET (Pages 4, 5, 6)
    # ------------------------------------------------------------------
    story.append(PageBreak())  # Page 4
    story.append(Paragraph("7. Master Viva Voce Q&amp;A Defense Cheat Sheet (Top 25 Questions)", h1_style))
    story.append(Paragraph(
        "Use this section to prepare for external examiners, college viva voce panels, and faculty reviews. "
        "Each question includes a <b>Quick Pitch (15-second oral answer)</b> followed by the <b>Technical / Ethical Rationale</b>.",
        body_style
    ))
    story.append(Spacer(1, 4))pend(Spacer(1, 6))

    qa_list = [
        # Category A: Architecture & Stack
        ("Q1: Why did you choose Spring Boot instead of Express.js or Django?",
         "Quick Pitch: \"We chose Spring Boot 3 with Java 21 for enterprise type-safety, robust Spring Security with stateless JWT, and built-in JPA transactions that ensure zero data corruption in municipal audit trails.\"",
         "Full Rationale: Spring Boot's dependency injection and mature security ecosystem allow seamless role-based access control (RBAC). It also integrates effortlessly with SQLite or cloud PostgreSQL via Hibernate dialects with zero code rewrites."),

        ("Q2: Why use an embedded SQLite database instead of MySQL or MongoDB?",
         "Quick Pitch: \"SQLite provides a zero-maintenance, self-contained relational database that runs 100% offline on any college or panchayat machine without installing extra server software.\"",
         "Full Rationale: In rural panchayats, server maintenance is difficult. SQLite stores data in a single file (data/pestrisk.db), supporting full ACID transactions. If scaled, Spring Data JPA allows switching to PostgreSQL by simply updating the DB_URL property."),

        ("Q3: How does JWT authentication work in your application?",
         "Quick Pitch: \"When staff members sign in, Spring Security verifies their BCrypt password hash and generates an encrypted HMAC-SHA256 JWT token. The browser sends this token in the HTTP Authorization header for subsequent API calls.\"",
         "Full Rationale: JWT is stateless, meaning the server doesn't store session state in memory. This improves performance and allows the backend to be hosted on serverless or cloud container environments like Render without session synchronization issues."),

        ("Q4: How does the frontend communicate with the backend?",
         "Quick Pitch: \"The React 19 frontend uses a centralized API client module (pestRiskApi.js) that makes asynchronous HTTP fetch requests to Spring Boot REST endpoints, automatically passing Bearer tokens.\"",
         "Full Rationale: The API handles cross-origin requests (CORS), parses Spring Boot error payloads, and manages localStorage session keys (pestrisk_token and pestrisk_user)."),

        ("Q5: What happens if two citizens submit duplicate reports for the same spot?",
         "Quick Pitch: \"The system stores reports with GPS latitude/longitude. In the Staff Dashboard, reports in the same ward or nearby coordinates can be cross-verified and linked by the inspector before dispatching crews.\"",
         "Full Rationale: This avoids duplicated municipal effort while keeping citizen submissions open and welcoming."),

        # Category B: Mathematical Algorithm & Risk Engine
        ("Q6: How does your risk formula prevent human bias?",
         "Quick Pitch: \"The algorithm calculates scores strictly based on objective physical inputs (waste category multipliers and proximity to schools, waterbodies, and clinics) rather than who reported it.\"",
         "Full Rationale: In traditional panchayat operations, influential citizens often get their complaints resolved first. PestRisk enforces an algorithmic priority queue where a critical mosquito breeding site in a poor ward always takes precedence over dry litter in a VIP ward."),

        ("Q7: Why does stagnant water get a multiplier of 10 and organic waste 15?",
         "Quick Pitch: \"Organic decomposing food waste immediately attracts stray dogs and rodents (rabies and leptospirosis), while stagnant water serves as the direct incubator for Aedes aegypti mosquitoes carrying Dengue virus.\"",
         "Full Rationale: These weights were calibrated using public health epidemiological guidelines for rural Kerala, reflecting local vector dynamics."),

        ("Q8: Why is proximity to schools weighted highest at 20 points?",
         "Quick Pitch: \"Children are the most vulnerable demographic to stray dog attacks, dengue fever, and infectious pathogens. Proximity to an educational institution poses the highest social risk.\"",
         "Full Rationale: School zones experience heavy foot traffic of minors twice daily, making immediate vector abatement a critical community safeguard."),

        ("Q9: What are the four priority classification tiers?",
         "Quick Pitch: \"CRITICAL (80–100, 12h SLA), HIGH (60–79, 24h SLA), MODERATE (40–59, 48h SLA), and LOW (0–39, routine weekly cycle).\"",
         "Full Rationale: These tiers provide clear operational response targets for panchayat sanitation supervisors and prevent resource exhaustion."),

        ("Q10: How does the worker PPE safety alert work?",
         "Quick Pitch: \"When a report flags Broken Glass or Sharp Metals, the system sets hasPuncturesHazard = true, which triggers a high-visibility hazard badge in the dashboard alerting supervisors to issue puncture-resistant gloves.\"",
         "Full Rationale: This embodies the UHV principle of worker dignity, ensuring that sanitation crews are not subjected to preventable tetanus or physical laceration injuries."),

        # Category C: UHV Philosophy & Ethics
        ("Q11: How does this project reflect the UHV concept of 'Harmony in Nature' (Sah-Astitva)?",
         "Quick Pitch: \"By clearing clogged irrigation canals and organic dumps, PestRisk restores natural water flow and microbial balance without dumping pollutants into downstream rivers.\"",
         "Full Rationale: Human activity often disrupts ecological cycles by generating non-biodegradable blockages. PestRisk acts as a restorative mechanism to re-establish coexistence."),

        ("Q12: How does the platform embody 'Sanyam' (Self-Restraint) and 'Swasthya' (Health)?",
         "Quick Pitch: \"Sanyam is cultivated through citizen awareness of waste segregation at source, which directly protects community health (Swasthya) by preventing vector reproduction.\"",
         "Full Rationale: Health is not merely the absence of disease, but a state of bodily harmony maintained through responsible interaction with one's physical surroundings."),

        ("Q13: Why is citizen anonymity preserved in the Public Community Feed?",
         "Quick Pitch: \"To protect citizen privacy and prevent village feuds or social retribution from neighbors whose unsegregated waste was reported.\"",
         "Full Rationale: In close-knit panchayat communities, publicizing names causes friction. PestRisk only displays generalized ward names and risk tiers, ensuring peace and social harmony."),

        ("Q14: How does the system uphold Worker Dignity?",
         "Quick Pitch: \"Sanitation workers are often treated as invisible manual laborers. PestRisk mandates advance hazard warnings, ensuring workers receive proper protective gear and recognition.\"",
         "Full Rationale: Universal Human Values demand compassion and respect for all human roles. Sanitation crews perform essential ecological work and deserve maximum safety."),

        ("Q15: Can a citizen report waste without logging in? Why?",
         "Quick Pitch: \"Yes, public reporting is 100% barrier-free. Requiring accounts or phone OTPs discourages poor or elderly residents from participating in community sanitation.\"",
         "Full Rationale: Accessibility is an ethical imperative. If reporting is complicated, critical disease hotspots in vulnerable neighborhoods go unnoticed."),

        # Category D: Vadama Village Field Study
        ("Q16: Why was Vadama Village chosen instead of Kodakara?",
         "Quick Pitch: \"Group 5 was specifically assigned Vadama Gram Panchayat, focusing on communicable disease risks from stray animals, mosquitoes, and rodents near irrigation canals.\"",
         "Full Rationale: Kodakara is the parent college taluk location, but the specific empirical field investigation was documented across Vadama's canal bunds and junctions."),

        ("Q17: What was unique about Vadama Sub-Case 1 (Canal Culvert)?",
         "Quick Pitch: \"It documented 400 kg of plastic trash and weeds blocking a main culvert along Vadama Canal Road, creating a stagnant pool with a High Risk score of 75.\"",
         "Full Rationale: Stagnant storm runoff provides the ideal habitat for Aedes mosquito larvae, threatening nearby residential clusters with Dengue outbreaks."),

        ("Q18: What was documented in Sub-Case 2 (Weekly Market)?",
         "Quick Pitch: \"Over 300 kg of decomposing fish and butchery waste at Vadama Market Junction, attracting aggressive packs of 8–10 stray dogs (Moderate Risk score of 55).\"",
         "Full Rationale: Stray dog packs scavenging rotting carcasses create immediate physical attack hazards and elevate Rabies transmission risks for commuters."),

        ("Q19: Why did Sub-Case 3 receive a CRITICAL score of 85?",
         "Quick Pitch: \"It involved 650 kg of food refuse along the canal bank directly bordering drinking water wells, showing active rodent burrows and high Leptospirosis threat.\"",
         "Full Rationale: Rat urine containing Leptospira bacteria easily washes into water sources during seasonal rain, causing fatal Weil's disease in humans."),

        ("Q20: What did Sub-Case 4 demonstrate regarding frontline safety?",
         "Quick Pitch: \"A central plot with 500 kg of mixed trash containing shattered glass and rusted metal near a school footpath, triggering the automatic Worker PPE Alert.\"",
         "Full Rationale: This case proved the practical necessity of the PPE warning system, protecting workers from severe cuts and tetanus."),

        # Category E: Security, Testing & Cloud Deployment
        ("Q21: How do you protect passwords in the database?",
         "Quick Pitch: \"Passwords are hashed using BCrypt with a work factor of 10 and unique salt. Plaintext passwords are never stored in the database or logged.\"",
         "Full Rationale: Even if an unauthorized party accesses data/pestrisk.db, the hashes cannot be reversed due to BCrypt's computationally expensive key-derivation function."),

        ("Q22: What tests were conducted to ensure system reliability?",
         "Quick Pitch: \"The backend includes 58 automated unit and integration tests covering risk calculations, JWT authorization, lifecycle state transitions, and RBAC security.\"",
         "Full Rationale: The test suite runs against an in-memory H2 database using MockMvc, guaranteeing that regressions are caught before deployment."),

        ("Q23: How is PestRisk hosted on cloud platforms?",
         "Quick Pitch: \"The React frontend is hosted on Vercel with automatic Vite builds and edge routing (vercel.json), while the Spring Boot backend runs on Render via a multi-stage Docker container.\"",
         "Full Rationale: The backend Dockerfile uses Eclipse Temurin JDK 21 and bundles SQLite. Vercel connects to the Render URL using the VITE_API_BASE_URL environment variable."),

        ("Q24: What are the credentials for the demonstration?",
         "Quick Pitch: \"Sanitation Staff ID is STAFF-101 (password: StaffPassword123), and Admin ID is ADMIN-01 (password: AdminPassword123). Both can be logged into with 1 click using the Fast Demo buttons.\"",
         "Full Rationale: The 1-click Fast Login buttons auto-populate credentials and sign in instantly, preventing typing mistakes during live examiner demonstrations."),

        ("Q25: What is the Hotspot Lifecycle State Machine?",
         "Quick Pitch: \"REPORTED &longrightarrow; VERIFIED &longrightarrow; IN_PROGRESS &longrightarrow; RESOLVED. Each transition requires staff authentication and logs inspector remarks into the permanent audit trail.\"",
         "Full Rationale: This guarantees accountability. A spot cannot jump from Reported to Resolved without being verified by staff, ensuring actual physical cleanup occurred.")
    ]

    for q, pitch, full in qa_list:
        qa_flowables = [
            Paragraph(f"<b>{q}</b>", question_style),
            Paragraph(f"🗣️ <b>Quick Pitch:</b> {pitch}", answer_pitch),
            Paragraph(f"🔍 <b>Detailed Rationale:</b> {full}", body_style),
            Spacer(1, 4)
        ]
        story.append(KeepTogether(qa_flowables))

    story.append(Spacer(1, 10))

    # ------------------------------------------------------------------
    # SECTION 8: QUICK RUN REFERENCE & CREDENTIALS CHEAT SHEET
    # ------------------------------------------------------------------
    story.append(PageBreak())
    story.append(Paragraph("8. Quick Execution Cheat Sheet &amp; Credentials Reference", h1_style))
    story.append(Paragraph(
        "Keep this page handy when presenting or running the application on your computer:",
        body_style
    ))

    # Running instructions box
    run_guide_text = (
        "<b>How to Launch Everything with 1 Click:</b><br/>"
        "1. Open File Explorer to <code>D:\\uhv</code> and double-click <b><code>run.bat</code></b>.<br/>"
        "2. It automatically starts the Spring Boot backend on <b>port 8080</b> and the Vite React frontend on <b>port 5173</b>.<br/>"
        "3. Open your browser and navigate to: <b>http://localhost:5173</b>"
    )
    run_table = Table([[Paragraph(run_guide_text, callout_style)]], colWidths=[487])
    run_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), bg_light_purple),
        ('BOX', (0,0), (-1,-1), 1, border_purple),
        ('TOPPADDING', (0,0), (-1,-1), 6),
        ('BOTTOMPADDING', (0,0), (-1,-1), 6),
        ('LEFTPADDING', (0,0), (-1,-1), 10),
    ]))
    story.append(run_table)
    story.append(Spacer(1, 8))

    # Credentials Reference Table
    cred_data = [
        [Paragraph("<b>Portal View</b>", table_header_style), 
         Paragraph("<b>URL Anchor</b>", table_header_style), 
         Paragraph("<b>Authorized ID</b>", table_header_style), 
         Paragraph("<b>Password</b>", table_header_style), 
         Paragraph("<b>Permissions &amp; Capabilities</b>", table_header_style)],

        [Paragraph("<b>Public Map</b>", table_cell_bold),
         Paragraph("<code>#map</code>", table_cell_style),
         Paragraph("<i>None (Public)</i>", table_cell_style),
         Paragraph("<i>None</i>", table_cell_style),
         Paragraph("Report waste; calculate risk preview; view pins.", table_cell_style)],

        [Paragraph("<b>Alerts Feed</b>", table_cell_bold),
         Paragraph("<code>#alerts</code>", table_cell_style),
         Paragraph("<i>None (Public)</i>", table_cell_style),
         Paragraph("<i>None</i>", table_cell_style),
         Paragraph("Privacy-preserving public notification board.", table_cell_style)],

        [Paragraph("<b>Staff Portal</b>", table_cell_bold),
         Paragraph("<code>#staff</code>", table_cell_style),
         Paragraph("<code>STAFF-101</code>", table_cell_bold),
         Paragraph("<code>StaffPassword123</code>", table_cell_style),
         Paragraph("Priority queue; PPE hazard alert; inspect &amp; resolve.", table_cell_style)],

        [Paragraph("<b>Admin Portal</b>", table_cell_bold),
         Paragraph("<code>#staff</code>", table_cell_style),
         Paragraph("<code>ADMIN-01</code>", table_cell_bold),
         Paragraph("<code>AdminPassword123</code>", table_cell_style),
         Paragraph("Full staff account management + triage queue.", table_cell_style)]
    ]
    cred_table = Table(cred_data, colWidths=[70, 60, 75, 102, 180])
    cred_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), primary_purple),
        ('BOX', (0,0), (-1,-1), 1, border_purple),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor("#e2e8f0")),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor("#f8fafc")]),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
        ('LEFTPADDING', (0,0), (-1,-1), 5),
    ]))
    story.append(cred_table)
    story.append(Spacer(1, 10))

    # Emergency Exam-Day Troubleshooting Tips
    trouble_text = (
        "<b>Exam-Day Emergency Troubleshooting Checklist:</b><br/>"
        "&bull; <b>If login says 'Invalid username or password':</b> Clear the password input! Your browser may have autofilled an old password. Use the 1-Click Fast Demo button or click the 👁️ toggle to ensure it is <code>StaffPassword123</code>.<br/>"
        "&bull; <b>If frontend says 'Failed to fetch reports':</b> The backend is not running. Double-click <code>run.bat</code> or run <code>.\\start_backend.ps1</code> in PowerShell.<br/>"
        "&bull; <b>If port 8080 is blocked:</b> Run in PowerShell: <code>Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process -Force</code><br/>"
        "&bull; <b>To re-seed sample demo reports:</b> Open PowerShell and run: <code>.\\seed_demo_data.ps1</code>"
    )
    trouble_table = Table([[Paragraph(trouble_text, callout_style)]], colWidths=[487])
    trouble_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#fef2f2")),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor("#fecaca")),
        ('TOPPADDING', (0,0), (-1,-1), 6),
        ('BOTTOMPADDING', (0,0), (-1,-1), 6),
        ('LEFTPADDING', (0,0), (-1,-1), 10),
    ]))
    story.append(trouble_table)

    # Build PDF
    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"[+] Successfully generated Team Master Guide PDF: {output_path}")

if __name__ == '__main__':
    out_pdf = r"D:\uhv\PestRisk_Complete_Team_Guide_A_to_Z.pdf"
    create_team_guide_pdf(out_pdf)
