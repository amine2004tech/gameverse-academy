# GameVerse Academy

<p align="center">
  <img src="https://img.shields.io/badge/Java-EE-red?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Architecture-MVC%2B-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Database-SQLite%20%2F%20MariaDB-blueviolet?style=for-the-badge"/>
</p>

**GameVerse Academy** is a cinematic, high-performance mod archive and submission platform built with Java EE. It is designed to manage game modifications, user submissions, reviews, profiles, and secure content distribution.

---

## 🚀 Overview

GameVerse Academy features a custom MVC-inspired architecture utilizing vanilla Java Servlets and JSP running on an embedded Tomcat container. Key features include:
- **Authentication**: Salted password hashing, registration, and session hijack defenses.
- **Mod Submission Pipeline**: Drag-and-drop image reordering, primary thumbnail designation, and whitelist validation.
- **Review Engine**: Community rating and comment threads.
- **User Profiles**: Custom username updates and avatar uploads.
- **Security Hardening**: Anti-DoS file upload constraints, session fixation protection, and XSS input filters.

---

## 🏗️ System Architecture (MVC++)

```
            ┌────────────────────┐
            │   JSP / Frontend   │
            └─────────┬──────────┘
                      │
            ┌─────────▼──────────┐
            │   Servlets Layer   │  (Controllers)
            └─────────┬──────────┘
                      │
            ┌─────────▼──────────┐
            │  Service Layer     │  (Business Logic)
            └─────────┬──────────┘
                      │
            ┌─────────▼──────────┐
            │ Repository Layer   │  (JDBC / DAO)
            └─────────┬──────────┘
                      │
            ┌─────────▼──────────┐
            │   Database (SQL)   │
            └────────────────────┘
```

### Domain Model
- **User**: Authentication, identity management, and role-based access control.
- **Mod**: Mod info, rating, downloads, and tags.
- **Game**: Container games grouping specific mods.
- **Review**: Comment and star-rating feedback.
- **Tag / ModTag**: Metadata tagging engine.
- **ModImage**: Gallery images associated with submissions.

---

## ⚡ Key Upgrades

- **Salted Password Hashing**: Upgraded login/register to PBKDF2 with unique salts, with transparent legacy migration.
- **File Upload Limits**: Hardcoded anti-DoS protection (max 10 MB per image, 500 MB per ZIP).
- **Session Hijack Prevention**: Automatic session ID regeneration upon login.
- **Secure File Whitelists**: Mime-type whitelists and double-extension detection (e.g., `.exe.zip` blocked).
- **Cinematic UI/UX**: Draggable submit reorder list, dark-themed glassmorphic loader/error popups, and a customized media preview slider.

---

## ⚙️ Tech Stack & Requirements

- **Backend**: Java 17, Java EE (Servlets, JSP), Apache Tomcat 9 (Embedded)
- **Database**: SQLite (Portable Mode) / MariaDB (Production Mode)
- **Frontend**: HTML5, Vanilla CSS, JS
- **Build System**: Maven 3.8+

---

## 📦 Standalone Packages

Heavy binary distributions are hosted externally. Use these official links to download pre-packaged standalone archives:

* 📦 **Sample JAR (Runnable Archive)**:  
  [Download gameverse.jar (Google Drive)](https://drive.google.com/file/d/1QowF6v6k9CpRkw4fsQUjjK9I8kt1wmLx/view?usp=drive_link)
* 🚀 **Sample EXE (Windows Standalone)**:  
  [Download GameVerseAcademy.exe (Google Drive)](https://drive.google.com/file/d/1m5KB_N1GccXvG0H-ONwnAqkec5xki4So/view?usp=drive_link)

---

## 🔑 Setup & Demo Credentials

Seeded accounts are provided for sandbox testing:
- **Admin**: `amine@test.com` (Password: `admin123`)
- **Player**: `player1@gmail.com` (Password: `pass123`)

For step-by-step local running instructions, configuring environment variables, and restoring database seeds, please refer to the local **`SETUP_GUIDE.md`** file.
The list of testing accounts can be found in **`DEMO_LOGINS.txt`**.
