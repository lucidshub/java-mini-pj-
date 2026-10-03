package com.campusfind.model;

public class User {
    public long id;
    public String username;
    public String name;
    public String role; // student | faculty

    public boolean isFaculty() { return "faculty".equals(role); }
}
