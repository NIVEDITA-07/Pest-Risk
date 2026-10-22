# PestRisk — Village Waste Hotspot Prioritization Platform
### Universal Human Values (UHV II), Life Skills & Community Work Project
**Case Study Location:** Vadama Village, Thrissur District, Kerala  
**Academic Group:** Group 5 — Sahrdaya College of Engineering and Technology (SCET), Kodakara

---

## ⚡ QUICK START: How to Run the Project (Choose Any Method)

### 🚀 Method 1: The 1-Click Double-Click (Easiest!)
1. Open File Explorer and navigate to `D:\uhv`.
2. **Double-click `run.bat`**.
3. That's it! Two console windows will open automatically:
   - One for the **Spring Boot Java Backend** (`http://localhost:8080`)
   - One for the **React + Vite Frontend** (`http://localhost:5173`)
4. Open your web browser and go to: **[http://localhost:5173](http://localhost:5173)**

---

### 💻 Method 2: One-Line PowerShell Command
1. Open PowerShell in `D:\uhv`:
   ```powershell
   cd D:\uhv
   .\start_all.ps1
   ```
2. Both backend and frontend will launch in their own dedicated console windows.

---

### 🛠️ Method 3: Running Separately in Two Terminals

If you prefer to run backend and frontend independently:

#### Terminal 1 — Start the Backend (Java 21 / Spring Boot):
```powershell
cd D:\uhv
.\start_backend.ps1
```
*Starts Tomcat on `http://localhost:8080`. Automatically connects to embedded SQLite at `D:\uhv\data\pestrisk.db`.*

#### Terminal 2 — Start the Frontend (React 19 / Vite):
```powershell
cd D:\uhv
.\start_frontend.ps1
```
*Starts Vite development server on `http://localhost:5173`.*

---

## 🌐 How to Use the Application in Your Browser

Once the application is running, open Google Chrome, Edge, or Firefox:

### 1. Resident Waste Reporting Portal (Public — No Login Required)
👉 **URL:** [http://localhost:5173/#resident](http://localhost:5173/#resident)
* **What you can do:**
  1. Select a **Ward** in Vadama Village (e.g. `Vadama Canal Road`, `Vadama Market Junction`, `Vadama Central`).
  2. Enter a **Landmark / Description** (e.g. `Near canal culvert bridge`).
  3. Enter **Days Accumulated** and estimated **Waste Volume** (Low, Medium, High).
  4. Check active vector indicators:
     - 💧 **Stagnant Water / Blocked Drainage** (+20 pts)
     - 🦟 **Mosquito Swarms** (+15 pts)
     - 🐀 **Rodent Sightings / Burrows** (+15 pts)
     - 🐕 **Stray Animal Foraging** (+10 pts)
     - 🗑️ **Unsegregated Decomposing Organics** (+10 pts)
     - ⚠️ **Broken Glass / Sharp Metals** *(Triggers worker hazard warning)*
  5. Watch the **Real-Time Risk Gauge** calculate the score live (`LOW`, `MODERATE`, `HIGH`, or `CRITICAL`).
  6. Click **Submit Hotspot Report** to save to the database.

---

### 2. Gram Panchayat Staff & Admin Dashboard (Login Required)
👉 **URL:** [http://localhost:5173/#staff](http://localhost:5173/#staff)

#### ⚡ 1-Click Fast Demo Login (No Typing Required!)
On the Staff Sign-In screen, you will find two **1-Click Fast Demo Login** buttons:
* **`[👷 Sanitation Staff (STAFF-101)]`**: Instantly signs in as Sanitation Staff in one click.
* **`[🛡️ System Admin (ADMIN-01)]`**: Instantly signs in as System Administrator in one click.

#### 🔑 Manual Login Credentials:
| Account Type | Staff ID / Username | Password | Access Role |
| :--- | :--- | :--- | :--- |
| **Sanitation Staff** | `STAFF-101` *(or `staff_kavita`)* | `StaffPassword123` | **STAFF** (Inspect, triage, advance lifecycle) |
| **System Admin** | `ADMIN-01` *(or `admin`)* | `AdminPassword123` | **ADMIN** (Full staff management + triage) |

> **Note on "Sign In":** You only need to click the **1-Click Fast Demo Login** button or enter your **Staff ID** and **Password** and click **Sign In**. (No manual tokens or complicated keys needed).

* **What Staff Can Do:**
  - **Review Priority Queue:** Reports automatically sorted by risk score (CRITICAL hotspots at top).
  - **Worker Safety Alerts:** Prominent red badges alert supervisors when reported sites contain **Broken Glass** or **Sharp Metals**, ensuring workers are issued puncture-resistant PPE before dispatch.
  - **Manage Lifecycle Status:**
    $$\text{REPORTED} \longrightarrow \text{VERIFIED} \longrightarrow \text{IN\_PROGRESS} \longrightarrow \text{RESOLVED}$$
  - **Add Inspector Remarks:** Record field inspection notes and remediation logs into the permanent SQLite audit trail.

---

### 3. Public Community Alerts Feed (Privacy-Preserving)
👉 **URL:** [http://localhost:5173/#alerts](http://localhost:5173/#alerts)
* Publicly visible notification board showing active community remediation priorities.
* **Privacy by Design:** Displays only generalized ward names and risk classifications; private citizen names, contact numbers, and home landmarks are strictly hidden to prevent community friction.

---

## 🧪 Optional: How to Populate Demonstration Data (Vadama Sub-Cases)

To populate the database with realistic sample reports modeled after the **four documented Vadama Village sub-cases**:
```powershell
cd D:\uhv
.\seed_demo_data.ps1
```
*This seeds:*
- **Sub-Case 1:** Clogged drainage culvert on Vadama Canal Road (Score 75 — HIGH)
- **Sub-Case 2:** Open market food waste attracting stray dog packs (Score 55 — MODERATE)
- **Sub-Case 3:** Canal buffer rodent burrows & Leptospirosis risk (Score 85 — CRITICAL)
- **Sub-Case 4:** Central vacant plot mixed waste with broken glass (Score 65 — HIGH + Crew PPE Alert)

---

## 📄 How to Build / Regenerate the Project Report (.docx)

The official project report matching the KTU / Sahrdaya UHV format (31 pages) can be compiled at any time:
```powershell
cd D:\uhv
python main_build_report.py
```
*Generated reports are saved directly in `D:\uhv\`:*
- `PestRisk_UHV_Project_Report.docx`
- `PestRisk_UHV_Project_Report_v3.docx` *(Latest 31-page build with all 11 Programme Outcomes PO1–PO11)*

---

## 🔬 How to Run Automated Verification & Tests

To execute the 58 automated unit, risk engine, and security integration tests:
```powershell
cd D:\uhv\backend
.\mvnw.cmd test
```

To run frontend linting:
```powershell
cd D:\uhv\frontend
npm run lint
```

---

## 📁 System Architecture & Directory Layout

```
D:\uhv/
├── run.bat                     # 1-Click Double-Click launcher for Windows
├── start_all.ps1               # PowerShell root launcher (backend + frontend)
├── start_backend.ps1           # Launches Spring Boot API on port 8080
├── start_frontend.ps1          # Launches React/Vite dev server on port 5173
├── seed_demo_data.ps1          # Seeds Vadama village demonstration scenarios
├── main_build_report.py        # Compiles academic project report (.docx)
├── report_front_matter.py      # Front matter generator (Cover, Decl, Cert, POs, TOC)
├── report_chapters.py          # Chapters 1-7 & References generator
├── PestRisk_UHV_Project_Report_v3.docx # Final generated academic report (31 pages)
├── backend/                    # Java 21 / Spring Boot 3.5.0 REST microservice
│   ├── src/main/java/          # RiskEngine, Services, Controllers, Entities
│   └── src/test/java/          # 58 automated unit and integration tests
├── frontend/                   # React 19 + Vite single page application
│   ├── src/components/         # ResidentPortal.jsx, StaffDashboard.jsx, StatusBadge.jsx
│   └── src/api/                # pestRiskApi.js REST client
└── data/
    └── pestrisk.db             # Local embedded SQLite relational database
```

---

## 🌐 How to Host on Cloud (Vercel + Render - 100% Free)

You can host the entire PestRisk platform online for free in under 5 minutes:
* **Frontend (React + Vite):** Hosted on **Vercel** (Global CDN, instant builds).
* **Backend (Spring Boot API):** Hosted on **Render** (Free Web Service / Docker container).

```
   ┌───────────────────────┐            REST API Requests           ┌────────────────────────┐
   │     Vercel (Free)     │ ─────────────────────────────────────> │      Render (Free)     │
   │  React 19 + Vite SPA  │                                        │  Java 21 Spring Boot   │
   │  pestrisk.vercel.app  │ <───────────────────────────────────── │ pestrisk.onrender.com  │
   └───────────────────────┘           JSON + JWT Sessions          └────────────────────────┘
```

---

### Step 1: Deploy Backend to Render (Free)

1. **Push your code to GitHub:**
   ```bash
   git init
   git add .
   git commit -m "Deploy PestRisk to cloud"
   git remote add origin https://github.com/YOUR_USERNAME/pestrisk.git
   git push -u origin main
   ```
2. Log in to [Render.com](https://render.com) and click **New +** $\rightarrow$ **Web Service**.
3. Connect your GitHub repository.
4. Configure the Web Service settings:
   * **Name:** `pestrisk-backend`
   * **Runtime:** `Docker` (Render automatically detects `backend/Dockerfile`)
   * **Root Directory / Docker Context:** `backend`
   * **Instance Type:** `Free`
5. Under **Environment Variables**, add:
   * `PORT` = `10000`
   * `PESTRISK_ADMIN_USERNAME` = `ADMIN-01`
   * `PESTRISK_ADMIN_PASSWORD` = `AdminPassword123` *(or any strong password)*
   * `PESTRISK_JWT_SECRET` = `a_very_secure_secret_key_minimum_32_characters_long!`
   * `CORS_ALLOWED_ORIGINS` = `*` *(or your Vercel URL once deployed)*
6. Click **Create Web Service**. Once deployed, copy your backend URL (e.g. `https://pestrisk-backend.onrender.com`).

---

### Step 2: Deploy Frontend to Vercel (Free)

1. Log in to [Vercel.com](https://vercel.com) and click **Add New...** $\rightarrow$ **Project**.
2. Import your GitHub repository.
3. Configure project settings:
   * **Framework Preset:** `Vite`
   * **Root Directory:** Click `Edit` and select `frontend`
   * **Build Command:** `npm run build` *(auto-detected)*
   * **Output Directory:** `dist` *(auto-detected)*
4. Under **Environment Variables**, add:
   * `VITE_API_BASE_URL` = `https://pestrisk-backend.onrender.com` *(your Render URL from Step 1)*
5. Click **Deploy**. Vercel will build the frontend and provide your live URL (e.g. `https://pestrisk.vercel.app`)!

---

### Step 3: Git Safety & Privacy Verification
The repository includes a strict `.gitignore` that prevents private secrets from ever being uploaded to GitHub:
* ✅ `.env` and `.env.*` files are **strictly excluded** (only safe `.env.example` is committed).
* ✅ Local databases (`data/*.db`, `*.sqlite`) are **strictly excluded**.
* ✅ Python cache (`__pycache__`) and build artifacts (`node_modules/`, `target/`, `dist/`) are **strictly excluded**.

---

## ❓ Troubleshooting & Frequently Asked Questions

### Q: Port 8080 or Port 5173 is already in use?
Stop any previously running instances by pressing `Ctrl + C` in their console windows, or run:
```powershell
# Stop process on port 8080
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess -ErrorAction SilentlyContinue | Stop-Process -Force
# Stop process on port 5173
Get-Process -Id (Get-NetTCPConnection -LocalPort 5173).OwningProcess -ErrorAction SilentlyContinue | Stop-Process -Force
```

### Q: Where is the database stored?
The database is a single embedded SQLite file located at `D:\uhv\data\pestrisk.db`. You do not need to install or run MySQL, PostgreSQL, or any database server!

### Q: Do I need to be connected to the internet to run?
No. Once dependencies are installed, PestRisk runs completely offline on your local machine (`localhost`).

---

### Universal Human Values (UHV) Alignment Summary
- **Harmony in Self:** Fosters individual restraint and responsibility (*Sanyam*) in waste segregation to safeguard bodily health (*Swasthya*).
- **Harmony in Society:** Provides an auditable, transparent priority queue that eliminates discriminatory or biased municipal response.
- **Harmony in Nature (*Sah-Astitva*):** Directly targets stagnant water and decomposing organic waste to restore ecological balance and eliminate vector pest breeding pockets.
- **Worker Dignity:** Mandates advance notifications for broken glass and sharps to protect frontline sanitation crews from physical injury.
