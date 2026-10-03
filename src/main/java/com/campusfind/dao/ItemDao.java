package com.campusfind.dao;

import com.campusfind.db.DB;
import com.campusfind.model.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** Plain JDBC Item DAO. */
public class ItemDao {

    public static List<Item> list(String q) throws Exception {
        String like = (q == null) ? "" : q.trim().toLowerCase();
        String sql;
        boolean hasQ = !like.isEmpty();
        if (hasQ) {
            sql = "SELECT * FROM items WHERE claimed=FALSE AND (LOWER(item_name) LIKE ? OR LOWER(description) LIKE ? OR LOWER(location) LIKE ?) ORDER BY id DESC";
        } else {
            sql = "SELECT * FROM items WHERE claimed=FALSE ORDER BY id DESC";
        }
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (hasQ) {
                String p = "%" + like + "%";
                ps.setString(1, p);
                ps.setString(2, p);
                ps.setString(3, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Item> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        }
    }

    public static Item find(long id) throws Exception {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM items WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        }
    }

    public static long create(String type, String itemName, String description, String location,
                              String date, String contact, String imageUrl,
                              Long reporterId, String reporterName, String reporterRole) throws Exception {
        if (!"lost".equals(type) && !"found".equals(type)) throw new IllegalArgumentException("type must be lost/found");
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO items (type,item_name,description,location,item_date,image_url,contact,reporter_id,reporter_name,reporter_role,claimed) VALUES (?,?,?,?,?,?,?,?,?,?,FALSE)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, type);
            ps.setString(2, itemName);
            ps.setString(3, description);
            ps.setString(4, location);
            ps.setString(5, date);
            ps.setString(6, imageUrl == null ? "" : imageUrl);
            ps.setString(7, contact);
            if (reporterId == null) ps.setNull(8, java.sql.Types.BIGINT); else ps.setLong(8, reporterId);
            ps.setString(9, reporterName);
            ps.setString(10, reporterRole);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { k.next(); return k.getLong(1); }
        }
    }

    public static void markClaimed(long id, long byUser) throws Exception {
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement("UPDATE items SET claimed=TRUE, claimed_by=?, claimed_at=CURRENT_TIMESTAMP WHERE id=?")) {
            ps.setLong(1, byUser);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    private static Item map(ResultSet rs) throws Exception {
        Item i = new Item();
        i.id = rs.getLong("id");
        i.type = rs.getString("type");
        i.itemName = rs.getString("item_name");
        i.description = rs.getString("description");
        i.location = rs.getString("location");
        i.date = rs.getString("item_date");
        i.imageUrl = rs.getString("image_url");
        i.contact = rs.getString("contact");
        long rid = rs.getLong("reporter_id");
        i.reporterId = rs.wasNull() ? null : rid;
        i.reporterName = rs.getString("reporter_name");
        i.reporterRole = rs.getString("reporter_role");
        i.claimed = rs.getBoolean("claimed");
        return i;
    }
}
