package ma.ac.esi.gameverseacademy.util;

import java.sql.*;
import java.text.SimpleDateFormat;

public class DatabaseMigrator {
    private static final String MARIADB_URL = "jdbc:mariadb://localhost:3306/gv_up?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String SQLITE_URL = "jdbc:sqlite:data/gameverse.db";
    private static final String USER = "root";
    private static final String PASSWORD = "ggez";

    public static void main(String[] args) {
        try {
            System.out.println("Starting migration from MariaDB to SQLite...");
            
            Class.forName("org.mariadb.jdbc.Driver");
            Class.forName("org.sqlite.JDBC");
            
            try (Connection mariaConn = DriverManager.getConnection(MARIADB_URL, USER, PASSWORD);
                 Connection sqliteConn = DriverManager.getConnection(SQLITE_URL)) {
                 
                try (Statement stmt = sqliteConn.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON");
                }
                
                DatabaseMetaData metaData = mariaConn.getMetaData();
                try (ResultSet tables = metaData.getTables(mariaConn.getCatalog(), null, "%", new String[]{"TABLE"})) {
                    while (tables.next()) {
                        String tableName = tables.getString("TABLE_NAME");
                        System.out.println("Migrating table: " + tableName);
                        
                        try (Statement stmt = sqliteConn.createStatement()) {
                            stmt.execute("DROP TABLE IF EXISTS " + tableName);
                        }
                        
                        createTableInSQLite(mariaConn, sqliteConn, tableName);
                        copyData(mariaConn, sqliteConn, tableName);
                    }
                }
                System.out.println("Migration complete!");
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void createTableInSQLite(Connection mariaConn, Connection sqliteConn, String tableName) throws SQLException {
        StringBuilder createSql = new StringBuilder("CREATE TABLE " + tableName + " (");
        
        DatabaseMetaData metaData = mariaConn.getMetaData();
        try (ResultSet columns = metaData.getColumns(mariaConn.getCatalog(), null, tableName, "%")) {
            boolean first = true;
            while (columns.next()) {
                if (!first) createSql.append(", ");
                String colName = columns.getString("COLUMN_NAME");
                String colType = columns.getString("TYPE_NAME");
                
                createSql.append(colName).append(" ");
                
                if (colType.toUpperCase().contains("INT")) {
                    createSql.append("INTEGER");
                } else if (colType.toUpperCase().contains("VARCHAR") || colType.toUpperCase().contains("TEXT")) {
                    createSql.append("TEXT");
                } else if (colType.toUpperCase().contains("DATE") || colType.toUpperCase().contains("TIMESTAMP")) {
                    createSql.append("TEXT");
                } else {
                    createSql.append(colType);
                }
                
                if ("id".equalsIgnoreCase(colName)) {
                    createSql.append(" PRIMARY KEY");
                }
                first = false;
            }
        }
        createSql.append(")");
        
        try (Statement stmt = sqliteConn.createStatement()) {
            stmt.execute(createSql.toString());
        }
    }
    
    private static void copyData(Connection mariaConn, Connection sqliteConn, String tableName) throws SQLException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        try (Statement mariaStmt = mariaConn.createStatement();
             ResultSet rs = mariaStmt.executeQuery("SELECT * FROM " + tableName)) {
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            
            StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
            for (int i = 0; i < columnCount; i++) {
                insertSql.append(i == 0 ? "?" : ", ?");
            }
            insertSql.append(")");
            
            try (PreparedStatement sqliteStmt = sqliteConn.prepareStatement(insertSql.toString())) {
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        Object val = rs.getObject(i);
                        if (val instanceof Timestamp) {
                            sqliteStmt.setString(i, dateFormat.format((Timestamp) val));
                        } else if (val instanceof java.util.Date) {
                            sqliteStmt.setString(i, dateFormat.format((java.util.Date) val));
                        } else {
                            sqliteStmt.setObject(i, val);
                        }
                    }
                    sqliteStmt.executeUpdate();
                }
            }
        }
    }
}
