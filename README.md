# GameVerse Academy

<p align="center">
  <img src="https://img.shields.io/badge/Java-EE-red?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Architecture-MVC%2B-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Database-SQLite%20%2F%20MariaDB-blueviolet?style=for-the-badge"/>
</p>

GameVerse Academy is a high-performance, cinematic **modding ecosystem platform** built with Java EE. It is designed to manage game modifications, user submissions, reviews, profiles, and secure content distribution under a modern, responsive, and secure architecture.

---

## 🚀 Project Overview

GameVerse Academy provides game enthusiasts and creators with a secure vault to host, discuss, and download modifications. The system features a custom MVC-inspired architecture built on vanilla Java Servlets and JSP, running on an embedded Tomcat server.

Key capabilities include:
- **Authentication & Registration**: Modern sign-up and secure session management.
- **Mod Submission Pipeline**: Drag-and-drop image reordering, star-based thumbnail selection, and ZIP file parsing.
- **Review Engine**: Community rating and comment threads.
- **User Profiles**: Custom username updates and dynamic avatar uploads.
- **Security Hardening**: Session fixation protection, secure password hashing, strict file upload limits, and input sanitization filters.

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

## 📦 Sample Downloads

To prevent repository bloat, heavy binary artifacts are hosted externally. Please use the following official links to download pre-packaged standalone distributions:

* 📦 **Sample JAR (Runnable Archive)**:  
  [Download gameverse.jar (Google Drive)](https://drive.google.com/file/d/1QowF6v6k9CpRkw4fsQUjjK9I8kt1wmLx/view?usp=drive_link)
* 🚀 **Sample EXE (Windows Standalone)**:  
  [Download GameVerseAcademy.exe (Google Drive)](https://drive.google.com/file/d/1m5KB_N1GccXvG0H-ONwnAqkec5xki4So/view?usp=drive_link)

--