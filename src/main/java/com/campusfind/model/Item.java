package com.campusfind.model;

public class Item {
    public long id;
    public String type; // lost | found
    public String itemName;
    public String description;
    public String location;
    public String date;
    public String imageUrl;
    public String contact;
    public Long reporterId;
    public String reporterName;
    public String reporterRole;
    public boolean claimed;

    public boolean isLost() { return "lost".equals(type); }
}
