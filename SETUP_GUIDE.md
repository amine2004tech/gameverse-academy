# GameVerse Academy - Local Setup & Database Restoration Guide

This guide describes how to run GameVerse Academy locally, set up the database from the seed database, and perform manual testing.

---

## 🛠️ Requirements

1. **Java Development Kit (JDK) 17** or higher.
2. **Maven 3.8+** (or use the included `./mvnw.cmd` wrapper).
3. **Microsoft .NET Framework** (only if you want to compile `Launcher.cs` to `.exe` locally via `csc.exe`).

---

## 💾 Database Configuration & Seed Restoration

By default, the application runs in **Portable Mode** using **SQLite**, requiring zero configuration.

### Option A: SQLite (Default)
When starting the server, the app automatically extracts the seed database from resources into `data/gameverse.db` in your execution directory.
To restore or reset the database to the clean seed manually:
1. Ensure the server is stopped.
2. Delete the local file `data/gameverse.db`.
3. Copy `database/seed/gameverse.db` to `data/gameverse.db`.

### Option B: MariaDB / MySQL (Production/Cloud Mode)
1. Ensure MariaDB/MySQL is running on `localhost:3306` with database name `gv_up`.
2. Copy `.env.example` to `.env` in the root folder.
3. Configure the environment variables:
   ```ini
   DB_URL=jdbc:mariadb://localhost:3306/gv_up?allowPublicKeyRetrieval=true&useSSL=false
   DB_USER=your_username
   DB_PASSWORD=your_password
   ```

---

## 🚀 Running the Application

### 1. Running in Development Mode
Run the following Maven command from the repository root:
```powershell
.\mvnw.cmd compile exec:java
```
Access the application in your browser:
👉 **[http://localhost:8080/gameverseacademy/](http://localhost:8080/gameverseacademy/)**

### 2. Packaging Standalone Deployments
Run the custom packager script:
```powershell
.\package-server.ps1
```
This will compile the project and build two deployable folders in the `release/` directory:
- **`release/Server-JAR/`**: Contains the runnable `gameverse.jar` along with start scripts (`start-server.bat`, `start-server.sh`) and external `assets/`.
- **`release/Server-EXE/`**: Contains `GameVerseAcademy.exe` and external `assets/`.

---

## 🔑 Testing Credentials

A complete list of seeded accounts and passwords is provided in **`DEMO_LOGINS.txt`**.
- **Admin**: `amine@test.com` (Password: `admin123`)
- **Player**: `player1@gmail.com` (Password: `pass123`)
- **Creator**: `creator@mods.com` (Password: `creator123`)
