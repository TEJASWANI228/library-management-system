# 📚 Java Library Management System

Welcome to the **Java Library Management System**! This project is a professional, industrial-strength, console-driven application built using **Java** and an embedded **H2 SQL Database**. 

The goal of this project is to simulate a real-world library resource management system. It handles everything from adding books and registering members to issuing checkouts, tracking returns, logging audit trails, and running automated tests—all without requiring you to install any external database software like MySQL or PostgreSQL.

---

## 🌟 What This Project Does

This system acts as a complete backend and interactive management console. Here is a breakdown of its primary capabilities:

1. **Catalog & Inventory Management**
   * Add new books to the library system with titles, authors, unique ISBN numbers, and stock quantities.
   * Update existing book records or search through the catalog instantly by title and author.
   * Track available stock versus total stock in real time.

2. **Member Profile Administration**
   * Register new library patrons by collecting their names and email addresses.
   * Assign unique member identification numbers to keep user accounts organized and distinct.

3. **Circulation & Transaction Lifecycle (Checkouts & Returns)**
   * **Issuing Books**: Safely assign books to registered members. The system automatically verifies if copies are available and decrements the inventory count.
   * **Returning Books**: Process book returns, validate active checkout relationships, and safely add the book quantity back into the library catalog.

4. **Persistent State & Automatic Seeding**
   * Runs on an embedded H2 database that saves data locally in your project folder (`library_db`).
   * Automatically initializes and seeds classic software engineering books and initial records on first startup, ensuring the library is never empty when you launch it.

5. **Persistent Audit Logging & Reporting**
   * Every important action—such as registering a member, adding a book, or checking out an item—is automatically written to a local text file named `library_report.txt`. This creates a permanent audit trail for debugging and compliance review.

6. **Robust Error Handling & Guardrails**
   * Uses a custom exception hierarchy (`LibraryException`) to catch illegal states, missing database entries, or out-of-stock requests gracefully without crashing your terminal.

---

## 🏗️ Project Architecture & File Structure

The project follows a clean **Multi-Layered Architecture**. This means the code is neatly organized into separate folders and classes, keeping user interfaces separate from business logic and database operations:

```text
LibraryApp/
├── src/
│   ├── main/java/com/library/main/
│   │   ├── Main.java                   # 🖥️ CLI Menu Router (Handles user input and menus)
│   │   ├── LibraryService.java         # ⚙️ Business Logic (Handles all core CRUD and rules)
│   │   ├── Database.java               # 🗄️ Database Seeder & Connection (Manages H2 embedded storage)
│   │   ├── AuditLogger.java            # 📝 Audit Tracker (Writes transactions to report files)
│   │   └── LibraryException.java       # 🛡️ Custom Error Wrapper (Handles safe exception messages)
│   └── test/java/com/library/main/
│       └── LibraryServiceTest.java     # 🧪 JUnit 5 Test Suite (Runs automated unit tests)
├── CONTRIBUTING.md                     # Collaboration guidelines and git branching rules
├── library_report.txt                  # Persistent output audit log and activity trace
└── pom.xml                             # Maven configuration file (Manages project dependencies)
