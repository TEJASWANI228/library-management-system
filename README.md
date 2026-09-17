# Library Management System

A simple library management system built with Java, H2, HTML, CSS, and JavaScript.

## Live Demo

[Open the live frontend](https://tejaswani228.github.io/library-management-system/)

The web app supports:

- Adding books
- Borrowing and returning books
- Searching and filtering books
- Tracking available and borrowed books
- Exporting the catalog as CSV
- Saving frontend data in browser `localStorage`

## Features

### Java application

- View library statistics
- Add and search books
- Issue and return books
- Store data in an embedded H2 database
- Export inventory reports
- Record audit logs

### Web frontend

The frontend is a dependency-free static website located in [`my-portfolio`](my-portfolio). It can be opened directly in a browser and is deployed with GitHub Pages.

## Technology Stack

- Java 11+
- Maven
- H2 Database
- JUnit 5
- HTML, CSS, and JavaScript
- GitHub Actions and GitHub Pages

## Project Structure

```text
.github/workflows/       GitHub Pages deployment workflow
my-portfolio/            Static frontend
src/main/java/           Java application source
pom.xml                 Maven configuration
README.md               Project documentation
```

## Run the Java Application

Requirements:

- Java 11 or newer
- Maven 3.8 or newer

Compile the project from the root directory:

```bash
mvn clean compile
```

Run `com.library.main.Main` from your IDE using the Maven project classpath.

## Run the Frontend Locally

Open this file directly in a browser:

```text
my-portfolio/index.html
```

Or start a local server with Python:

```bash
python -m http.server 8080 --directory my-portfolio
```

Then visit `http://localhost:8080`.

## Data Storage

The Java application uses a local H2 database. The frontend stores its data in browser `localStorage`.

The frontend and Java application currently run independently. Connecting them through REST APIs is a future improvement.

## Deployment

The frontend deploys automatically through [`.github/workflows/deploy-frontend.yml`](.github/workflows/deploy-frontend.yml) whenever changes are pushed to `main`.

The Java application requires separate Java-capable hosting.

## License

This project is for learning and demonstration purposes.
