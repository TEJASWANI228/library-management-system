# Library Management System

A simple library management system with a Java command-line application and a browser-based library manager.

## Features

### Java application

- View library dashboard statistics
- List and search books
- Add books
- Issue and return books
- Store book data in an embedded H2 database
- Export an inventory report
- Record audit events

### Web application

The frontend is in [`my-portfolio`](my-portfolio) and works as a static page without a build step.

- Add books through a form
- Borrow and return books
- Track total, available, and borrowed books
- View available and borrowed book names
- Search by title or author
- Filter by category and status
- Sort books
- Save book state in browser `localStorage`
- Export the current books as CSV

## Run the Java application

Requirements:

- Java 11 or newer
- Maven

From the project root:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.library.main.Main"
```

If the Maven exec plugin is not configured, compile the project with Maven and run `com.library.main.Main` from your IDE.

## Run the frontend

Open [`my-portfolio/index.html`](my-portfolio/index.html) directly in a browser.

The frontend currently uses mock data and browser `localStorage`. It does not yet call the Java application over HTTP. The browser data can be cleared by removing the site's local storage.

## Project layout

```text
src/main/java/com/library/main/  Java application source
my-portfolio/                    Static frontend
library_db.mv.db                 H2 database file
library_report.txt               Exported inventory report
system_audit.log                 Audit log
pom.xml                          Maven project configuration
```

## Future integration

The frontend service layer is prepared to be replaced with REST calls for books, borrowing, returns, members, and fines when Spring Boot API endpoints are added.