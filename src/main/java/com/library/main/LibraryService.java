package com.library.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class LibraryService {

    public void showDashboard() {
        String sqlTotal = "SELECT COUNT(*) FROM books";
        String sqlAvailable = "SELECT COUNT(*) FROM books WHERE isAvailable = TRUE";
        String sqlIssued = "SELECT COUNT(*) FROM books WHERE isAvailable = FALSE";

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement()) {

            ResultSet rsTotal = stmt.executeQuery(sqlTotal);
            int total = rsTotal.next() ? rsTotal.getInt(1) : 0;

            ResultSet rsAvailable = stmt.executeQuery(sqlAvailable);
            int available = rsAvailable.next() ? rsAvailable.getInt(1) : 0;

            ResultSet rsIssued = stmt.executeQuery(sqlIssued);
            int issued = rsIssued.next() ? rsIssued.getInt(1) : 0;

            System.out.println("\n==================================");
            System.out.println("       LIBRARY DASHBOARD          ");
            System.out.println("==================================");
            System.out.println(" Total Books in System : " + total);
            System.out.println(" Available for Issue   : " + available);
            System.out.println(" Currently Issued Out  : " + issued);
            System.out.println("==================================");

        } catch (SQLException e) {
            System.out.println("[Error] Failed to fetch dashboard stats: " + e.getMessage());
        }
    }

    public void viewAllBooks() {
        String sql = "SELECT * FROM books";
        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- BOOK INVENTORY ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean isAvailable = rs.getBoolean("isAvailable");
                String borrower = rs.getString("borrower_name");

                String status = isAvailable ? "Available" : "Issued to: " + borrower;
                System.out.printf("[%d] \"%s\" by %s - Status: %s%n", id, title, author, status);
            }
            if (!found) {
                System.out.println("No books currently found in the system.");
            }

        } catch (SQLException e) {
            System.out.println("[Error] Failed to retrieve books: " + e.getMessage());
        }
    }

    public void addBook(Scanner scanner) {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("[Error] Title cannot be empty.");
            return;
        }

        System.out.print("Enter book author: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            System.out.println("[Error] Author cannot be empty.");
            return;
        }

        String sql = "INSERT INTO books (title, author, isAvailable, borrower_name) VALUES (?, ?, TRUE, NULL)";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.executeUpdate();

            System.out.println("[Success] Book added successfully!");
            AuditLogger.logAction("INSERT", "Added new book: '" + title + "' by " + author);

        } catch (SQLException e) {
            System.out.println("[Error] Failed to add book: " + e.getMessage());
        }
    }

    public void searchBook(Scanner scanner) {
        System.out.print("Enter title keyword to search: ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) {
            System.out.println("[Error] Search keyword cannot be empty.");
            return;
        }

        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- SEARCH RESULTS ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean isAvailable = rs.getBoolean("isAvailable");
                String borrower = rs.getString("borrower_name");

                String status = isAvailable ? "Available" : "Issued to: " + borrower;
                System.out.printf("[%d] \"%s\" by %s - Status: %s%n", id, title, author, status);
            }
            if (!found) {
                System.out.println("No books found matching '" + keyword + "'.");
            }

        } catch (SQLException e) {
            System.out.println("[Error] Failed to search books: " + e.getMessage());
        }
    }

    public void issueBook(Scanner scanner) {
        System.out.print("Enter Book ID to issue: ");
        int bookId = getSafeIntInput(scanner);

        String checkSql = "SELECT title, isAvailable FROM books WHERE id = ?";
        String updateSql = "UPDATE books SET isAvailable = FALSE, borrower_name = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("[Error] Book ID " + bookId + " does not exist.");
                return;
            }

            boolean isAvailable = rs.getBoolean("isAvailable");
            String title = rs.getString("title");

            if (!isAvailable) {
                System.out.println("[Error] Book \"" + title + "\" is already issued out.");
                return;
            }

            System.out.print("Enter borrower's name: ");
            String borrowerName = scanner.nextLine().trim();
            if (borrowerName.isEmpty()) {
                System.out.println("[Error] Borrower name cannot be empty.");
                return;
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, borrowerName);
                updateStmt.setInt(2, bookId);
                updateStmt.executeUpdate();

                System.out.println("[Success] Book \"" + title + "\" successfully issued to " + borrowerName + ".");
                AuditLogger.logAction("CIRCULATION", "Book ID " + bookId + " issued to " + borrowerName);
            }

        } catch (SQLException e) {
            System.out.println("[Error] Failed to issue book: " + e.getMessage());
        }
    }

    public void returnBook(Scanner scanner) {
        System.out.print("Enter Book ID to return: ");
        int bookId = getSafeIntInput(scanner);

        String checkSql = "SELECT title, isAvailable FROM books WHERE id = ?";
        String updateSql = "UPDATE books SET isAvailable = TRUE, borrower_name = NULL WHERE id = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("[Error] Book ID " + bookId + " does not exist.");
                return;
            }

            boolean isAvailable = rs.getBoolean("isAvailable");
            String title = rs.getString("title");

            if (isAvailable) {
                System.out.println("[Error] Book \"" + title + "\" is already available in the library.");
                return;
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();

                System.out.println("[Success] Book \"" + title + "\" has been successfully returned.");
                AuditLogger.logAction("CIRCULATION", "Book ID " + bookId + " returned to inventory.");
            }

        } catch (SQLException e) {
            System.out.println("[Error] Failed to return book: " + e.getMessage());
        }
    }

    public void exportReport() {
        String reportFileName = "library_report.txt";
        String sqlTotal = "SELECT COUNT(*) FROM books";
        String sqlAvailable = "SELECT COUNT(*) FROM books WHERE isAvailable = TRUE";
        String sqlIssued = "SELECT COUNT(*) FROM books WHERE isAvailable = FALSE";
        String sqlAllBooks = "SELECT * FROM books";

        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(reportFileName));
                Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement()) {

            writer.println("==========================================");
            writer.println("       OFFICIAL LIBRARY AUDIT REPORT      ");
            writer.println("==========================================");
            writer.println("Generated on: " + java.time.LocalDateTime.now());
            writer.println();

            ResultSet rsTotal = stmt.executeQuery(sqlTotal);
            int total = rsTotal.next() ? rsTotal.getInt(1) : 0;
            ResultSet rsAvailable = stmt.executeQuery(sqlAvailable);
            int available = rsAvailable.next() ? rsAvailable.getInt(1) : 0;
            ResultSet rsIssued = stmt.executeQuery(sqlIssued);
            int issued = rsIssued.next() ? rsIssued.getInt(1) : 0;

            writer.println("--- SYSTEM STATISTICS ---");
            writer.println("Total Books in System : " + total);
            writer.println("Available for Issue   : " + available);
            writer.println("Currently Issued Out  : " + issued);
            writer.println();

            writer.println("--- COMPLETE INVENTORY ---");
            ResultSet rsBooks = stmt.executeQuery(sqlAllBooks);
            while (rsBooks.next()) {
                int id = rsBooks.getInt("id");
                String title = rsBooks.getString("title");
                String author = rsBooks.getString("author");
                boolean isAvailable = rsBooks.getBoolean("isAvailable");
                String borrower = rsBooks.getString("borrower_name");

                String status = isAvailable ? "Available" : "Issued to: " + (borrower != null ? borrower : "Unknown");
                writer.println(
                        String.format("ID: %d | Title: %s | Author: %s | Status: %s", id, title, author, status));
            }

            writer.println("==========================================");
            System.out.println("[Success] Report successfully exported to '" + reportFileName + "'!");
            AuditLogger.logAction("REPORT", "Official inventory audit report exported to file.");

        } catch (Exception e) {
            throw new LibraryException("Failed to generate export report: " + e.getMessage());
        }
    }

    private int getSafeIntInput(Scanner scanner) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                return input;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.print("[Error] Invalid input. Please enter a valid number: ");
            }
        }
    }
}