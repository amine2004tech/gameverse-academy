#  GameVerse Academy

<p align="center">
  <img src="https://img.shields.io/badge/Java-EE-red?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Architecture-MVC%2B-blue?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Database-MariaDB-blueviolet?style=for-the-badge"/>
</p>

<p align="center">
  <b>A cinematic Java EE platform for game mods, reviews, and creator ecosystems.</b>
</p>

---

## 🚀 Overview

GameVerse Academy is a **modding ecosystem platform** built with Java EE, designed to manage game modifications, user interactions, reviews, and secure content distribution in a structured, scalable architecture.

> Think of it as a mini Steam Workshop + developer academy system.
---

## 🚀 Downloads

- EXE + DB Package  
https://drive.google.com/file/d/1U5KVp7Ru2B2vRatjH2a-z5OIyeiLIQSl/view?usp=drive_link

- JAR + SQL Package  
https://drive.google.com/file/d/1hXLQbjqwTOMa0LvSjkNmk2iD39rAcIOO/view?usp=drive_link

---

## 🎯 Core Features

- 🔐 Authentication system (login/logout)
- 🎮 Game catalog browsing
- 📦 Mod upload + management system
- ⭐ Review & rating engine
- 🧑 User profiles with avatars
- ⬇️ Secure download tracking system
- 🛡️ Security filter + input sanitization layer
- 🧩 Tag-based mod classification
- 🧠 Admin moderation dashboard

---

## 🏗️ System Architecture (MVC++)

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

---

## 🧠 Domain Model

- User → authentication + roles
- Mod → core content unit (downloads, rating, tags)
- Game → parent container for mods
- Review → community feedback system
- Tag → classification engine
- ModImage → media gallery system
---

## 🔐 Security Engine

- XSS filtering via InputSanitizer
- Validation rules via InputValidator
- Request interception via SecurityFilter
- File upload size limits (anti-DoS protection)
- Session-based RBAC (Admin / User / Moderator)

---

## ⚙️ Tech Stack

- Java EE (Servlets + JSP)
- JDBC (raw SQL layer)
- MariaDB / SQLite hybrid support
- HTML5 / CSS3 / JavaScript
- Apache Tomcat embedded runtime

---

## 📦 Deployment Architecture

- Main.java → Embedded Tomcat bootstrap
- /assets → External persistent static storage
- DBUtil → dynamic DB switching
  - Production → MariaDB
  - Local → SQLite fallback

---

## 📊 Database Schema

| Table | Key | Relationships |
|------|-----|--------------|
| USERS | id | - |
| GAMES | id | - |
| MODS | id | user_id, game_id |
| REVIEWS | id | user_id, mod_id |
| TAGS | id | - |
| MOD_TAGS | mod_id + tag_id | junction table |
| MOD_IMAGES | id | mod_id |


---

## 🧩 Key Modules

### Controllers
- LoginController
- ModSubmitController
- AdminController
- DownloadController
- ReviewController
- ProfileController
### Services
- ModService
- UserService
- ModImageService
- GameService
- ReviewService
- TagService

### Security
- InputValidator
- SecureUploadService
- SecurityFilter
- InputSanitizer

---

## 👨‍💻 Author

CHAFIK Mohamed Amine 48 ISSIC

---
