package com.campusfind.dao;

import com.campusfind.db.DB;
import com.campusfind.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Plain JDBC User DAO (syllabus-style).
 */
public class UserDao {

    public static String roleFromUsername(String username) {
        if (username == null) return null;
        String u = username.trim().toLowerCase();
        if (u.matches("\\d{9}")) return "student";                    // 9-digit PRN
        if (u.matches("[^\\s@]+@acpce\\.ac\\.in")) return "faculty"; // college email
        return null;
    }

    public static User findByUsername(String username) throws Exception {
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE username=?")) {
            ps.setString(1, username.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        }
    }

    public static User findById(long id) throws Exception {
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        }
    }

    public static User create(String username, String password, String name) throws Exception {
        String role = roleFromUsername(username);
        if (role == null) throw new IllegalArgumentException("Use 9-digit PRN (student) or @acpce.ac.in email (faculty).");
        if (password == null || password.length() < 4) throw new IllegalArgumentException("Password must be at least 4 characters.");
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO users (username,password,name,role) VALUES (?,?,?,?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username.toLowerCase());
            ps.setString(2, hash);
            ps.setString(3, name);
            ps.setString(4, role);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return findById(keys.getLong(1));
            }
        }
    }

    public static User checkLogin(String username, String password) throws Exception {
        User u;
        String hash;
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE username=?")) {
            ps.setString(1, username.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                hash = rs.getString("password");
                u = map(rs);
            }
        }
        if (!BCrypt.checkpw(password, hash)) return null;
        return u;
    }

    private static User map(ResultSet rs) throws Exception {
        User u = new User();
        u.id = rs.getLong("id");
        u.username = rs.getString("username");
        u.name = rs.getString("name");
        u.role = rs.getString("role");
        return u;
    }
}
