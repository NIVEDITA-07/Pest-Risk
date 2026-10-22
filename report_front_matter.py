# -*- coding: utf-8 -*-
"""
report_front_matter.py
Constructs Front Matter for PestRisk UHV Project Report (Streamlined for 30-35 pages):
- Section 0: Title / Cover Page (Vadama Village - Group 5)
- Section 1: Declaration & Certificate (Guide: Ms. Merin Michael T, HOD: Dr. Sreeraj R)
- Section 2: Acknowledgement, Institute Vision/Mission, Department Vision/Mission,
             PEOs, PSOs, POs (Course Outcomes box REMOVED),
             Abstract (1 page), Table of Contents (1 page), List of Tables & Abbreviations.
"""

import os
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from build_pestrisk_full_report import (
    set_cell_margins, set_table_borders, set_cell_shading, add_p, add_bullet, 
    add_header_text, add_footer_page_number
)

def build_cover_page(doc):
    """Section 0: Cover / Title Page matching template layout."""
    s0 = doc.sections[0]
    s0.top_margin = Inches(1.0)
    s0.bottom_margin = Inches(1.0)
    s0.left_margin = Inches(1.25)
    s0.right_margin = Inches(1.0)

    # Title
    add_p(doc, "PESTRISK", font_size=22, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=16, space_after=4)
    add_p(doc, "VILLAGE WASTE HOTSPOT PRIORITIZATION PLATFORM", font_size=12, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=4)
    add_p(doc, "A Case Study on Communicable Disease Vector Risk in Vadama Village (Group 5)", font_size=10.5, bold=False, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=12)
    
    add_p(doc, "A PROJECT REPORT", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=4, space_after=4)
    add_p(doc, "submitted by", font_size=11, bold=False, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=6)

    # Submitters
    add_p(doc, "NIVEDITHA V S (SHR25CS197)", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    add_p(doc, "SHARON PAULET (SHR24CS191)", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    add_p(doc, "SHEFIN HASHIR (SHR24CS203)", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    add_p(doc, "VISMAYA E A (SHR24CS249)", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=10)

    add_p(doc, "to", font_size=11, bold=False, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    add_p(doc, "the APJ Abdul Kalam Technological University", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    add_p(doc, "in partial fulfillment of the requirements for the award of the Degree of", font_size=11, bold=False, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    add_p(doc, "Bachelor of Technology", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    add_p(doc, "in", font_size=11, bold=False, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    add_p(doc, "Computer Science and Engineering", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=12)

    # Logo
    p_img = doc.add_paragraph()
    p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_img.paragraph_format.space_before = Pt(2)
    p_img.paragraph_format.space_after = Pt(10)
    logo_path = os.path.join("extracted_images", "image1.png")
    if os.path.exists(logo_path):
        run_img = p_img.add_run()
        run_img.add_picture(logo_path, width=Inches(1.4), height=Inches(1.5))

    add_p(doc, "Department of Computer Science Engineering", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    add_p(doc, "SAHRDAYA COLLEGE OF ENGINEERING AND TECHNOLOGY", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    add_p(doc, "KODAKARA, THRISSUR - 680684", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=6)
    add_p(doc, "November 2026", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=0)

def build_declaration_and_certificate(doc):
    """Section 1: Declaration and Certificate."""
    s1 = doc.add_section(docx.enum.section.WD_SECTION.NEW_PAGE)
    s1.top_margin = Inches(1.0)
    s1.bottom_margin = Inches(1.0)
    s1.left_margin = Inches(1.25)
    s1.right_margin = Inches(1.0)
    s1.header.is_linked_to_previous = False
    s1.footer.is_linked_to_previous = False

    # DECLARATION
    add_p(doc, "DECLARATION", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=16, space_after=16)
    
    decl_text = (
        "We undersigned, hereby declare that the project report entitled “PestRisk — Village Waste Hotspot "
        "Prioritization Platform (A Case Study on Communicable Disease Vector Risk in Vadama Village — Group 5)”, "
        "submitted for partial fulfillment of the requirements for the award of degree of Bachelor of Technology in "
        "Computer Science and Engineering under the Universal Human Values (UHV II), Life Skills and Community Work "
        "course of the APJ Abdul Kalam Technological University, Kerala, is a bonafide work done by us under the "
        "guidance of Ms. Merin Michael T, Assistant Professor, Department of Computer Science & Engineering. This submission "
        "represents our ideas in our own words and where ideas or words of others have been included; we have adequately and "
        "accurately cited and referenced the original sources. We also declare that we have adhered to the ethics of "
        "academic honesty and integrity, universal human values, and community welfare, and have not misrepresented or "
        "fabricated any data, fact, or source in our submission. We understand that any violation of the above will be a "
        "cause for disciplinary action by the institute and/or the University. This report has not been previously formed "
        "the basis for the award of any degree, diploma, or similar title of any other University."
    )
    add_p(doc, decl_text, font_size=11.5, bold=False, align=WD_ALIGN_PARAGRAPH.JUSTIFY, space_before=4, space_after=20)

    # Signature Table 0
    t0 = doc.add_table(rows=1, cols=2)
    t0.alignment = WD_TABLE_ALIGNMENT.CENTER
    cell_l, cell_r = t0.rows[0].cells
    cell_l.width = Inches(3.2)
    cell_r.width = Inches(3.2)
    set_cell_margins(cell_l, 40, 40, 60, 60)
    set_cell_margins(cell_r, 40, 40, 60, 60)

    p_l = cell_l.paragraphs[0]
    r_l = p_l.add_run("Place: Kodakara\nDate: 22-10-2026")
    r_l.font.name = "Times New Roman"
    r_l.font.size = Pt(11)

    p_r = cell_r.paragraphs[0]
    p_r.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    names_text = (
        "NIVEDITHA V S (SHR25CS197)\n"
        "SHARON PAULET (SHR24CS191)\n"
        "SHEFIN HASHIR (SHR24CS203)\n"
        "VISMAYA E A (SHR24CS249)"
    )
    r_r = p_r.add_run(names_text)
    r_r.font.name = "Times New Roman"
    r_r.font.size = Pt(10)
    r_r.bold = True

    doc.add_page_break()

    # CERTIFICATE
    add_p(doc, "DEPARTMENT OF COMPUTER SCIENCE ENGINEERING", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=6, space_after=2)
    add_p(doc, "SAHRDAYA COLLEGE OF ENGINEERING AND TECHNOLOGY", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    add_p(doc, "KODAKARA, THRISSUR - 680684", font_size=11, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=8)

    p_cimg = doc.add_paragraph()
    p_cimg.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_cimg.paragraph_format.space_before = Pt(2)
    p_cimg.paragraph_format.space_after = Pt(8)
    cert_logo_path = os.path.join("extracted_images", "image2.png")
    if os.path.exists(cert_logo_path):
        run_cimg = p_cimg.add_run()
        run_cimg.add_picture(cert_logo_path, width=Inches(1.2), height=Inches(1.3))

    add_p(doc, "CERTIFICATE", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=4, space_after=12)

    cert_text = (
        "This is to certify that the project report entitled “PestRisk — Village Waste Hotspot "
        "Prioritization Platform (A Case Study on Communicable Disease Vector Risk in Vadama Village — Group 5)”, "
        "is a bonafide record of the work carried out by NIVEDITHA V S (SHR25CS197), SHARON PAULET (SHR24CS191), "
        "SHEFIN HASHIR (SHR24CS203), and VISMAYA E A (SHR24CS249) in partial fulfillment of the requirements for the "
        "award of the Degree of Bachelor of Technology in Computer Science and Engineering under the APJ Abdul Kalam "
        "Technological University, during the academic year 2026."
    )
    add_p(doc, cert_text, font_size=11.5, bold=False, align=WD_ALIGN_PARAGRAPH.JUSTIFY, space_before=4, space_after=24)

    # Signature Table 1
    t1 = doc.add_table(rows=1, cols=2)
    t1.alignment = WD_TABLE_ALIGNMENT.CENTER
    c1_l, c1_r = t1.rows[0].cells
    c1_l.width = Inches(3.2)
    c1_r.width = Inches(3.2)
    set_cell_margins(c1_l, 40, 40, 60, 60)
    set_cell_margins(c1_r, 40, 40, 60, 60)

    p_t1_l = c1_l.paragraphs[0]
    r = p_t1_l.add_run(
        "COURSE FACULTY\nMs. Merin Michael T\nAssistant Professor\n\n"
        "HEAD OF THE DEPARTMENT\nDr. Sreeraj R\nAssociate Professor"
    )
    r.font.name = "Times New Roman"
    r.font.size = Pt(10)
    r.bold = True

def build_preliminary_matter(doc):
    """Section 2: Preliminary matter with roman page numbering."""
    s2 = doc.add_section(docx.enum.section.WD_SECTION.NEW_PAGE)
    s2.top_margin = Inches(1.0)
    s2.bottom_margin = Inches(1.0)
    s2.left_margin = Inches(1.25)
    s2.right_margin = Inches(1.0)
    s2.header.is_linked_to_previous = False
    s2.footer.is_linked_to_previous = False
    
    # Configure lowerRoman page numbering restarting at 1 (i)
    pgNumType = OxmlElement('w:pgNumType')
    pgNumType.set(qn('w:fmt'), 'lowerRoman')
    pgNumType.set(qn('w:start'), '1')
    s2._sectPr.append(pgNumType)

    add_header_text(s2.header, "PestRisk - UHV II Community Project (Vadama)")
    add_footer_page_number(s2.footer, "SCET, Kodakara\t\t", "i")

    # ACKNOWLEDGEMENT
    add_p(doc, "ACKNOWLEDGEMENT", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=16, space_after=12)
    ack_p1 = (
        "We would like to express our immense gratitude and profound thanks to all those who helped us to make this "
        "Universal Human Values project a great success. We express our gratitude to the almighty God for all the blessings endowed on us."
    )
    add_p(doc, ack_p1, font_size=11.5, space_before=0, space_after=8)
    
    ack_p2 = (
        "We express our sincere thanks to our Executive Director Rev. Fr. Dr. Anto Chungath and Principal Dr. Ramkumar S "
        "for providing us with such a supportive academic ecosystem and opportunity for value-based learning. "
        "We convey our heartfelt gratitude to our Head of the Department Dr. Sreeraj R for his constant inspiration, "
        "encouragement, and valuable support throughout the semester."
    )
    add_p(doc, ack_p2, font_size=11.5, space_before=0, space_after=8)

    ack_p3 = (
        "We can hardly find words to express our deep appreciation and sincere thanks for the guidance, constructive suggestions, "
        "and warm encouragement that we received from our course teacher Ms. Merin Michael T, Assistant Professor, Department of "
        "Computer Science & Engineering, for her whole-hearted support throughout this work."
    )
    add_p(doc, ack_p3, font_size=11.5, space_before=0, space_after=8)

    ack_p4 = (
        "We are also deeply indebted to our parents and friends who continuously encouraged and supported us in all aspects of our academic "
        "and project endeavors."
    )
    add_p(doc, ack_p4, font_size=11.5, space_before=0, space_after=16)

    doc.add_page_break()

    # INSTITUTE VISION & MISSION, QUALITY POLICY
    add_p(doc, "INSTITUTE VISION", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=8, space_after=4)
    add_p(doc, "To be a leading global centre of engineering education and research that fosters innovation, ethical leadership, and transformative societal impact.", font_size=11.5, space_before=0, space_after=10)

    add_p(doc, "INSTITUTE MISSION", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=4, space_after=6)
    add_bullet(doc, "", "Provide high-quality, outcome-based engineering education that prepares future-proof graduates with strong technical competence, critical thinking, and problem-solving abilities.", font_size=11)
    add_bullet(doc, "", "Promote interdisciplinary research, innovation, and entrepreneurship through vibrant collaboration with industry, academia, and research organizations to address local and global challenges.", font_size=11)
    add_bullet(doc, "", "Nurture values of integrity, responsibility, sustainability, and social commitment in students and faculty, shaping ethical leaders dedicated to societal transformation and community welfare.", font_size=11)
    add_bullet(doc, "", "Strengthen institutional excellence through continuous improvement, robust quality assurance systems, and effective stakeholder engagement aligned with global standards.", font_size=11)

    add_p(doc, "QUALITY POLICY", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=4)
    add_p(doc, "We at Sahrdaya are committed to provide Quality Technical Education through continual improvement and by inculcating moral and universal human values in students, empowering them to serve society with competence and compassion.", font_size=11.5, space_before=0, space_after=12)

    doc.add_page_break()

    # DEPARTMENT VISION & MISSION (Tables 2 & 3)
    add_p(doc, "DEPARTMENT VISION", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=8, space_after=6)
    t2 = doc.add_table(rows=1, cols=1)
    t2.alignment = WD_TABLE_ALIGNMENT.CENTER
    c2 = t2.rows[0].cells[0]
    set_cell_margins(c2, 60, 60, 80, 80)
    p2 = c2.paragraphs[0]
    p2.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
    r2 = p2.add_run("To emerge as a globally acclaimed Centre of Excellence in Computer Science and Engineering by transformative education and research in intelligent systems and digital technologies, while ensuring innovation, ethical leadership, and sustainable societal impact.")
    r2.font.name = "Times New Roman"
    r2.font.size = Pt(11)
    set_table_borders(t2)

    add_p(doc, "DEPARTMENT MISSION", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=12, space_after=6)
    t3 = doc.add_table(rows=1, cols=1)
    t3.alignment = WD_TABLE_ALIGNMENT.CENTER
    c3 = t3.rows[0].cells[0]
    set_cell_margins(c3, 60, 60, 80, 80)
    p3 = c3.paragraphs[0]
    p3.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
    mission_text = (
        "M1: To deliver high-quality, outcome-based education in core and emerging computer science fields to produce skilled, industry-ready graduates with strong problem-solving abilities.\n\n"
        "M2: To promote interdisciplinary research and innovation in advanced computing, collaborating with industry and academia to create real-world solutions.\n\n"
        "M3: To nurture responsible computing professionals through ethics, sustainability, and social responsibility to create inclusive, impactful digital solutions.\n\n"
        "M4: To strengthen institutional excellence through continuous curriculum improvement, quality assurance, stakeholder engagement, and effective governance aligned with global standards."
    )
    r3 = p3.add_run(mission_text)
    r3.font.name = "Times New Roman"
    r3.font.size = Pt(10.5)
    set_table_borders(t3)

    doc.add_page_break()

    # PEOs & PSOs (Tables 4 & 5)
    add_p(doc, "PROGRAMME EDUCATIONAL OBJECTIVES (PEOs)", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=8, space_after=6)
    t4 = doc.add_table(rows=3, cols=2)
    t4.alignment = WD_TABLE_ALIGNMENT.CENTER
    peos = [
        ("PEO1", "To establish successful careers in industry, academia, or entrepreneurship by applying core and emerging knowledge in computer science within global and multidisciplinary environments."),
        ("PEO2", "To analyze complex real-world problems and develop innovative, research-oriented solutions using intelligent and data-driven computing technologies."),
        ("PEO3", "To demonstrate ethical practices, leadership, and teamwork while developing sustainable technological solutions that contribute positively to society.")
    ]
    for idx, (code, desc) in enumerate(peos):
        row = t4.rows[idx]
        row.cells[0].width = Inches(1.1)
        row.cells[1].width = Inches(5.1)
        set_cell_margins(row.cells[0], 40, 40, 60, 60)
        set_cell_margins(row.cells[1], 40, 40, 60, 60)
        p_c0 = row.cells[0].paragraphs[0]
        r = p_c0.add_run(code)
        r.font.name = "Times New Roman"
        r.font.size = Pt(10.5)
        r.bold = True
        p_c1 = row.cells[1].paragraphs[0]
        p_c1.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        r = p_c1.add_run(desc)
        r.font.name = "Times New Roman"
        r.font.size = Pt(10.5)
    set_table_borders(t4)

    add_p(doc, "PROGRAMME SPECIFIC OUTCOMES (PSOs)", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=12, space_after=6)
    t5 = doc.add_table(rows=3, cols=2)
    t5.alignment = WD_TABLE_ALIGNMENT.CENTER
    psos = [
        ("PSO1", "To design and develop efficient computing solutions using core computer science principles along with emerging technologies such as artificial intelligence, machine learning, and data analytics."),
        ("PSO2", "To build scalable, secure, and high-quality software systems using modern tools, programming paradigms, cloud platforms, and cybersecurity practices aligned with industry standards."),
        ("PSO3", "To apply advanced computing concepts in interdisciplinary domains to create innovative solutions, pursue research, and develop entrepreneurial ventures with ethical and societal responsibility.")
    ]
    for idx, (code, desc) in enumerate(psos):
        row = t5.rows[idx]
        row.cells[0].width = Inches(1.1)
        row.cells[1].width = Inches(5.1)
        set_cell_margins(row.cells[0], 40, 40, 60, 60)
        set_cell_margins(row.cells[1], 40, 40, 60, 60)
        p_c0 = row.cells[0].paragraphs[0]
        r = p_c0.add_run(code)
        r.font.name = "Times New Roman"
        r.font.size = Pt(10.5)
        r.bold = True
        p_c1 = row.cells[1].paragraphs[0]
        p_c1.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        r = p_c1.add_run(desc)
        r.font.name = "Times New Roman"
        r.font.size = Pt(10.5)
    set_table_borders(t5)

    doc.add_page_break()

    # PROGRAMME OUTCOMES (POs) (Tables 6 & 7) - Complete PO1 to PO11
    add_p(doc, "PROGRAMME OUTCOMES (POs)", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=8, space_after=6)
    t6 = doc.add_table(rows=7, cols=2)
    t6.alignment = WD_TABLE_ALIGNMENT.CENTER
    pos_part1 = [
        ("PO1", "Engineering Knowledge: Apply knowledge of mathematics, natural science, computing, engineering fundamentals and an engineering specialization as specified in WK1 to WK4 respectively to develop to the solution of complex engineering problems."),
        ("PO2", "Problem Analysis: Identify, formulate, review research literature and analyze complex engineering problems reaching substantiated conclusions with consideration for sustainable development. (WK1 to WK4)"),
        ("PO3", "Design/Development of Solutions: Design creative solutions for complex engineering problems and design/develop systems/components/processes to meet identified needs with consideration for the public health and safety, whole-life cost, net zero carbon, culture, society and environment as required. (WK5)."),
        ("PO4", "Conduct Investigations of Complex Problems: Conduct investigations of complex engineering problems using research-based knowledge including design of experiments, modelling, analysis & interpretation of data to provide valid conclusions. (WK8)."),
        ("PO5", "Engineering Tool Usage: Create, select and apply appropriate techniques, resources and modern engineering & IT tools, including prediction and modelling recognizing their limitations to solve complex engineering problems. (WK2 and WK6)"),
        ("PO6", "The Engineer and The World: Analyze and evaluate societal and environmental aspects while solving complex engineering problems for its impact on sustainability with reference to economy, health, safety, legal framework, culture and environment. (WK1, WK5, and WK7)."),
        ("PO7", "Ethics: Apply ethical principles and commit to professional ethics, human values, diversity and inclusion; adhere to national & international laws. (WK9)")
    ]
    for idx, (code, desc) in enumerate(pos_part1):
        row = t6.rows[idx]
        row.cells[0].width = Inches(1.1)
        row.cells[1].width = Inches(5.1)
        set_cell_margins(row.cells[0], 35, 35, 50, 50)
        set_cell_margins(row.cells[1], 35, 35, 50, 50)
        p_c0 = row.cells[0].paragraphs[0]
        r = p_c0.add_run(code)
        r.font.name = "Times New Roman"
        r.font.size = Pt(10.5)
        r.bold = True
        p_c1 = row.cells[1].paragraphs[0]
        p_c1.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        r = p_c1.add_run(desc)
        r.font.name = "Times New Roman"
        r.font.size = Pt(9.5)
    set_table_borders(t6)

    doc.add_page_break()

    add_p(doc, "PROGRAMME OUTCOMES (POs) - Continued", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=8, space_after=6)
    t7 = doc.add_table(rows=4, cols=2)
    t7.alignment = WD_TABLE_ALIGNMENT.CENTER
    pos_part2 = [
        ("PO8", "Individual and Collaborative Team work: Function effectively as an individual, and as a member or leader in diverse/multi-disciplinary teams."),
        ("PO9", "Communication: Communicate effectively and inclusively within the engineering community and society at large, such as being able to comprehend and write effective reports and design documentation, make effective presentations considering cultural, language, and learning differences."),
        ("PO10", "Project Management and Finance: Apply knowledge and understanding of engineering management principles and economic decision-making and apply these to one's own work, as a member and leader in a team, and to manage projects and in multidisciplinary environments."),
        ("PO11", "Life-Long Learning: Recognize the need for, and have the preparation and ability for i) independent and life-long learning ii) adaptability to new and emerging technologies and iii) critical thinking in the broadest context of technological change. (WK8)")
    ]
    for idx, (code, desc) in enumerate(pos_part2):
        row = t7.rows[idx]
        row.cells[0].width = Inches(1.1)
        row.cells[1].width = Inches(5.1)
        set_cell_margins(row.cells[0], 35, 35, 50, 50)
        set_cell_margins(row.cells[1], 35, 35, 50, 50)
        p_c0 = row.cells[0].paragraphs[0]
        r = p_c0.add_run(code)
        r.font.name = "Times New Roman"
        r.font.size = Pt(10.5)
        r.bold = True
        p_c1 = row.cells[1].paragraphs[0]
        p_c1.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        r = p_c1.add_run(desc)
        r.font.name = "Times New Roman"
        r.font.size = Pt(9.5)
    set_table_borders(t7)

    doc.add_page_break()

    # ABSTRACT (Single Page Format - Crisp, Comprehensive, Balanced)
    add_p(doc, "Abstract", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=14, space_after=12)
    
    abs_1 = (
        "In rural environmental health and community sanitation, uncollected and unsegregated solid waste deposited in open "
        "commons represents a severe vector-breeding hazard. This project focuses on the community problem assigned to Group 5: "
        "“Attracts stray animals, mosquitoes, and rodents, raising the risk of communicable disease (Vadama)”. In Vadama village "
        "(Thrissur district, Kerala), unmanaged refuse and putrescible organic waste left along roadside culverts, canal buffers, and "
        "market commons attract packs of free-roaming stray animals, mosquitoes (Culex and Aedes), and burrowing rodents. This significantly "
        "elevates communicable disease risks—including Dengue, Chikungunya, and Leptospirosis—compounding the public health burden "
        "already created by stagnant water and sluggish drainage in the locality."
    )
    add_p(doc, abs_1, font_size=11.5, space_before=0, space_after=6)

    abs_2 = (
        "Guided by the Universal Human Values (UHV II), Life Skills and Community Work curriculum, this work presents PestRisk: "
        "Village Waste Hotspot Prioritization Platform. Grounded in the principle of Sah-Astitva (Co-existence across Self, Society, and "
        "Nature), PestRisk rejects expensive IoT hardware and opaque black-box machine learning models in favor of an explainable, "
        "deterministic 7-factor mathematical risk engine. Implemented via a Java 21 / Spring Boot 3.5.0 backend with an embedded SQLite "
        "database (pestrisk.db) and a responsive React 19 single-page frontend, the platform evaluates waste volume, accumulation duration "
        "(≥4 days), stagnant water, mosquito swarms, rodent burrows, stray animal attraction, and unsegregated waste, computing an "
        "auditable score bounded between 0 and 100 classified into LOW, MODERATE, HIGH, and CRITICAL priority tiers."
    )
    add_p(doc, abs_2, font_size=11.5, space_before=0, space_after=6)

    abs_3 = (
        "PestRisk embeds ethical values through citizen privacy preservation and frontline worker dignity protection. Public community "
        "alert feeds display only generalized ward names and risk categories without personal identifiers, preventing social friction. "
        "Simultaneously, the staff triage dashboard generates upfront hazard warnings for broken glass and sharp metals, ensuring sanitation "
        "workers are equipped with puncture-resistant personal protective equipment (PPE). The system was verified across automated unit/integration "
        "tests (58 tests) and simulated sanitation scenarios modeled directly after the four documented Vadama sub-cases. The platform demonstrates "
        "how transparent software engineering can foster civic trust, protect worker safety, and mitigate communicable disease risks in rural communities."
    )
    add_p(doc, abs_3, font_size=11.5, space_before=0, space_after=12)

    doc.add_page_break()

    # TABLE OF CONTENTS (Single Page Compact Format - Levels 1 & 2)
    add_p(doc, "TABLE OF CONTENTS", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=14, space_after=10)
    t9 = doc.add_table(rows=1, cols=3)
    t9.alignment = WD_TABLE_ALIGNMENT.CENTER
    c0, c1, c2 = t9.rows[0].cells
    c0.width = Inches(0.9)
    c1.width = Inches(4.5)
    c2.width = Inches(0.8)
    for c, text in [(c0, "Chapter"), (c1, "TITLE"), (c2, "Page No.")]:
        set_cell_margins(c, 30, 30, 40, 40)
        p = c.paragraphs[0]
        r = p.add_run(text)
        r.font.name = "Times New Roman"
        r.font.size = Pt(10.5)
        r.bold = True

    toc_entries = [
        ("", "Institute Vision, Mission and Quality Policy", "v"),
        ("", "Department Vision, Mission, PEOs, POs And PSOs", "vi"),
        ("", "Abstract", "ix"),
        ("", "Table of Contents", "x"),
        ("", "List of Tables", "xii"),
        ("", "List of Abbreviations", "xiii"),
        ("1", "INTRODUCTION", "1"),
        ("", "1.1 General & Problem Statement (Group 5 - Vadama)", "1"),
        ("", "1.1.1 Documented Problems and Sub-Cases in Vadama Village", "1"),
        ("", "1.2 Universal Human Values & Community Framework", "2"),
        ("", "1.2.1 Nomenclature", "2"),
        ("", "1.3 Project Overview", "3"),
        ("", "1.4 Literature Survey", "4"),
        ("2", "SCOPE AND DELIVERABLES", "5"),
        ("", "2.1 Scope of the Project", "5"),
        ("", "2.2 Project Deliverables", "5"),
        ("3", "METHODOLOGY", "6"),
        ("", "3.1 Overview", "6"),
        ("", "3.2 Development and Community-Centered Approach", "6"),
        ("", "3.3 Tools and Frameworks Used", "6"),
        ("", "3.4 Universal Human Values Principles Applied", "7"),
        ("", "3.5 Quality Assurance and Prototype Validation", "7"),
        ("4", "PROJECT ACTIVITIES", "8"),
        ("", "4.1 Overview", "8"),
        ("", "4.2 Backend and Core Analytical Engine Activities", "8"),
        ("", "4.3 Frontend and Community Portal Activities", "8"),
        ("", "4.4 System Integration and Vadama Scenario Testing", "9"),
        ("", "4.5 Documentation, Life Skills & Value Reflection Activities", "9"),
        ("5", "RESULTS AND FINDINGS", "11"),
        ("", "5.1 Overview", "11"),
        ("", "5.2 Functional and Scenario Results", "11"),
        ("", "5.3 Major Findings and UHV Insights", "13"),
        ("6", "CONCLUSION", "14"),
        ("", "6.1 Conclusion", "14"),
        ("", "6.2 Future Enhancements", "14"),
        ("7", "APPENDIX", "15"),
        ("", "7.1 Resident Reporting Portal & Vector Risk Gauge", "15"),
        ("", "7.2 Gram Panchayat Staff Console & Remediation Workflow", "16"),
        ("", "REFERENCE", "17")
    ]

    for chap, title, pg in toc_entries:
        row = t9.add_row()
        c0, c1, c2 = row.cells
        c0.width = Inches(0.9)
        c1.width = Inches(4.5)
        c2.width = Inches(0.8)
        set_cell_margins(c0, 15, 15, 30, 30)
        set_cell_margins(c1, 15, 15, 30, 30)
        set_cell_margins(c2, 15, 15, 30, 30)
        
        p0 = c0.paragraphs[0]
        r0 = p0.add_run(chap)
        r0.font.name = "Times New Roman"
        r0.font.size = Pt(9.5)
        r0.bold = bool(chap)
        
        p1 = c1.paragraphs[0]
        r1 = p1.add_run(title)
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(9.5)
        r1.bold = bool(chap)
        
        p2 = c2.paragraphs[0]
        r2 = p2.add_run(pg)
        r2.font.name = "Times New Roman"
        r2.font.size = Pt(9.5)
        r2.bold = bool(chap)

    doc.add_page_break()

    # LIST OF TABLES
    add_p(doc, "LIST OF TABLES", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=6)
    t10 = doc.add_table(rows=1, cols=3)
    t10.alignment = WD_TABLE_ALIGNMENT.CENTER
    c0, c1, c2 = t10.rows[0].cells
    c0.width = Inches(1.1)
    c1.width = Inches(4.3)
    c2.width = Inches(0.8)
    for c, text in [(c0, "Table No."), (c1, "TITLE"), (c2, "Page No.")]:
        set_cell_margins(c, 30, 30, 40, 40)
        p = c.paragraphs[0]
        r = p.add_run(text)
        r.font.name = "Times New Roman"
        r.font.size = Pt(10)
        r.bold = True

    table_entries = [
        ("1.1.1", "Documented Vadama Village Waste Hotspot Sub-Cases (Group 5)", "01"),
        ("1.2.1", "UHV & Technical Nomenclature", "02"),
        ("1.4", "Literature survey summary", "04"),
        ("3.3", "Tools and frameworks used", "06"),
        ("4.2.2", "Tools and technologies used (Backend)", "08"),
        ("4.3.2", "Tools and technologies used (Frontend)", "08"),
        ("4.4.2", "Tools and technologies used (Vadama Scenario Testing)", "09"),
        ("4.5.2", "Tools and technologies used (Documentation & Reflection)", "10")
    ]

    for num, title, pg in table_entries:
        row = t10.add_row()
        c0, c1, c2 = row.cells
        c0.width = Inches(1.1)
        c1.width = Inches(4.3)
        c2.width = Inches(0.8)
        set_cell_margins(c0, 15, 15, 30, 30)
        set_cell_margins(c1, 15, 15, 30, 30)
        set_cell_margins(c2, 15, 15, 30, 30)
        
        p0 = c0.paragraphs[0]
        r0 = p0.add_run(num)
        r0.font.name = "Times New Roman"
        r0.font.size = Pt(9.5)
        
        p1 = c1.paragraphs[0]
        r1 = p1.add_run(title)
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(9.5)
        
        p2 = c2.paragraphs[0]
        r2 = p2.add_run(pg)
        r2.font.name = "Times New Roman"
        r2.font.size = Pt(9.5)

    doc.add_page_break()

    # LIST OF ABBREVIATIONS
    add_p(doc, "LIST OF ABBREVIATIONS", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=14, space_after=8)
    abbrevs = [
        ("UHV", "Universal Human Values"),
        ("PO", "Programme Outcome"),
        ("PEO", "Programme Educational Objective"),
        ("PSO", "Programme Specific Outcome"),
        ("FSM", "Finite State Machine"),
        ("REST", "Representational State Transfer"),
        ("API", "Application Programming Interface"),
        ("JWT", "JSON Web Token"),
        ("HMAC", "Hash-based Message Authentication Code"),
        ("JPA", "Java Persistence API"),
        ("SPA", "Single-Page Application"),
        ("PPE", "Personal Protective Equipment"),
        ("SQLite", "Structured Query Language Lite")
    ]
    for short, full in abbrevs:
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(1)
        p.paragraph_format.space_after = Pt(1)
        p.paragraph_format.line_spacing = 1.15
        r_s = p.add_run(f"{short:<10}  -  ")
        r_s.font.name = "Times New Roman"
        r_s.font.size = Pt(10)
        r_s.bold = True
        r_f = p.add_run(full)
        r_f.font.name = "Times New Roman"
        r_f.font.size = Pt(10)

print("report_front_matter.py streamlined for compact page count.")
