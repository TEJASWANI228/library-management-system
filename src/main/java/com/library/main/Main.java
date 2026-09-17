package com.library.main;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    // Helper method to prevent application crashes on invalid text input
    private static int getSafeIntInput(Scanner scanner) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // consume newline
                return input;
            } catch (InputMismatchException e) {
                scanner.nextLine(); // clear invalid buffer
                System.out.print("[Error] Invalid input. Please enter a valid number: ");
            }
        }
    }

    public static void main(String[] args) {
        Database.initializeDatabase();

        Scanner scanner = new Scanner(System.in);
        LibraryService libraryService = new LibraryService();
        boolean running = true;

        System.out.println("\n=== Welcome to the Library Management System ===");

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. View Dashboard & Stats");
            System.out.println("2. View All Books");
            System.out.println("3. Add a Book");
            System.out.println("4. Search Book by Title");
            System.out.println("5. Update Book");
            System.out.println("6. Delete Book");
            System.out.println("7. Issue Book (with Borrower Name)");
            System.out.println("8. Return Book");
            System.out.println("9. Export Inventory Report (to File)");
            System.out.println("10. Exit");
            System.out.print("Choose an option (1-10): ");

            int choice = getSafeIntInput(scanner);

            switch (choice) {
                case 1:
                    libraryService.showDashboard();
                    break;
                case 2:
                    libraryService.viewAllBooks();
                    break;
                case 3:
                    libraryService.addBook(scanner);
                    break;
                case 4:
                    libraryService.searchBook(scanner);
                    break;
                case 5:
                    libraryService.updateBook(scanner);
                    break;
                case 6:
                    libraryService.deleteBook(scanner);
                    break;
                case 7:
                    libraryService.issueBook(scanner);
                    break;
                case 8:
                    libraryService.returnBook(scanner);
                    break;
                case 9:
                    try {
                        libraryService.exportReport();
                    } catch (LibraryException e) {
                        System.out.println("[Error] " + e.getMessage());
                    }
                    break;
                case 10:
                    running = false;
                    System.out.println("Exiting application. Thank you!");
                    break;
                default:
                    System.out.println("[Error] Invalid choice. Please enter a number between 1 and 10.");
            }
        }
        scanner.close();
    }
}