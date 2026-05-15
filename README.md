# 🌌 GameVerse Academy

[![Java](https://img.shields.io/badge/Java-17-orange.svg?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Server](https://img.shields.io/badge/Server-Embedded%20Tomcat%209-blue.svg?style=flat-square)](https://tomcat.apache.org/)
[![Database](https://img.shields.io/badge/Database-SQLite%20%2F%20MariaDB-brightgreen.svg?style=flat-square)](https://www.sqlite.org/)
[![Status](https://img.shields.io/badge/Status-Production%20Ready-success.svg?style=flat-square)](#)

**GameVerse Academy** is a high-performance, cinematic web ecosystem designed for modding enthusiasts and game developers. Built on a modern Java stack, it delivers a seamless, secure, and visually stunning experience for managing game modifications, community reviews, and architectural assets.

---

# 🚀 Ready-to-Run Distributions

Experience GameVerse Academy instantly. We provide two distinct, professionally packaged distributions tailored for different environments.

## 🖥️ Portable EXE Edition
*The ultimate plug-and-play experience.*

Designed for immediate deployment on Windows, the Portable Edition is a fully self-contained environment. No external dependencies, no database configuration—just pure performance.

- **✔ Zero Setup:** Everything you need is bundled inside.
- **✔ Self-Contained:** Includes a portable Java runtime and an embedded SQLite database.
- **✔ Persistent Data:** Automatically handles uploads and datasets within the local folder.
- **✔ Preloaded:** Comes with a rich, pre-configured dataset for immediate testing.

### **Quick Start:**
1. **Download** the latest release.
2. **Extract** the ZIP archive.
3. **Launch** `GameVerse.exe`.

---

## ☕ Server Edition
*Built for scale. Ready for production.*

The Server Edition is a deployment-oriented "Fat JAR" package, perfect for cloud hosting or local development where configuration flexibility is key.

- **✔ Deployment Ready:** A single executable JAR containing the entire application logic.
- **✔ Scalable Storage:** Supports both external MariaDB clusters and local SQL exports.
- **✔ Configurable Assets:** Easily link external asset directories for large-scale mod hosting.
- **✔ Modern DevOps:** Seamlessly integrates with Docker or standard Linux server environments.

### **Quick Start:**
```bash
java -jar gameverse.jar
```

---

# ✨ Core Features

- **🎬 Cinematic UI:** A premium dark-mode interface with a focus on high-fidelity iconography and smooth transitions.
- **💎 Dynamic Rating System:** An interactive, architectural star-rating component with real-time feedback.
- **📂 Advanced Mod Archive:** Multi-tag filtering and categorized search for discovering high-quality game modifications.
- **👤 Creator Ecosystem:** Personalized profiles for modders, featuring portfolio showcases and community interaction.
- **📥 Secure Downloads:** Integrated download management with tracking and integrity checks.

---

# 🛡️ Security Layer

GameVerse Academy implements a robust security architecture to ensure data integrity and user safety:

- **🔐 Session-Based Auth:** Secure user authentication with encrypted session management.
- **🏢 Role-Based Access (RBAC):** Distinct permissions for Users, Moderators, and Administrators.
- **🛡️ Input Sanitization:** Protection against common web vulnerabilities via strict request validation.
- **📦 Isolated Runtime:** The portable distribution operates within its own sandbox, ensuring zero system interference.

---

# 🏗️ Architecture

The project follows a clean **MVC (Model-View-Controller)** pattern, decoupled for maximum maintainability:

- **Controller Layer:** Optimized request handling and business logic routing.
- **Service Layer:** Decoupled business logic ensuring reusable and testable components.
- **Repository Layer:** Abstracted data access support for multiple database backends (SQLite/MariaDB).
- **View Layer:** Modern JSP-based templates with a shared architectural design system.

---

# 🛠️ Tech Stack

- **Core:** Java 17 (LTS)
- **Web Engine:** Embedded Apache Tomcat 9
- **Frontend:** HTML5, CSS3 (Custom Design System), JSP/JSTL
- **Persistence:** 
    - **Development/Portable:** SQLite & H2
    - **Production:** MariaDB
- **Build System:** Maven (Shade & Exec plugins)

---

# 🚀 Future Improvements

- [ ] **Dockerization:** Official multi-architecture Docker images.
- [ ] **API Access:** RESTful endpoints for third-party integrations.
- [ ] **Cloud Storage:** Native support for AWS S3 and Azure Blob storage.
- [ ] **Real-time Notifications:** WebSocket integration for community interactions.

---

<p align="center">
  <i>Developed with precision by the GameVerse Academy Team.</i>
</p>
