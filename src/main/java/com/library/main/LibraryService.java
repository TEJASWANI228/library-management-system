package com.library.main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
            System.out.println("[Error] Failed to load dashboard stats: " + e.getMessage());
        }
    }

    public void addBook(Scanner scanner) {
        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author Name: ");
        String author = scanner.nextLine();

        String sql = "INSERT INTO books (title, author, isAvailable, borrower_name) VALUES (?, ?, TRUE, NULL)";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.executeUpdate();
            System.out.println("[Success] Book added successfully!");
        } catch (SQLException e) {
            System.out.println("[Error] Failed to add book: " + e.getMessage());
        }
    }

    public void viewAllBooks() {
        String sql = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("isAvailable"),
                        rs.getString("borrower_name")));
            }
        } catch (SQLException e) {
            System.out.println("[Error] Failed to fetch books: " + e.getMessage());
        }

        if (books.isEmpty()) {
            System.out.println("\n[Info] No books found in the library.");
        } else {
            System.out.println("\n=== LIBRARY BOOK INVENTORY ===");
            for (Book book : books) {
                System.out.println(book);
            }
        }
    }

    public void searchBook(Scanner scanner) {
        System.out.print("Enter search keyword (Title): ");
        String keyword = scanner.nextLine();
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE ?";

        List<Book> books = new ArrayList<>();
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("isAvailable"),
                        rs.getString("borrower_name")));
            }
        } catch (SQLException e) {
            System.out.println("[Error] Search failed: " + e.getMessage());
        }

        if (books.isEmpty()) {
            System.out.println("\n[Info] No books matching '" + keyword + "' found.");
        } else {
            System.out.println("\n=== SEARCH RESULTS ===");
            for (Book book : books) {
                System.out.println(book);
            }
        }
    }

    public void issueBook(Scanner scanner) {
        System.out.print("Enter Book ID to issue: ");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.print("Enter Borrower's Name (Student/Member): ");
        String borrowerName = scanner.nextLine();

        String checkSql = "SELECT isAvailable FROM books WHERE id = ?";
        String updateSql = "UPDATE books SET isAvailable = FALSE, borrower_name = ? WHERE id = ? AND isAvailable = TRUE";

        try (Connection conn = Database.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                boolean available = rs.getBoolean("isAvailable");
                if (!available) {
                    System.out.println("[Error] This book is already issued out!");
                    return;
                }
            } else {
                System.out.println("[Error] Book ID not found.");
                return;
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, borrowerName);
                updateStmt.setInt(2, bookId);
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("[Success] Book successfully issued to " + borrowerName + "!");
                } else {
                    System.out.println("[Error] Could not issue the book.");
                }
            }
        } catch (SQLException e) {
            System.out.println("[Error] Failed to issue book: " + e.getMessage());
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

            // Write Header
            writer.println("==========================================");
            writer.println("       OFFICIAL LIBRARY AUDIT REPORT      ");
            writer.println("==========================================");
            writer.println("Generated on: " + java.time.LocalDateTime.now());
            writer.println();

            // Write Stats
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

            // Write Inventory List
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

        } catch (Exception e) {
            throw new LibraryException("Failed to generate export report: " + e.getMessage());
        }
    }

    public void returnBook(Scanner scanner) {
        System.out.print("Enter Book ID to return: ");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        String sql = "UPDATE books SET isAvailable = TRUE, borrower_name = NULL WHERE id = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Success] Book returned successfully and is now available!");
            } else {
                System.out.println("[Error] Book ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("[Error] Failed to return book: " + e.getMessage());
        }
    }
}