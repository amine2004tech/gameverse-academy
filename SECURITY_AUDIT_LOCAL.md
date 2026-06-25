# GameVerse Academy - Local Security Audit Report
**Date:** 2026-06-25
**Scope:** Authorized localhost-only offensive security review and remediation.
**Target:** GameVerse Academy (Java EE Web Application)

## Executive Summary
A comprehensive security review was conducted on the GameVerse Academy application focusing on authentication, authorization, input validation, CSRF, and file upload security. Multiple high-severity vulnerabilities were identified and systematically remediated to align with secure coding standards.

---

## 1. Authentication & Session Management

### 1.1 Plaintext Password Storage (CRITICAL)
**Vulnerability:** Passwords were stored in the database in plaintext, risking total compromise if the database is leaked.
**Remediation:** 
- Introduced `PasswordHasher.java` using SHA-256 with per-user 16-byte random salts.
- Updated `UserRepository.java` to hash passwords on creation/update and use constant-time verification.
- Maintained a migration path for legacy plaintext passwords.
- Refactored `UserService.java` to use `PasswordHasher` for validating the current password during updates.

### 1.2 Session Fixation (HIGH)
**Vulnerability:** The application did not invalidate the existing session upon successful login in `LoginController.java`, allowing an attacker to fixate a session ID and hijack an authenticated user.
**Remediation:** 
- Added `session.invalidate()` for the pre-authentication session in `LoginController.java`.
- Explicitly created a new session and applied a 30-minute timeout limit (`session.setMaxInactiveInterval(1800)`).

---

## 2. Authorization & Access Control (RBAC)

### 2.1 Missing RBAC on Admin Actions (HIGH)
**Vulnerability:** `AdminController.java` verified the `ADMIN` role for `doGet`, but omitted this check in `doPost`. An authenticated non-admin user could theoretically forge a POST request to approve or reject mods.
**Remediation:** 
- Implemented robust RBAC checks in the `doPost` method identical to the GET guard.

### 2.2 Insecure GET Requests for State Changes (MEDIUM)
**Vulnerability:** `DeleteModController` relied on HTTP GET requests, which makes it highly susceptible to CSRF attacks via `<img>` or `<a>` tags.
**Remediation:** 
- Converted `DeleteModController` to accept only POST requests. GET requests now return `405 Method Not Allowed`.

---

## 3. Input Validation & Injection (XSS)

### 3.1 Stored Cross-Site Scripting (XSS) (HIGH)
**Vulnerability:** `modDetails.jsp` rendered user-controlled inputs (mod title, description, tags, author name, review usernames, review comments) as raw HTML using `<%= ... %>`. Even with `InputSanitizer`, this was vulnerable to complex payloads and lacking defense-in-depth output encoding.
**Remediation:** 
- Created `HtmlEncoder.java` to securely encode HTML entities (`&`, `<`, `>`, `"`, `'`).
- Refactored `modDetails.jsp` to wrap all user-controlled data in `HtmlEncoder.encode()`.

### 3.2 Unhandled NumberFormatExceptions (LOW)
**Vulnerability:** Malformed numeric parameters in `AdminController`, `ModController`, `ReviewController`, `UpdateReviewController`, and `DeleteReviewController` led to stack trace disclosures (`500 Internal Server Error`).
**Remediation:** 
- Introduced `try-catch` blocks for all `Integer.parseInt()` calls handling user input, failing safely or returning `400 Bad Request`.

---

## 4. Cross-Site Request Forgery (CSRF) & Headers

### 4.1 Lack of CSRF Protections (HIGH)
**Vulnerability:** Forms modifying state (mod upload, reviews, admin actions, profile updates) lacked anti-CSRF tokens.
**Remediation:** 
- Upgraded `SecurityFilter.java` to map to `/*`.
- Configured the filter to securely generate a CSRF token per session.
- Enforced CSRF token presence and equality on all sensitive POST routes.
- Updated all JSP files (`login.jsp`, `submit.jsp`, `admin.jsp`, `profile.jsp`, `modDetails.jsp`, `profile_modals.jsp`) to include the `<input type="hidden" name="csrfToken"...>` field.

### 4.2 Missing Security Headers (MEDIUM)
**Vulnerability:** The application was vulnerable to Clickjacking and MIME-sniffing.
**Remediation:** 
- Added essential headers via `SecurityFilter.java`: `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Content-Security-Policy`, and Cache-Control headers to prevent caching of authenticated pages.

---

## 5. File Upload & Download Security

### 5.1 Unrestricted File Upload & Executable Uploads (HIGH)
**Vulnerability:** `SecureUploadService.java` relied merely on filename sanitization, failing to check file size limits or extension validity. It was possible to upload `file.php.jpg` or excessively large binaries.
**Remediation:** 
- Introduced explicit extension allowlisting (`jpg`, `jpeg`, `png`, `gif`, `webp` for images; `zip`, `rar`, `7z` for archives).
- Applied MIME type validation ensuring image uploads matched `image/*`.
- Enforced 10MB limits for images and 50MB for mods.

### 5.2 Directory Traversal & Header Injection (MEDIUM)
**Vulnerability:** The `DownloadController` could potentially be manipulated via the database `fileName` field, causing directory traversal or HTTP Header Injection on `Content-Disposition`.
**Remediation:** 
- Added a canonical path check against `getCanonicalPath()` to strictly enforce downloads originating from the authorized `assets` directory.
- Sanitized the filename dynamically before inserting it into the `Content-Disposition` header, neutralizing CRLF injection risks.

---

## Conclusion
The identified vulnerabilities have been successfully remediated. The codebase now exhibits improved resilience against CSRF, XSS, Path Traversal, and unauthorized access, leveraging robust design patterns like centralized security filtering and strict output encoding.
