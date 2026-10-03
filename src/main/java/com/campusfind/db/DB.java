package com.campusfind.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * H2 file database. Zero setup - perfect for college demo.
 * DB file: ./campusfind_db.mv.db (auto-created on first run).
 * Uses plain JDBC (as taught in college syllabus).
 */
public class DB {
    private static final String URL = "jdbc:h2:./campusfind_db;AUTO_SERVER=TRUE;MODE=MySQL";
    private static final String USER = "sa";
    private static final String PASS = "";

    static {
        try {
            Class.forName("org.h2.Driver");
            init();
        } catch (Exception e) {
            throw new RuntimeException("DB init failed: " + e.getMessage(), e);
        }
    }

    public static Connection get() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static void init() throws Exception {
        try (Connection c = get(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(255) UNIQUE NOT NULL, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "name VARCHAR(255) NOT NULL, "
                    + "role VARCHAR(20) NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            s.execute("CREATE TABLE IF NOT EXISTS items ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "type VARCHAR(10) NOT NULL, "
                    + "item_name VARCHAR(255) NOT NULL, "
                    + "description CLOB NOT NULL, "
                    + "location VARCHAR(255) NOT NULL, "
                    + "item_date VARCHAR(50) NOT NULL, "
                    + "image_url VARCHAR(1024) DEFAULT '', "
                    + "contact VARCHAR(255) NOT NULL, "
                    + "reporter_id BIGINT, "
                    + "reporter_name VARCHAR(255), "
                    + "reporter_role VARCHAR(20), "
                    + "claimed BOOLEAN DEFAULT FALSE, "
                    + "claimed_by BIGINT, "
                    + "claimed_at TIMESTAMP NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Demo seed (only if empty) so viva/demo looks alive
            var rs = s.executeQuery("SELECT COUNT(*) FROM items");
            rs.next();
            if (rs.getInt(1) == 0) {
                s.execute("INSERT INTO items (type,item_name,description,location,item_date,image_url,contact,reporter_name,reporter_role,claimed) VALUES "
                        + "('found','Black Wallet','Black leather wallet found near library. Contains no cash, has a college ID inside.','Central Library','2026-08-24','','library-desk@acpce.ac.in','Library Desk','faculty',FALSE),"
                        + "('lost','Blue Water Bottle','Steel bottle with stickers, name Shubham written at bottom.','Canteen','2026-09-20','','shubham@example.com','Demo Student','student',FALSE),"
                        + "('found','Casio Calculator','FX-991ES found in classroom B-204 after lecture.','B-204 Classroom','2026-09-28','','helpdesk@acpce.ac.in','Help Desk','faculty',FALSE)");
            }
        }
    }
}
