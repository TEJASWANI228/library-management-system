# Library Management System

A lightweight library management project with a Java desktop-style application and a simple browser frontend for managing books, borrowing, and returns.

## Live Demo

The frontend is deployed on GitHub Pages:

**[Open the Library Management System](https://tejaswani228.github.io/library-management-system/)**

The live page lets you:

- Add books through a form
- Borrow and return books
- View available and borrowed book names
- Search books by title or author
- Filter by category and availability
- Sort the book list
- Export the catalog as CSV
- Keep frontend changes in browser `localStorage`

## Project Overview

This repository contains two related parts.

### Java application

The Java application provides a menu-driven library system backed by an embedded H2 database. It supports:

- Dashboard statistics
- Book inventory management
- Book title search
- Adding books
- Issuing books to borrowers
- Returning books
- Inventory report export
- Audit logging

### Web frontend

The frontend is a dependency-free static website in [`my-portfolio`](my-portfolio). It uses HTML, CSS, and JavaScript, so it can run directly in a browser without Node.js or a build tool.

The frontend currently uses mock book data and browser storage. It is deployed independently from the Java application and is ready to connect to REST endpoints in the future.

## Technology Stack

| Area | Technology |
| --- | --- |
| Backend application | Java 11+ |
| Database | H2 embedded database |
| Build tool | Maven |
| Testing | JUnit 5 |
| Frontend | HTML, CSS, JavaScript |
| Frontend hosting | GitHub Pages |
| Deployment | GitHub Actions |

## Repository Structure

```text
LibraryApp/
├── .github/
│   └── workflows/
│       └── deploy-frontend.yml
├── my-portfolio/
│   ├── index.html
│   ├── script.js
│   └── style.css
├── src/
│   └── main/java/com/library/main/
│       ├── AuditLogger.java
│       ├── Book.java
│       ├── Database.java
│       ├── LibraryException.java
│       ├── LibraryService.java
│       └── Main.java
├── pom.xml
└── README.md
```

## Run the Java Application

### Requirements

- Java 11 or newer
- Maven 3.8 or newer

### Compile

From the repository root:

```bash
mvn clean compile
```

### Run

Run `com.library.main.Main` from your IDE using the project classpath.

For IntelliJ IDEA or VS Code, import the project as a Maven project, then run the `Main` class located at:

```text
src/main/java/com/library/main/Main.java
```

The application creates or updates the local H2 database when it starts.

## Run the Frontend Locally

No installation is required. Open this file in a browser:

```text
my-portfolio/index.html
```

For a local development server, use any static file server. For example, with Python installed:

```bash
python -m http.server 8080 --directory my-portfolio
```

Then open:

```text
http://localhost:8080
```

## Data Storage

The Java application stores data in the local H2 database file. The browser frontend stores its book list in `localStorage` for the current browser origin.

The Java backend and frontend do not currently share data automatically. Adding a REST API layer is the next step required for the deployed website to use the H2-backed Java service.

## Deployment

The frontend is deployed automatically by [`.github/workflows/deploy-frontend.yml`](.github/workflows/deploy-frontend.yml) whenever changes are pushed to the `main` branch.

GitHub Pages hosts the static frontend only. The Java/H2 application requires a separate Java-capable host, such as a virtual machine, container platform, or application hosting service.

## Future Improvements

- Add Spring Boot REST endpoints for books and circulation
- Connect the frontend service layer to the backend API
- Add member accounts and borrowing history
- Add due dates and fine tracking
- Add automated frontend tests
- Deploy the Java backend with H2 or a production database

## License

This project is intended for learning and demonstration purposes.
