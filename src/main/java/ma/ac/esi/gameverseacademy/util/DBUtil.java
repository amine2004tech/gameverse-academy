package ma.ac.esi.gameverseacademy.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class DBUtil {

    private static final String DEFAULT_MARIADB_URL =
            "jdbc:mariadb://localhost:3306/gv_up?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "ggez";

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            // 1. Check for explicit SQLite path (Portable Mode)
            String dbPath = System.getProperty("database.path");
            if (dbPath != null) {
                System.out.println("[DBUtil] Portable Mode (SQLite): " + dbPath);
                return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            }

            // 2. Check for Cloud/Environment Config (Production Mode)
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPass = System.getenv("DB_PASSWORD");

            if (dbUrl != null) {
                System.out.println("[DBUtil] Production Mode (Cloud MariaDB/MySQL)");
                return DriverManager.getConnection(dbUrl, dbUser != null ? dbUser : DEFAULT_USER, dbPass != null ? dbPass : DEFAULT_PASSWORD);
            }

            // 3. DEFAULT: Local MariaDB
            System.out.println("[DBUtil] Local Server Mode: Connecting to MariaDB...");
            return DriverManager.getConnection(DEFAULT_MARIADB_URL, DEFAULT_USER, DEFAULT_PASSWORD);
            
        } catch (SQLException e) {
            System.err.println("[DBUtil] Connection failed: " + e.getMessage());
            throw new RuntimeException("DATABASE_CONNECTION_FAILED: Please verify your connection settings!", e);
        }
    }
}
