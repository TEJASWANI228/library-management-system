package com.library.main;

public class Book {
    private int id;
    private String title;
    private String author;
    private boolean isAvailable;
    private String borrowerName;

    public Book(int id, String title, String author, boolean isAvailable, String borrowerName) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
        this.borrowerName = borrowerName;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    @Override
    public String toString() {
        String status = isAvailable ? "Available" : "Issued to: " + (borrowerName != null ? borrowerName : "Unknown");
        return "ID: " + id + " | Title: " + title + " | Author: " + author + " | Status: " + status;
    }
}