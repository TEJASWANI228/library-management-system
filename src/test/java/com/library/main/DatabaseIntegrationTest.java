package com.library.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatabaseIntegrationTest {
    private static final String TEST_TITLE = "JUnit Verification Book";

    @BeforeEach
    void setUp() {
        Database.initializeDatabase();
    }

    @AfterEach
    void cleanUp() throws Exception {
        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM books WHERE title = ?")) {
            statement.setString(1, TEST_TITLE);
            statement.executeUpdate();
        }
    }

    @Test
    void initializesSchemaAndSeedsCatalog() throws Exception {
        try (Connection connection = Database.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM books")) {
            assertTrue(resultSet.next());
            assertTrue(resultSet.getInt(1) >= 5);
        }
    }

    @Test
    void supportsBookLifecyclePersistence() throws Exception {
        int bookId;
        try (Connection connection = Database.getConnection();
                PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO books (title, author, isAvailable, borrower_name) VALUES (?, ?, TRUE, NULL)",
                        Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, TEST_TITLE);
            insert.setString(2, "Test Author");
            assertEquals(1, insert.executeUpdate());
            try (ResultSet keys = insert.getGeneratedKeys()) {
                assertTrue(keys.next());
                bookId = keys.getInt(1);
            }
        }

        try (Connection connection = Database.getConnection();
                PreparedStatement select = connection
                        .prepareStatement("SELECT title, author FROM books WHERE id = ?")) {
            select.setInt(1, bookId);
            try (ResultSet resultSet = select.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals(TEST_TITLE, resultSet.getString("title"));
                assertEquals("Test Author", resultSet.getString("author"));
            }
        }

        try (Connection connection = Database.getConnection();
                PreparedStatement update = connection.prepareStatement(
                        "UPDATE books SET title = ?, author = ? WHERE id = ?")) {
            update.setString(1, "Updated Verification Book");
            update.setString(2, "Updated Author");
            update.setInt(3, bookId);
            assertEquals(1, update.executeUpdate());
        }

        try (Connection connection = Database.getConnection();
                PreparedStatement delete = connection.prepareStatement("DELETE FROM books WHERE id = ?")) {
            delete.setInt(1, bookId);
            assertEquals(1, delete.executeUpdate());
        }
    }
}