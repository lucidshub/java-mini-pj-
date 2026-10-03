package com.campusfind.web;

import com.campusfind.dao.UserDao;
import com.campusfind.model.User;
import com.campusfind.util.Auth;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/logout".equals(path)) {
            Auth.logout(req);
            res.sendRedirect(req.getContextPath() + "/");
            return;
        }
        if (Auth.current(req) != null) { res.sendRedirect(req.getContextPath() + "/"); return; }
        if ("/register".equals(path)) req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, res);
        else req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            if ("/login".equals(path)) {
                String username = trim(req.getParameter("username"));
                String password = req.getParameter("password");
                User u = UserDao.checkLogin(username, password == null ? "" : password);
                if (u == null) { fail(req, res, "/WEB-INF/jsp/login.jsp", "Invalid username or password."); return; }
                Auth.login(req, u);
                res.sendRedirect(redirectTarget(req));
                return;
            }
            if ("/register".equals(path)) {
                String username = trim(req.getParameter("username"));
                String password = req.getParameter("password");
                String name = trim(req.getParameter("name"));
                if (username.isEmpty() || name.isEmpty()) { fail(req, res, "/WEB-INF/jsp/register.jsp", "All fields required."); return; }
                if (UserDao.findByUsername(username) != null) { fail(req, res, "/WEB-INF/jsp/register.jsp", "Username already taken."); return; }
                try {
                    User u = UserDao.create(username, password, name);
                    Auth.login(req, u);
                    res.sendRedirect(req.getContextPath() + "/");
                } catch (IllegalArgumentException e) {
                    fail(req, res, "/WEB-INF/jsp/register.jsp", e.getMessage());
                }
                return;
            }
            res.sendError(404);
        } catch (Exception e) {
            fail(req, res, "/register".equals(path) ? "/WEB-INF/jsp/register.jsp" : "/WEB-INF/jsp/login.jsp",
                    "Something went wrong: " + e.getMessage());
        }
    }

    private String redirectTarget(HttpServletRequest req) {
        String from = req.getParameter("from");
        if (from != null && from.startsWith("/") && !from.startsWith("//")) return req.getContextPath() + from;
        return req.getContextPath() + "/";
    }

    private void fail(HttpServletRequest req, HttpServletResponse res, String jsp, String msg) throws ServletException, IOException {
        req.setAttribute("error", msg);
        req.getRequestDispatcher(jsp).forward(req, res);
    }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
