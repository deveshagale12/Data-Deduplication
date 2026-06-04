Data Deduplicaton using Blockchain and Advance Security using Cloud Computing
VaultPro / CloudPortal — Secure File Vault & Sharing System
VaultPro is a robust, secure backend platform built on Spring Boot designed for identity management, storage minimization, and controlled, granular file sharing. It implements modern security patterns such as SHA-256 deduplication and time-sensitive One-Time Passwords (OTPs) to manage file transmission safely between platform members.

🚀 Core Features
⚡ Cryptographic File Deduplication: Prevents server storage waste by calculating a unique SHA-256 hash of any uploaded file, rejecting duplicate file transfers instantly at the database layer.

🔑 One-Time Password (OTP) Security: Files are hidden by default. Access requires generating a time-restricted token sent natively via integrated email channels.

🛡️ Administrative Insight Panels: Admin controllers collect cross-system metrics, track audit/download logs, and group analytical metrics (Line charts for daily uploads, Pie charts for user role distributions).

📧 Asynchronous Communication: Email alerts (Welcome letters, Password resets, File access requests) utilize background thread workers (CompletableFuture) to prevent UI lag.
