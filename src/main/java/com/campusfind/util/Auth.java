package com.campusfind.util;

import com.campusfind.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class Auth {
    public static User current(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return null;
        Object o = s.getAttribute("user");
        return (o instanceof User) ? (User) o : null;
    }
    public static void login(HttpServletRequest req, User u) {
        req.getSession(true).setAttribute("user", u);
    }
    public static void logout(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
    }
}
