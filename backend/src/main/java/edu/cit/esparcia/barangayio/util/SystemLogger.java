package edu.cit.esparcia.barangayio.util;

public class SystemLogger {
    private static SystemLogger instance;

    private SystemLogger() {
    }

    public static synchronized SystemLogger getInstance() {
        if (instance == null) {
            instance = new SystemLogger();
        }
        return instance;
    }

    // testing
    public void log(String message) {
        System.out.println("[BARANGAY.IO AUDIT LOG]: " + message);
    }
}
