# Library Management System

## Problem Statement

Small libraries often manage books and circulation with paper records or disconnected spreadsheets. This makes it difficult to know which books are available, update catalog information, remove obsolete records, identify borrowers, and produce a reliable inventory report. The Library Management System provides a single Java application for catalog CRUD operations, circulation, reporting, persistence, and audit logging.

## Project Scope

The implemented project includes:

- A Java 11 command-line application.
- An embedded H2 relational database.
- Catalog CRUD: create, read, update, and delete books.
- Search by book title.
- Book issue and return workflows with borrower names.
- Dashboard statistics for total, available, and issued books.
- Text report export and audit logging.
- Automated JUnit 5 integration tests for database initialization and CRUD persistence.
- A separate static browser frontend demonstrating catalog browsing and circulation interactions with local storage.

A deleted book must be available; issued books must be returned before deletion. Authentication, reservations, due dates, REST integration, and multi-branch synchronization are outside the current scope.

## Target Users

- Librarians managing a small book collection.
- Students learning Java, JDBC, SQL, CRUD operations, validation, testing, and modular application design.

## High-Level Features

1. **Create:** add a book with a required title and author.
2. **Read:** view the full catalog, search by title, and view dashboard statistics.
3. **Update:** change the title and author of an existing book.
4. **Delete:** remove an available book while protecting issued records.
5. **Circulation:** issue an available book to a borrower and return it safely.
6. **Reporting:** export the current inventory to `library_report.txt`.
7. **Monitoring:** record create, update, delete, circulation, and report actions in `system_audit.log`.

## Input and Output

| Operation | Input | Output |
|---|---|---|
| Add | title, author | saved book and audit event |
| View | none | catalog rows with availability |
| Search | title keyword | matching catalog rows |
| Update | book ID, new title, new author | updated book and audit event |
| Delete | book ID | deleted available book or validation error |
| Issue | book ID, borrower name | issued state and audit event |
| Return | book ID | available state and audit event |
| Report | none | inventory report file |

## Subject Relevance

The project applies object-oriented Java, JDBC database connectivity, SQL CRUD operations, prepared statements, input validation, exception handling, file I/O, modular package structure, automated integration testing, and version-controlled documentation in a realistic library workflow.
