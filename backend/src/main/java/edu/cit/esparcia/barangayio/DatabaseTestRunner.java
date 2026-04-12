package edu.cit.esparcia.barangayio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//@Component
public class DatabaseTestRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=== Testing Database Connection ===");
        
        String url = "jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:5432/postgres?user=postgres.wqisqoevshaohpclduxn&password=Barangay@1403202003";
        
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✓ PostgreSQL Driver loaded successfully");
            
            Connection conn = DriverManager.getConnection(url);
            System.out.println("✓ Connection successful!");
            System.out.println("  Database URL: " + conn.getMetaData().getURL());
            System.out.println("  Database User: " + conn.getMetaData().getUserName());
            System.out.println("  Database Product: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("  Database Version: " + conn.getMetaData().getDatabaseProductVersion());
            conn.close();
        } catch (ClassNotFoundException e) {
            System.err.println("✗ PostgreSQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("✗ Connection failed: " + e.getMessage());
            System.err.println("  SQL State: " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Full details:");
            e.printStackTrace();
        }
        
        System.out.println("=== Test Complete ===\n");
    }
}
