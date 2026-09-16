package com.library.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_URL = "jdbc:h2:./library_db;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS books (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "author VARCHAR(255) NOT NULL, " +
                "isAvailable BOOLEAN DEFAULT TRUE, " +
                "borrower_name VARCHAR(255))";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);

            // Check if table is empty, if so, seed real software engineering books
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM books");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute(
                        "INSERT INTO books (title, author, isAvailable, borrower_name) VALUES ('Designing Data-Intensive Applications', 'Martin Kleppmann', TRUE, NULL)");
                stmt.execute(
                        "INSERT INTO books (title, author, isAvailable, borrower_name) VALUES ('Clean Code', 'Robert Martin', TRUE, NULL)");
                stmt.execute(
                        "INSERT INTO books (title, author, isAvailable, borrower_name) VALUES ('The Pragmatic Programmer', 'Andrew Hunt', TRUE, NULL)");
                stmt.execute(
                        "INSERT INTO books (title, author, isAvailable, borrower_name) VALUES ('Introduction to Algorithms', 'Thomas H. Cormen', TRUE, NULL)");
                stmt.execute(
                        "INSERT INTO books (title, author, isAvailable, borrower_name) VALUES ('Refactoring', 'Martin Fowler', TRUE, NULL)");

                System.out.println(
                        "[Info] Database initialized and auto-seeded with professional software engineering classics.");
            } else {
                System.out.println("[Info] H2 Database initialized successfully.");
            }

        } catch (SQLException e) {
            System.out.println("[Error] Failed to initialize database: " + e.getMessage());
        }
    }
}