package com.library.main;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLogger {
    private static final String LOG_FILE = "system_audit.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logAction(String actionType, String details) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = String.format("[%s] [%s] - %s", timestamp, actionType.toUpperCase(), details);

        // Print to console for immediate visibility
        System.out.println(logEntry);

        // Append to the persistent log file
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(logEntry);
        } catch (Exception e) {
            System.out.println("[Error] Failed to write to audit log: " + e.getMessage());
        }
    }
}